package sba.sms.services;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import sba.sms.models.Course;
import sba.sms.models.Student;
import sba.sms.utils.CommandLine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StudentServiceTest {
    private static StudentService studentService;
    private static CourseService courseService;
    private static SessionFactory sessionFactory;
    private static Session session;
    private static Transaction transaction;
    private static Student student;

    @BeforeAll
    static void setUp() {
        sessionFactory = new Configuration().configure().buildSessionFactory();
        session = sessionFactory.openSession();
        transaction = session.beginTransaction();
        studentService = new StudentService();
        student = new Student("reema@gmail.com","reema","password");
        studentService.createStudent(student);
        courseService = new CourseService();
    }

    @AfterAll
    static void tearDown() {
        if (transaction != null) {
            transaction.rollback();
        }
        if (session != null) {
            session.close();
        }
        if (sessionFactory != null) {
            sessionFactory.close();
        }
        if (student != null) {
            student = null;
        }
    }

    @Test
    @Order(1)
    void testCreateStudent() {
        Student registeredStudent = studentService.getAllStudents().get(0);
        assertEquals(student.getName(), registeredStudent.getName());
    }

    @Test
    @Order(2)
    void testGetAllStudentsCount() {
        assertEquals(1, studentService.getAllStudents().size());
    }

    @Test
    @Order(3)
    void testGetStudentByEmail() {
        Student fetchedStudent = studentService.getStudentByEmail("reema@gmail.com");
        assertNotNull(fetchedStudent, "Student should exist in the database");
        assertEquals(student.getName(), fetchedStudent.getName());
    }

    @Test
    @Order(4)
    void testValidateStudent() {
        assertTrue(studentService.validateStudent(student.getEmail(), student.getPassword()),
                "Student is valid");

        assertFalse(studentService.validateStudent("random guy", "12345"),
                "Student is not valid");
    }

    @Test
    @Order(5)
    void testRegisterStudentToCourse() {
        //CourseService courseService = new CourseService();
        courseService.createCourse(new Course("Coding 101", "Mr. Dobek"));
        Course course = courseService.getAllCourses().get(0);

        studentService.registerStudentToCourse(student.getEmail(), course.getId());
        List<Course> studentCourses = studentService.getStudentCourses(student.getEmail());
        assertEquals(course.getId(), studentCourses.get(0).getId());
        assertEquals(course.getName(), studentCourses.get(0).getName());
    }

    @Test
    @Order(6)
    void testGetStudentCourses() {
        Student newStudent = new Student("sarah@gmail.com","sarah","password");
        studentService.createStudent(newStudent);
        List<Course> studentCourses = studentService.getStudentCourses(newStudent.getEmail());
        assertEquals(0, studentCourses.size());


        courseService.createCourse(new Course("Coding 101", "Mr. Dobek"));
        courseService.createCourse(new Course("Chinese 101", "Mr. Anthony"));
        List<Course> courses = courseService.getAllCourses();
        studentService.registerStudentToCourse(newStudent.getEmail(), courses.get(0).getId());
        studentService.registerStudentToCourse(newStudent.getEmail(), courses.get(1).getId());

        studentCourses = studentService.getStudentCourses(newStudent.getEmail());
        assertEquals(2, studentCourses.size());
    }
}