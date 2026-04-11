package Kizuna_core_service.inventory.domain;

import Kizuna_core_service.shared.exception.BusinessException;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "inventory")
@Data
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String category;
    private String location;
    private Double quantity;
    private Double minStock;
    private String supplier;
    @Enumerated(EnumType.STRING)
    private Status status;
    private Boolean active=true;


    public void addStock(Double quantity){
        this.quantity+=quantity;
    }
    public void removeStock(Double quantity){
        if (this.quantity < quantity) {
            throw new BusinessException("Not enough stock");
        }
        this.quantity -= quantity;
        updateStatus();
    }
   public void updateStatus(){
        if(this.quantity <= this.minStock){
            this.status=Status.CRITICAL;
        }else{
            this.status=Status.GOOD;
        }
    }

}