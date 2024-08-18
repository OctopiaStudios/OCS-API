package fr.octopiastudios.api.integrations;

import com.google.common.collect.Lists;
import fr.octopiastudios.api.OSPlugin;
import fr.octopiastudios.api.integrations.commands.IntegrationsCommands;
import fr.octopiastudios.api.integrations.integrations.VaultIntegration;
import fr.octopiastudios.api.integrations.listener.IntegrationListener;
import fr.octopiastudios.api.modules.objects.Module;
import lombok.Getter;

import java.util.List;

@Getter
public class IntegrationManager extends Module {

    @Getter
    public static IntegrationManager instance;
    private final List<Integration> integrations;

    public IntegrationManager(OSPlugin plugin) {
        super(plugin, "Integrations");
        this.setDesactivable(false);
        instance = this;

        this.integrations = Lists.newArrayList();

        // COMMANDS
        this.registerCommand(new IntegrationsCommands());

        // LISTENERS
        this.registerListener(new IntegrationListener());

        // INTEGRATIONS
        this.registerIntegration(new VaultIntegration());
    }

    public Integration getIntegration(String pluginName) {
        return this.integrations.stream().filter(integration -> integration.getPluginName().equalsIgnoreCase(pluginName) && integration.isEnabled()).findFirst().orElse(null);
    }

    public void registerIntegration(Integration integration) {
        this.integrations.add(integration);
    }
}
