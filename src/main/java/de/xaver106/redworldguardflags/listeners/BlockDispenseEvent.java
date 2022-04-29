package de.xaver106.redworldguardflags.listeners;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import de.tr7zw.nbtapi.NBTItem;
import de.xaver106.redworldguardflags.RedWorldguardFlags;
import org.bukkit.block.Dispenser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class BlockDispenseEvent implements Listener {

    private final HashMap<Class<?>, HashMap<String, Flag<?>>> flags;
    private final RedWorldguardFlags plugin;

    public BlockDispenseEvent(RedWorldguardFlags plugin){

        this.flags = plugin.getFlags();
        this.plugin = plugin;

    }

    @EventHandler
    public void onBlockDispenseEvent(org.bukkit.event.block.BlockDispenseEvent event) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(event.getBlock().getLocation()));

        // Testing for the Dispense NBT SpawnEggs Flag
        if (!set.testState(null,(StateFlag) flags.get(StateFlag.class).get("dispense-nbt-spawneggs"))) {
            NBTItem nbtItem = new NBTItem(event.getItem());
            if (nbtItem.hasKey("EntityTag") && event.getItem().getType().toString().contains("SPAWN_EGG")) {
                event.setCancelled(true);
                Dispenser dispenser = (Dispenser) event.getBlock().getState();

                new BukkitRunnable() {
                    public void run() {
                        dispenser.getSnapshotInventory().remove(event.getItem().getType());
                        dispenser.update();
                    }
                }.runTaskLater(plugin, 1);
            }
        }

    }

}
