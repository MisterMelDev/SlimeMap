package tech.mistermel.slimemap;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class SlimeMap extends JavaPlugin {

	public static final String ITEM_NAME = ChatColor.GOLD + "Slime Map";
	public static final List<String> ITEM_LORE = Arrays.asList(ChatColor.WHITE + "Functions like a regular map, exept", ChatColor.WHITE + "it shows you where slime chunks are located.");
	
	private static SlimeMap instance;
	
	private NamespacedKey recipeKey;
	private ItemStack emptyMapItem;
	private SlimeRenderer slimeRenderer;
	
	@Override
	public void onEnable() {
		instance = this;
		
		this.slimeRenderer = new SlimeRenderer();
		this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		
		this.createItem();
		this.createRecipe();
	}
	
	private void createRecipe() {
		this.recipeKey = new NamespacedKey(this, "slime_map_recipe");
		ShapedRecipe recipe = new ShapedRecipe(recipeKey, emptyMapItem);
		
		recipe.shape(" S ", "SMS", " S ");
		recipe.setIngredient('S', Material.SLIME_BALL);
		recipe.setIngredient('M', Material.MAP);
	
		Bukkit.addRecipe(recipe);
	}
	
	@SuppressWarnings("deprecation")
	private void createItem() {
		this.emptyMapItem = new ItemStack(Material.LEGACY_EMPTY_MAP);
		
		ItemMeta meta = emptyMapItem.getItemMeta();
		meta.setDisplayName(ITEM_NAME);
		meta.setLore(ITEM_LORE);
		emptyMapItem.setItemMeta(meta);
	}
	
	public ItemStack getEmptyMapItem() {
		return emptyMapItem;
	}
	
	public SlimeRenderer getSlimeRenderer() {
		return slimeRenderer;
	}
	
	public NamespacedKey getRecipeKey() {
		return recipeKey;
	}
	
	public boolean isSlimeChunk(World world, int chunkX, int chunkZ) {
		Random random = new Random(
                world.getSeed() +
                (int) (chunkX * chunkX * 0x4c1906) +
                (int) (chunkX * 0x5ac0db) +
                (int) (chunkZ * chunkZ) * 0x4307a7L +
                (int) (chunkZ * 0x5f24f) ^ 0x3ad8025fL
        );
		
		return random.nextInt(10) == 0;
	}
	
	public static SlimeMap instance() {
		return instance;
	}
	
}
