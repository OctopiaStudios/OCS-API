package fr.octopiastudios.api.scoreboard;

import com.google.common.collect.Lists;
import fr.octopiastudios.api.OSPlugin;
import fr.octopiastudios.api.modules.objects.Module;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class ScoreboardManager extends Module {

    @Getter
    private static ScoreboardManager instance;

    private Assemble scoreboardHandler;
    private final List<AssembleAdapter> scoreboards;

    public ScoreboardManager(OSPlugin plugin) {
        super(plugin, "Scoreboard");
        instance = this;

        this.scoreboards = Lists.newArrayList();
    }

    public void addScoreboard(AssembleAdapter adapter) {
        this.destroyScoreboard(adapter.getClass());
        this.scoreboards.add(adapter);

        this.setScoreboard(adapter);
    }

    public void addScoreboard(AssembleAdapter adapter, List<Player> players) {
        this.destroyScoreboard(adapter.getClass());
        this.scoreboards.add(adapter);

        this.setScoreboard(adapter, players);
    }

    private void setScoreboard(AssembleAdapter adapter) {
        if (this.scoreboardHandler != null) this.scoreboardHandler.cleanup();

        this.scoreboardHandler = new Assemble(this.getPlugin(), adapter, Lists.newArrayList(Bukkit.getServer().getOnlinePlayers()));
        this.scoreboardHandler.setTicks(20);
        this.scoreboardHandler.setAssembleStyle(AssembleStyle.MODERN);
    }

    private void setScoreboard(AssembleAdapter adapter, List<Player> players) {
        if (this.scoreboardHandler != null) this.scoreboardHandler.cleanup();

        this.scoreboardHandler = new Assemble(this.getPlugin(), adapter, players);
        this.scoreboardHandler.setTicks(20);
        this.scoreboardHandler.setAssembleStyle(AssembleStyle.MODERN);
    }

    public void destroyScoreboard(Class<? extends AssembleAdapter> clazz) {
        if (this.scoreboardHandler == null) return;

        this.scoreboards.removeIf(el -> el.getClass() == clazz);
        this.scoreboardHandler.cleanup();

        // Show the first scoreboard in list.
        if (!this.scoreboards.isEmpty()) this.setScoreboard(this.scoreboards.get(0));
        else this.scoreboardHandler = null;
    }
}
