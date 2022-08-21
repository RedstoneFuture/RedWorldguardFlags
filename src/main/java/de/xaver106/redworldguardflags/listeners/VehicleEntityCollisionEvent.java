package de.xaver106.redworldguardflags.listeners;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import de.xaver106.redworldguardflags.RedWorldguardFlags;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class VehicleEntityCollisionEvent implements Listener {

    private final RedWorldguardFlags plugin;

    public VehicleEntityCollisionEvent(RedWorldguardFlags plugin) {

        this.plugin = plugin;

    }

    @EventHandler
    public void onVehicleEntityCollision(org.bukkit.event.vehicle.VehicleEntityCollisionEvent event) {
        // WorldGuard Query
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(event.getEntity().getLocation()));

        // Check if the flag applies and if it is set to deny
        if (!set.testState(null, (StateFlag) plugin.getFlags().get(StateFlag.class).get("vehicle-entity-collision"))) {

            // Check if the collided entity is a player or a mob
            if (event.getEntity() instanceof Player || event.getEntity() instanceof Mob) {
                event.setCollisionCancelled(true);
                event.setCancelled(true);
            }
        }

    }

}
