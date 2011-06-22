package edu.cens.spatial;

import org.openstreetmap.gui.jmapviewer.OsmMercator;
import org.openstreetmap.gui.jmapviewer.Tile;
import org.openstreetmap.gui.jmapviewer.MemoryTileCache;
import org.openstreetmap.gui.jmapviewer.tilesources.BingAerialTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;
import org.openstreetmap.gui.jmapviewer.TileController;
import org.openstreetmap.gui.jmapviewer.interfaces.TileCache;
import org.openstreetmap.gui.jmapviewer.interfaces.TileLoaderListener;
import org.openstreetmap.gui.jmapviewer.interfaces.TileSource;

public class RTileController extends TileController{

	static RTileController contrBing;
	static RTileController contrOsm;
	
	public RTileController(TileSource source, TileCache tileCache,
			TileLoaderListener listener) {
		super(source, tileCache, listener);
		tileSource = source;
	}

	public static RTileController getInstance(String type){
		if(contrBing==null){
			final MemoryTileCache tc1 = new MemoryTileCache();
			contrBing = new RTileController(new BingAerialTileSource(),tc1, new TileLoaderListener(){

				public TileCache getTileCache() {
					return tc1;
				}

				public void tileLoadingFinished(Tile arg0, boolean arg1) {}
				
			});
			
		}
		if(contrOsm==null){
			final MemoryTileCache tc2 = new MemoryTileCache();
			contrOsm = new RTileController(new OsmTileSource.Mapnik(),tc2, new TileLoaderListener(){

				public TileCache getTileCache() {
					return tc2;
				}

				public void tileLoadingFinished(Tile arg0, boolean arg1) {}				
			});
		}
		//System.out.println(type + " " + "bing".equals(type) + " " +type.equals("bing"));
		if("bing".equals(type))
			return contrBing;
		else
			return contrOsm;	
	}

    public Tile getTile(int tilex, int tiley, int zoom) {
        int max = (1 << zoom);
        if (tilex < 0 || tilex >= max || tiley < 0 || tiley >= max)
            return null;
        Tile tile = tileCache.getTile(tileSource, tilex, tiley, zoom);
        if (tile == null) {
            tile = new Tile(tileSource, tilex, tiley, zoom);
            tileCache.addTile(tile);
            tile.loadPlaceholderFromCache(tileCache);
        }
        if (!tile.isLoaded()) {
            tileLoader.createTileLoaderJob(tileSource, tilex, tiley, zoom).run();
        }
        return tile;
    }
	
    public int[] getTileValues(double tilex, double tiley, double zoom) {
    	try{
    	int[] res = new int[]{};
    	Tile tile = getTile((int)Math.round(tilex),(int)Math.round(tiley),(int)Math.round(zoom));
    	return tile.getImage().getRGB(0, 0, 255, 255, null, 0, 255);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return null;
    }
    
    public static double[] getTileBBox(double tilex, double tiley, double zoom){
    	double[] res = new double[]{0,0,0,0};
    	int x = (int) Math.round(tilex);
    	int y = (int) Math.round(tiley);
    	int z = (int) Math.round(zoom);
    	res[0] = OsmMercator.XToLon(x, z);
    	res[2] = OsmMercator.XToLon(x+1, z);
    	res[1] = OsmMercator.YToLat(y, z);
    	res[3] = OsmMercator.YToLat(y+1, z);
    	
    	return res;
    }
    
	
}
