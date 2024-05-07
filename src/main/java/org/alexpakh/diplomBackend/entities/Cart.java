package org.alexpakh.diplomBackend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "cart")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal rentAmount;

    @OneToMany(cascade = CascadeType.ALL)
    @MapsId
    @JoinColumn(name = "customer_id")
    private Set<Customer> customers = new HashSet<>();


    @ManyToMany
            (fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            })
    @JoinTable(name = "cart_tool",
            joinColumns = {@JoinColumn(name = "cart_id")},
            inverseJoinColumns = {@JoinColumn(name = "tool_id")})
    private Set<Tool> toolsCart = new HashSet<>();
}
