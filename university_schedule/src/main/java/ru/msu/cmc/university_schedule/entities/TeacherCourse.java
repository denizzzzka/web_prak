package ru.msu.cmc.university_schedule.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "teacher_courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TeacherCourse implements CommonEntity<TeacherCourseId>{
    @EmbeddedId
    private TeacherCourseId id;

    @ManyToOne
    @MapsId("teacherId")
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @ManyToOne
    @MapsId("courseId")
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(name = "year",insertable = false, nullable = false, updatable=false)
    private int year;
}

