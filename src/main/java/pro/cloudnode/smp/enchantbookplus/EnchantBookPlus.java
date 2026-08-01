package pro.cloudnode.smp.enchantbookplus;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import pro.cloudnode.smp.enchantbookplus.event.PrepareAnvil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;

@NullMarked
public final class EnchantBookPlus extends JavaPlugin {
    /**
     * Config enchantments cache
     */
    private List<ConfigEnchantmentEntry> configEnchantments = new ArrayList<>();
    /**
     * "ALL" enchantment cache
     */
    private ConfigEnchantmentEntry.@Nullable AllConfigEnchantmentEntry allConfigEnchantment;

    /**
     * Register event listeners.
     */
    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new PrepareAnvil(this), this);
    }

    /**
     * Config enchantments cache
     */
    private List<ConfigEnchantmentEntry> getConfigEnchantments() {
        return configEnchantments;
    }

    /**
     * "ALL" enchantment cache
     */
    public Optional<ConfigEnchantmentEntry.AllConfigEnchantmentEntry> getAllConfigEnchantment() {
        return Optional.ofNullable(allConfigEnchantment);
    }

    /**
     * Get enchantment from cache
     *
     * @param enchantment The Minecraft enchantment
     */
    public Optional<ConfigEnchantmentEntry> getConfigEnchantment(final Enchantment enchantment) {
        final Optional<ConfigEnchantmentEntry> entry = getConfigEnchantments().stream()
                .filter(c -> c.isEnchantment(enchantment))
                .findFirst();

        if (entry.isPresent()) {
            return entry;
        }

        // If "ALL" entry exists, create a specific entry for this enchantment
        final Optional<ConfigEnchantmentEntry.AllConfigEnchantmentEntry> allEntry = getAllConfigEnchantment();
        if (allEntry.isPresent()) {
            final ConfigEnchantmentEntry specificEntry = allEntry.get().enchant(enchantment);
            // If the enchantment was skipped (null), return empty
            return Optional.ofNullable(specificEntry);
        }

        return Optional.empty();
    }

    /**
     * Reload config
     */
    void reload() {
        reloadConfig();

        // Load the allow-custom-enchantments setting from config
        final boolean allowCustom = getConfig().getBoolean("allow-custom-enchantments", true);
        ConfigEnchantmentEntry.setAllowCustomEnchantments(allowCustom);

        final List<ConfigEnchantmentEntry> enchants;

        try {
            enchants = ConfigEnchantmentEntry.config(getConfig().get("enchantments"));
        } catch (final IllegalArgumentException | IndexOutOfBoundsException | ClassCastException exception) {
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