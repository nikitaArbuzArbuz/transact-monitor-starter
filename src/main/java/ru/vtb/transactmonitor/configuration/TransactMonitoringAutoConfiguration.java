package ru.vtb.transactmonitor.configuration;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import ru.vtb.transactmonitor.aop.TransactMonitoringAspect;

@AutoConfiguration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ConditionalOnProperty(
    prefix = "transaction-monitoring",
    name = {"enabled"},
    havingValue = "true",
    matchIfMissing = true)
public class TransactMonitoringAutoConfiguration {

  @Bean
  public TransactMonitoringAspect transactTrackingAspect() {
    return new TransactMonitoringAspect();
  }
}
