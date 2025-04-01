DROP TABLE IF EXISTS lesson_students, student_courses, teacher_courses, lessons, students, teachers, groups, streams, auditoriums, courses CASCADE;


CREATE TABLE streams (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE groups (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    stream_id INT NOT NULL REFERENCES streams(id) 
);

CREATE TABLE students (
    id SERIAL PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    year_of_study INT NOT NULL,
    stream_id INT NOT NULL REFERENCES streams(id),
    group_id INT NOT NULL REFERENCES groups(id) 
);

CREATE TABLE teachers (
    id SERIAL PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL
);

CREATE TABLE auditoriums (
    id SERIAL PRIMARY KEY,
    number VARCHAR(20) UNIQUE NOT NULL,
    capacity INT NOT NULL
);

CREATE TABLE courses (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    stream_id INT REFERENCES streams(id) ON DELETE SET NULL,
    group_id INT REFERENCES groups(id) ON DELETE SET NULL,
    special_course BOOLEAN DEFAULT FALSE,
    weekly_intensity INT NOT NULL,
    year_of_study INT 
);

CREATE TABLE lessons (
    id SERIAL PRIMARY KEY,
    course_id INT NOT NULL REFERENCES courses(id),
    teacher_id INT NOT NULL REFERENCES teachers(id),
    auditorium_id INT NOT NULL REFERENCES auditoriums(id),
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL CHECK (end_time > start_time)
);

CREATE TABLE lesson_students (
    lesson_id INT NOT NULL REFERENCES lessons(id),
    student_id INT NOT NULL REFERENCES students(id),
    PRIMARY KEY (lesson_id, student_id)
);

CREATE TABLE teacher_courses (
    teacher_id INT NOT NULL REFERENCES teachers(id),
    course_id INT NOT NULL REFERENCES courses(id),
    year INT NOT NULL,
    PRIMARY KEY (teacher_id, course_id, year)
);

CREATE TABLE student_courses (
    student_id INT NOT NULL REFERENCES students(id),
    course_id INT NOT NULL REFERENCES courses(id),
    year INT NOT NULL,
    PRIMARY KEY (student_id, course_id, year)
);
