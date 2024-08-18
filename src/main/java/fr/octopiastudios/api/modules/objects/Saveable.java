package fr.octopiastudios.api.modules.objects;

import com.google.gson.Gson;
import fr.octopiastudios.api.OSPlugin;
import fr.octopiastudios.api.saveable.FilePersist;

import java.io.File;

public abstract class Saveable extends Module implements FilePersist {

    public boolean needDir, needFirstSave;

    public Saveable(OSPlugin plugin, String name) {
        this(plugin, name, false, false);
    }

    public Saveable(OSPlugin plugin, String name, boolean needDir, boolean needFirstSave) {
        super(plugin, name);
        this.needDir = needDir;
        this.needFirstSave = needFirstSave;
        if (this.needDir) {
            if (this.needFirstSave) this.saveData(false);
            else {
                File directory = this.getFile();
                if (!directory.exists()) {
                    try {
                        directory.mkdir();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        }
    }

    public Gson getGson() {
        return this.getPlugin().getGson();
    }
}
