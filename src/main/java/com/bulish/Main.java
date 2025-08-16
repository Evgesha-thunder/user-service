package com.bulish;

import com.bulish.config.HibernateUtil;
import com.bulish.dao.UserDao;
import com.bulish.dao.UserDaoImpl;
import com.bulish.dto.UserDto;
import com.bulish.exception.UserNotFoundException;
import com.bulish.mapper.UserMapper;
import com.bulish.service.UserService;
import com.bulish.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final UserDao userDao = new UserDaoImpl(HibernateUtil.getSessionFactory());
    private static final UserMapper userMapper = new UserMapper();
    private static final UserService userService = new UserServiceImpl(userDao, userMapper);
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

            if (checkIfUserExistsById(id) == null) return;

            userService.deleteById(id);
            System.out.println("User deleted successfully");
        } catch (Exception e) {
            System.err.println("Error while deleteUser: " + e.getMessage());
        }
    }

    private static void updateUser() {
        System.out.print("Enter user ID to update: ");

        try {
            Long id = Long.parseLong(scanner.nextLine());
            UserDto user = checkIfUserExistsById(id);
            if (user == null) return;

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

            userService.update(user);
            System.out.println("User updated successfully");
        } catch (Exception e) {
            System.err.println("Error while updateUser: " + e.getMessage());
        }
    }

    private static void findAllUsers() {
        try {
            List<UserDto> users = userService.findAll();
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
            UserDto user = userService.findById(id);
            System.out.println(user);
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

            int age = askAge();

            Long id = userService.save(UserDto.builder()
                    .name(name)
                    .email(email)
                    .age(age)
                    .createdAt(LocalDateTime.now())
                    .build());

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

    private static UserDto checkIfUserExistsById(Long id) {
        UserDto userDto = null;
        try {
            userDto = userService.findById(id);
        } catch (UserNotFoundException e) {
            System.out.println("User not found!");
        }
        return userDto;
    }

    private static int askAge() {
        while (true) {
            System.out.print("Enter age: ");
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid age, enter number format");
            }
        }
    }

}