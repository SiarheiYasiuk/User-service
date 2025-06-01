package com.example.userservice;

import com.example.userservice.dao.UserDao;
import com.example.userservice.dao.UserDaoImpl;
import com.example.userservice.entity.User;
import com.example.userservice.util.HibernateUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    private static final UserDao userDao = new UserDaoImpl();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            boolean running = true;
            while (running) {
                System.out.println("\nМеню управления пользователями:");
                System.out.println("1. Создать пользователя");
                System.out.println("2. Просмотреть всех пользователей");
                System.out.println("3. Найти пользователя по ID");
                System.out.println("4. Обновить пользователя");
                System.out.println("5. Удалить пользователя");
                System.out.println("0. Выход");
                int choice = readInt("Выберите действие: ");

                switch (choice) {
                    case 1 -> createUser();
                    case 2 -> listAllUsers();
                    case 3 -> findUserById();
                    case 4 -> updateUser();
                    case 5 -> deleteUser();
                    case 0 -> running = false;
                    default -> System.out.println("Неверный выбор. Попробуйте снова.");
                }
            }
        } catch (Exception e) {
            logger.error("Ошибка в приложении: ", e);
            System.err.println("Произошла ошибка: " + e.getMessage());
        } finally {
            HibernateUtil.shutdown();
            scanner.close();
        }
    }

    private static void createUser() {
        System.out.print("Введите имя: ");
        String name = scanner.nextLine();

        System.out.print("Введите email: ");
        String email = scanner.nextLine();

        int age = readInt("Введите возраст: ");

        User user = new User(name, email, age);
        userDao.save(user);
        System.out.println("Пользователь успешно создан: " + user);
        logger.info("Создан новый пользователь: {}", user);
    }

    private static void listAllUsers() {
        List<User> users = userDao.findAll();
        if (users.isEmpty()) {
            System.out.println("Список пользователей пуст.");
        } else {
            System.out.println("Список всех пользователей:");
            users.forEach(System.out::println);
        }
        logger.info("Запрошен список всех пользователей. Найдено {} записей.", users.size());
    }

    private static void findUserById() {
        Long id = readLong("Введите ID пользователя: ");

        User user = userDao.findById(id);
        if (user != null) {
            System.out.println("Найден пользователь: " + user);
            logger.info("Найден пользователь по ID {}: {}", id, user);
        } else {
            System.out.println("Пользователь с ID " + id + " не найден.");
            logger.warn("Пользователь с ID {} не найден", id);
        }
    }

    private static void updateUser() {
        Long id = readLong("Введите ID пользователя для обновления: ");

        User user = userDao.findById(id);
        if (user == null) {
            System.out.println("Пользователь с ID " + id + " не найден.");
            return;
        }

        System.out.print("Введите новое имя (текущее: " + user.getName() + "): ");
        String name = scanner.nextLine();

        System.out.print("Введите новый email (текущий: " + user.getEmail() + "): ");
        String email = scanner.nextLine();

        int age = readInt("Введите новый возраст (текущий: " + user.getAge() + "): ");

        user.setName(name);
        user.setEmail(email);
        user.setAge(age);

        userDao.update(user);
        System.out.println("Пользователь успешно обновлён: " + user);
        logger.info("Обновлён пользователь: {}", user);
    }

    private static void deleteUser() {
        Long id = readLong("Введите ID пользователя для удаления: ");

        User user = userDao.findById(id);
        if (user == null) {
            System.out.println("Пользователь с ID " + id + " не найден.");
            return;
        }

        userDao.delete(user);
        System.out.println("Пользователь с ID " + id + " успешно удалён.");
        logger.info("Удалён пользователь: {}", user);
    }

    private static int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите корректное целое число.");
            }
        }
    }

    private static long readLong(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine();
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите корректное число (long).");
            }
        }
    }
}