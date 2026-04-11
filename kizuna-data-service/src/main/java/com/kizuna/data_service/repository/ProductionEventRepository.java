package com.kizuna.data_service.repository;

import com.kizuna.data_service.domain.ProductionEvent;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductionEventRepository extends MongoRepository<ProductionEvent, String> {
}
