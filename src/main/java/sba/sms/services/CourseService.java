package sba.sms.services;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import sba.sms.dao.CourseI;
import sba.sms.models.Course;
import sba.sms.utils.HibernateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * CourseService is a concrete class. This class implements the
 * CourseI interface, overrides all abstract service methods and
 * provides implementation for each method.
 */
public class CourseService implements CourseI {
    SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    @Override
    public void createCourse(Course course) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {

            transaction = session.beginTransaction();
            session.persist(course);
            transaction.commit();
        }catch(Exception e){
            if(transaction != null) {
                transaction.rollback();
            }
        }



    }

    @Override
    public List<Course> getAllCourses() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Course", Course.class).list();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }


    }

    @Override
    public Course getCourseById(int courseId) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Course.class, courseId);
        }catch (Exception e) {
            e.printStackTrace(); // Log properly
            return null; // Handle gracefully
        }


    }
}
