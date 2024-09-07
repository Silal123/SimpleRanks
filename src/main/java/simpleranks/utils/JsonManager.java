package simpleranks.utils;

import com.google.gson.*;

import java.util.List;

public class JsonManager {

    private JsonObject jsonObject;

    public JsonManager() {
        this.jsonObject = new JsonObject();
    }

    public JsonManager(String json) {
        if (json != null && !json.isEmpty()) {
            this.jsonObject = JsonParser.parseString(json).getAsJsonObject();
        } else {
            this.jsonObject = new JsonObject();
        }
    }

    public JsonObject jsonObject() { return jsonObject; }

    public JsonManager addProperty(String key, String value) {
        removeProperty(key);
        jsonObject.addProperty(key, value);
        return this;
    }

    public JsonManager addProperty(String key, Number value) {
        removeProperty(key);
        jsonObject.addProperty(key, value);
        return this;
    }

    public JsonManager addProperty(String key, Boolean value) {
        removeProperty(key);
        jsonObject.addProperty(key, value);
        return this;
    }

    public JsonManager addProperty(String key, JsonObject value) {
        removeProperty(key);
        jsonObject.add(key, value);
        return this;
    }

    public JsonManager addProperty(String key, JsonArray value) {
        removeProperty(key);
        jsonObject.add(key, value);
        return this;
    }

    public JsonManager addProperty(String key, JsonManager value) {
        removeProperty(key);
        jsonObject.add(key, value.jsonObject);
        return this;
    }

    public JsonManager addProperty(String key, List<?> values) {
        removeProperty(key);
        JsonArray jsonArray = new JsonArray();
        for (Object value : values) {
            if (value instanceof String) {
                jsonArray.add((String) value);
            } else if (value instanceof Number) {
                jsonArray.add((Number) value);
            } else if (value instanceof Boolean) {
                jsonArray.add((Boolean) value);
            } else if (value instanceof JsonObject) {
                jsonArray.add((JsonObject) value);
            }
            // Add more conditions for other data types if needed
        }
        jsonObject.add(key, jsonArray);
        return this;
    }

    public JsonManager removeProperty(String key) {
        jsonObject.remove(key);
        return this;
    }

    public String toJsonString() {
        return new Gson().toJson(jsonObject);
    }

    public static boolean isValidJson(String json) {
        try {
            JsonParser.parseString(json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String makePrettier(String json) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = gson.toJson(gson.fromJson(json, Object.class));
        return prettyJson;
    }

    public boolean hasKey(String key) {
        return jsonObject.has(key);
    }
    public String getString(String key) {
        return jsonObject.get(key).getAsString();
    }

    public Number getNumber(String key) {
        return jsonObject.get(key).getAsNumber();
    }

    public Boolean getBoolean(String key) {
        return jsonObject.get(key).getAsBoolean();
    }

    public JsonObject getObject(String key) {
        return jsonObject.get(key).getAsJsonObject();
    }

    public JsonArray getArray(String key) {
        return jsonObject.get(key).getAsJsonArray();
    }

}
