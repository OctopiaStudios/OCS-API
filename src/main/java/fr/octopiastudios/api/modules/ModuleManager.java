package fr.octopiastudios.api.modules;

import com.google.common.collect.Lists;
import fr.octopiastudios.api.OSPlugin;
import fr.octopiastudios.api.modules.objects.Module;
import fr.octopiastudios.api.modules.objects.Saveable;
import lombok.Getter;

import java.util.List;

@Getter
public class ModuleManager {

    @Getter
    private static ModuleManager instance;
    public List<Module> modules;

    public ModuleManager() {
        instance = this;
        this.modules = Lists.newArrayList();
    }

    public void registerManager(Module module) {
        this.modules.add(module);
    }

    public void registerPersist(Saveable module) {
        this.registerManager(module);
        OSPlugin plugin = module.getPlugin();
        plugin.registerPersists(module);
    }

    public Module getModuleByName(String name) {
        return this.modules.stream().filter(module -> module.getModuleName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
