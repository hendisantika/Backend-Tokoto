package com.apiecommerce.tokoto.gender;

import com.apiecommerce.tokoto.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Setter
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Gender {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "gender")
    private List<User> users;

    public Gender(String name) {
        this.name = name;
    }
}