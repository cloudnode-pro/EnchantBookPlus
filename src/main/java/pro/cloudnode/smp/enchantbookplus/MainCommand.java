package pro.cloudnode.smp.enchantbookplus;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public final class MainCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload"))
            return reload(sender, command);
        return overview(sender);
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (sender.hasPermission(Permissions.RELOAD))
            return List.of("reload");
        return List.of();
    }

    /**
     * Plugin overview
     */
    public static boolean overview(final @NotNull CommandSender sender) {
        final @NotNull EnchantBookPlus plugin = EnchantBookPlus.getInstance();
        sender.sendMessage(MiniMessage.miniMessage()
                .deserialize("<green><name></green> <white>v<version> by</white> <gray><author></gray>", Placeholder.unparsed("name", plugin
                        .getPluginMeta().getName()), Placeholder.unparsed("version", plugin.getPluginMeta()
                        .getVersion()), Placeholder.unparsed("author", String.join(", ", plugin.getPluginMeta()
                        .getAuthors()))));

        return true;
    }

    /**
     * Reload plugin configuration
     */
    public static boolean reload(final @NotNull CommandSender sender, final @NotNull Command command) {
        if (!sender.hasPermission(Permissions.RELOAD)) {
            sender.sendMessage(Optional.ofNullable(command.permissionMessage()).orElse(sender.getServer().permissionMessage()));
            return true;
        }
        EnchantBookPlus.getInstance().reload();
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>(!) Plugin configuration reloaded."));
        return true;
    }
}
