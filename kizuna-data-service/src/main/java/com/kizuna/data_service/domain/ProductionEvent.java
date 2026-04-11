package com.kizuna.data_service.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "production_events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductionEvent {
    @Id
    private String id;
    private Long orderId;
    private String recipeName;
    private String type;
    private LocalDateTime timestamp;

}
