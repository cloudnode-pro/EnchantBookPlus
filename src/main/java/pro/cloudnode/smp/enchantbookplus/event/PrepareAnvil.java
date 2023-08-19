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
import pro.cloudnode.smp.enchantbookplus.ConfigEnchantmentEntry;
import pro.cloudnode.smp.enchantbookplus.EnchantBookPlus;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class PrepareAnvil implements Listener {
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
        if (upgradeEnchants.isEmpty()) return;
        final @NotNull HashMap<@NotNull Enchantment, @NotNull Integer> upgrades = new HashMap<>();
        int cost = 0;
        for (final @NotNull Map.Entry<@NotNull Enchantment, @NotNull Integer> entry : upgradeEnchants.entrySet()) {
            final @NotNull Enchantment enchantment = entry.getKey();
            if (enchantment.getMaxLevel() == 1) continue;
            final @NotNull Optional<@NotNull ConfigEnchantmentEntry> configEnchantment = EnchantBookPlus.getInstance().getConfigEnchantment(enchantment.getKey().getKey());
            if (configEnchantment.isEmpty()) continue;
            final int upgradeLevel = entry.getValue();
            final int finalLevel;
            if (itemEnchants.containsKey(enchantment)) {
                final int itemLevel = itemEnchants.get(enchantment);
                if (itemLevel > upgradeLevel) finalLevel = itemLevel;
                else if (itemLevel == upgradeLevel) finalLevel = upgradeLevel + 1;
                else finalLevel = upgradeLevel;
            }
            else finalLevel = upgradeLevel;
            if (finalLevel < enchantment.getMaxLevel()) continue;
            if (configEnchantment.get().getMaxLevel(enchantment).isPresent() && finalLevel > configEnchantment.get().getMaxLevel(enchantment).get()) continue;
            if (finalLevel > upgradeLevel) cost += configEnchantment.get().getMultiplyCostByLevel() ? configEnchantment.get().getCost() * (finalLevel - enchantment.getMaxLevel()) : configEnchantment.get().getCost();
            upgrades.put(enchantment, finalLevel);
        }
        if (upgrades.isEmpty()) return;
        final int vanillaCost = inventory.getRepairCost();
        inventory.setRepairCost(vanillaCost + cost);
        for (final @NotNull Map.Entry<@NotNull Enchantment, @NotNull Integer> entry : upgrades.entrySet()) {
            if (result.get().getItemMeta() instanceof final @NotNull EnchantmentStorageMeta resultMeta) {
                if (!resultMeta.getStoredEnchants().containsKey(entry.getKey())) continue;
                resultMeta.addStoredEnchant(entry.getKey(), entry.getValue(), true);
                result.get().setItemMeta(resultMeta);
            }
            else {
                if (!result.get().getEnchantments().containsKey(entry.getKey())) continue;
                result.get().addUnsafeEnchantment(entry.getKey(), entry.getValue());
            }
        }
    }
}
