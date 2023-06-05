package team.floracore.common.util.gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class JArray implements JElement {
    private final JsonArray array = new JsonArray();

    @Override
    public JsonArray toJson() {
        return this.array;
    }

    public JArray add(JsonElement value) {
        if (value == null) {
            return add(JsonNull.INSTANCE);
        }
        this.array.add(value);
        return this;
    }

    public JArray add(String value) {
        if (value == null) {
            return add(JsonNull.INSTANCE);
        }
        this.array.add(new JsonPrimitive(value));
        return this;
    }

    public JArray addAll(Iterable<String> iterable) {
        for (String s : iterable) {
            add(s);
        }
        return this;
    }

    public JArray add(JElement value) {
        if (value == null) {
            return add(JsonNull.INSTANCE);
        }
        return add(value.toJson());
    }

    public JArray add(Supplier<? extends JElement> value) {
        return add(value.get().toJson());
    }

    public JArray consume(Consumer<? super JArray> consumer) {
        consumer.accept(this);
        return this;
    }
}