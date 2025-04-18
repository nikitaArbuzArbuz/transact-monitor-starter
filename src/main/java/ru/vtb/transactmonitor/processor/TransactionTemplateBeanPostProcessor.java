package ru.vtb.transactmonitor.processor;

import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

@Component
public class TransactionTemplateBeanPostProcessor implements BeanPostProcessor {

  private static final Logger logger =
      LoggerFactory.getLogger(TransactionTemplateBeanPostProcessor.class);

  @Override
  public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName)
      throws BeansException {
    if (bean instanceof TransactionTemplate original) {
      return getEnhancer(original)
          .create(
              new Class[] {PlatformTransactionManager.class},
              new Object[] {original.getTransactionManager()});
    }

    return bean;
  }

  private Enhancer getEnhancer(TransactionTemplate original) {
    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(TransactionTemplate.class);
    enhancer.setCallback(
        (MethodInterceptor)
            (obj, method, args, proxy) -> {
              if ("execute".equals(method.getName())
                  && args.length == 1
                  && args[0] instanceof TransactionCallback) {
                return logTransactionTemplate(original, method, args);
              }

              return method.invoke(original, args);
            });
    return enhancer;
  }

  private <T> T logTransactionTemplate(
      TransactionTemplate transactionTemplate, Method method, Object[] args) {
    long startTime = System.currentTimeMillis();

    try {
      @SuppressWarnings("unchecked")
      TransactionCallback<T> action = (TransactionCallback<T>) args[0];
      T result = transactionTemplate.execute(action);
      long executeTime = System.currentTimeMillis() - startTime;
      logger.info("TransactionTemplate committed in {} ms, method {}", executeTime, method);
      return result;
    } catch (Exception e) {
      long executeTime = System.currentTimeMillis() - startTime;
      logger.error(
          "TransactionTemplate rolled back in {} ms, method {}, error: {}",
          executeTime,
          method,
          e.getMessage());
      throw e;
    }
  }
}
