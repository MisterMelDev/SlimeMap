package tech.mistermel.slimemap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.map.MapView.Scale;
import org.bukkit.persistence.PersistentDataType;

public class PlayerListener implements Listener {

	private NamespacedKey slimeMapKey;
	
	public PlayerListener() {
		this.slimeMapKey = new NamespacedKey(SlimeMap.instance(), "is_slime_map");
	}
	
	public boolean isSlimeMap(ItemStack item) {
		return item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().get(slimeMapKey, PersistentDataType.BYTE) == 1;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		e.getPlayer().discoverRecipe(SlimeMap.instance().getRecipeKey());
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if(e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		
		if(SlimeMap.instance().getEmptyMapItem().isSimilar(e.getItem())) {
			e.setCancelled(true);
			e.getItem().setAmount(e.getItem().getAmount() - 1);
			
			MapView view = Bukkit.createMap(e.getPlayer().getWorld());
			view.setUnlimitedTracking(true);
			view.setTrackingPosition(true);
			view.setScale(Scale.CLOSE); // For some reason, this is FAR by default
			view.getRenderers().clear();
			view.addRenderer(new SlimeRenderer());
			
			Chunk playerChunk = e.getPlayer().getLocation().getChunk();
			view.setCenterX(playerChunk.getX() * 16);
			view.setCenterZ(playerChunk.getZ() * 16);
			
			ItemStack item = new ItemStack(Material.FILLED_MAP);
			MapMeta meta = (MapMeta) item.getItemMeta();
			meta.setDisplayName(SlimeMap.ITEM_NAME);
			meta.setLore(SlimeMap.ITEM_LORE);
			meta.setColor(Color.GREEN);
			meta.getPersistentDataContainer().set(slimeMapKey, PersistentDataType.BYTE, (byte) 1);
			meta.setMapView(view);
			item.setItemMeta(meta);
			
			Inventory inv = e.getPlayer().getInventory();
			inv.setItem(inv.firstEmpty(), item);
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if(e.getAction() == InventoryAction.PLACE_ALL || e.getAction() == InventoryAction.PLACE_ONE || e.getAction() == InventoryAction.PLACE_SOME) {
			if(e.getClickedInventory() != null && e.getClickedInventory().getType() == InventoryType.CARTOGRAPHY && this.isSlimeMap(e.getCursor())) {
				e.setCancelled(true);
				e.getWhoClicked().sendMessage(ChatColor.RED + "Slime maps cannot be modified in cartography tables.");
			}
		} else if(e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
			if(e.getClickedInventory() != null && e.getInventory().getType() == InventoryType.CARTOGRAPHY && this.isSlimeMap(e.getCurrentItem())) {
				e.setCancelled(true);
				e.getWhoClicked().sendMessage(ChatColor.RED + "Slime maps cannot be modified in cartography tables.");
			}
		}
	}
	
}
