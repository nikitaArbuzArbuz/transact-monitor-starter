package ru.vtb.transactmonitor.aop;

import org.aopalliance.aop.AspectException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Aspect
@Component
public class TransactMonitoringAspect {

  private static final Logger logger = LoggerFactory.getLogger(TransactMonitoringAspect.class);

  @Around("@annotation(org.springframework.transaction.annotation.Transactional)")
  public Object logTransaction(ProceedingJoinPoint pjp) throws Throwable {
    return aroundTransactionalMethod(pjp);
  }

  private Object aroundTransactionalMethod(ProceedingJoinPoint pjp) {
    String methodName = pjp.getSignature().toShortString();
    long startTime = System.currentTimeMillis();

    try {
      registerTransactionSynchronization(methodName, startTime);
      return pjp.proceed();
    } catch (Throwable e) {
      throw new AspectException(e.getMessage(), e);
    }
  }

  private void registerTransactionSynchronization(String methodName, long startTime) {
    if (TransactionSynchronizationManager.isSynchronizationActive()) {
      TransactionSynchronizationManager.registerSynchronization(
          new TransactionSynchronization() {
            @Override
            public void afterCommit() {
              try {
                long executeTime = System.currentTimeMillis() - startTime;
                logger.info("Transaction committed in {} ms, method {}", executeTime, methodName);
              } catch (Exception e) {
                logger.error("Error afterCommit for {}: {}", methodName, e.getMessage());
              }
            }

            @Override
            public void afterCompletion(int status) {
              try {
                long executeTime = System.currentTimeMillis() - startTime;
                if (status == STATUS_ROLLED_BACK) {
                  logger.warn(
                      "Transaction rolled back in {} ms, method {}", executeTime, methodName);
                } else {
                  logger.warn(
                      "Transaction completion status is unknown in {} ms, method {}",
                      executeTime,
                      methodName);
                }
              } catch (Exception e) {
                logger.error("Error afterCompletion for {}: {}", methodName, e.getMessage());
              }
            }
          });
    } else {
      logger.warn("No active transaction for {}", methodName);
    }
  }
}
