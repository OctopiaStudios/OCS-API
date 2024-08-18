package fr.octopiastudios.api.utils.menu.items;

import fr.octopiastudios.api.utils.menu.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

@Getter
@Setter
public class PageItem extends VirtualItem {

    private int page;
    private boolean previous;

    public PageItem(int page) {
        this(page, "&6Page suivante: &f");
    }

    public PageItem(int page, boolean previous) {
        this(page, "&6Page précédente: &f");
    }

    public PageItem(int page, String value) {
        super((new ItemBuilder(Material.PAPER)).displayname(value + page).build());
        this.page = page;
    }
}
