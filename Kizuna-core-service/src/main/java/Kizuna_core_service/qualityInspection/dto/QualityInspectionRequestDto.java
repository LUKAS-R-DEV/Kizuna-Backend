package Kizuna_core_service.qualityInspection.dto;

import Kizuna_core_service.qualityInspection.domain.QualityInspectionStatus;

public record QualityInspectionRequestDto(Long productionOrderId, QualityInspectionStatus status,String notes) {

}
