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
     * Register event listeners.
     */
    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new PrepareAnvil(this), this);
    }

    /**
     * Config enchantments cache
     */
    private @NotNull List<@NotNull ConfigEnchantmentEntry> configEnchantments = new ArrayList<>();

    /**
     * Config enchantments cache
     */
    @NotNull List<@NotNull ConfigEnchantmentEntry> getConfigEnchantments() {
        return configEnchantments;
    }

    /**
     * "ALL" enchantment cache
     */
    private @Nullable ConfigEnchantmentEntry.AllConfigEnchantmentEntry allConfigEnchantment;

    /**
     * "ALL" enchantment cache
     */
    public @NotNull Optional<ConfigEnchantmentEntry.AllConfigEnchantmentEntry> getAllConfigEnchantment() {
        return Optional.ofNullable(allConfigEnchantment);
    }

    /**
     * Get enchantment from cache
     *
     * @param enchantment The Minecraft enchantment
     */
    public @NotNull Optional<@NotNull ConfigEnchantmentEntry> getConfigEnchantment(final @NotNull Enchantment enchantment) {
        final Optional<ConfigEnchantmentEntry> entry = getConfigEnchantments().stream()
                .filter(c -> c.isEnchantment(enchantment))
                .findFirst();

        return entry.isEmpty()
                ? getAllConfigEnchantment().map(a -> a.enchant(enchantment))
                : entry;
    }

    /**
     * Reload config
     */
    void reload() {
        reloadConfig();

        final List<ConfigEnchantmentEntry> enchants;

        try {
            enchants = ConfigEnchantmentEntry.config(getConfig().get("enchantments"));
        }
        catch (final IllegalArgumentException | IndexOutOfBoundsException | ClassCastException exception) {
            getLogger().log(Level.SEVERE, "Failed to load config", exception);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        allConfigEnchantment = enchants.stream()
                .filter(c -> c.name.equalsIgnoreCase("ALL"))
                .findFirst()
                .map(ConfigEnchantmentEntry.AllConfigEnchantmentEntry::from)
                .orElse(null);

        configEnchantments = enchants.stream()
                .filter(c -> !c.name.equalsIgnoreCase("ALL"))
                .collect(Collectors.toList());
    }

    @Override
    public void onEnable() {
        Permissions.init(this);
        Objects.requireNonNull(getCommand("enchantbookplus")).setExecutor(new MainCommand(this));

        registerEvents();
        saveDefaultConfig();
        reload();
    }

    @Override
    public void onDisable() {
        allConfigEnchantment = null;
        configEnchantments.clear();
    }
}
