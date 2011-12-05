# TODO: Add comment
# 
# Author: ianfellows
###############################################################################



.onLoad <- function(libname, pkgname) {
	
	deducerLoaded <- try(.deducer != .jnull(),silent=TRUE)
	if(inherits(deducerLoaded,"try-error") || !deducerLoaded)
		return(NULL)
	
	if (!nzchar(Sys.getenv("NOAWT")) || .jgr==TRUE){
		.jpackage(pkgname)  
		.jengine(TRUE)
		DeducerSpatial <- J("edu.cens.spatial.DeducerSpatial")
		DeducerSpatial$init()
	}
	
	#x <- .jnew(J("edu.cens.spatial.Spatial"));
	#x <- try(x$initJGR(), silent=TRUE);
	return(TRUE)
}

