package org.alexpakh.diplomBackend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "tool")

public class Tool {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String article;
    private String title;
    private String condition;
    private String price;

    @ManyToOne
    @JoinColumn(name="category_id")
    private ToolCategory category;


    @Lob
    @Column(name="image_data")
    private byte[] image;


    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            },
            mappedBy = "toolsCart")
    @JsonIgnore
    private Set<Cart> carts = new HashSet<>();


    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            },
            mappedBy = "tools")
    @JsonIgnore
    private Set<Order> orders = new HashSet<>();




}
