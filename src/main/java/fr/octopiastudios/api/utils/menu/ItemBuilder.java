package fr.octopiastudios.api.utils.menu;

import com.google.common.collect.Lists;
import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class ItemBuilder {

    private ItemStack item;
    private ItemMeta meta;
    private Material material = Material.STONE;
    private int amount = 1;
    private MaterialData data;
    private short damage = 0;
    private Map<Enchantment, Integer> enchantments = new HashMap<Enchantment, Integer>();
    private String displayname;
    private List<String> lore = new ArrayList<String>();
    private boolean andSymbol = true;
    private boolean unsafeStackSize = false;

    public ItemBuilder(Material material) {
        if (material == null) {
            material = Material.AIR;
        }
        this.item = new ItemStack(material);
        this.material = material;
    }

    public ItemBuilder(Material material, int amount) {
        if (material == null) {
            material = Material.AIR;
        }
        if (!(amount <= material.getMaxStackSize() && amount > 0 || this.unsafeStackSize)) {
            amount = 1;
        }
        this.amount = amount;
        this.item = new ItemStack(material, amount);
        this.material = material;
    }

    public ItemBuilder(Material material, int amount, String displayname) {
        if (material == null) {
            material = Material.AIR;
        }
        Validate.notNull((Object) displayname, (String) "The Displayname is null.");
        this.item = new ItemStack(material, amount);
        this.material = material;
        if (!(amount <= material.getMaxStackSize() && amount > 0 || this.unsafeStackSize)) {
            amount = 1;
        }
        this.amount = amount;
        this.displayname = displayname;
    }

    public ItemBuilder(Material material, String displayname) {
        if (material == null) {
            material = Material.AIR;
        }
        Validate.notNull((Object) displayname, (String) "The Displayname is null.");
        this.item = new ItemStack(material);
        this.material = material;
        this.displayname = displayname;
    }

    public ItemBuilder(ItemStack item) {
        Validate.notNull((Object) item, (String) "The Item is null.");
        this.item = new ItemStack(item.getType());
        this.meta = this.item.getItemMeta();
        this.material = item.getType();
        this.amount = item.getAmount();
        this.data = item.getData();
        this.damage = item.getDurability();
        this.enchantments = item.getEnchantments();
        if (item.hasItemMeta()) {
            this.displayname = item.getItemMeta().getDisplayName();
        }
        this.lore = item.getItemMeta().getLore();
        if (item.getType() == Material.ENCHANTED_BOOK) {
            EnchantmentStorageMeta enchantMeta = (EnchantmentStorageMeta) item.getItemMeta();
            this.enchantments = enchantMeta.getStoredEnchants();
        }
    }

    public ItemBuilder amount(int amount) {
        if (!(amount <= this.material.getMaxStackSize() && amount > 0 || this.unsafeStackSize)) {
            amount = 1;
        }
        this.amount = amount;
        return this;
    }

    public ItemBuilder data(MaterialData data) {
        Validate.notNull((Object) data, (String) "The Data is null.");
        this.data = data;
        return this;
    }

    @Deprecated
    public ItemBuilder damage(short damage) {
        this.damage = damage;
        return this;
    }

    public ItemBuilder durability(short damage) {
        this.damage = damage;
        return this;
    }

    public ItemBuilder dye(DyeColor color) {
        this.damage = color.getDyeData();
        return this;
    }

    public ItemBuilder material(Material material) {
        Validate.notNull((Object) material, (String) "The Material is null.");
        this.material = material;
        return this;
    }

    public ItemBuilder meta(ItemMeta meta) {
        if (meta == null) {
            return this;
        }
        this.meta = meta;
        return this;
    }

    public ItemBuilder enchant(Enchantment enchant, int level) {
        Validate.notNull((Object) enchant, (String) "The Enchantment is null.");
        this.enchantments.put(enchant, level);
        return this;
    }

    public ItemBuilder enchant(Map<Enchantment, Integer> enchantments) {
        Validate.notNull(enchantments, (String) "The Enchantments are null.");
        this.enchantments = enchantments;
        return this;
    }

    public ItemBuilder displayname(String displayname) {
        Validate.notNull((Object) displayname, (String) "The Displayname is null.");
        this.displayname = this.andSymbol ? ChatColor.translateAlternateColorCodes((char) '&', (String) displayname) : displayname;
        return this;
    }

    public ItemBuilder lore(String line) {
        Validate.notNull((Object) line, (String) "The Line is null.");
        if (this.lore == null) {
            this.lore = Lists.newArrayList();
        }
        this.lore.add(this.andSymbol ? ChatColor.translateAlternateColorCodes((char) '&', (String) line) : line);
        return this;
    }

    public ItemBuilder lore(List<String> lores) {
        Validate.notNull(lores, (String) "The Lores are null.");

        for (String lore : lores) {
            this.lore(lore);
        }
        return this;
    }

    @Deprecated
    public ItemBuilder lores(String... lines) {
        Validate.notNull((Object) lines, (String) "The Lines are null.");
        String[] arrstring = lines;
        int n = arrstring.length;
        int n2 = 0;
        while (n2 < n) {
            String line = arrstring[n2];
            this.lore(this.andSymbol ? ChatColor.translateAlternateColorCodes((char) '&', (String) line) : line);
            ++n2;
        }
        return this;
    }

    public /* varargs */ ItemBuilder lore(String... lines) {
        Validate.notNull((Object) lines, (String) "The Lines are null.");
        String[] arrstring = lines;
        int n = arrstring.length;
        int n2 = 0;
        while (n2 < n) {
            String line = arrstring[n2];
            this.lore(this.andSymbol ? ChatColor.translateAlternateColorCodes((char) '&', (String) line) : line);
            ++n2;
        }
        return this;
    }

    public ItemBuilder lore(String line, int index) {
        Validate.notNull((Object) line, (String) "The Line is null.");
        this.lore.set(index, this.andSymbol ? ChatColor.translateAlternateColorCodes((char) '&', (String) line) : line);
        return this;
    }

    @Deprecated
    public ItemBuilder owner(String user) {
        Validate.notNull((Object) user, (String) "The Username is null.");
        if (this.material == Material.PLAYER_HEAD || this.material == Material.PLAYER_WALL_HEAD) {
            SkullMeta smeta = (SkullMeta) this.meta;
            smeta.setOwner(user);
            this.meta = smeta;
        }
        return this;
    }

    @Deprecated
    public ItemBuilder replaceAndSymbol() {
        this.replaceAndSymbol(!this.andSymbol);
        return this;
    }

    public ItemBuilder replaceAndSymbol(boolean replace) {
        this.andSymbol = replace;
        return this;
    }

    public ItemBuilder toggleReplaceAndSymbol() {
        this.replaceAndSymbol(!this.andSymbol);
        return this;
    }

    public ItemBuilder unsafeStackSize(boolean allow) {
        this.unsafeStackSize = allow;
        return this;
    }

    public ItemBuilder toggleUnsafeStackSize() {
        this.unsafeStackSize(!this.unsafeStackSize);
        return this;
    }

    public ItemStack build() {
        this.item.setType(this.material);
        this.item.setAmount(this.amount);
        this.item.setDurability(this.damage);
        this.meta = this.item.getItemMeta();
        if (this.data != null) {
            this.item.setData(this.data);
        }
        if (this.displayname != null) {
            this.meta.setDisplayName(this.displayname);
        }
        if (this.lore != null && this.lore.size() > 0) {
            this.meta.setLore(this.lore);
        }
        this.item.setItemMeta(this.meta);
        if (this.enchantments.size() > 0) {
            if (this.material == Material.ENCHANTED_BOOK) {
                this.meta = this.item.getItemMeta();
                EnchantmentStorageMeta enchantMeta = (EnchantmentStorageMeta) this.meta;
                for (Map.Entry<Enchantment, Integer> entry : this.enchantments.entrySet()) {
                    enchantMeta.addStoredEnchant(entry.getKey(), entry.getValue().intValue(), true);
                }
                this.item.setItemMeta(enchantMeta);
            } else {
                this.item.addUnsafeEnchantments(this.enchantments);
            }
        }
        return this.item;
    }
}

