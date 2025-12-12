package pro.cloudnode.smp.enchantbookplus;

import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;

public final class Permissions {
    public static @NotNull String enchant(final @NotNull Enchantment enchantment) {
        return "enchantbookplus.enchant." + enchantment.getKey().getKey();
    }

    public static @NotNull String RELOAD = "enchantbookplus.reload";

    public static void init() {
        final @NotNull PluginManager pm = EnchantBookPlus.getInstance().getServer().getPluginManager();
        pm.addPermission(new Permission(
                RELOAD,
                "Reload plugin config using `/enchantbookplus reload`",
                PermissionDefault.OP
        ));
        for (Enchantment enchantment : Registry.ENCHANTMENT)
            pm.addPermission(new Permission(
                    enchant(enchantment),
                    "Allow enchanting " + enchantment.getKey() + "above the vanilla level, as configured in the plugin",
                    PermissionDefault.TRUE
            ));
    }
}
