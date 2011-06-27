# TODO: Add comment
# 
# Author: ianfellows
###############################################################################



#'get an open street map tile. tpe can be "osm" or "bing"
#' @param x location in osm native coordinates
#' @param y location in osm native coordinates
#' @param zoom zoom level
#' @param type osm for mapnik open street map, or 'bing' for bing aerial
#' @return a tile
osmtile <- function(x,y,zoom,type="osm"){
	x <- as.double(x)
	y <- as.double(y)
	zoom <- as.double(zoom)
	TC <- J("edu.cens.spatial.RTileController")
	res <- TC$getInstance(type)$getTileValues(x,y,zoom)
	res <- as.character(as.hexmode(res))
	red <- substr(res, 3, 4)
	green <- substr(res, 5, 6)
	blue <- substr(res, 7, 8)
	colrs <- paste("#", red, green, blue, sep = "")
	
	sc <- 20037508*2
	minim <- -20037508
	
	p1 <- c(x/(2^zoom)*sc+minim,-(y/(2^zoom)*sc+minim))
	p2 <- c((x+1)/(2^zoom)*sc+minim,-((y+1)/(2^zoom)*sc+minim))
	bbox <- list(p1=p1,p2=p2)
	res <- list(colorData=colrs,bbox=bbox)
	class(res) <- "osmtile"
	res
}

#'add tile to plot
#' @param x the tile
#' @param y ignored
#' @param add add to current plot
#' @param ... additional parameters to image
plot.osmtile <- function(x,y=NULL,add=TRUE,...){
	image(x=seq(x$bbox$p1[1],x$bbox$p2[1],length=255),
			y=seq(x$bbox$p2[2],x$bbox$p1[2],length=255),
			z=matrix(1:(255*255),ncol=255)[,255:1],
			col=x$colorData,add=add,...)
	
}

#' get a map based on lat long coordinates 
#' @param upperLeft the upper left lat and long
#' @param lowerRight the lower right lat and long
#' @param zoom the zoom level
#' @param type osm for mapnik open street map, or 'bing' for bing aerial
openmap <- function(upperLeft,lowerRight,zoom,type="osm"){
	zoom <- as.integer(zoom)
	ts <- new(J("org.openstreetmap.gui.jmapviewer.tilesources.BingAerialTileSource"))
	minY <-as.integer(floor(ts$latToTileY(upperLeft[1],zoom)))
	maxY <-as.integer(floor(ts$latToTileY(lowerRight[1],zoom)))
	
	minX <-as.integer(floor(ts$lonToTileX(upperLeft[2],zoom)))
	maxX <-as.integer(floor(ts$lonToTileX(lowerRight[2],zoom)))
	
	map <- list(tiles=list())
	for( x in minX:maxX){
		for(y in minY:maxY){
			tile <- osmtile(x,y,zoom,type)
			map$tiles[[length(map$tiles)+1]] <- tile
		}
	}
	map$bbox <- list(p1=project_mercator(upperLeft[1],upperLeft[2]),p2=project_mercator(lowerRight[1],lowerRight[2]))
	class(map) <- "OpenStreetMap"
	map
}

#'plot the map in mercator coordinates. see osm().
#' @param x the OpenStreetMap
#' @param y ignored
#' @param add add to current plot
#' @param ... additional parameters to be passed to plot
plot.OpenStreetMap <- function(x,y=NULL,add=FALSE,...){
	if(add==FALSE){
		plot.new()
		par(mar=c(0,0,0,0))
		plot.window(xlim=c(x$bbox$p1[1],x$bbox$p2[1]),ylim=c(x$bbox$p2[2],x$bbox$p1[2]), 
				xaxs = 'i', yaxs = 'i',asp=abs((x$bbox$p2[2]-x$bbox$p1[2])/(x$bbox$p1[1]-x$bbox$p2[1])))
	}
	for(tile in x$tiles)
		plot(tile,...)
}

#m <- c(25.7738889,-80.1938889)
#j <- c(58.3019444,-134.4197222)
#miami <- project_mercator(25.7738889,-80.1938889)
#jun <- project_mercator(58.3019444,-134.4197222)
#data(states)
#map <- openmap(j,m,4)
#plot(map)
#plot(states,add=T)
