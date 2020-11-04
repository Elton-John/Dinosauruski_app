INSERT INTO `DayName` (name, displayOrder, isDayOff) VALUES ('poniedziałek', 1, true),( 'wtorek', 2, true),('środa', 3, true),('czwartek', 4, true),('piątek', 5, true),( 'sobota', 6, true),( 'niedziela', 7, true);
INSERT INTO Teacher (name, surname, nickname, email, login, password, repeatPassword ) VALUES ('Tomas','Mann','TheBestTeacher', '222@222', 'tomas', '222','222');
INSERT INTO dinosauruski.Student (active, email, login, name, nickname, password, priceForOneLesson, surname) VALUES (true, 'elton@john', 'aaa', 'Elton', 'Rocketman', '111', 90, 'John');
INSERT INTO dinosauruski.Student (active, email, login, name, nickname, password, priceForOneLesson, surname) VALUES (true, 'john@john', 'bbb', 'John', 'Dirty', '111', 100, 'Johnovich');
INSERT INTO AvailableSlot (isBooked, isOnceFree, date, time, dayName_id, regularStudent_id, teacher_id) VALUES (true, true, '2020-11-19', '20:10:00', 1, 1, 1);
INSERT INTO AvailableSlot (isBooked, isOnceFree, date, time, dayName_id, regularStudent_id, teacher_id) VALUES (true, false, null, '19:10:00', 2, 1, 1);
INSERT INTO AvailableSlot (isBooked, isOnceFree, date, time, dayName_id, regularStudent_id, teacher_id) VALUES (false, false, null, '15:10:00', 4, 2, 1);
