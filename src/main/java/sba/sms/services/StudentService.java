package sba.sms.services;

import lombok.extern.java.Log;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import sba.sms.dao.StudentI;
import sba.sms.models.Course;
import sba.sms.models.Student;
import sba.sms.utils.HibernateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * StudentService is a concrete class. This class implements the
 * StudentI interface, overrides all abstract service methods and
 * provides implementation for each method. Lombok @Log used to
 * generate a logger file.
 */

public class StudentService implements StudentI {
    SessionFactory sessionFactory = HibernateUtil.getSessionFactory();


   @Override
    public void createStudent(Student student) {
       Transaction transaction = null;
        try(Session session = sessionFactory.openSession()) { //sessionFactory.openSession() Open a session, creates a new session to interact with the database.
            transaction = session.beginTransaction(); //begins a database transaction to ensure atomicity.
            session.persist(student); //saves the student object in the database.
            transaction.commit(); //finalizes and saves the changes to the database.
        }catch(Exception e){
            if(transaction != null){
                transaction.rollback();
            }
        }
    }
   @Override
    public List<Student> getAllStudents() {
        try(Session session = sessionFactory.openSession()) {
            return session.createQuery("from Student", Student.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public Student getStudentByEmail(String email) {
        try(Session session = sessionFactory.openSession()) {
            return session.get(Student.class, email);
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public boolean validateStudent(String email, String password) {
        try(Session session = sessionFactory.openSession()) {
            Student student = session.get(Student.class, email);
            //BCrypt.checkpw(password, student.getPassword()) → Compares the hashed password with the provided input. This method prevents password leaks if the database is compromised.
            if(student != null && student.getPassword().equals(password)) {
                return true;
            }
        }

        return false;
    }

    public void registerStudentToCourse(String email, int courseId) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            Student student = session.get(Student.class, email);
            Course course = session.get(Course.class, courseId);

            //Checking Conditions Before Registering. Ensures that: 1.The student exists. 2.The course exists. 3.The student is not already registered for the course (prevents duplicates).
            if (student != null && course != null && !student.getCourses().contains(course)) {

                course.getStudents().add(student);  //Adds the student to the course's list of students
                student.getCourses().add(course); //Adds the course to the student's list of courses
                session.merge(course); //merge the updated course object into the database, This ensures that Hibernate recognizes changes and updates the database accordingly.
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null){
                transaction.rollback(); // rollback on error
            }
            e.printStackTrace();
        }

    }

    public List<Course> getStudentCourses(String email) {
        try(Session session = sessionFactory.openSession()) {
            Student student = session.get(Student.class, email);

            if(student == null){
                return new ArrayList<>(); // Return an empty list if no student found
            }
            return new ArrayList<>(student.getCourses());
        }catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

    }
}
