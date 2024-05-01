package com.apiecommerce.tokoto.location.village;

import com.apiecommerce.tokoto.location.district.District;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "villages")
public class Village {

    @Id
    @Column(unique = true, nullable = false, length = 10)
    private String id;

    @ManyToOne
    @JoinColumn(name = "district_id")
    @Size(min = 7, max = 7)
    private District district;

    private String name;

    public Village(String name) {
        this.name = name;
    }
}