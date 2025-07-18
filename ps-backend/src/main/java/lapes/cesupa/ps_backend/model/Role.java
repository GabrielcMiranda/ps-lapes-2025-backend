package lapes.cesupa.ps_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "roles")
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long id;

    private String name;

    public enum Values{

        ADMIN(1L),
        KITCHEN(2L),
        DELIVERY(3L),
        COSTUMER(4L);

        long roleId;

        Values(long roleId){
            this.roleId = roleId;
        }
 
    }

    
}
