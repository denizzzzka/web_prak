INSERT INTO streams (name) VALUES 
    ('Информатика 2024'),
    ('Математика 2024'),
    ('Физика 2024');

INSERT INTO groups (name, stream_id) VALUES 
    ('Группа 101', 1),
    ('Группа 102', 1),
    ('Группа 201', 2),
    ('Группа 202', 2),
    ('Группа 301', 3);

INSERT INTO students (full_name, year_of_study, stream_id, group_id) VALUES 
    ('Швец Иван Иванов', 1, 1, 1),
    ('Швец Петр Петров', 1, 1, 2),
    ('Швец Сергей Сергеев', 2, 2, 3),
    ('Швец Анна Сидорова', 2, 2, 4),
    ('Швец Мария Кузнецова', 3, 3, 5);

INSERT INTO teachers (full_name) VALUES 
    ('Швец Алексей Алексеев'),
    ('Швец Ольга Иванова'),
    ('Швец Дмитрий Смирнов'),
    ('Швец Татьяна Орлова');

INSERT INTO auditoriums (number, capacity) VALUES 
    ('101А', 30),
    ('102Б', 25),
    ('103В', 20),
    ('104Г', 40),
    ('105Д', 50);

INSERT INTO courses (name, stream_id, group_id, special_course, weekly_intensity, year_of_study) VALUES 
    ('Программирование', 1, NULL, FALSE, 4, 1),
    ('Алгебра', 2, NULL, FALSE, 3, 2),
    ('Физика', NULL, 5, FALSE, 3, 3),
    ('Машинное обучение', NULL, NULL, TRUE, 2, NULL),
    ('Дифференциальные уравнения', NULL, 4, FALSE, 2, 2);

INSERT INTO lessons (course_id, teacher_id, auditorium_id, start_time, end_time) VALUES 
    (1, 1, 1, '2024-03-01 09:00:00', '2024-03-01 10:30:00'),
    (2, 2, 2, '2024-03-01 11:00:00', '2024-03-01 12:30:00'),
    (3, 3, 3, '2024-03-02 09:00:00', '2024-03-02 10:30:00'),
    (4, 4, 4, '2024-03-02 11:00:00', '2024-03-02 12:30:00'),
    (5, 2, 5, '2024-03-03 14:00:00', '2024-03-03 15:30:00');

INSERT INTO lesson_students (lesson_id, student_id) VALUES 
    (1, 1), 
    (1, 2),
    (2, 3), 
    (3, 4),
    (4, 5);

INSERT INTO teacher_courses (teacher_id, course_id, year) VALUES 
    (1, 1, 2023),
    (2, 2, 2023),
    (3, 3, 2023),
    (4, 4, 2024),
    (2, 5, 2024);

INSERT INTO student_courses (student_id, course_id, year) VALUES 
    (1, 1, 2023),
    (2, 2, 2023),
    (3, 3, 2023),
    (4, 4, 2024),
    (5, 5, 2024);