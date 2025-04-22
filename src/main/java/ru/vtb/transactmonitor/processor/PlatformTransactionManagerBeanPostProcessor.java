package ru.vtb.transactmonitor.processor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import ru.vtb.transactmonitor.proxy.LoggingTransactionManager;

@Component
public class PlatformTransactionManagerBeanPostProcessor implements BeanPostProcessor {

  public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName)
      throws BeansException {
    if (bean instanceof PlatformTransactionManager ptm
        && !(bean instanceof LoggingTransactionManager)) {
      return new LoggingTransactionManager(ptm);
    }

    return bean;
  }
}
