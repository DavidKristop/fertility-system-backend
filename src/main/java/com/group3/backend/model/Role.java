package com.group3.backend.model;

import java.util.List;

import com.group3.backend.constants.Roles;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, unique = true, nullable = false)
    private Roles name;
    
    // @OneToMany(mappedBy = "role")
    // private List<User> users;

    public Role(Roles name) {
        this.name = name;
    }

}
