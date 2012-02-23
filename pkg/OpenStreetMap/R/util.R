# TODO: Add comment
# 
# Author: ianfellows
###############################################################################


#' open street map (and google) mercator projection
osm <- function(){
	CRS("+proj=merc +a=6378137 +b=6378137 +lat_ts=0.0 +lon_0=0.0 +x_0=0.0 +y_0=0 +k=1.0 +units=m +nadgrids=@null +no_defs")
}

#' Latitude Longitude projection
longlat <- function(){
	CRS("+proj=longlat +datum=WGS84")
}

#'maps long lat values to the open street map mercator projection
#' @param lat a vector of latitudes
#' @param long a vector of longitudes
#' @param drop drop to lowest dimension
project_mercator <- function(lat,long,drop=TRUE){
	library(rgdal)
	df <- data.frame(long=long,lat=lat)
	coordinates(df) <- ~long+lat
	proj4string(df) <- CRS("+proj=longlat +datum=WGS84")
	df1 <- spTransform(df,osm())
	coords <- coordinates(df1)
	colnames(coords) <- c("x","y")
	if(drop)
		coords <- drop(coords)
	coords
}


#'plot spatial points with colors
#' @param x a SpatialPointsDataFrame
#' @param color_var a vector
#' @param pch plotting symbol
#' @param legend.loc the location of the legend
#' @param legend.title title
#' @param ... additional parameters for plot 
colored_points<-function(x, color_var, pch=1, legend.loc="bottomleft",
		legend.title=NULL,...){
	if(is.character(color_var))
		color_var <- as.factor(color_var)
	if(is.factor(color_var)){
		clrs <- rainbow_hcl(length(levels(color_var)), start = 30, end = 300)
		org <- levels(color_var) 
		levels(color_var) <- clrs
		color_var <- as.character(color_var)
		plot(x,col=color_var,add=TRUE,pch=pch,...)
		legend(legend.loc,,org,col=clrs,pch=pch,title=legend.title)
	}else{
		color_var <- as.numeric(color_var)
		cv <- color_var - min(color_var,na.rm=TRUE)
		cv <- cv/max(cv,na.rm=TRUE)
		cv <- ceiling(cv*99)+1
		clrs <- rev(heat_hcl(100, h = c(0, 90), c = c(100, 30), l = c(50, 90), power = c(1/5, 1)))
		repl <- clrs[cv]
		leg.col <- clrs[c(1,25,50,100)]
		leg.val <- c(.01,.25,.50,1)*(max(color_var,na.rm=TRUE)-min(color_var,na.rm=TRUE)) + min(color_var,na.rm=TRUE)
		leg.val <- format(leg.val,digits=3)
		plot(x,col=repl,add=TRUE,pch=pch,...)
		legend(legend.loc,,leg.val,col=leg.col,pch=pch,title=legend.title)
	}
}



#Plot Bubble

#'bubble plot
#' @param x a SpatialPointsDataFrame
#' @param z a vector to be mapped to size
#' @param minRadius smallest point size
#' @param maxRadius largest point size
#' @param color the color of the points
#' @param ... additional parameters for symbol
bubble_plot <- function(x,z,minRadius=.01,
		maxRadius=.05,color="#F75252", ...){
	mat <- coordinates(x)
	z <- (z-min(z, na.rm=TRUE)) 	
	z <- sqrt(z/pi)
	z <- z / max(z, na.rm=TRUE)
	r <- z*(maxRadius-minRadius) + minRadius;
	dd <- data.frame(x=mat[,1],y=mat[,2],r=r);
	dd <- dd[order(-r),];
	symbols(dd$x, dd$y, circles=dd$r,
			inches=maxRadius,add=TRUE, fg="white", bg=color, ...);
}

#'Plot text
#' @param x a spatial data frame (points or polygon
#' @param text the labels
#' @param ... additional parameters for text
#' @examples \dontrun{
#' library(rgdal)
#' data(LA_places)
#' plot.new()
#' par(mar=c(.5,.5,2.25,.5), oma=c(1,1,1,1))
#' plot.window(c(-1.3160249151515616E7,-1.3155204307648793E7),c(3992993.9205893227,3996691.5618326175), xaxs = 'i', yaxs = 'i')
#' plot(openmap(c(33.760525217369974,-118.22052955627441),c(33.73290566922855,-118.17521095275879),14,'bing'),add=TRUE)
#' plot(x = LA_places,add = TRUE,pch = 16.0,col = '#e00700')
#' text_plot(LA_places,text = slot(LA_places,"data")[,'NAME'],adj = 0.0,col = '#fff7f9')
#' title('Long Beach Terminal')
#' }

text_plot <- function(x,text,...){
	coord <- coordinates(x)
	text(coord[,1],coord[,2],text,...)
}


#'lifted from choropleth in the USCensus2000 package
#' @param sp a SpatialPolygonsDataFrame
#' @param dem the variable to map to color
#' @param cuts how to cut dem
#' @param alpha transparency
#' @param main title
#' @param sub subtitle
#' @param legend.loc legend location
#' @param legend.title title
#' @param add add to current plor
#' @param ... additional parameters for plot
#' @examples \dontrun{
#' library(UScensus2000)
#' 
#' lat <- c(43.834526782236814,30.334953881988564)
#' lon <- c(-131.0888671875  ,-107.8857421875)
#' southwest <- openmap(c(lat[1],lon[1]),c(lat[2],lon[2]),5,'bing')
#' data(california.tract)
#' california.tract <- spTransform(california.tract,osm())
#' 
#' plot(southwest,removeMargin=TRUE)
#' choro_plot(california.tract,dem = slot(california.tract,"data")[,'med.age'],
#' 		legend.title = 'Median Age',alpha=1)
#' }
choro_plot <- function (sp, dem , cuts = list("quantile", seq(0, 
						1, 0.25)), alpha=.5,
		main = NULL, sub = "", legend.loc = "bottomleft", 
				legend.title = "",add=TRUE, ...) 
{
	color.map <- function(x, dem, y = NULL) {
		l.poly <- length(x@polygons)
		dem.num <- cut(dem, breaks = ceiling(do.call(cuts[[1]], 
								list(x = dem, probs = cuts[[2]]))), dig.lab = 6)
		dem.num[which(is.na(dem.num) == TRUE)] <- levels(dem.num)[1]
		l.uc <- length(table(dem.num))
		if (is.null(y)) {
			col.heat <- do.call(color$fun, color$attr)
		}
		else {
			col.heat <- y
		}
		dem.col <- cbind(col.heat, names(table(dem.num)))
		colors.dem <- vector(length = l.poly)
		for (i in 1:l.uc) {
			colors.dem[which(dem.num == dem.col[i, 2])] <- dem.col[i, 
					1]
		}
		out <- list(colors = colors.dem, dem.cut = dem.col[, 
						2], table.colors = dem.col[, 1])
		out
	}
	color <- list(fun = "hsv", attr = list(h = c(0.4, 
							0.5, 0.6, 0.7), s = 0.6, v = 0.6, alpha = alpha))
	colors.use <- color.map(sp, dem)
	col <- colors.use$color
	args <- list(x = sp, ..., col = colors.use$color,add=add,border="transparent")
	do.call("plot", args)
	title(main = main, sub = sub)
	legend(legend.loc, legend = colors.use$dem.cut, fill = colors.use$table.colors, 
			bty = "o", title = legend.title, bg = "white")
}



