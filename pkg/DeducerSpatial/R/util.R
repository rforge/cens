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


