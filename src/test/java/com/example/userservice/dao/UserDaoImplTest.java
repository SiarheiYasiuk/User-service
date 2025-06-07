package com.example.userservice.dao;

import com.example.userservice.entity.User;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserDaoImplTest {

    private static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    private static SessionFactory sessionFactory;
    private static UserDao userDao;

    @BeforeAll
    static void startContainerAndBuildSessionFactory() {
        postgres.start();

        Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
        configuration.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
        configuration.setProperty("hibernate.connection.username", postgres.getUsername());
        configuration.setProperty("hibernate.connection.password", postgres.getPassword());
        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        configuration.setProperty("hibernate.hbm2ddl.auto", "update");
        configuration.setProperty("hibernate.show_sql", "true");
        configuration.addAnnotatedClass(User.class);

        sessionFactory = configuration.buildSessionFactory();
        userDao = new UserDaoImpl(() -> sessionFactory);
    }

    @AfterAll
    static void shutdown() {
        sessionFactory.close();
        postgres.stop();
    }

    @BeforeEach
    void cleanUp() {
        try (var session = sessionFactory.openSession()) {
            var tx = session.beginTransaction();
            session.createQuery("delete from User").executeUpdate();
            tx.commit();
        }
    }

    @Test
    void saveAndFindById() {
        User user = new User("Maria", "maria@test.com", 25);
        userDao.save(user);

        assertNotNull(user.getId());

        User found = userDao.findById(user.getId());
        assertEquals("Maria", found.getName());
    }

    @Test
    void findAllShouldReturnMultipleUsers() {
        userDao.save(new User("User1", "u1@test.com", 30));
        userDao.save(new User("User2", "u2@test.com", 35));

        List<User> users = userDao.findAll();
        assertEquals(2, users.size());
    }

    @Test
    void updateShouldModifyUser() {
        User user = new User("Yan", "yan@test.com", 28);
        userDao.save(user);

        user.setName("Yanio");
        userDao.update(user);

        User updated = userDao.findById(user.getId());
        assertEquals("Yanio", updated.getName());
    }

    @Test
    void deleteShouldRemoveUser() {
        User user = new User("DeleteMe", "delete@test.com", 20);
        userDao.save(user);

        userDao.delete(user);
        assertNull(userDao.findById(user.getId()));
    }
}
