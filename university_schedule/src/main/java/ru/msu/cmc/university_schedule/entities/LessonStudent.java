package ru.msu.cmc.university_schedule.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lesson_students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LessonStudent {
    @EmbeddedId
    private LessonStudentId id;

    @ManyToOne
    @MapsId("lessonId")
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    @ManyToOne
    @MapsId("studentId")
    @JoinColumn(name = "student_id")
    private Student student;
}

