package com.apiecommerce.tokoto.location.regency;

import com.apiecommerce.tokoto.location.province.Province;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name  = "regencies")
public class Regency {

    @Id
    @Column(unique = true, nullable = false, length = 4)
    private String id;

    @ManyToOne
    @JoinColumn(name = "province_id", referencedColumnName = "id")
    @Size(min = 2, max = 2)
    private Province province;

    private String name;

    public Regency(String name) {
        this.name = name;
    }
}