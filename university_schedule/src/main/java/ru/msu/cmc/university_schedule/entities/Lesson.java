package ru.msu.cmc.university_schedule.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lessons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @ManyToOne
    @JoinColumn(name = "auditorium_id", nullable = false)
    private Auditorium auditorium;

    @Column(name = "start_time", nullable = false)
    private java.time.LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private java.time.LocalDateTime endTime;
}

