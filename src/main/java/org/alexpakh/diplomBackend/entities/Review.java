package org.alexpakh.diplomBackend.entities;


import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder


@Table(name = "review")
public class Review {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String textReviews;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
}
