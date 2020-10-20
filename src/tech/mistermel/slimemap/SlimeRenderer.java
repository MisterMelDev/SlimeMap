package tech.mistermel.slimemap;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class SlimeRenderer extends MapRenderer {
	
	@SuppressWarnings("deprecation")
	private static final byte LINE_COLOR = MapPalette.WHITE, NORMAL_COLOR = MapPalette.GRAY_1, SLIME_COLOR = MapPalette.DARK_GREEN;
	
	private boolean rendered;
	
	@Override
	public void render(MapView view, MapCanvas canvas, Player player) {
		if(rendered) {
			return;
		}
		this.rendered = true;
		
		Location centerLoc = new Location(view.getWorld(), view.getCenterX(), 0, view.getCenterZ());
		Location mapCorner = centerLoc.clone().subtract(128, 0, 128);
		
		for(int chunkX = 0; chunkX < 16; chunkX++) {
			for(int chunkZ = 0; chunkZ < 16; chunkZ++) {
				Location chunkCorner = mapCorner.clone().add(chunkX * 16, 0, chunkZ * 16);
				boolean isSlime = SlimeMap.instance().isSlimeChunk(view.getWorld(), chunkCorner.getChunk().getX(), chunkCorner.getChunk().getZ());
				
				this.drawChunk(canvas, chunkX, chunkZ, isSlime ? SLIME_COLOR : NORMAL_COLOR);
			}
		}
	}
	
	private void drawChunk(MapCanvas canvas, int chunkX, int chunkZ, byte color) {
		int originX = chunkX * 8;
		int originZ = chunkZ * 8;
		
		this.fill(canvas, color, originX, originZ, originX + 8, originZ + 8);
		
		if(chunkX != 0) {
			this.lineY(canvas, LINE_COLOR, originZ, originZ + 8, originX);
		}
		
		if(chunkZ != 0) {
			this.lineX(canvas, LINE_COLOR, originX, originX + 8, originZ);
		}
		
		this.lineX(canvas, LINE_COLOR, originX, originX + 8, originZ + 8);
		this.lineY(canvas, LINE_COLOR, originZ, originZ + 8, originX + 8);
	}
	
	private void fill(MapCanvas canvas, byte color, int x1, int y1, int x2, int y2) {
		for(int x = x1; x < x2; x++) {
			for(int y = y1; y < y2; y++) {
				canvas.setPixel(x, y, color);
			}
		}
	}
	
	private void lineX(MapCanvas canvas, byte color, int x1, int x2, int y) {
		for(int x = x1; x < x2; x++) {
			canvas.setPixel(x, y, color);
		}
	}
	
	private void lineY(MapCanvas canvas, byte color, int y1, int y2, int x) {
		for(int y = y1; y < y2; y++) {
			canvas.setPixel(x, y, color);
		}
	}

}
