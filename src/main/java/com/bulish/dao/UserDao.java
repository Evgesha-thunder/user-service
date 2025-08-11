package com.bulish.dao;

import com.bulish.exception.DaoException;
import com.bulish.model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class UserDao {

    public Long save(User user) throws DaoException {
        Transaction transaction = null;
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            return user.getId();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new DaoException("Error while saving user", e);
        }
    }

    public Optional<User> findById(Long id) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
           return Optional.ofNullable(session.get(User.class, id));
        } catch (Exception e) {
            throw new DaoException("Error while finding user by id: " + id, e);
        }
    }

    public List<User> findAll() {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("from User", User.class);
            return query.list();
        } catch (Exception e) {
            throw new DaoException("Error while find all user", e);
        }

    }
    public void update(User user) {
        Transaction transaction = null;
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new DaoException("Error while user update", e);
        }
    }

    public void deleteById(Long id) {
        Transaction transaction = null;
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User user = session.get(User.class, id);
            session.remove(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new DaoException("Error while delete user by id: " + id, e);
        }
    }
}
