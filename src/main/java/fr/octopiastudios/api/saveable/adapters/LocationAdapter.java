package fr.octopiastudios.api.saveable.adapters;

import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import fr.octopiastudios.api.OSPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;


public class LocationAdapter extends GsonAdapter<Location> {
    private static Type seriType = new TypeToken<Map<String, Object>>() {
    }.getType();

    private static String NAME = "name";
    private static String X = "x";
    private static String Y = "y";
    private static String Z = "z";
    private static String YAW = "yaw";
    private static String PITCH = "pitch";

    public LocationAdapter(OSPlugin plugin) {
        super(plugin);
    }

    public void write(JsonWriter jsonWriter, Location location) throws IOException {
        if (location == null) {
            jsonWriter.nullValue();
            return;
        }
        jsonWriter.value(getRaw(location));
    }

    public Location read(JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
            return null;
        }
        return fromRaw(jsonReader.nextString());
    }

    private String getRaw(Location location) {
        if (location.getWorld() == null) {
            return null;
        }

        Map<String, Object> serial = new HashMap<>();
        serial.put(NAME, location.getWorld().getName());
        serial.put(X, Double.toString(location.getX()));
        serial.put(Y, Double.toString(location.getY()));
        serial.put(Z, Double.toString(location.getZ()));
        serial.put(YAW, Float.toString(location.getYaw()));
        serial.put(PITCH, Float.toString(location.getPitch()));
        return this.getGson().toJson(serial);
    }

    private Location fromRaw(String raw) {
        Map<String, Object> keys = this.getGson().fromJson(raw, seriType);
        World w = Bukkit.getWorld((String) keys.get(NAME));
        if (w == null) {
            return null;
        }
        return new Location(w, Double.parseDouble((String) keys.get(X)), Double.parseDouble((String) keys.get(Y)),
                Double.parseDouble((String) keys.get(Z)), Float.parseFloat((String) keys.get(YAW)),
                Float.parseFloat((String) keys.get(PITCH)));
    }
}
