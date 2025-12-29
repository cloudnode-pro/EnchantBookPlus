package pro.cloudnode.smp.enchantbookplus;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class ConfigEnchantmentEntry {
    /**
     * Name of the enchantment.
     */
    public final @NotNull String name;

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
     * Maximum level of the enchantment.
     */
    public final @NotNull Optional<Integer> getMaxLevel() {
        if (Optional.ofNullable(maxLevel).isEmpty())
            return Optional.empty();

        if (maxLevelRelative)
            return Optional.of(getEnchantment().getMaxLevel() + maxLevel);

        return Optional.of(maxLevel);
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
    public final Enchantment getEnchantment() {
        return Registry.ENCHANTMENT.get(NamespacedKey.minecraft(name));
    }

    /**
     * Is enchantment
     *
     * @param enchantment The enchantment
     */
    public final boolean isEnchantment(final @NotNull Enchantment enchantment) {
        return name.equalsIgnoreCase(enchantment.getKey().getKey());
    }

    /**
     * @param name                Name of the enchantment.
     * @param maxLevel            Maximum level of the enchantment.
     * @param maxLevelRelative    Max level relative
     * @param cost                Cost of the enchantment.
     * @param multiplyCostByLevel Multiply cost by level.
     */
    public ConfigEnchantmentEntry(
            final @NotNull String name,
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
    public static @NotNull ConfigEnchantmentEntry configValue(
            final @NotNull Map<@NotNull String, @NotNull Object> configValue
    ) throws NumberFormatException, IndexOutOfBoundsException, ClassCastException {
        final String name = (String) Objects.requireNonNull(configValue.get("name"));

        final @Nullable Integer maxLevel;

        final boolean maxLevelRelative;

        if (!configValue.containsKey("max-level")) {
            maxLevel = null;
            maxLevelRelative = false;
        }

        else {
            if (!(configValue.get("max-level") instanceof final String string)) {
                maxLevel = (Integer) configValue.get("max-level");
                maxLevelRelative = false;
            }

            else {
                if (string.startsWith("+")) {
                    maxLevel = Integer.parseInt(string.substring(1));
                    maxLevelRelative = true;
                }
                else {
                    maxLevel = Integer.parseInt(string);
                    maxLevelRelative = false;
                }
            }
        }

        if (!configValue.containsKey("cost"))
            return new ConfigEnchantmentEntry(name, maxLevel, maxLevelRelative, 0, false);

        if (!(configValue.get("cost") instanceof final @NotNull String costString))
            return new ConfigEnchantmentEntry(
                    name,
                    maxLevel,
                    maxLevelRelative,
                    (Integer) configValue.get("cost"),
                    false
            );

        if (costString.startsWith("*"))
            return new ConfigEnchantmentEntry(
                    name,
                    maxLevel,
                    maxLevelRelative,
                    Integer.parseInt(costString.substring(1)),
                    true
            );

        return new ConfigEnchantmentEntry(
                name,
                maxLevel,
                maxLevelRelative,
                Integer.parseInt(costString),
                false
        );
    }


    /**
     * From config object array
     *
     * @param configValue Config object array
     */
    public static @NotNull List<@NotNull ConfigEnchantmentEntry> configArray(
            final @NotNull ArrayList<@NotNull Map<@NotNull String, @NotNull Object>> configValue
    ) throws NumberFormatException, IndexOutOfBoundsException, ClassCastException {
        return configValue.stream().map(ConfigEnchantmentEntry::configValue).collect(Collectors.toList());
    }

    /**
     * Check if valid config object
     *
     * @param configValue Config object
     */
    private static boolean isValidConfigValue(final @Nullable Object configValue) {
        if (configValue == null)
            return false;

        if (!(configValue instanceof final ArrayList<?> arrayList))
            return false;

        for (final Object object : arrayList) {
            if (!(object instanceof final Map<?, ?> hashMap))
                return false;

            if (!hashMap.containsKey("name"))
                return false;

            if (!(hashMap.get("name") instanceof String))
                return false;

            if (hashMap.containsKey("max-level") &&
                    !(hashMap.get("max-level") instanceof String) &&
                    !(hashMap.get("max-level") instanceof Integer))
                return false;

            if (hashMap.containsKey("cost") &&
                    !(hashMap.get("cost") instanceof String) &&
                    !(hashMap.get("cost") instanceof Integer))
                return false;
        }

        return true;
    }

    /**
     * From config
     *
     * @param configValue Config object
     */
    public static @NotNull List<@NotNull ConfigEnchantmentEntry> config(
            final @Nullable Object configValue
    ) throws IllegalArgumentException, IndexOutOfBoundsException, ClassCastException {
        if (!isValidConfigValue(configValue))
            throw new IllegalArgumentException("Invalid config value");

        //noinspection unchecked
        return configArray((ArrayList<Map<String, Object>>) configValue);
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

        public static @NotNull AllConfigEnchantmentEntry from(
                final @NotNull ConfigEnchantmentEntry configEnchantmentEntry
        ) {
            return new AllConfigEnchantmentEntry(
                    configEnchantmentEntry.maxLevel,
                    configEnchantmentEntry.maxLevelRelative,
                    configEnchantmentEntry.cost,
                    configEnchantmentEntry.multiplyCostByLevel
            );
        }

        public @NotNull ConfigEnchantmentEntry enchant(final @NotNull Enchantment enchantment) {
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
