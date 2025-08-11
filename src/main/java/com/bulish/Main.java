package com.bulish;

import com.bulish.dao.HibernateUtil;
import com.bulish.dao.UserDao;
import com.bulish.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {

    private static final UserDao userDao = new UserDao();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            boolean running = true;
            while (running) {
                printOperation();
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1 -> findUserById();
                    case 2 -> findAllUsers();
                    case 3 -> createUser();
                    case 4 -> updateUser();
                    case 5 -> deleteUser();
                    case 6 -> running = false;
                    default -> System.out.println("Invalid operation number");
                }
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            HibernateUtil.shutdown();
            scanner.close();
        }
    }

    private static void deleteUser() {
        System.out.print("Enter user ID to delete: ");

        try {
            Long id = Long.parseLong(scanner.nextLine());

            if (checkIfUserExistsById(id).isEmpty()) return;

            userDao.deleteById(id);
            System.out.println("User deleted successfully");
        } catch (Exception e) {
            System.err.println("Error while deleteUser: " + e.getMessage());
        }
    }
    private static void updateUser() {
        System.out.print("Enter user ID to update: ");

        try {
            Long id = Long.parseLong(scanner.nextLine());
            Optional<User> optionalUser = checkIfUserExistsById(id);
            if (optionalUser.isEmpty()) return;

            User user = optionalUser.get();
            System.out.println("Current user: " + user);

            System.out.print("Enter new name or leave blank to keep the same: ");
            String name = scanner.nextLine();
            if (!name.isBlank()) user.setName(name);

            System.out.print("Enter new email or leave blank to keep the same: ");
            String email = scanner.nextLine();
            if (!email.isBlank()) user.setEmail(email);

            System.out.print("Enter new age or leave blank to keep the same: ");
            String age = scanner.nextLine();
            if (!age.isBlank()) user.setAge(Integer.parseInt(age));

            userDao.update(user);
            System.out.println("User updated successfully");
        } catch (Exception e) {
            System.err.println("Error while updateUser: " + e.getMessage());
        }
    }

    private static void findAllUsers() {
        try {
            List<User> users = userDao.findAll();
            if (users.isEmpty()) {
                System.out.println("No users found");
            } else {
                users.forEach(System.out::println);
            }
        } catch (Exception e) {
            System.err.println("Error while findAllUsers : " + e.getMessage());
        }
    }

    private static void findUserById() {
        System.out.print("Enter id: ");

        try {
          Long id = Long.parseLong(scanner.nextLine());
          Optional<User> user = userDao.findById(id);
          user.ifPresentOrElse(
                  System.out::println,
                  () -> System.out.println("User not found with id: " + id));
        } catch (Exception e) {
            System.err.println("Error while findUserById : " + e.getMessage());
        }
    }

    private static void createUser() {
        try {
        System.out.print("Enter name: ");
        String name = scanner.nextLine();

        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        System.out.print("Enter age: ");
        int age = Integer.parseInt(scanner.nextLine());

        Long id = userDao.save(new User(name, email, age));
        System.out.println("User created with ID: " + id);
        } catch (Exception e) {
            System.err.println("Error creating user: " + e.getMessage());
        }
    }

    private static void printOperation() {
        System.out.println("1. Find user by id");
        System.out.println("2. Find all users");
        System.out.println("3. Save new user");
        System.out.println("4. Update existing user");
        System.out.println("5. Delete user by id");
        System.out.println("6. Exit");
        System.out.println("Choose operation number");
    }

    private static Optional<User> checkIfUserExistsById(Long id) {
        Optional<User> optionalUser = userDao.findById(id);
        if (optionalUser.isEmpty()) {
            System.out.println("User not found!");
        }
       return optionalUser;
    }
}