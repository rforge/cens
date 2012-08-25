.onLoad <- function(libname, pkgname) {
		if(!.jgr)
			return(TRUE)
        .jpackage("DeducerText");
        x <- .jnew(J("edu.cens.text.Text"));
        x <- try(x$initJGR(), silent=TRUE);
        return(TRUE);
}
