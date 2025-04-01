package ru.msu.cmc.university_schedule.entities;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentCourseId implements java.io.Serializable {
    private Long studentId;
    private Long courseId;
    private int year;
}
