package com.pe.appventas.msorderservice.entities;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "ORDER_DETAILS")
@Entity
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "QUANTITY")
    private Integer quantity;

    @Column(name = "PRICE")
    private Double price;

    @Column(name = "IGV")
    private Double igv;

    @Column(name = "UPC")
    private String upc;

    @ManyToOne(cascade = CascadeType.ALL)
    private Order order;
}
