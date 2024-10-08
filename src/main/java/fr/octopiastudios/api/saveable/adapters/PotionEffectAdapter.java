package fr.octopiastudios.api.saveable.adapters;

import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import fr.octopiastudios.api.OSPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

public class PotionEffectAdapter extends GsonAdapter<PotionEffect> {

    private static Type seriType = new TypeToken<Map<String, Object>>() {
    }.getType();
    private static String TYPE = "effect";
    private static String DURATION = "duration";
    private static String AMPLIFIER = "amplifier";
    private static String AMBIENT = "ambient";

    public PotionEffectAdapter(OSPlugin plugin) {
        super(plugin);
    }

    public void write(JsonWriter jsonWriter, PotionEffect potionEffect) throws IOException {
        if (potionEffect == null) {
            jsonWriter.nullValue();
            return;
        }
        jsonWriter.value(getRaw(potionEffect));
    }

    public PotionEffect read(JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
            return null;
        }
        return fromRaw(jsonReader.nextString());
    }

    private String getRaw(PotionEffect potion) {
        Map<String, Object> serial = potion.serialize();

        return this.getGson().toJson(serial);
    }

    @SuppressWarnings("deprecation")
    private PotionEffect fromRaw(String raw) {
        Map<String, Object> keys = this.getGson().fromJson(raw, seriType);
        return new PotionEffect(Objects.requireNonNull(PotionEffectType.getById(((Double) keys.get(TYPE)).intValue())),
                ((Double) keys.get(DURATION)).intValue(), ((Double) keys.get(AMPLIFIER)).intValue(),
                (Boolean) keys.get(AMBIENT));
    }
}
