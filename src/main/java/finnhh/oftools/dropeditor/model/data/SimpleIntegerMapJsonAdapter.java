package finnhh.oftools.dropeditor.model.data;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;

import java.io.IOException;
import java.util.LinkedHashMap;

public class SimpleIntegerMapJsonAdapter extends TypeAdapter<MapProperty<Integer, Integer>> {
    @Override
    public void write(JsonWriter out, MapProperty<Integer, Integer> value) throws IOException {
        out.beginObject();
        for (var entry : value.entrySet()) {
            out.name(entry.getKey().toString());
            out.value(entry.getValue());
        }
        out.endObject();
    }

    @Override
    public MapProperty<Integer, Integer> read(JsonReader in) throws IOException {
        LinkedHashMap<Integer, Integer> map = new LinkedHashMap<>();
        in.beginObject();
        while (in.hasNext()) {
            String name = in.nextName();
            Integer value = in.nextInt();
            map.put(Integer.parseInt(name), value);
        }
        in.endObject();
        return new SimpleMapProperty<>(FXCollections.observableMap(map));
    }
}
