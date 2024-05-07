package org.alexpakh.diplomBackend.entities;

import jakarta.persistence.*;
import lombok.*;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "sales_manager")
public class SalesManager {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String firstName;
    private String secondName;
    private String patronymic;
    private String login;
    private String password;
    private String telephoneNumber;
    private String email;


}
