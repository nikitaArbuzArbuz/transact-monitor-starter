package ru.vtb.transactmonitor.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TransactionEventListenerAspect {

  private static final Logger logger =
      LoggerFactory.getLogger(TransactionEventListenerAspect.class);

  @Pointcut("@annotation(org.springframework.transaction.event.TransactionalEventListener)")
  public void transactionEventListenerPointcut() {}

  @Around("transactionEventListenerPointcut()")
  public Object logTransactionEventListener(ProceedingJoinPoint joinPoint) throws Throwable {
    long startTime = System.currentTimeMillis();
    try {
      Object result = joinPoint.proceed();
      long executionTime = System.currentTimeMillis() - startTime;
      logger.info(
          "TransactionalEventListener executed in {} ms, method {}",
          executionTime,
          joinPoint.getSignature());

      return result;
    } catch (Throwable e) {
      long executionTime = System.currentTimeMillis() - startTime;
      logger.error(
          "TransactionalEventListener failed in {} ms, method {}",
          executionTime,
          joinPoint.getSignature(),
          e);
      throw e;
    }
  }
}
