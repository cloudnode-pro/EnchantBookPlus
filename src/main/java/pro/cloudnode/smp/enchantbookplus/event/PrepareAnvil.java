package pro.cloudnode.smp.enchantbookplus.event;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PrepareAnvil implements Listener {
    @EventHandler
    public void onPrepareAnvil(final @NotNull PrepareAnvilEvent event) {
        final @NotNull Optional<@NotNull ItemStack> result = Optional.ofNullable(event.getResult());
        if (result.isEmpty()) return;
        final @NotNull AnvilInventory inventory = event.getInventory();
        final @NotNull ItemStack item = Objects.requireNonNull(inventory.getItem(0));
        final @NotNull ItemStack upgrade = Objects.requireNonNull(inventory.getItem(1));
        final @NotNull Map<@NotNull Enchantment, @NotNull Integer> itemEnchants =
                item.getType() == Material.ENCHANTED_BOOK && item.getItemMeta() instanceof final @NotNull EnchantmentStorageMeta itemMeta ?
                        itemMeta.getStoredEnchants() : item.getEnchantments();
        final @NotNull Map<@NotNull Enchantment, @NotNull Integer> upgradeEnchants =
                upgrade.getType() == Material.ENCHANTED_BOOK && upgrade.getItemMeta() instanceof final @NotNull EnchantmentStorageMeta upgradeMeta ?
                        upgradeMeta.getStoredEnchants() : upgrade.getEnchantments();
        if (itemEnchants.isEmpty() || upgradeEnchants.isEmpty()) return;
        final @NotNull HashMap<@NotNull Enchantment, @NotNull Integer> upgrades = new HashMap<>();
        for (final @NotNull Map.Entry<@NotNull Enchantment, @NotNull Integer> entry : upgradeEnchants.entrySet()) {
            final @NotNull Enchantment enchantment = entry.getKey();
            final int upgradeLevel = entry.getValue();
            if (upgradeLevel <= enchantment.getMaxLevel()) continue;
            if (itemEnchants.containsKey(enchantment)) {
                final int itemLevel = itemEnchants.get(enchantment);
                if (itemLevel > upgradeLevel) upgrades.put(enchantment, itemLevel);
                else if (itemLevel == upgradeLevel) upgrades.put(enchantment, upgradeLevel + 1);
                else upgrades.put(enchantment, upgradeLevel);
            }
            else upgrades.put(enchantment, upgradeLevel);
        }
        if (upgrades.isEmpty()) return;
        for (final @NotNull Map.Entry<@NotNull Enchantment, @NotNull Integer> entry : upgrades.entrySet()) {
            if (result.get().getItemMeta() instanceof final @NotNull EnchantmentStorageMeta resultMeta) {
                resultMeta.addStoredEnchant(entry.getKey(), entry.getValue(), true);
                result.get().setItemMeta(resultMeta);
            }
            else result.get().addUnsafeEnchantment(entry.getKey(), entry.getValue());
        }
    }
}
