# TODO: Add comment
# 
# Author: ianfellows
###############################################################################


#' open street map (and google) mercator projection
#'
osm <- function(){
	CRS("+proj=merc +a=6378137 +b=6378137 +lat_ts=0.0 +lon_0=0.0 +x_0=0.0 +y_0=0 +k=1.0 +units=m +nadgrids=@null +no_defs")
}

#' maps long lat values to the open street map mercator projection
project_mercator <- function(lat,long,drop=TRUE){
	df <- data.frame(long=long,lat=lat)
	coordinates(df) <- ~long+lat
	proj4string(df) <- CRS("+proj=longlat")
	df1 <- spTransform(df,osm())
	coords <- coordinates(df1)
	colnames(coords) <- c("x","y")
	if(drop)
		coords <- drop(coords)
	coords
}


#'plot spatial points with colors
colored_points<-function(x,color_var,pch=1,legend.loc="bottomleft",
		legend.title=NULL,...){
	if(is.character(color_var))
		color_var <- as.factor(color_var)
	
	if(is.factor(color_var)){
		clrs <- rainbow_hcl(length(color_var), start = 30, end = 300)
		org <- levels(color_var) 
		levels(color_var) <- clrs
		color_var <- as.character(color_var)
		plot(x,y,col=color_var,add=TRUE,...)
		legend(legend.loc,,org,col=clrs,pch=pch,title=legend.title,...)
	}else{
		color_var <- as.numeric(color_var)
		cv <- color_var - min(color_var,na.rm=TRUE)
		cv <- cv/max(cv,na.rm=TRUE)
		cv <- ceiling(cv*99)+1
		clrs <- rev(heat_hcl(100, h = c(0, 90), c = c(100, 30), l = c(50, 90), power = c(1/5, 1)))
		repl <- clrs[cv]
		leg.col <- clrs[c(1,25,50,100)]
		leg.val <- c(.01,.25,.50,1)*(max(color_var)-min(color_var)) + min(color_var)
		leg.val <- format(leg.val,digits=3)
		plot(x,col=repl,add=TRUE,...)
		legend(legend.loc,,leg.val,col=leg.col,pch=pch,title=legend.title,...)
	}
}


#'bubbleplot
#Plot Bubble

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

text_plot <- function(x,text,...){
	coord <- coordinates(x)
	text(coord[,1],coord[,2],text,...)
}


#'lifted from choropleth in the USCensus2000 package
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

