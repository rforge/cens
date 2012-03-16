# TODO: Add comment
# 
# Author: ianfellows
###############################################################################

.isMac <- function(){
	length(grep("^darwin",R.version$os))>0
}

.tryJava <- function(){
	ty <- try(new(J("org.openstreetmap.gui.jmapviewer.tilesources.BingAerialTileSource")))
	if(inherits(ty,"try-error")){
		stop(
"Java classes could not be loaded. Most likely because Java is not set up with your R installation.
Here are some trouble shooting tips:

1. Run 
\tR CMD javareconf
in the terminal. If you are using Mac OS X 10.7 you may want to try
\tR CMD javareconf JAVA_CPPFLAGS=-I/System/Library/Frameworks/JavaVM.framework/Headers
instead.
"
		)
	}
}

.requireRgdal <- function(){
	isLoaded <- require(rgdal)
	if(isLoaded)
		return(TRUE)
	if(interactive()){
		resp <- readline("rgdal is required but not installed. Would you like to install it now? \n(y/n) >")
		if(substr(resp, 1, 1) == "n")
			stop("rgdal is required but not installed")
		if(.isMac())
			install.packages('rgdal',repos="http://www.stats.ox.ac.uk/pub/RWin")
		else
			install.packages("rgdal")
		return(.tryRgdal())
	}
	stop("rgdal is required but not installed")
}

.onLoad <- function(libname, pkgname) {
	if(.isMac() && !.jniInitialized)
		Sys.setenv(NOAWT=1)

	.jpackage(pkgname, lib.loc=libname)  
}

