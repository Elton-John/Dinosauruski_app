# Dinosauruski_app

Imagine you have 25 individual students and you charge them monthly in advance. 

Some have lessons once a week and some have more. Some have a fixed slots and some have flexible.

Sometimes some people cancel classes, some want to move exceptionally this week to another day, some cancel them at the last minute, and you have a policy that late cancellations should be paid.

At the end of the month, 15 of the 25 students ask you: How much do I have to pay because I think I canceled this month, but I don't remember how much and when? 

This can be a hell.


**This web application helps to manage all processes for individual classes for teachers and their students.**

![alt text][main]

**As a teacher:**
* :star2: you can generate your schedule of classes by adding slots (ex. Mondays, 3 p.m), students and relations between them. 
* :star2: you can manage generated lessons e.g cancel or rebook for other students.
* :star2: you can add payment received from your students and follow lessons payment status.

# Author and License
**OLGA KRYUKOVA**   
MIT license (It lets people do almost anything they want with the project, like making and distributing closed source versions)
# This is how it works
 

# PAGES:

:point_right:  **LOGIN** 

:point_right: **REGISTRATION**

:point_down: **COCKPIT:**

![alt text][cockpit]

:heavy_check_mark: Display current week with lessons plan

:heavy_check_mark: Access to current week lesson management

:heavy_check_mark: Display available slots

:heavy_check_mark: Access to slots management

:point_right: **DROPDOWN TEACHER PANEL:**

:heavy_check_mark: Editing  teacher profile

:heavy_check_mark: Logout

:heavy_check_mark: Deleting  teacher profile

:point_down: **MANAGEMENT SLOTS:**

![alt text][slots]

:heavy_check_mark: Display all booked and free slots

:heavy_check_mark: Editing time and day of week of slots

:heavy_check_mark: Deleting slots after chosen date

:heavy_check_mark: Adding new slots
 

 
:point_down: **CALENDAR:**
:point_down: Before generation
![alt text][cal-before]
:point_down: After generation
![alt text][cal-after]

:heavy_check_mark: Generating current month lessons

:heavy_check_mark: Display current month lessons

:heavy_check_mark: Generating and displaying any month and year lessons

:heavy_check_mark: Access to lesson management
 
:point_down: **LESSON MANAGEMENT:**
![alt text][rebooking]

:heavy_check_mark: Cancelling lesson by teacher (payment will be saved for student)

:heavy_check_mark: Cancelling lesson by student (payment will be saved for student)

:heavy_check_mark: Adding other student to vacated lesson 

:heavy_check_mark: Late cancelling a lesson (payment will be charged)

:heavy_check_mark: Deleting lesson from plan  (payment will be saved for student)
 
:point_down: **PAYMENT:**
![alt text][payments]

:heavy_check_mark: Display last 10 added payments

:heavy_check_mark: Adding new payment

:heavy_check_mark: Deleting payment

 
:point_down: **STUDENTS:**
![alt text][students]

:heavy_check_mark: Display all active and suspended students

:heavy_check_mark: Adding new student

:heavy_check_mark: Access to student’s profile
 
:point_down: **STUDENT PROFILE:**
![alt text][student-profile]

:heavy_check_mark: Display information about student:

 name, 
surname, 
price for one lesson
booked slot
planned lessons for current month (green if paid)
all planned lessons until last day of next month
overpayment
required payment for lessons until last day of next month

:heavy_check_mark: Editing personal data

:heavy_check_mark: Access to lesson management

:heavy_check_mark: Access to student’s slots  management:

:heavy_check_mark: Suspending student after chosen date

:heavy_check_mark: Activating student
 
:point_down: **STUDENT'S SLOTS MANAGEMENT:**
![alt text][student-slots]

:heavy_check_mark: deleting booked slots after chosen date

:heavy_check_mark:  adding free slot to student after chosen date
 
# Technologies
* Java
* Spring
* Hibernate
* Thymeleaf
* MySQL
* Maven
* Bootstrap

[cockpit]: https://github.com/Elton-John/Dinosauruski_app/blob/main/demoViews/main.png "cockpit"
[edit]: https://github.com/Elton-John/Dinosauruski_app/blob/main/demoViews/edit_teacher.png "editing teacher profile"
[slots]: https://github.com/Elton-John/Dinosauruski_app/blob/main/demoViews/slots-manage.png "slots"
[cal-before]: https://github.com/Elton-John/Dinosauruski_app/blob/main/demoViews/calendar_before_gen.png "calendar before generation"
[cal-after]: https://github.com/Elton-John/Dinosauruski_app/blob/main/demoViews/cal-after-gen.png "calendar after generation"
[rebooking]: https://github.com/Elton-John/Dinosauruski_app/blob/main/demoViews/rebooking.png "lesson changes"
[payments]: https://github.com/Elton-John/Dinosauruski_app/blob/main/demoViews/payments.png "payments"
[students]: https://github.com/Elton-John/Dinosauruski_app/blob/main/demoViews/students.png "students"
[student-profile]: https://github.com/Elton-John/Dinosauruski_app/blob/main/demoViews/student_profile.png "student profile"
[main]: https://github.com/Elton-John/Dinosauruski_app/blob/main/demoViews/cal-after-change.png "demo view"
[student-slots]: https://github.com/Elton-John/Dinosauruski_app/blob/main/demoViews/student_slots.png "changing studet's slots"
