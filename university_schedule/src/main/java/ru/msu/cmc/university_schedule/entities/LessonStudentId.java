package ru.msu.cmc.university_schedule.entities;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LessonStudentId implements java.io.Serializable {
    private Long lessonId;
    private Long studentId;
}
