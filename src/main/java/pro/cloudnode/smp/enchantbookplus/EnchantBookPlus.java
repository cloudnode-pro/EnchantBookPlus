package pro.cloudnode.smp.enchantbookplus;

import org.bukkit.plugin.java.JavaPlugin;
import pro.cloudnode.smp.enchantbookplus.event.PrepareAnvil;

public final class EnchantBookPlus extends JavaPlugin {
    /**
     * Register event listeners.
     */
    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new PrepareAnvil(), this);
    }

    @Override
    public void onEnable() {
        registerEvents();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
