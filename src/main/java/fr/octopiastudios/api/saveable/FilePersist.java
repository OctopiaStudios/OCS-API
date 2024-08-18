package fr.octopiastudios.api.saveable;

import com.google.gson.Gson;
import fr.octopiastudios.api.OSAPI;

import java.io.File;

/**
 * This class is called FilePersist because I intended to use it for several file types (YAML, JSON, etc.),
 * but we're now going to use <a href="https://github.com/Exlll/ConfigLib">ConfigLIb</a> from Exlll.
 * This class will therefore be used mainly for JSON files
 */
public interface FilePersist {

    Gson gson = OSAPI.getAPI().getGson();

    File getFile();

    void loadData();

    void saveData(boolean sync);
}
