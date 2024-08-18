package fr.octopiastudios.api.saveable.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import fr.octopiastudios.api.OSAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemStackAdapter implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {

    private static final String MATERIAL = "material";
    private static final String AMOUNT = "amount";
    private static final String DURABILITY = "durability";
    private static final String DISPLAY_NAME = "displayName";
    private static final String LORE = "lore";
    private static final String ENCHANTMENTS = "enchants";

    @SuppressWarnings("unchecked")
    @Override
    public ItemStack deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
        try {
            JsonObject obj = json.getAsJsonObject();
            Material material = Material.getMaterial(obj.get(ItemStackAdapter.MATERIAL).getAsString());
            ItemStack itemStack = new ItemStack(material, 1, (short) 0);
            itemStack.setAmount(obj.get(ItemStackAdapter.AMOUNT).getAsInt());
            itemStack.setDurability((short) obj.get(ItemStackAdapter.DURABILITY).getAsInt());

            if (itemStack.getAmount() <= 0) {
                itemStack.setAmount(1);
            }

            JsonElement displayName = obj.get(ItemStackAdapter.DISPLAY_NAME);
            JsonElement lore = obj.get(ItemStackAdapter.LORE);
            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(material);
            if (displayName != null) {
                meta.setDisplayName(displayName.getAsString());
            }
            if (lore != null) {
                meta.setLore(OSAPI.getAPI().getGson().fromJson(lore, List.class));
            }
            JsonElement enchants = obj.get(ItemStackAdapter.ENCHANTMENTS);
            if (enchants != null) {
                Map<String, Double> enchantsMap = OSAPI.getAPI().getGson().fromJson(enchants, Map.class);
                for (Map.Entry<String, Double> entry : enchantsMap.entrySet()) {
                    Enchantment enchant = Enchantment.getByName(entry.getKey());
                    int level = entry.getValue().intValue();
                    if (material == Material.ENCHANTED_BOOK) {
                        ((EnchantmentStorageMeta) meta).addStoredEnchant(enchant, level, true);
                    } else {
                        meta.addEnchant(enchant, level, true);
                    }
                }
            }

            itemStack.setItemMeta(meta);
            return itemStack;
        } catch (final Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public JsonElement serialize(final ItemStack src, final Type typeOfSrc, final JsonSerializationContext context) {
        final JsonObject obj = new JsonObject();
        try {
            final ItemMeta meta = src.getItemMeta();
            obj.addProperty(ItemStackAdapter.MATERIAL, src.getType().name());
            obj.addProperty(ItemStackAdapter.AMOUNT, src.getAmount() == 0 ? 1 : src.getAmount());
            obj.addProperty(ItemStackAdapter.DURABILITY, src.getDurability());
            obj.addProperty(ItemStackAdapter.DISPLAY_NAME, (meta == null ? null : meta.getDisplayName()));
            obj.add(ItemStackAdapter.LORE, OSAPI.getAPI().getGson().toJsonTree(meta == null ? null : meta.getLore(), List.class));

            // - enchants
            Map<Enchantment, Integer> srcEnchants = null;
            if (src.getType() == Material.ENCHANTED_BOOK) {
                srcEnchants = ((EnchantmentStorageMeta) meta).getStoredEnchants();
            } else {
                srcEnchants = src.getEnchantments();
            }
            Map<String, Integer> enchants = new HashMap<>();
            for (Map.Entry<Enchantment, Integer> entry : srcEnchants.entrySet()) {
                enchants.put(entry.getKey().getName(), entry.getValue());
            }
            obj.add(ItemStackAdapter.ENCHANTMENTS, OSAPI.getAPI().getGson().toJsonTree(enchants, Map.class));

            return obj;
        } catch (final Exception ex) {
            ex.printStackTrace();
            return obj;
        }
    }
}
