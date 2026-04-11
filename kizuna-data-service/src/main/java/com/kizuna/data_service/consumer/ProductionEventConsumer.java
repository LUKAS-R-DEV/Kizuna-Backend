package com.kizuna.data_service.consumer;

import com.kizuna.data_service.domain.ProductionEvent;
import com.kizuna.data_service.dto.ProductionEventDto;
import com.kizuna.data_service.repository.ProductionEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor

public class ProductionEventConsumer {
    private final ProductionEventRepository productionEventRepository;

    @RabbitListener(queues = "production.events")
    public void consume(ProductionEventDto productionEventResponseDto) {
        ProductionEvent productionEvent= ProductionEvent.builder()
                .orderId(productionEventResponseDto.orderId())
                .recipeName(productionEventResponseDto.recipeName())
                .timestamp(productionEventResponseDto.timestamp())
                .type(productionEventResponseDto.type())
                .build();
        productionEventRepository.save(productionEvent);
        System.out.println("Evento salvo: " + productionEvent);
    }
}
