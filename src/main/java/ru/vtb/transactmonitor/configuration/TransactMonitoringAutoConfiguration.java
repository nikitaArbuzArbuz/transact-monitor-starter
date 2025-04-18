package ru.vtb.transactmonitor.configuration;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Role;
import ru.vtb.transactmonitor.aop.TransactMonitoringAspect;
import ru.vtb.transactmonitor.aop.TransactionEventListenerAspect;

@AutoConfiguration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ConditionalOnProperty(
    prefix = "transaction-monitoring",
    name = {"enabled"},
    havingValue = "true")
public class TransactMonitoringAutoConfiguration {

  @Bean
  @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
  public TransactMonitoringAspect transactTrackingAspect() {
    return new TransactMonitoringAspect();
  }

  @Bean
  @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
  public TransactionEventListenerAspect transactionEventListenerAspect() {
    return new TransactionEventListenerAspect();
  }
}
