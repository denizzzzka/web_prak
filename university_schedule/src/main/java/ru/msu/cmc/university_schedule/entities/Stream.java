package ru.msu.cmc.university_schedule.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "streams")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Stream {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;
}
