package pro.cloudnode.smp.enchantbookplus;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.cloudnode.smp.enchantbookplus.event.PrepareAnvil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;

public final class EnchantBookPlus extends JavaPlugin {
    /**
     * Get plugin instance.
     */
    public static EnchantBookPlus getInstance() {
        return getPlugin(EnchantBookPlus.class);
    }

    /**
     * Register event listeners.
     */
    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new PrepareAnvil(), this);
    }

    /**
     * Config enchantments cache
     */
    private @NotNull List<@NotNull ConfigEnchantmentEntry> configEnchantments = new ArrayList<>();

    /**
     * Config enchantments cache
     */
    public @NotNull List<@NotNull ConfigEnchantmentEntry> getConfigEnchantments() {
        return configEnchantments;
    }

    /**
     * "ALL" enchantment cache
     */
    private @Nullable ConfigEnchantmentEntry.AllConfigEnchantmentEntry allConfigEnchantment;

    /**
     * "ALL" enchantment cache
     */
    public @NotNull Optional<ConfigEnchantmentEntry.@NotNull AllConfigEnchantmentEntry> getAllConfigEnchantment() {
        return Optional.ofNullable(allConfigEnchantment);
    }

    /**
     * Get enchantment from cache
     *
     * @param enchantment The Minecraft enchantment
     */
    public @NotNull Optional<@NotNull ConfigEnchantmentEntry> getConfigEnchantment(final @NotNull Enchantment enchantment) {
        final @NotNull Optional<@NotNull ConfigEnchantmentEntry> entry = getConfigEnchantments().stream().filter(c -> c.isEnchantment(enchantment)).findFirst();
        return entry.isEmpty() ? getAllConfigEnchantment().map(a -> a.enchant(enchantment)) : entry;
    }

    /**
     * Reload config
     */
    public void reload() {
        reloadConfig();
        final @NotNull List<@NotNull ConfigEnchantmentEntry> enchants;
        try {
            enchants = ConfigEnchantmentEntry.config(getConfig().get("enchantments"));
        }
        catch (final @NotNull Exception exception) {
            getLogger().log(Level.SEVERE, "Failed to load config", exception);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        allConfigEnchantment = enchants.stream()
                .filter(c -> c.name.equalsIgnoreCase("ALL")).findFirst().map(ConfigEnchantmentEntry.AllConfigEnchantmentEntry::from).orElse(null);
        configEnchantments = enchants.stream().filter(c -> !c.name.equalsIgnoreCase("ALL")).collect(Collectors.toList());
    }

    @Override
    public void onEnable() {
        Objects.requireNonNull(getCommand("enchantbookplus")).setExecutor(new MainCommand());

        registerEvents();
        saveDefaultConfig();
        reload();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
