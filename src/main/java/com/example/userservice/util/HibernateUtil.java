package com.example.userservice.util;

import io.github.cdimascio.dotenv.Dotenv;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class HibernateUtil {
    private static final Logger logger = LogManager.getLogger(HibernateUtil.class);
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            Dotenv dotenv = Dotenv.load();

            String dbUrl = dotenv.get("DB_URL", "jdbc:postgresql://localhost:5432/user_db");
            String dbUser = dotenv.get("DB_USER", "postgres");
            String dbPassword = dotenv.get("DB_PASSWORD", "1111");

            StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                    .applySetting("hibernate.connection.url", dbUrl)
                    .applySetting("hibernate.connection.username", dbUser)
                    .applySetting("hibernate.connection.password", dbPassword)
                    .configure()
                    .build();

            return new MetadataSources(registry)
                    .getMetadataBuilder()
                    .build()
                    .getSessionFactoryBuilder()
                    .build();
        } catch (Exception ex) {
            logger.error("Ошибка инициализации SessionFactory", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        getSessionFactory().close();
    }
}
