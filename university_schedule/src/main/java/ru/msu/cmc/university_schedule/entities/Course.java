package ru.msu.cmc.university_schedule.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "stream_id")
    private Stream stream;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @Column(name = "special_course", nullable = false)
    private boolean specialCourse;

    @Column(name = "weekly_intensity", nullable = false)
    private int weeklyIntensity;

    @Column(name = "year_of_study")
    private Integer yearOfStudy;
}
