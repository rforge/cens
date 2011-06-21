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
	legend(legend.loc,,leg.val,col=leg.col,pch=pch,title=legend.title,...)
}
