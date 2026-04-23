package Kizuna_core_service.productionOrder.service;


import Kizuna_core_service.inventory.repository.InventoryRepository;
import Kizuna_core_service.productionOrder.domain.ProductionOrder;
import Kizuna_core_service.productionOrder.domain.ProductionOrderStatus;
import Kizuna_core_service.productionOrder.repository.ProductionOrderRepository;
import Kizuna_core_service.recipe.domain.Recipe;
import Kizuna_core_service.shared.dto.ApiResponseGeneric;
import Kizuna_core_service.shared.messaging.EventPublisher;
import Kizuna_core_service.shared.util.SecurityUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductionOrderServiceTest {

    @Mock
    private ProductionOrderRepository productionOrderRepository;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private ProductionOrderService productionOrderService;

    // Mock estático para SecurityUtils (necessário se SecurityUtils for uma classe com métodos estáticos)
    private MockedStatic<SecurityUtils> securityUtilsMock;

    @BeforeEach
    void setUp() {
        securityUtilsMock = mockStatic(SecurityUtils.class);
    }

    @AfterEach
    void tearDown() {
        securityUtilsMock.close();
    }


    @Test
    void pauseProductionOrder() {
        Long orderId = 1L;
        String userId = "user123";

        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setName("Bolo de Chocolate");

        ProductionOrder order = new ProductionOrder();
        order.setId(orderId);
        order.setStatus(ProductionOrderStatus.IN_PROGRESS);
        order.setOperatorId(userId);
        order.setRecipe(recipe);


        when(productionOrderRepository.findById(orderId)).thenReturn(Optional.of(order));
        securityUtilsMock.when(SecurityUtils::getUserId).thenReturn(userId);
        securityUtilsMock.when(SecurityUtils::getUsername).thenReturn("Joao");

        // Act
        ApiResponseGeneric response = productionOrderService.pause(orderId);

        // Assert
        assertEquals(ProductionOrderStatus.PAUSED, order.getStatus());
        verify(productionOrderRepository).save(order);
        verify(eventPublisher, times(3)).publish(any(), any(), any(), any(), any(), any());
      ;

    }






}
