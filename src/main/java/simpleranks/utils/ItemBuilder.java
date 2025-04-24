package simpleranks.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ItemBuilder {

    private final ItemStack itemStack;
    private ItemMeta itemMeta;

    public ItemBuilder(Material mat, int amount) {
        itemStack = new ItemStack(mat, amount);
        itemMeta = itemStack.getItemMeta();
    }
    public ItemBuilder setDisplayName(String s) {
        itemMeta.setDisplayName("§r§f" + s);
        return this;
    }
    public ItemBuilder setLore(String... s) {
        itemMeta.setLore(Arrays.asList(s));
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        itemMeta.setLore(lore);
        return this;
    }
    public ItemBuilder setLocalizedName(String s) {
        itemMeta.setLocalizedName(s);
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }
    public ItemBuilder setLocalizedName(Enum e) {
        itemMeta.setLocalizedName(e.name());
        return this;
    }
    public ItemBuilder setUnbreakable(boolean s) {
        itemMeta.setUnbreakable(s);
        return this;
    }
    public ItemBuilder addEnchantment(Enchantment ench, int level) {
        itemMeta.addEnchant(ench, level, true);
        return this;
    }
    public ItemBuilder addItemFlags(ItemFlag... s) {
        itemMeta.addItemFlags(s);
        return this;
    }
    public ItemBuilder addPotionEffect(PotionEffectType potion, int duration, int level) {
        ((PotionMeta) itemMeta).addCustomEffect(new PotionEffect(potion, duration, level, false, false, false), true);
        return this;
    }
    public ItemBuilder setPlayerHead(UUID uuid) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

        SkullMeta skullMeta = (SkullMeta) itemMeta;
        skullMeta.setOwningPlayer(player);
        return this;
    }

    public ItemBuilder setCustomHead(CustomHead customHead) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", customHead.texture()));
        Field profileField = null;

        try {
            profileField = itemMeta.getClass().getDeclaredField("profile");
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }

        if (profileField == null) {
            return this;

        }
        profileField.setAccessible(true);

        try {
            profileField.set(itemMeta, profile);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return this;
    }

    public ItemBuilder setLeatherArmorColor(Color c) {
        LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemMeta;
        leatherArmorMeta.setColor(c);
        return this;
    }

    public ItemBuilder setItemMeta(ItemMeta itemMeta) {
        this.itemMeta = itemMeta;
        return this;
    }

    public ItemStack build() {
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public ItemBuilder clone() {
        return new ItemBuilder(itemStack.getType(), itemStack.getAmount()).setItemMeta(itemMeta);
    }

    @Override
    public String toString() {
        return "ItemBuilderUtil{" + "itemMeta=" + itemMeta + ", itemStack=" + itemStack + '}';
    }

    public enum CustomHead {

        OAK_ARROW_RIGHT("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTliZjMyOTJlMTI2YTEwNWI1NGViYTcxM2FhMWIxNTJkNTQxYTFkODkzODgyOWM1NjM2NGQxNzhlZDIyYmYifX19"),
        OAK_ARROW_LEFT("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ=="),
        GREEN_PLUS("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjA1NmJjMTI0NGZjZmY5OTM0NGYxMmFiYTQyYWMyM2ZlZTZlZjZlMzM1MWQyN2QyNzNjMTU3MjUzMWYifX19"),
        CHECK_MARK("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTkyZTMxZmZiNTljOTBhYjA4ZmM5ZGMxZmUyNjgwMjAzNWEzYTQ3YzQyZmVlNjM0MjNiY2RiNDI2MmVjYjliNiJ9fX0=");

        CustomHead(String base64) {
            this.base64 = base64;
        }
        private String base64;
        public String texture() { return base64; }
    }
}
