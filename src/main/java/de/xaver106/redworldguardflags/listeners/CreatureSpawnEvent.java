package de.xaver106.redworldguardflags.listeners;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import de.xaver106.redworldguardflags.RedWorldguardFlags;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class CreatureSpawnEvent implements Listener {

    private final RedWorldguardFlags plugin;

    /**
     * Native entities can be spawned if the "mob-spawning" flag is enabled. The flags implemented here,
     * override the restriction for:
     *
     * (A) spawning entities by manually using spawn eggs and
     * (B) spawning entities by dispensing spawn eggs.
     */
    public CreatureSpawnEvent(RedWorldguardFlags plugin) {

        this.plugin = plugin;

    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onCreatureSpawn(org.bukkit.event.entity.CreatureSpawnEvent event) {

        if ((event.getSpawnReason() != org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) &&
                (event.getSpawnReason() != org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.DISPENSE_EGG)) return;
        
        // WorldGuard Query
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(event.getLocation()));

        // Spawn-egg using:
        if (event.getSpawnReason() == org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) {
            // Check if the flag applies and if it is set to deny
            if (set.testState(null, (StateFlag) plugin.getFlags().get(StateFlag.class).get("spawnegg-use"))) {
                event.setCancelled(false);
                return;
            } else {
                event.setCancelled(true);
                return;
            }
        }

        // Spawn-egg dispensing:
        if (event.getSpawnReason() == org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.DISPENSE_EGG) {
            // Check if the flag applies and if it is set to deny
            if (set.testState(null, (StateFlag) plugin.getFlags().get(StateFlag.class).get("spawnegg-dispense"))) {
                event.setCancelled(false);
                return;
            } else {
                event.setCancelled(true);
                return;
            }
        }

    }

}
