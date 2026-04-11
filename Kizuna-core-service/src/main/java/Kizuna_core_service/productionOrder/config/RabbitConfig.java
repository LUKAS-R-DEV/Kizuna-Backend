package Kizuna_core_service.productionOrder.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitConfig {

    @Bean
    public Queue productionEventsQueue() {
        return new Queue("production.events",true);
    }
    
}
