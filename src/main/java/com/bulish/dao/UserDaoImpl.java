package com.bulish.dao;

import com.bulish.exception.DaoException;
import com.bulish.exception.UserNotFoundException;
import com.bulish.model.User;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class UserDaoImpl implements UserDao {

    private final SessionFactory sessionFactory;

    public Long save(User user) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.persist(user);
                transaction.commit();
                return user.getId();
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                throw new DaoException("Error while saving user", e);
            }
        }
    }

    public Optional<User> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.get(User.class, id));
        } catch (Exception e) {
            throw new DaoException("Error while finding user by id: " + id, e);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from User where email = :email", User.class)
                    .setParameter("email", email)
                    .uniqueResultOptional();
        } catch (Exception e) {
            throw new DaoException("Error while finding user by email: " + email, e);
        }
    }

    public List<User> findAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User", User.class);
            return query.list();
        } catch (Exception e) {
            throw new DaoException("Error while find all user : " + e.getMessage(), e);
        }
    }

    public void update(User user) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                if (session.get(User.class, user.getId()) == null) {
                    throw new UserNotFoundException("User not found with id " + user.getId());
                }

                session.merge(user);
                transaction.commit();
            } catch (UserNotFoundException e) {
                if (transaction != null) transaction.rollback();
                throw e;
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                throw new DaoException("Error while user update", e);
            }
        }
    }

    public void deleteById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                User user = session.get(User.class, id);
                if (user == null) {
                    throw new UserNotFoundException("User not found with id " + id);
                }

                session.remove(user);
                transaction.commit();
            } catch (UserNotFoundException e) {
                if (transaction != null) transaction.rollback();
                throw e;
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                throw new DaoException("Error while delete user by id: " + e.getMessage(), e);
            }
        }
    }
}