package fr.octopiastudios.api.integrations.listener;

import com.google.common.collect.Lists;
import fr.octopiastudios.api.integrations.Integration;
import fr.octopiastudios.api.integrations.IntegrationManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

import java.util.List;

public class IntegrationListener implements Listener {

    private final IntegrationManager integrationManager = IntegrationManager.getInstance();

    /**
     * If the plugin is enabled, we enable the integrations
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPluginEnable(PluginEnableEvent event) {
        Integration integration = this.integrationManager.getIntegration(event.getPlugin().getName());
        if (integration == null) return;
        integration.enableIntegration();
    }

    /**
     * If the plugin is disabled, we disable the integrations
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPluginDisable(PluginDisableEvent event) {
        List<Integration> toRemove = Lists.newArrayList();
        for (Integration integration : this.integrationManager.getIntegrations()) {
            if (integration == null) continue;

            boolean tryToLoad = integration.tryToLoad();
            if (!tryToLoad) toRemove.add(integration);
        }
        toRemove.forEach(Integration::disableIntegration);
        this.integrationManager.getIntegrations().removeAll(toRemove);
    }
}
