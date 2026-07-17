package pro.cloudnode.smp.enchantbookplus;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.jspecify.annotations.NullMarked;

import java.util.function.Consumer;

@NullMarked
public final class Permissions {
    public static final String RELOAD = "enchantbookplus.reload";

    public static String enchant(final Enchantment enchantment) {
        if (enchantment.getKey().getNamespace().equals(NamespacedKey.MINECRAFT)) {
            return "enchantbookplus.enchant." + enchantment.getKey().getKey();
        }

        return "enchantbookplus.enchant." + enchantment.getKey().getNamespace() + "." + enchantment.getKey().getKey();
    }

    public static void register(final EnchantBookPlus plugin) {
        final PluginManager pm = plugin.getServer().getPluginManager();
        forEach(pm::addPermission);
    }

    public static void unregister(final EnchantBookPlus plugin) {
        final PluginManager pm = plugin.getServer().getPluginManager();
        forEach(pm::removePermission);
    }

    private static void forEach(final Consumer<Permission> consumer) {
        consumer.accept(new Permission(
                RELOAD,
                "Reload plugin config using ‘/enchantbookplus reload’",
                PermissionDefault.OP
        ));

        for (final Enchantment enchantment : Registry.ENCHANTMENT) {
            consumer.accept(new Permission(
                    enchant(enchantment),
                    "Allow enchanting " + enchantment.getKey()
                            + " above the vanilla level, as configured in the plugin",
                    PermissionDefault.TRUE
            ));
        }
    }
}
