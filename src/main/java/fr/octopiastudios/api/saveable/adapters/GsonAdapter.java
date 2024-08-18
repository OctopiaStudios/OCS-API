package fr.octopiastudios.api.saveable.adapters;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import fr.octopiastudios.api.OSPlugin;

public abstract class GsonAdapter<T> extends TypeAdapter<T> {

    private final OSPlugin plugin;

    public GsonAdapter(OSPlugin plugin) {
        this.plugin = plugin;
    }

    public Gson getGson() {
        return this.plugin.getGson();
    }
}
