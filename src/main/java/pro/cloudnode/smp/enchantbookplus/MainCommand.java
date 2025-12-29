package pro.cloudnode.smp.enchantbookplus;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class MainCommand implements CommandExecutor, TabCompleter {
    private final @NotNull EnchantBookPlus plugin;

    public MainCommand(final @NotNull EnchantBookPlus plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(
            final @NotNull CommandSender sender,
            final @NotNull Command command,
            final @NotNull String label,
            final @NotNull String @NotNull [] args
    ) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload"))
            return reload(sender, command);

        return overview(sender);
    }

    @Override
    public @NotNull List<String> onTabComplete(
            final @NotNull CommandSender sender,
            final @NotNull Command command,
            final @NotNull String label,
            final @NotNull String @NotNull [] args
    ) {
        if (sender.hasPermission(Permissions.RELOAD))
            return List.of("reload");

        return List.of();
    }

    /**
     * Plugin overview
     */
    @SuppressWarnings("SameReturnValue")
    public boolean overview(final @NotNull CommandSender sender) {
        PluginDescriptionFile description = plugin.getDescription();
        sender.sendMessage("§а" + description.getName() + " §fv" + description.getVersion() + " by §7"
                + String.join(", ", description.getAuthors()));

        return true;
    }

    /**
     * Reload plugin configuration
     */
    @SuppressWarnings("SameReturnValue")
    public boolean reload(final @NotNull CommandSender sender, final @NotNull Command command) {
        if (!sender.hasPermission(Permissions.RELOAD))
            return overview(sender);

        plugin.reload();

        sender.sendMessage("§a(!) Plugin configuration reloaded.");

        return true;
    }
}
