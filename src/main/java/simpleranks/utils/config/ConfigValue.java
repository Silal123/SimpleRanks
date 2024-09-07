package simpleranks.utils.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import simpleranks.utils.JsonManager;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class ConfigValue<T> {
    private final Path path;
    private final String key;
    private final Class<T> type;
    private T defaultValue = null;

    public ConfigValue(String key, Class<T> type, Path filePath) {
        this.key = key;
        this.type = type;
        this.path = filePath;
    }

    public ConfigValue(String key, Class<T> type, Path filePath, T defaultValue) {
        this.key = key;
        this.type = type;
        this.path = filePath;
        this.defaultValue = defaultValue;
    }

    public T defaultValue() {
        return defaultValue;
    }
    public void set(T value) {
        JsonManager jsonManager = new JsonManager();
        String json;

        try {
            json = Files.readString(path, StandardCharsets.UTF_8);
            jsonManager = new JsonManager(json);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String[] spjson = key.split("\\.");
        JsonObject currentObject = jsonManager.jsonObject();

        for (int i = 0; i < spjson.length - 1; i++) {
            String currentKey = spjson[i];
            if (!currentObject.has(currentKey) || !currentObject.get(currentKey).isJsonObject()) {
                currentObject.add(currentKey, new JsonObject());
            }
            currentObject = currentObject.getAsJsonObject(currentKey);
        }

        String finalKey = spjson[spjson.length - 1];
        if (value instanceof JsonObject && currentObject.has(finalKey) && currentObject.get(finalKey).isJsonObject()) {
            JsonObject existingObject = currentObject.getAsJsonObject(finalKey);
            JsonObject newValueObject = (JsonObject) value;
            for (Map.Entry<String, JsonElement> entry : newValueObject.entrySet()) {
                existingObject.add(entry.getKey(), entry.getValue());
            }
            currentObject.add(finalKey, existingObject);
        } else {
            currentObject.add(finalKey, new Gson().toJsonTree(value));
        }

        try {
            Files.writeString(path, JsonManager.makePrettier(jsonManager.toJsonString()), StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public T get() {
        JsonManager jsonManager = new JsonManager();
        String json;
        try {
            json = Files.readString(path, StandardCharsets.UTF_8);
            jsonManager = new JsonManager(json);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String[] spjson = key.split("\\.");
        String tmpkey = null;

        int now = 0;
        for (String s : spjson) {
            if (spjson.length-1 == now) { tmpkey = s; break; }
            if (!jsonManager.hasKey(s)) return null;
            jsonManager = new JsonManager(new Gson().toJson(jsonManager.getObject(s)));
            now++;
        }

        if (!jsonManager.hasKey(tmpkey)) {
            if (defaultValue != null) set(defaultValue);
            return defaultValue;
        }
        if (type == Arrays.class) {
            return (T) jsonManager.getArray(tmpkey);
        } else if (type == String.class) {
            return (T) jsonManager.getString(tmpkey);
        } else  if (type == Number.class) {
            return (T) jsonManager.getNumber(tmpkey);
        } else if (type == Boolean.class) {
            return (T) jsonManager.getBoolean(tmpkey);
        }

        else {
            return null;
        }
    }

}
