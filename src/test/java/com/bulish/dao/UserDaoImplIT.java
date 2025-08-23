package com.bulish.dao;

import com.bulish.HibernateTestUtil;
import com.bulish.TestUserFactory;
import com.bulish.exception.DaoException;
import com.bulish.exception.UserNotFoundException;
import com.bulish.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserDaoImplIT {

    private SessionFactory sessionFactory;
    private UserDaoImpl userDaoImpl;

    private static final String USER_NOT_FOUND = "User not found with id";

    @BeforeAll
    void setup() {
        sessionFactory = HibernateTestUtil.getSessionFactory();
        userDaoImpl = new UserDaoImpl(sessionFactory);
    }

    @BeforeEach
    void clearDatabase() {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createQuery("DELETE FROM User").executeUpdate();
            transaction.commit();
        }
    }

    @AfterAll
    void cleanup() {
        sessionFactory.close();
    }

    @Test
    @DisplayName("save user - OK")
    void saveOk() {
        User user = TestUserFactory.createUserWithEmail("emailForSave@gmail.com");

        userDaoImpl.save(user);
        Optional<User> optionalUser = userDaoImpl.findById(user.getId());

        assertThat(optionalUser.isPresent());

        User createdUser = optionalUser.get();

        assertAll("created user fields",
                () -> assertEquals(createdUser.getCreatedAt(), user.getCreatedAt()),
                () -> assertEquals(createdUser.getAge(), user.getAge()),
                () -> assertEquals(createdUser.getId(), user.getId()),
                () -> assertEquals(createdUser.getName(), user.getName()),
                () -> assertEquals(createdUser.getEmail(), user.getEmail())
        );
    }

    @Test
    @DisplayName("save - duplicate email")
    void saveDuplicateEmail() {
        String duplicateEmail = "duplicate@email.com";
        User firstUser = TestUserFactory.createUserWithEmail(duplicateEmail);
        userDaoImpl.save(firstUser);

        User secondUser = TestUserFactory.createUserWithEmail(duplicateEmail);

        DaoException exception = assertThrows(DaoException.class,
                () -> userDaoImpl.save(secondUser));

        assertThat(exception.getMessage())
                .contains("Error while saving user");

        List<User> users = userDaoImpl.findAll();

        assertThat(users)
                .hasSize(1)
                .extracting(User::getEmail)
                .containsExactly(duplicateEmail);
    }

    @Test
    @DisplayName("findById - OK")
    void findByIdOk() {
        User user = TestUserFactory.createUserWithEmail("emailForFind@gmail.com");
        userDaoImpl.save(user);

        Optional<User> optionalUser = userDaoImpl.findById(user.getId());
        assertThat(optionalUser.isPresent());

        User foundUser = optionalUser.get();

        assertAll("found user fields",
                () -> assertEquals(foundUser.getCreatedAt(), user.getCreatedAt()),
                () -> assertEquals(foundUser.getAge(), user.getAge()),
                () -> assertEquals(foundUser.getId(), user.getId()),
                () -> assertEquals(foundUser.getName(), user.getName()),
                () -> assertEquals(foundUser.getEmail(), user.getEmail())
        );
    }

    @Test
    @DisplayName("findById - user not found")
    void findByIdUserNotFound() {
        Optional<User> user = userDaoImpl.findById(999L);
        assertThat(user).isEmpty();
    }

    @Test
    @DisplayName("findByEmail - OK")
    void findByEmailOk() {
        User user = TestUserFactory.createUserWithEmail("emailForFind@gmail.com");
        userDaoImpl.save(user);

        Optional<User> optionalUser = userDaoImpl.findByEmail(user.getEmail());
        assertThat(optionalUser.isPresent());

        User foundUser = optionalUser.get();

        assertAll("found user fields",
                () -> assertEquals(foundUser.getCreatedAt(), user.getCreatedAt()),
                () -> assertEquals(foundUser.getAge(), user.getAge()),
                () -> assertEquals(foundUser.getId(), user.getId()),
                () -> assertEquals(foundUser.getName(), user.getName()),
                () -> assertEquals(foundUser.getEmail(), user.getEmail())
        );
    }

    @Test
    @DisplayName("findByEmail - user not found")
    void findByEmailUserNotFound() {
        Optional<User> user = userDaoImpl.findByEmail("notFound@email.com");
        assertThat(user).isEmpty();
    }

    @Test
    @DisplayName("findAll - OK")
    void findAllOk() {
        User user1 = TestUserFactory.createUserWithEmail("newUser1@yandex.ru");
        User user2 = TestUserFactory.createUserWithEmail("newUser2@yandex.ru");
        userDaoImpl.save(user1);
        userDaoImpl.save(user2);

        List<User> users = userDaoImpl.findAll();

        assertTrue(users.size() >= 2);

        assertThat(users)
                .extracting(User::getEmail)
                .containsExactlyInAnyOrder(user1.getEmail(), user2.getEmail());

    }

    @Test
    @DisplayName("findAll - empty list")
    void findAllEmptyList() {
        List<User> users = userDaoImpl.findAll();
        assertThat(users).isEmpty();
    }

    @Test
    @DisplayName("update - OK")
    void updateOk() {
        User user = TestUserFactory.createUserWithEmail("emailForUpdate@gmail.com");
        Long id = userDaoImpl.save(user);

        user.setName("NewName");
        userDaoImpl.update(user);

        User updatedUser = userDaoImpl.findById(id).orElseThrow();
        assertThat(updatedUser.getName()).isEqualTo(user.getName());
    }

    @Test
    @DisplayName("update - user not found")
    void updateUserNotFound() {
        User user = TestUserFactory.createUser(999L);

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userDaoImpl.update(user));

        assertThat(exception.getMessage())
                .containsAnyOf(USER_NOT_FOUND);
    }

    @Test
    @DisplayName("deleteById - OK")
    void deleteByIdOk() {
        User user = TestUserFactory.createUserWithEmail("emailForDelete@gmail.com");

        Long userId = userDaoImpl.save(user);

        userDaoImpl.deleteById(userId);

        Optional<User> deletedUser = userDaoImpl.findById(userId);
        assertTrue(deletedUser.isEmpty());
    }

    @Test
    @DisplayName("deleteById - user not found")
    void deleteByIdUserNotFound() {
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userDaoImpl.deleteById(999L));

        assertThat(exception.getMessage())
                .containsAnyOf(USER_NOT_FOUND);
    }
}