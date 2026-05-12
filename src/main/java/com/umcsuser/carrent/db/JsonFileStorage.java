package com.umcsuser.carrent.db;

import com.google.gson.Gson;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class JsonFileStorage<T> {

    private final Gson gson = new Gson();
    private final Path path;
    private final Type type;

    public JsonFileStorage(String filename, Type type) {
        this.path = Paths.get(filename);
        this.type = type;
    }

    public List<T> load() {
        if (!Files.exists(path)) return new ArrayList<>();
        try {
            String json = Files.readString(path);
            List<T> list = gson.fromJson(json, type);
            return list != null ? list : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void save(List<T> data) {
        try {
            String json = gson.toJson(data);
            Files.writeString(path, json, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}