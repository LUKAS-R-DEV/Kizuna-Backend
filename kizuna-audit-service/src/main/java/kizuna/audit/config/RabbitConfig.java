package kizuna.audit.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public Queue auditQueue() {
        return QueueBuilder.durable("audit.queue")
                .withArgument("x-dead-letter-exchange", "audit.dlx")
                .withArgument("x-dead-letter-routing-key", "audit.dlq")
                .build();
    }

    @Bean
    public Queue deadLetterQueue() {
        return new Queue("audit.dlq");
    }


    @Bean
    public TopicExchange auditExchange() {
        return new TopicExchange("audit.exchange");
    }

    @Bean
    public TopicExchange deadLetterExchange() {
        return new TopicExchange("audit.dlx");
    }


    @Bean
    public Binding binding() {
        return BindingBuilder
                .bind(auditQueue())
                .to(auditExchange())
                .with("audit.key");
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder
                .bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with("audit.dlq");
    }
}