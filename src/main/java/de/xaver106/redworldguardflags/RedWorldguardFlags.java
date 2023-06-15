package de.xaver106.redworldguardflags;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import de.xaver106.redworldguardflags.listeners.*;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.logging.Level;

public final class RedWorldguardFlags extends JavaPlugin implements Listener {

    private HashMap<Class<?>, HashMap<String, Flag<?>>> flags;

    @Override
    public void onEnable() {

        // register Events with Bukkit
        getServer().getPluginManager().registerEvents(new BlockDispenseEvent(this), this);
        getServer().getPluginManager().registerEvents(new VehicleEntityCollisionEvent(this), this);
        getServer().getPluginManager().registerEvents(new LecternBookPlaceEvent(this), this);
        getServer().getPluginManager().registerEvents(new CreatureSpawnEvent(this), this);
    }

    @Override
    public void onLoad() {

        // register WorldGuard flags
        this.register_flag(new StateFlag("dispense-nbt-spawneggs", true));
        this.register_flag(new StateFlag("vehicle-entity-collision", true));
        this.register_flag(new StateFlag("lectern-book-place", true));
        this.register_flag(new StateFlag("spawnegg-use", true));
        this.register_flag(new StateFlag("spawnegg-dispense", true));
    }

    /**
     * Registering a Flag with WorldGuard and saving it inside the HashMap
     *
     * @param flag The new Flag to register
     */
    @SuppressWarnings("DuplicatedCode")
    private void register_flag(Flag<?> flag) {
        // Code modified from: https://worldguard.enginehub.org/en/latest/developer/regions/custom-flags/#registering-new-flags
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        if (this.flags == null) {
            this.flags = new HashMap<>();
        }
        try {
            registry.register(flag);
            if (flags.containsKey(flag.getClass())) {
                this.flags.get(flag.getClass()).put(flag.getName(), flag);
            } else {
                this.flags.put(flag.getClass(), new HashMap<>());
                this.flags.get(flag.getClass()).put(flag.getName(), flag);
            }
        } catch (FlagConflictException e) {
            Flag<?> existing = registry.get(flag.getName());
            if (existing instanceof StateFlag) {
                if (flags.containsKey(flag.getClass())) {
                    this.flags.get(flag.getClass()).put(flag.getName(), flag);
                } else {
                    this.flags.put(flag.getClass(), new HashMap<>());
                    this.flags.get(flag.getClass()).put(flag.getName(), flag);
                }
            } else {
                this.getLogger().log(Level.SEVERE, "Flag: " + flag.getName() + " could not be registered!");
            }
        }
    }

    /**
     * Gets flags HashMap.
     *
     * @return the flags HashMap
     */
    public HashMap<Class<?>, HashMap<String, Flag<?>>> getFlags() {
        return flags;
    }
}
