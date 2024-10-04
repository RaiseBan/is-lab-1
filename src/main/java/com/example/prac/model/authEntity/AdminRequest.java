package com.example.prac.model.authEntity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
@Data
@Entity
public class AdminRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Связь с пользователем, который сделал запрос
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "requester_id")
    private User requester; // Пользователь, который запросил админку

    private boolean approvedByAll; // Флаг, когда все админы одобрят

    // Связь с администраторами, которые одобрили
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "admin_request_approved_by",
            joinColumns = @JoinColumn(name = "admin_request_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> approvedBy;
}
