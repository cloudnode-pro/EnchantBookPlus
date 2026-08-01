package pro.cloudnode.smp.enchantbookplus;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.logging.Level;
import java.util.stream.Collectors;

@NullMarked
public class ConfigEnchantmentEntry {
    /**
     * Name of the enchantment.
     */
    public final String name;

    /**
     * Maximum level of the enchantment.
     */
    protected final @Nullable Integer maxLevel;

    /**
     * Max level relative
     */
    protected final boolean maxLevelRelative;

    /**
     * Cost of the enchantment.
     */
    protected final int cost;

    /**
     * Multiply cost by level.
     */
    protected final boolean multiplyCostByLevel;

    /**
     * Whether to allow custom enchantments from other plugins.
     * Set this from the main plugin class.
     */
    private static boolean allowCustomEnchantments = true;

    /**
     * Set whether custom enchantments are allowed.
     *
     * @param allow true to allow custom enchantments, false to only use vanilla enchantments
     */
    public static void setAllowCustomEnchantments(final boolean allow) {
        allowCustomEnchantments = allow;
    }

    /**
     * @param name Name of the enchantment.
     * @param maxLevel Maximum level of the enchantment.
     * @param maxLevelRelative Max level relative
     * @param cost Cost of the enchantment.
     * @param multiplyCostByLevel Multiply cost by level.
     */
    public ConfigEnchantmentEntry(
            final String name,
            final @Nullable Integer maxLevel,
            final boolean maxLevelRelative,
            final int cost,
            final boolean multiplyCostByLevel
    ) {
        this.name = name;
        this.maxLevel = maxLevel;
        this.maxLevelRelative = maxLevelRelative;
        this.cost = cost;
        this.multiplyCostByLevel = multiplyCostByLevel;
    }

    /**
     * From config object
     *
     * @param configValue Config object
     */
    public static ConfigEnchantmentEntry configValue(
            final Map<String, Object> configValue
    ) throws NumberFormatException, IndexOutOfBoundsException, ClassCastException {
        final String name = (String) Objects.requireNonNull(configValue.get("name"));

        final Integer maxLevel;

        final boolean maxLevelRelative;

        if (!configValue.containsKey("max-level")) {
            maxLevel = null;
            maxLevelRelative = false;
        } else {
            if (!(configValue.get("max-level") instanceof final String string)) {
                maxLevel = (Integer) configValue.get("max-level");
                maxLevelRelative = false;
            } else {
                if (string.startsWith("+")) {
                    maxLevel = Integer.parseInt(string.substring(1));
                    maxLevelRelative = true;
                } else {
                    maxLevel = Integer.parseInt(string);
                    maxLevelRelative = false;
                }
            }
        }

        if (!configValue.containsKey("cost")) {
            return new ConfigEnchantmentEntry(name, maxLevel, maxLevelRelative, 0, false);
        }

        if (!(configValue.get("cost") instanceof final String costString)) {
            return new ConfigEnchantmentEntry(
                    name,
                    maxLevel,
                    maxLevelRelative,
                    (Integer) configValue.get("cost"),
                    false
            );
        }

        if (costString.startsWith("*")) {
            return new ConfigEnchantmentEntry(
                    name,
                    maxLevel,
                    maxLevelRelative,
                    Integer.parseInt(costString.substring(1)),
                    true
            );
        }

        return new ConfigEnchantmentEntry(name, maxLevel, maxLevelRelative, Integer.parseInt(costString), false);
    }

    /**
     * From config object array
     *
     * @param configValue Config object array
     */
    public static List<ConfigEnchantmentEntry> configArray(final ArrayList<Map<String, Object>> configValue)
            throws NumberFormatException, IndexOutOfBoundsException, ClassCastException {
        return configValue.stream().map(ConfigEnchantmentEntry::configValue).collect(Collectors.toList());
    }

    /**
     * Check if valid config object
     *
     * @param configValue Config object
     */
    private static boolean isValidConfigValue(final @Nullable Object configValue) {
        if (configValue == null) {
            return false;
        }

        if (!(configValue instanceof final ArrayList<?> arrayList)) {
            return false;
        }

        for (final Object object : arrayList) {
            if (!(object instanceof final Map<?, ?> hashMap)) {
                return false;
            }

            if (!hashMap.containsKey("name")) {
                return false;
            }

            if (!(hashMap.get("name") instanceof String)) {
                return false;
            }

            if (hashMap.containsKey("max-level") && !(hashMap.get("max-level") instanceof String) && !(hashMap.get(
                    "max-level") instanceof Integer)) {
                return false;
            }

            if (hashMap.containsKey("cost") && !(hashMap.get("cost") instanceof String)
                    && !(hashMap.get("cost") instanceof Integer)) {
                return false;
            }
        }

        return true;
    }

    /**
     * From config
     *
     * @param configValue Config object
     */
    public static List<ConfigEnchantmentEntry> config(final @Nullable Object configValue)
            throws IllegalArgumentException, IndexOutOfBoundsException, ClassCastException {
        if (!isValidConfigValue(configValue)) {
            throw new IllegalArgumentException("Invalid config value");
        }

        //noinspection unchecked
        return configArray((ArrayList<Map<String, Object>>) configValue);
    }

    /**
     * Maximum level of the enchantment.
     */
    public final OptionalInt getMaxLevel() {
        if (maxLevel == null) {
            return OptionalInt.empty();
        }

        final Enchantment enchantment = getEnchantment();
        if (enchantment == null) {
            return OptionalInt.empty();
        }

        if (maxLevelRelative) {
            return OptionalInt.of(enchantment.getMaxLevel() + maxLevel);
        }

        return OptionalInt.of(maxLevel);
    }

    /**
     * Cost of the enchantment.
     */
    public final int getCost() {
        return cost;
    }

    /**
     * Multiply cost by level.
     */
    public final boolean getMultiplyCostByLevel() {
        return multiplyCostByLevel;
    }

    /**
     * Get enchantment
     */
    public final @Nullable Enchantment getEnchantment() {
        final Enchantment enchantment = Registry.ENCHANTMENT.get(NamespacedKey.minecraft(name));
        if (enchantment == null) {
            java.util.logging.Logger.getLogger("EnchantBookPlus").warning("Enchantment '" + name + "' not found in registry");
        }
        return enchantment;
    }

    /**
     * Is enchantment
     *
     * @param enchantment The enchantment
     */
    public final boolean isEnchantment(final Enchantment enchantment) {
        return name.equalsIgnoreCase(enchantment.getKey().getKey());
    }

    public static final class AllConfigEnchantmentEntry extends ConfigEnchantmentEntry {
        private AllConfigEnchantmentEntry(
                final @Nullable Integer maxLevel,
                final boolean maxLevelRelative,
                final int cost,
                final boolean multiplyCostByLevel
        ) {
            super("ALL", maxLevel, maxLevelRelative, cost, multiplyCostByLevel);
        }

        public static AllConfigEnchantmentEntry from(final ConfigEnchantmentEntry configEnchantmentEntry) {
            return new AllConfigEnchantmentEntry(
                    configEnchantmentEntry.maxLevel,
                    configEnchantmentEntry.maxLevelRelative,
                    configEnchantmentEntry.cost,
                    configEnchantmentEntry.multiplyCostByLevel
            );
        }

        /**
         * Create a ConfigEnchantmentEntry for a specific enchantment.
         * Returns null if the enchantment is custom and custom enchantments are disabled.
         *
         * @param enchantment The enchantment to create an entry for
         * @return A new ConfigEnchantmentEntry, or null if skipped
         */
        public @Nullable ConfigEnchantmentEntry enchant(final Enchantment enchantment) {
            // Skip custom enchantments if they are not allowed
            if (!allowCustomEnchantments && !enchantment.getKey().getNamespace().equals(NamespacedKey.MINECRAFT)) {
                return null;
            }
            return new ConfigEnchantmentEntry(
                    enchantment.getKey().getKey(),
                    this.maxLevel,
                    maxLevelRelative,
                    cost,
                    multiplyCostByLevel
            );
        }
    }
}