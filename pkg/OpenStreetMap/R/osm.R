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
	if(is.null(res))
		stop(paste("could not obtain tile:",x,y,zoom))
	res1 <- as.character(as.hexmode(res))
	colrs <- paste("#",substring(res1,3),sep="")
	sc <- 20037508*2
	minim <- -20037508
	
	p1 <- c(x/(2^zoom)*sc+minim,-(y/(2^zoom)*sc+minim))
	p2 <- c((x+1)/(2^zoom)*sc+minim,-((y+1)/(2^zoom)*sc+minim))
	bbox <- list(p1=p1,p2=p2)
	res <- list(colorData=colrs,bbox=bbox,projection=osm(),xres=255,yres=255)
	class(res) <- "osmtile"
	res
}

#'add tile to plot
#' @param x the tile
#' @param y ignored
#' @param add add to current plot (if raster, then image is always added)
#' @param raster use raster image
#' @param ... additional parameters to image or rasterImage
#' @method plot osmtile
plot.osmtile <- function(x, y=NULL, add=TRUE, raster=FALSE, ...){
	xres <- x$xres
	yres <- x$yres
	if(!raster)
		image(x=seq(x$bbox$p1[1],x$bbox$p2[1],length=yres) + (x$bbox$p1[1]-x$bbox$p2[1])/yres,
			y=seq(x$bbox$p2[2],x$bbox$p1[2],length=xres) + (x$bbox$p1[2]-x$bbox$p2[2])/xres,
			z=t(matrix(1:(xres*yres),nrow=xres,byrow=TRUE))[,xres:1],
			col=x$colorData,add=add,...)
	else
		rasterImage(as.raster(matrix(x$colorData,nrow=xres,byrow=TRUE)[,yres:1]),
				x$bbox$p2[1],x$bbox$p2[2],x$bbox$p1[1],x$bbox$p1[2],...)
}

#' get a map based on lat long coordinates 
#' @param upperLeft the upper left lat and long
#' @param lowerRight the lower right lat and long
#' @param zoom the zoom level. If null, it is determined automatically
#' @param type 'osm' for mapnik open street map, or 'bing' for bing aerial
#' @param minNumTiles If zoom is null, zoom will be chosen such that
#' 					the number of map tiles is greater than or equal 
#' 					to this number.
#' @examples \dontrun{
#' #Korea
#' map <- openmap(c(43.46886761482925,119.94873046875),
#' 				c(33.22949814144951,133.9892578125),type='osm')
#' plot(map,raster=TRUE)
#' }
openmap <- function(upperLeft,lowerRight,zoom=NULL,type="osm",minNumTiles=9L){
	autoZoom <- is.null(zoom)
	if(autoZoom)
		zoom <- 1L
	else
		zoom <- as.integer(zoom)
	ts <- new(J("org.openstreetmap.gui.jmapviewer.tilesources.BingAerialTileSource"))
	for(i in 1:18){
		minY <-as.integer(floor(ts$latToTileY(upperLeft[1],zoom)))
		maxY <-as.integer(floor(ts$latToTileY(lowerRight[1],zoom)))
	
		minX <-as.integer(floor(ts$lonToTileX(upperLeft[2],zoom)))
		maxX <-as.integer(floor(ts$lonToTileX(lowerRight[2],zoom)))
		ntiles <- (maxX-minX+1)*(maxY-minY+1)
		if(!autoZoom)
			break
		if(ntiles>=minNumTiles)
			break
		else
			zoom <- as.integer(zoom + 1L)
	}
	map <- list(tiles=list())
	for( x in minX:maxX){
		for(y in minY:maxY){
			tile <- osmtile(x,y,zoom,type)
			map$tiles[[length(map$tiles)+1]] <- tile
		}
	}
	map$bbox <- list(p1=project_mercator(upperLeft[1],upperLeft[2]),p2=project_mercator(lowerRight[1],lowerRight[2]))
	class(map) <- "OpenStreetMap"
	attr(map,"zoom") <- zoom
	map
}

#'plot the map in mercator coordinates. see osm().
#' @param x the OpenStreetMap
#' @param y ignored
#' @param add add to current plot
#' @param removeMargin remove margins from plotting device
#' @param ... additional parameters to be passed to plot
#' @method plot OpenStreetMap
#' @examples \dontrun{
#' library(rgdal)
#' m <- c(25.7738889,-80.1938889)
#' j <- c(58.3019444,-134.4197222)
#' miami <- project_mercator(25.7738889,-80.1938889)
#' jun <- project_mercator(58.3019444,-134.4197222)
#' data(states)
#' map <- openmap(j,m,4)
#' plot(map,removeMargin=TRUE)
#' plot(states,add=TRUE)
#' 
#' }
plot.OpenStreetMap <- function(x,y=NULL,add=FALSE,removeMargin=FALSE, ...){
	if(add==FALSE){
		plot.new()
		if(removeMargin)
			par(mar=c(0,0,0,0))
		plot.window(xlim=c(x$bbox$p1[1],x$bbox$p2[1]),ylim=c(x$bbox$p2[2],x$bbox$p1[2]) ,
				xaxs = 'i', yaxs = 'i', asp=T)
	}
	for(tile in x$tiles)
		plot(tile,...)
}


#' Projects the open street map to an alternate coordinate system
#' @param x an OpenStreetMap object
#' @param projection a proj4 character string or CRS object
#' @param ... additional parameters for projectRaster
#' @examples \dontrun{
#' library(rgdal)
#' library(raster)
#' library(maps)
#' 
#' #plot map in native mercator coords
#' map <- openmap(c(70,-179),
#' 		c(-70,179),zoom=2,type='bing')
#' plot(map)
#' 
#' #using longlat projection lets us combine with the maps library
#' map_longlat <- openproj(map)
#' plot(map_longlat)
#' map("world",col="red",add=TRUE)
#' 
#' #robinson projection. good for whole globe viewing.
#' map_robinson <- openproj(map, projection=
#' 				"+proj=robin +lon_0=0 +x_0=0 +y_0=0 +ellps=WGS84 +datum=WGS84 +units=m +no_defs")
#' plot(map_robinson)			
#' }
openproj <- function(x,projection = "+proj=longlat",...) {
	UseMethod("openproj")
}

#' Projects the open street map to an alternate coordinate system
#' @param x an OpenStreetMap object
#' @param projection a proj4 character string or CRS object
#' @param ... additional parameters for projectRaster
#' @S3method openproj default
openproj.default <- function(x,projection = "+proj=longlat",...) {
	stop("unsupported")
}

#' Projects the open street map to an alternate coordinate system
#' @param x an OpenStreetMap object
#' @param projection a proj4 character string or CRS object
#' @param ... additional parameters for projectRaster
#' @method openproj osmtile
openproj.osmtile <- function(x,projection = "+proj=longlat",...){
	library(raster)

	rgbCol <- col2rgb(x$colorData)

	red <- matrix(rgbCol[1,],nrow=x$xres,byrow=TRUE)
	green <- matrix(rgbCol[2,],nrow=x$xres,byrow=TRUE)
	blue <- matrix(rgbCol[3,],nrow=x$xres,byrow=TRUE)
	xmn <- x$bbox$p1[1]
	xmx <- x$bbox$p2[1]
	ymn <- x$bbox$p2[2]
	ymx <- x$bbox$p1[2]
	ras <- stack(raster(red,xmn=xmn,xmx=xmx,ymn=ymn,ymx=ymx),
			raster(green,xmn=xmn,xmx=xmx,ymn=ymn,ymx=ymx),
			raster(blue,xmn=xmn,xmx=xmx,ymn=ymn,ymx=ymx))
	projection(ras) <- x$projection

	if(!is.character(projection))
		projection <- projection@projargs
	
	ras2 <- projectRaster(ras,crs=projection,...)
	vals <- values(ras2)

	vals <- pmin(pmax(vals,0L),255L)
	flag <- apply(vals,1,function(a)any(!is.finite(a)))
	vals1 <- vals
	vals1[!is.finite(vals)] <- 0L
	colors <- ifelse(flag,NA,rgb(vals1[,1],vals1[,2],vals1[,3],maxColorValue=255L))
	ext <- extent(ras2)
	result <- list()
	result$colorData <- colors
	result$bbox <- list(p1 = c(ext@xmin,ext@ymax), p2 = c(ext@xmax,ext@ymin))
	result$projection <- CRS(projection)
	result$xres <- dim(ras2)[1]
	result$yres <- dim(ras2)[2]
	class(result) <- "osmtile"
	result
}

#' Projects the open street map to an alternate coordinate system
#' @param x an OpenStreetMap object
#' @param projection a proj4 character string or CRS object
#' @param ... additional parameters for projectRaster
#' @method  openproj OpenStreetMap
openproj.OpenStreetMap <- function(x,projection = "+proj=longlat",...){
	p1 <- c(Inf,-Inf)
	p2 <- c(-Inf,Inf)
	for(i in 1:length(x$tiles)){
		tile <- openproj(x$tiles[[i]],projection=projection,...)
		x$tiles[[i]] <- tile
		p1 <- c(min(tile$bbox$p1[1],p1[1]), max(tile$bbox$p1[2],p1[2]))
		p2 <- c(max(tile$bbox$p2[1],p2[1]), min(tile$bbox$p2[2],p2[2]))
	}
	x$bbox$p1 <- p1
	x$bbox$p2 <- p2
	x
}

if(FALSE){
library(rgdal)
library(raster)
library(maps)

#plot map in native mercator coords
map <- openmap(c(70,-179),
		c(-70,179),zoom=2,type='bing')
plot(map)

#using longlat projection lets us combine with the maps library
map_longlat <- openproj(map)
plot(map_longlat)
map("world",col="red",add=TRUE)

#robinson projection. good for whole globe viewing.
map_robinson <- openproj(map, projection=
				"+proj=robin +lon_0=0 +x_0=0 +y_0=0 +ellps=WGS84 +datum=WGS84 +units=m +no_defs")
plot(map_robinson)			
				
				#"+proj=lcc +lat_1=33 +lat_2=45 +lat_0=39 +lon_0=-96")
plot(map_polar)

x <- map$tiles[[1]]
red <- matrix(as.integer(as.hexmode(
						substring(x$colorData,2,3))),nrow=255,byrow=TRUE)
green <- matrix(as.integer(as.hexmode(
						substring(x$colorData,4,5))),nrow=255,byrow=TRUE)
blue <- matrix(as.integer(as.hexmode(
						substring(x$colorData,6,7))),nrow=255,byrow=TRUE)
xmn <- x$bbox$p1[1]# + (x$bbox$p1[1]-x$bbox$p2[1])/255
xmx <- x$bbox$p2[1]# + (x$bbox$p1[1]-x$bbox$p2[1])/255
ymn <- x$bbox$p2[2]# + (x$bbox$p1[2]-x$bbox$p2[2])/255
ymx <- x$bbox$p1[2]# + (x$bbox$p1[2]-x$bbox$p2[2])/255
ras <- stack(raster(red,xmn=xmn,xmx=xmx,ymn=ymn,ymx=ymx),
		raster(green,xmn=xmn,xmx=xmx,ymn=ymn,ymx=ymx),
		raster(blue,xmn=xmn,xmx=xmx,ymn=ymn,ymx=ymx))
projection(ras) <- osm()
plotRGB(ras,r=1,g=2,b=3)

#ras2 <- projectRaster(ras,crs="+proj=longlat")
ras2 <- projectRaster(ras,crs="+proj=lcc +lat_1=33 +lat_2=45 +lat_0=39 +lon_0=-96")
#dev.new()
plotRGB(ras2,r=1,g=2,b=3)
}
#'print map
#' @param x the OpenStreetMap
#' @param ... ignored
#' @method print OpenStreetMap
print.OpenStreetMap <- function(x,...){
	print(str(x))
}

