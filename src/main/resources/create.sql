-- CREATE TABLE "category" (
--   "category_id" int PRIMARY KEY,
--   "category_name" text
-- );

-- CREATE TABLE "faculty" (
--   "faculty_id" int PRIMARY KEY,
--   "faculty_name" text
-- );	

-- CREATE TABLE "groupp" (
--   "group_id" int PRIMARY KEY,
--   "group_num" int,
--   "faculty_id" int
-- );

-- CREATE TABLE "discipline" (
--   "discipline_id" int PRIMARY KEY,
--   "discipline_name" text,
--   "discipline_year" int,
--   "discipline_semester" int
-- );

-- CREATE TABLE "event_type" (
--   "event_type_id" int PRIMARY KEY,
--   "event_type_name" text,
--   "event_num_of_hours" int
-- );

-- CREATE TABLE "control_form" (
--   "control_form_id" int PRIMARY KEY,
--   "control_form_name" text
-- );

-- CREATE TABLE "department" (
--   "department_id" int PRIMARY KEY,
--   "department_name" text,
--   "faculty_id" int
-- );

-- CREATE TABLE "student" (
--   "student_id" int PRIMARY KEY,
--   "student_name" text,
--   "student_lastname" text,
--   "group_id" int,
--   "student_course" int,
--   "student_birth_year" int,
--   "student_gender" text,
--   "student_age" int,
--   "student_have_kids" bool,
--   "student_have_scolarship" bool,
--   "student_scolarship_amount" int
-- );

-- CREATE TABLE "teacher" (
--   "teacher_id" int PRIMARY KEY,
--   "teacher_name" text,
--   "teacher_lastname" text,
--   "category_id" int,
--   "department_id" int,
--   "teacher_birth_year" int,
--   "teacher_gender" text,
--   "teacher_age" int,
--   "teacher_have_kids" bool,
--   "teacher_salary_amount" int,
--   "teacher_is_graduate" bool,
--   "teacher_has_defended_thesis" bool,
--   "teacher_dissertation_year" int
-- );

-- CREATE TABLE "exam" (
--   "exam_id" int PRIMARY KEY,
--   "discipline_id" int,
--   "student_id" int,
--   "teacher_id" int,
--   "exam_grade" text
-- );

-- CREATE TABLE "thesis" (
--   "thesis_id" int PRIMARY KEY,
--   "title" text,
--   "student_id" int,
--   "teacher_id" int
-- );

-- CREATE TABLE "dissertation" (
--   "dissertation_id" int PRIMARY KEY,
--   "dissertation_title" text,
--   "teacher_id" int
-- );

-- CREATE TABLE "discipline_and_event_type" (
--   "id" int PRIMARY KEY,
--   "discipline" int,
--   "event_type" int
-- );

-- CREATE TABLE "discipline_and_control_form" (
--   "id" int PRIMARY KEY,
--   "discipline" int,
--   "control_form" int
-- );

-- ALTER TABLE "groupp" ADD FOREIGN KEY ("faculty_id") REFERENCES "faculty" ("faculty_id");

-- ALTER TABLE "department" ADD FOREIGN KEY ("faculty_id") REFERENCES "faculty" ("faculty_id");

-- ALTER TABLE "student" ADD FOREIGN KEY ("group_id") REFERENCES "groupp" ("group_id");

-- ALTER TABLE "teacher" ADD FOREIGN KEY ("category_id") REFERENCES "category" ("category_id");

-- ALTER TABLE "teacher" ADD FOREIGN KEY ("department_id") REFERENCES "department" ("department_id");

-- ALTER TABLE "exam" ADD FOREIGN KEY ("discipline_id") REFERENCES "discipline" ("discipline_id");

-- ALTER TABLE "exam" ADD FOREIGN KEY ("student_id") REFERENCES "student" ("student_id");

-- ALTER TABLE "exam" ADD FOREIGN KEY ("teacher_id") REFERENCES "teacher" ("teacher_id");

-- ALTER TABLE "thesis" ADD FOREIGN KEY ("student_id") REFERENCES "student" ("student_id");

-- ALTER TABLE "thesis" ADD FOREIGN KEY ("teacher_id") REFERENCES "teacher" ("teacher_id");

-- ALTER TABLE "dissertation" ADD FOREIGN KEY ("teacher_id") REFERENCES "teacher" ("teacher_id");

-- ALTER TABLE "discipline_and_event_type" ADD FOREIGN KEY ("discipline") REFERENCES "discipline" ("discipline_id");

-- ALTER TABLE "discipline_and_event_type" ADD FOREIGN KEY ("event_type") REFERENCES "event_type" ("event_type_id");

-- ALTER TABLE "discipline_and_control_form" ADD FOREIGN KEY ("discipline") REFERENCES "discipline" ("discipline_id");

-- ALTER TABLE "discipline_and_control_form" ADD FOREIGN KEY ("control_form") REFERENCES "control_form" ("control_form_id");




-- INSERT INTO category (category_id, category_name) VALUES
-- (1, 'Assistant'),
-- (2, 'Teacher'),
-- (3, 'Senior Teacher'),
-- (4, 'Docent'),
-- (5, 'Professor');

-- SELECT * FROM category



-- INSERT INTO faculty (faculty_id, faculty_name) VALUES
-- (1, 'Faculty of Mechanics and Mathematics'),
-- (2, 'Faculty of Physics'),
-- (3, 'Faculty of Information Technologies'),
-- (4, 'Faculty of Economics'),
-- (5, 'Faculty of Law');

-- SELECT * FROM faculty



-- INSERT INTO groupp(group_id, group_num, faculty_id) VALUES 
-- -- ФИТ
-- (1, 21205, 3),
-- -- Право
-- (2, 22901, 5),
-- -- Мехмат
-- (3, 23111, 1),
-- -- Физика
-- (4, 21341, 2),
-- -- Эконом
-- (5, 22704, 4);

-- -- SELECT * FROM groupp



-- INSERT INTO discipline (discipline_id, discipline_name, discipline_year, discipline_semester) VALUES
-- (1, 'Calculus I', 2021, 1),
-- (2, 'Linear Algebra', 2021, 2),
-- (3, 'Classical Mechanics', 2022, 1),
-- (4, 'Quantum Physics', 2022, 2),
-- (5, 'Introduction to Programming', 2023, 1),
-- (6, 'Data Structures', 2023, 2),
-- (7, 'Microeconomics', 2021, 1),
-- (8, 'Macroeconomics', 2021, 2),
-- (9, 'Constitutional Law', 2022, 1),
-- (10, 'International Law', 2022, 2);

-- -- SELECT * FROM discipline



-- INSERT INTO event_type (event_type_id, event_type_name, event_num_of_hours) VALUES
-- (1, 'Lecture', 2),
-- (2, 'Seminar', 2),
-- (3, 'Lab Work', 2);

-- -- SELECT * FROM event_type



-- INSERT INTO control_form (control_form_id, control_form_name) VALUES
-- (1, 'Setoff'),
-- (2, 'Exam');

-- SELECT * FROM control_form



-- INSERT INTO department (department_id, department_name, faculty_id) VALUES
-- (1, 'Department of Algebra and Geometry', 1),
-- (2, 'Department of Mathematical Analysis', 1),
-- (3, 'Department of Theoretical Physics', 2),
-- (4, 'Department of Experimental Physics', 2),
-- (5, 'Department of Computer Science', 3),
-- (6, 'Department of Information Systems', 3),
-- (7, 'Department of Economic Theory', 4),
-- (8, 'Department of Finance', 4),
-- (9, 'Department of Civil Law', 5),
-- (10, 'Department of Criminal Law', 5);

-- -- SELECT * FROM department




-- INSERT INTO student (
--   student_id, 
--   student_name, 
--   student_lastname, 
--   group_id, 
--   student_course, 
--   student_birth_year, 
--   student_gender, 
--   student_age, 
--   student_have_kids, 
--   student_have_scolarship, 
--   student_scolarship_amount
-- ) VALUES
-- (1, 'John', 'Doe', 1, 2, 2001, 'Male', 23, FALSE, TRUE, 1000),
-- (2, 'Jane', 'Smith', 2, 3, 2000, 'Female', 24, FALSE, TRUE, 1200),
-- (3, 'Emily', 'Jones', 3, 1, 2002, 'Female', 22, FALSE, FALSE, 0),
-- (4, 'Michael', 'Brown', 4, 4, 1999, 'Male', 25, TRUE, FALSE, 0),
-- (5, 'Sarah', 'Davis', 5, 2, 2001, 'Female', 23, FALSE, TRUE, 1500),
-- (6, 'David', 'Wilson', 1, 3, 2000, 'Male', 24, TRUE, FALSE, 0),
-- (7, 'Laura', 'Moore', 2, 1, 2002, 'Female', 22, FALSE, TRUE, 900),
-- (8, 'James', 'Taylor', 3, 4, 1999, 'Male', 25, FALSE, FALSE, 0),
-- (9, 'Anna', 'Anderson', 4, 2, 2001, 'Female', 23, TRUE, TRUE, 1300),
-- (10, 'Chris', 'Thomas', 5, 3, 2000, 'Male', 24, FALSE, TRUE, 1100),
-- (11, 'Sophia', 'Jackson', 1, 1, 2002, 'Female', 22, FALSE, FALSE, 0),
-- (12, 'Daniel', 'White', 2, 4, 1999, 'Male', 25, TRUE, FALSE, 0),
-- (13, 'Olivia', 'Harris', 3, 2, 2001, 'Female', 23, FALSE, TRUE, 1000),
-- (14, 'Matthew', 'Martin', 4, 3, 2000, 'Male', 24, FALSE, TRUE, 1500),
-- (15, 'Emma', 'Thompson', 5, 1, 2002, 'Female', 22, TRUE, FALSE, 0),
-- (16, 'Joshua', 'Garcia', 1, 4, 1999, 'Male', 25, FALSE, FALSE, 0),
-- (17, 'Grace', 'Martinez', 2, 2, 2001, 'Female', 23, FALSE, TRUE, 1400),
-- (18, 'Andrew', 'Robinson', 3, 3, 2000, 'Male', 24, TRUE, TRUE, 1300),
-- (19, 'Ava', 'Clark', 4, 1, 2002, 'Female', 22, FALSE, FALSE, 0),
-- (20, 'Lucas', 'Rodriguez', 5, 4, 1999, 'Male', 25, FALSE, TRUE, 1200),
-- (21, 'Ella', 'Lewis', 1, 2, 2001, 'Female', 23, TRUE, TRUE, 1000),
-- (22, 'Mason', 'Lee', 2, 3, 2000, 'Male', 24, FALSE, FALSE, 0),
-- (23, 'Isabella', 'Walker', 3, 1, 2002, 'Female', 22, FALSE, TRUE, 900),
-- (24, 'Ethan', 'Hall', 4, 4, 1999, 'Male', 25, TRUE, FALSE, 0),
-- (25, 'Avery', 'Allen', 5, 2, 2001, 'Female', 23, FALSE, TRUE, 1100);


-- -- SELECT * FROM student




-- INSERT INTO teacher (
--   teacher_id, 
--   teacher_name, 
--   teacher_lastname, 
--   category_id, 
--   department_id, 
--   teacher_birth_year, 
--   teacher_gender, 
--   teacher_age, 
--   teacher_have_kids, 
--   teacher_salary_amount, 
--   teacher_is_graduate, 
--   teacher_has_defended_thesis, 
--   teacher_dissertation_year
-- ) VALUES
-- (1, 'Alice', 'Johnson', 1, 1, 1985, 'Female', 39, TRUE, 4000, TRUE, FALSE, NULL),
-- (2, 'Bob', 'Smith', 2, 2, 1978, 'Male', 46, TRUE, 5000, TRUE, TRUE, 2010),
-- (3, 'Carol', 'Williams', 3, 3, 1980, 'Female', 44, TRUE, 5500, TRUE, TRUE, 2012),
-- (4, 'David', 'Jones', 4, 4, 1975, 'Male', 49, TRUE, 6000, TRUE, TRUE, 2008),
-- (5, 'Eve', 'Brown', 5, 5, 1965, 'Female', 59, TRUE, 7500, TRUE, TRUE, 1995),
-- (6, 'Frank', 'Davis', 1, 6, 1988, 'Male', 36, FALSE, 4200, TRUE, FALSE, NULL),
-- (7, 'Grace', 'Miller', 2, 7, 1976, 'Female', 48, TRUE, 4800, TRUE, TRUE, 2007),
-- (8, 'Henry', 'Wilson', 3, 8, 1982, 'Male', 42, TRUE, 5200, TRUE, TRUE, 2013),
-- (9, 'Isabel', 'Moore', 4, 9, 1970, 'Female', 54, TRUE, 6400, TRUE, TRUE, 2000),
-- (10, 'Jack', 'Taylor', 5, 10, 1960, 'Male', 64, TRUE, 8000, TRUE, TRUE, 1988),
-- (11, 'Karen', 'Anderson', 1, 1, 1986, 'Female', 38, FALSE, 4100, TRUE, FALSE, NULL),
-- (12, 'Larry', 'Thomas', 2, 2, 1977, 'Male', 47, TRUE, 4900, TRUE, TRUE, 2009),
-- (13, 'Mona', 'Jackson', 3, 3, 1981, 'Female', 43, TRUE, 5300, TRUE, TRUE, 2011),
-- (14, 'Nate', 'White', 4, 4, 1972, 'Male', 52, TRUE, 6300, TRUE, TRUE, 1998),
-- (15, 'Olivia', 'Harris', 5, 5, 1963, 'Female', 61, TRUE, 7800, TRUE, TRUE, 1992),
-- (16, 'Paul', 'Martin', 1, 6, 1989, 'Male', 35, FALSE, 4150, TRUE, FALSE, NULL),
-- (17, 'Quinn', 'Lee', 2, 7, 1979, 'Female', 45, TRUE, 4950, TRUE, TRUE, 2014),
-- (18, 'Rachel', 'Perez', 3, 8, 1983, 'Female', 41, TRUE, 5350, TRUE, TRUE, 2012),
-- (19, 'Sam', 'Roberts', 4, 9, 1971, 'Male', 53, TRUE, 6450, TRUE, TRUE, 1997),
-- (20, 'Tina', 'Clark', 5, 10, 1961, 'Female', 63, TRUE, 7900, TRUE, TRUE, 1990);

-- -- SELECT * FROM teacher


-- INSERT INTO exam (
--   exam_id, 
--   discipline_id, 
--   student_id, 
--   teacher_id, 
--   exam_grade
-- ) VALUES
-- (1, 1, 1, 1, 'A'), -- Student 1 from Mechanics and Mathematics, Teacher 1 from Department 1
-- (2, 2, 2, 2, 'B'), -- Student 2 from Mechanics and Mathematics, Teacher 2 from Department 2
-- (3, 3, 5, 3, 'A'), -- Student 5 from Physics, Teacher 3 from Department 3
-- (4, 4, 6, 4, 'B'), -- Student 6 from Physics, Teacher 4 from Department 4
-- (5, 5, 9, 5, 'C'), -- Student 9 from Information Technologies, Teacher 5 from Department 5
-- (6, 6, 10, 6, 'A'), -- Student 10 from Information Technologies, Teacher 6 from Department 6
-- (7, 7, 13, 7, 'B'), -- Student 13 from Economics, Teacher 7 from Department 7
-- (8, 8, 14, 8, 'A'), -- Student 14 from Economics, Teacher 8 from Department 8
-- (9, 9, 17, 9, 'C'), -- Student 17 from Law, Teacher 9 from Department 9
-- (10, 10, 18, 10, 'B'); -- Student 18 from Law, Teacher 10 from Department 10

-- -- SELECT * FROM exam



-- INSERT INTO thesis (
--   thesis_id, 
--   title, 
--   student_id, 
--   teacher_id
-- ) VALUES
-- -- 3rd course students
-- (1, 'Modern Methods in Mathematical Analysis', 2, 2),  -- Student 2 from Mechanics and Mathematics, Teacher 2 from Department 2
-- (2, 'Experimental Techniques in Physics', 6, 4),  -- Student 6 from Physics, Teacher 4 from Department 4
-- (3, 'Data Structures and Algorithms', 10, 6),  -- Student 10 from Information Technologies, Teacher 6 from Department 6
-- (4, 'Macroeconomic Policy Analysis', 14, 8),  -- Student 14 from Economics, Teacher 8 from Department 8
-- (5, 'Comparative Criminal Law', 18, 10),  -- Student 18 from Law, Teacher 10 from Department 10

-- -- 4th course students
-- (6, 'Advanced Topics in Algebra', 4, 1),  -- Student 4 from Mechanics and Mathematics, Teacher 1 from Department 1
-- (7, 'Quantum Mechanics and Applications', 8, 3),  -- Student 8 from Physics, Teacher 3 from Department 3
-- (8, 'Artificial Intelligence and Machine Learning', 12, 5),  -- Student 12 from Information Technologies, Teacher 5 from Department 5
-- (9, 'Financial Analysis and Risk Management', 16, 7),  -- Student 16 from Economics, Teacher 7 from Department 7
-- (10, 'International Law and Human Rights', 20, 9);  -- Student 20 from Law, Teacher 9 from Department 9

-- -- SELECT * FROM thesis




-- INSERT INTO dissertation (
--   dissertation_id, 
--   dissertation_title, 
--   teacher_id
-- ) VALUES
-- (1, 'Algebraic Structures and Their Applications', 2),  -- Bob Smith, Teacher (defended thesis in 2010)
-- (2, 'Advanced Methods in Mathematical Analysis', 3),  -- Carol Williams, Senior Teacher (defended thesis in 2012)
-- (3, 'Quantum Field Theory in High Energy Physics', 4),  -- David Jones, Docent (defended thesis in 2008)
-- (4, 'Experimental Techniques in Solid State Physics', 5),  -- Eve Brown, Professor (defended thesis in 1995)
-- (5, 'Information Systems and Data Management', 7),  -- Grace Miller, Teacher (defended thesis in 2007)
-- (6, 'Artificial Intelligence and Machine Learning', 8),  -- Henry Wilson, Senior Teacher (defended thesis in 2013)
-- (7, 'Comparative Study of Civil Law Systems', 9),  -- Isabel Moore, Docent (defended thesis in 2000)
-- (8, 'Criminal Law and Justice', 10),  -- Jack Taylor, Professor (defended thesis in 1988)
-- (9, 'Geometric Structures and Their Applications', 12),  -- Larry Thomas, Teacher (defended thesis in 2009)
-- (10, 'Mathematical Modeling in Physics', 13),  -- Mona Jackson, Senior Teacher (defended thesis in 2011)
-- (11, 'Machine Learning Algorithms', 14),  -- Nate White, Docent (defended thesis in 1998)
-- (12, 'Economic Theory and Practice', 15),  -- Olivia Harris, Professor (defended thesis in 1992)
-- (13, 'International Law and Policy', 17),  -- Quinn Lee, Teacher (defended thesis in 2014)
-- (14, 'Artificial Neural Networks and Deep Learning', 18),  -- Rachel Perez, Senior Teacher (defended thesis in 2012)
-- (15, 'Advanced Economic Analysis', 19),  -- Sam Roberts, Docent (defended thesis in 1997)
-- (16, 'Human Rights Law in the Modern World', 20),  -- Tina Clark, Professor (defended thesis in 1990)

-- -- Assume the following have defended theses (if necessary for consistency):
-- (17, 'Innovations in Algebraic Theory', 1),  -- Alice Johnson, Assistant (assuming defended thesis)
-- (18, 'Current Trends in Mathematical Analysis', 6),  -- Frank Davis, Assistant (assuming defended thesis)
-- (19, 'Applications of Quantum Mechanics', 11),  -- Karen Anderson, Assistant (assuming defended thesis)
-- (20, 'Database Management Systems', 16);  -- Paul Martin, Assistant (assuming defended thesis)

-- -- SELECT * FROM dissertation



-- INSERT INTO discipline_and_event_type (id, discipline, event_type) VALUES
-- (1, 1, 1), -- Calculus I, Lecture
-- (2, 1, 2), -- Calculus I, Seminar
-- (4, 2, 1), -- Linear Algebra, Lecture
-- (5, 2, 2), -- Linear Algebra, Seminar
-- (7, 3, 1), -- Classical Mechanics, Lecture
-- (8, 3, 2), -- Classical Mechanics, Seminar
-- (9, 3, 3), -- Classical Mechanics, Lab Work
-- (10, 4, 1), -- Quantum Physics, Lecture
-- (11, 4, 2), -- Quantum Physics, Seminar
-- (12, 4, 3), -- Quantum Physics, Lab Work
-- (13, 5, 1), -- Introduction to Programming, Lecture
-- (14, 5, 2), -- Introduction to Programming, Seminar
-- (16, 6, 1), -- Data Structures, Lecture
-- (17, 6, 2), -- Data Structures, Seminar
-- (19, 7, 1), -- Microeconomics, Lecture
-- (20, 7, 2), -- Microeconomics, Seminar
-- (22, 8, 1), -- Macroeconomics, Lecture
-- (23, 8, 2), -- Macroeconomics, Seminar
-- (25, 9, 1), -- Constitutional Law, Lecture
-- (26, 9, 2), -- Constitutional Law, Seminar
-- (28, 10, 1), -- International Law, Lecture
-- (29, 10, 2); -- International Law, Seminar

-- -- SELECT * FROM discipline_and_event_type





-- INSERT INTO discipline_and_control_form (id, discipline, control_form) VALUES
-- (2, 1, 2), -- Calculus I, Exam
-- (4, 2, 2), -- Linear Algebra, Exam
-- (6, 3, 2), -- Classical Mechanics, Exam
-- (8, 4, 2), -- Quantum Physics, Exam
-- (10, 5, 2), -- Introduction to Programming, Exam
-- (12, 6, 2), -- Data Structures, Exam
-- (13, 7, 1), -- Microeconomics, Setoff
-- (15, 8, 1), -- Macroeconomics, Setoff
-- (18, 9, 2), -- Constitutional Law, Exam
-- (20, 10, 2); -- International Law, Exam


-- SELECT * FROM discipline_and_control_form
