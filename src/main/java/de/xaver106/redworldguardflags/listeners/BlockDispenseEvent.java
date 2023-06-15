package de.xaver106.redworldguardflags.listeners;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import de.tr7zw.nbtapi.NBTItem;
import de.xaver106.redworldguardflags.RedWorldguardFlags;
import org.bukkit.block.Dispenser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class BlockDispenseEvent implements Listener {

    private final RedWorldguardFlags plugin;

    public BlockDispenseEvent(RedWorldguardFlags plugin) {

        this.plugin = plugin;

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockDispense(org.bukkit.event.block.BlockDispenseEvent event) {
        // WorldGuard Query
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(event.getBlock().getLocation()));

        // Check if the flag applies and if it is set to deny
        if (!set.testState(null, (StateFlag) plugin.getFlags().get(StateFlag.class).get("dispense-nbt-spawneggs"))) {

            // Check if the item is a SpawnEgg and has the EntityTag NBT tag
            NBTItem nbtItem = new NBTItem(event.getItem());
            if (nbtItem.hasKey("EntityTag") && event.getItem().getType().toString().contains("SPAWN_EGG")) {
                event.setCancelled(true);

                // Get BlockState to delete all illegal items inside
                Dispenser dispenser = (Dispenser) event.getBlock().getState();
                new BukkitRunnable() {  // Create runnable to delete all items after a tick (necessary)
                    public void run() {
                        dispenser.getSnapshotInventory().remove(event.getItem().getType());
                        dispenser.update();
                    }
                }.runTaskLater(plugin, 1);
            }
        }

    }

}
