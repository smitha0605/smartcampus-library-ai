package com.smartcampus.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.smartcampus.models.Book;
import com.smartcampus.models.User;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    private static final String DATA_DIR     = "data/";
    private static final String BOOKS_FILE   = DATA_DIR + "books.json";
    private static final String USERS_FILE   = DATA_DIR + "users.json";

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    static {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
        } catch (IOException e) {
            System.err.println("[FileManager] Could not create data directory: " + e.getMessage());
        }
    }

    
    public static void saveBooks(List<Book> books) {
        try (Writer writer = new FileWriter(BOOKS_FILE)) {
            GSON.toJson(books, writer);
            System.out.println("  [SAVE] " + books.size() + " books saved to " + BOOKS_FILE);
        } catch (IOException e) {
            System.err.println("[FileManager] Error saving books: " + e.getMessage());
        }
    }

    
    public static List<Book> loadBooks() {
        File f = new File(BOOKS_FILE);
        if (!f.exists()) return new ArrayList<>();
        try (Reader reader = new FileReader(f)) {
            Type listType = new TypeToken<List<Book>>(){}.getType();
            List<Book> books = GSON.fromJson(reader, listType);
            System.out.println("  [LOAD] " + books.size() + " books loaded from " + BOOKS_FILE);
            return books;
        } catch (IOException e) {
            System.err.println("[FileManager] Error loading books: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static void saveUsers(List<User> users) {
        try (Writer writer = new FileWriter(USERS_FILE)) {
            GSON.toJson(users, writer);
            System.out.println("  [SAVE] " + users.size() + " users saved to " + USERS_FILE);
        } catch (IOException e) {
            System.err.println("[FileManager] Error saving users: " + e.getMessage());
        }
    }

    public static List<User> loadUsers() {
        File f = new File(USERS_FILE);
        if (!f.exists()) return new ArrayList<>();
        try (Reader reader = new FileReader(f)) {
            Type listType = new TypeToken<List<User>>(){}.getType();
            List<User> users = GSON.fromJson(reader, listType);
            System.out.println("  [LOAD] " + users.size() + " users loaded from " + USERS_FILE);
            return users;
        } catch (IOException e) {
            System.err.println("[FileManager] Error loading users: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
