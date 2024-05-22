package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class City {
    @Id
    private Long id;
    @Column
    private String name;
    @OneToMany(mappedBy = "city")
    List<User> users;

    public City(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
