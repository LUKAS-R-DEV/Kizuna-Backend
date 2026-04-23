package Kizuna_core_service.productionOrder.service;

import Kizuna_core_service.productionOrder.domain.ProductionOrder;
import Kizuna_core_service.productionOrder.domain.ProductionOrderStatus;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class ProductionOrderCalculate {
    public Long calculateRemainingTime(ProductionOrder order) {
        if (order.getStartTime() == null || order.getEstimatedTotalTime() == null || order.getStatus().equals(ProductionOrderStatus.PAUSED)  || order.getStatus().equals(ProductionOrderStatus.REJECTED) || order.getStatus().equals(ProductionOrderStatus.APPROVED) ) {
            return order.getEstimatedTotalTime();
        }
        long elapsed = Duration.between(order.getStartTime(), LocalDateTime.now()).toMinutes();
        long remaing = order.getEstimatedTotalTime() - elapsed;
        return Math.max(0, remaing);

    }

    public Double calculateProgress(ProductionOrder order) {
        if (order.getStartTime() == null || order.getEstimatedTotalTime() == null || order.getStatus().equals(ProductionOrderStatus.PAUSED)) {
            return 0.0;
        }
        if(order.getStatus().equals(ProductionOrderStatus.CANCELLED)){
            return 0.0;
        }
        if(order.getStatus().equals(ProductionOrderStatus.APPROVED) || order.getStatus().equals(ProductionOrderStatus.REJECTED)){
            return 0.0;
        }
        long total = order.getEstimatedTotalTime();
        long elapsed = Duration.between(order.getStartTime(), LocalDateTime.now()).toMinutes();
        double progress = ((double) elapsed / total) * 100;
        return Math.min(progress, 100.0);
    }

    public LocalDateTime calculateETA(ProductionOrder order) {
        if (order.getStartTime() == null || order.getEstimatedTotalTime() == null || order.getStatus().equals(ProductionOrderStatus.PAUSED)  || order.getStatus().equals(ProductionOrderStatus.APPROVED) || order.getStatus().equals(ProductionOrderStatus.REJECTED) ) {
            return null;
        }
        return order.getStartTime().plusMinutes(order.getEstimatedTotalTime());
    }

    public ProductionOrderStatus calculateStatus(ProductionOrder order) {
        if (order.getStatus() == ProductionOrderStatus.WAITING_INSPECTION ||
                order.getStatus() == ProductionOrderStatus.CANCELLED ||
                order.getStatus() == ProductionOrderStatus.FINISHED_BY_TIME ||
                order.getStatus() == ProductionOrderStatus.PAUSED ||
                order.getStatus() == ProductionOrderStatus.REWORK) {
            return order.getStatus();
        }

        if (order.getStartTime() == null || order.getEstimatedTotalTime() == null) {
            return ProductionOrderStatus.PLANNED;
        }

        long elapsed = Duration.between(order.getStartTime(), LocalDateTime.now()).toMinutes();

        if (elapsed >= order.getEstimatedTotalTime()) {
            return ProductionOrderStatus.FINISHED_BY_TIME;
        }

        return ProductionOrderStatus.IN_PROGRESS;
    }
}
