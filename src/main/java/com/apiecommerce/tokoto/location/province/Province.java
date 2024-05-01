package com.apiecommerce.tokoto.location.province;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name  = "provinces")
public class Province {

    @Id
    @Column(unique = true, nullable = false, length = 2)
    private String id;

    private String name;

    public Province(String name) {
        this.name = name;
    }
}