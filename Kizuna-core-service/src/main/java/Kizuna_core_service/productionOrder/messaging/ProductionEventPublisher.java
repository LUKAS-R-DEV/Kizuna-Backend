package Kizuna_core_service.productionOrder.messaging;


import Kizuna_core_service.productionOrder.dto.ProductionOrderEventMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor

public class ProductionEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    public void  sendEvent(Long orderId,String recipeName,String type){
        ProductionOrderEventMessage eventMessage=new ProductionOrderEventMessage(orderId,recipeName,type, LocalDateTime.now());
        rabbitTemplate.convertAndSend("production.events", eventMessage);
    }
}
