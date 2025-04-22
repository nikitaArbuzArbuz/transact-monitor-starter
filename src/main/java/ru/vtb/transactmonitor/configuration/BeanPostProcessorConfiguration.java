package ru.vtb.transactmonitor.configuration;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import ru.vtb.transactmonitor.processor.PlatformTransactionManagerBeanPostProcessor;
import ru.vtb.transactmonitor.processor.TransactionTemplateBeanPostProcessor;

@AutoConfiguration
@ConditionalOnClass({TransactionTemplate.class, PlatformTransactionManager.class})
@ConditionalOnProperty(
    prefix = "transaction-monitoring",
    name = {"enabled"},
    havingValue = "true")
public class BeanPostProcessorConfiguration {

  @Bean
  @ConditionalOnMissingBean(TransactionTemplate.class)
  public TransactionTemplate transactionTemplate(PlatformTransactionManager txManager) {
    return new TransactionTemplate(txManager);
  }

  @Bean
  public TransactionTemplateBeanPostProcessor transactionTemplateBeanPostProcessor() {
    return new TransactionTemplateBeanPostProcessor();
  }

  @Bean
  public PlatformTransactionManagerBeanPostProcessor coordinatorBeanPostProcessor() {
    return new PlatformTransactionManagerBeanPostProcessor();
  }
}
