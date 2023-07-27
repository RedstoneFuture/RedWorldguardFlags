package de.xaver106.redworldguardflags.listeners;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import de.xaver106.redworldguardflags.RedWorldguardFlags;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.Lectern;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.*;

public class LecternBookPlaceEvent implements Listener {

    private final RedWorldguardFlags plugin;

    /**
     * Players can open books at the lectern when the "use" flag (or "interact" flag) is enabled. 
     * They can also take the books if the "chest-access" flag is enabled (or activated "build" flag). 
     * But to place a book on an empty lectern, natively the player must have build rights 
     * (or activated "build" or "block-place" flag). For this purpose, there is now the new 
     * "lectern-book-place" flag that has been implemented here.
     */
    public LecternBookPlaceEvent(RedWorldguardFlags plugin) {

        this.plugin = plugin;

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(org.bukkit.event.player.PlayerInteractEvent event) {
        // WorldGuard Query
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(event.getPlayer().getLocation()));

        // Check if the flag applies and if it is set to deny
        if (set.testState(null, (StateFlag) plugin.getFlags().get(StateFlag.class).get("lectern-book-place"))) {

            PlayerInventory playerInventory = event.getPlayer().getInventory();
            ItemStack item = playerInventory.getItemInMainHand();
            if ((item.getType() != Material.WRITABLE_BOOK) && (item.getType() != Material.WRITTEN_BOOK)) return;

            Block block = event.getClickedBlock();
            if (block == null) return;
            if (block.getType() != Material.LECTERN) return;

            if (!(block.getBlockData() instanceof Lectern lecternData)) return;
            if (lecternData.hasBook()) return;

            BlockState state = block.getState();
            if (!(state instanceof InventoryHolder holder)) return;

            Inventory inventory = holder.getInventory();
            if (!(inventory instanceof LecternInventory lecternInventory)) return;

            // Only accept book placing in survival or creative mode. This is the vanilla behavior.
            if (!(event.getPlayer().getGameMode() == GameMode.SURVIVAL || event.getPlayer().getGameMode() == GameMode.CREATIVE)) return;

            lecternInventory.setBook(item);

            if (event.getPlayer().getGameMode() == GameMode.SURVIVAL) playerInventory.setItemInMainHand(new ItemStack(Material.AIR));

            event.setCancelled(true);
        }

    }

}
