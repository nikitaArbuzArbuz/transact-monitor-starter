package ru.vtb.transactmonitor.proxy;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;

@Slf4j
@RequiredArgsConstructor
public class LoggingTransactionManager implements PlatformTransactionManager {
  private final PlatformTransactionManager transactionManager;

  @Override
  @NonNull
  public TransactionStatus getTransaction(TransactionDefinition definition)
      throws TransactionException {
    long startTime = System.currentTimeMillis();
    TransactionStatus status = transactionManager.getTransaction(definition);
    return new TimedTransactionStatus(status, startTime);
  }

  @Override
  public void commit(@NonNull TransactionStatus status) throws TransactionException {
    long executeTime = System.currentTimeMillis() - ((TimedTransactionStatus) status).startTime();
    log.info("PlatformTransactionManager commited in {} ms", executeTime);
    transactionManager.commit(((TimedTransactionStatus) status).original());
  }

  @Override
  public void rollback(@NonNull TransactionStatus status) throws TransactionException {
    long executeTime = System.currentTimeMillis() - ((TimedTransactionStatus) status).startTime();
    log.info("PlatformTransactionManager rolled back in {} ms", executeTime);
    transactionManager.rollback(((TimedTransactionStatus) status).original());
  }

  private record TimedTransactionStatus(TransactionStatus original, long startTime)
      implements TransactionStatus {
    @NonNull
    @Override
    public String getTransactionName() {
      return original.getTransactionName();
    }

    @Override
    public boolean hasTransaction() {
      return original.hasTransaction();
    }

    @Override
    public boolean isNested() {
      return original.isNested();
    }

    @Override
    public boolean isReadOnly() {
      return original.isReadOnly();
    }

    @Override
    public boolean isNewTransaction() {
      return original.isNewTransaction();
    }

    @Override
    public void setRollbackOnly() {
      original.setRollbackOnly();
    }

    @Override
    public boolean isRollbackOnly() {
      return original.isRollbackOnly();
    }

    @Override
    public boolean isCompleted() {
      return original.isCompleted();
    }

    @Override
    public boolean hasSavepoint() {
      return original.hasSavepoint();
    }

    @Override
    public void flush() {
      original.flush();
    }

    @NonNull
    @Override
    public Object createSavepoint() throws TransactionException {
      return original.createSavepoint();
    }

    @Override
    public void rollbackToSavepoint(@NonNull Object savepoint) throws TransactionException {
      original.rollbackToSavepoint(savepoint);
    }

    @Override
    public void releaseSavepoint(@NonNull Object savepoint) throws TransactionException {
      original.releaseSavepoint(savepoint);
    }
  }
}
