package com.apiecommerce.tokoto.location.district;

import com.apiecommerce.tokoto.location.regency.Regency;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "districts")
public class District {

    @Id
    @Column(unique = true, nullable = false, length = 7)
    private String id;

    @ManyToOne
    @JoinColumn(name = "regency_id")
    @Size(min = 4, max = 4)
    private Regency regency;

    private String name;

    public District(String name) {
        this.name = name;
    }
}