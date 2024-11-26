package net.purocodigo.encuestabackend.entities;

import javax.persistence.*;
import lombok.Data;

@Entity
@Table(name = "roles")
@Data
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String roleName; // Ejemplo: ROLE_USER, ROLE_ADMIN
}
