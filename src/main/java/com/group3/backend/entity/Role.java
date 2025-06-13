package com.group3.backend.entity;

import com.group3.backend.entity.model.Roles;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, unique = true)
    private Roles name;

    public Role(Roles name) {
        this.name = name;
    }
}
