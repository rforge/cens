\name{choro_plot}
\alias{choro_plot}
\title{from choropleth in the USCensus2000 package...}
\usage{choro_plot(sp, dem, cuts=list("quantile", seq(0, 1, 0.25)), alpha=0.5,
    main, sub="", legend.loc="bottomleft", legend.title="", add=TRUE,
    ...)}
\description{from choropleth in the USCensus2000 package}
\arguments{\item{sp}{a SpatialPolygonsDataFrame}
\item{dem}{the variable to map to color}
\item{cuts}{how to cut dem}
\item{alpha}{transparency}
\item{main}{title}
\item{sub}{subtitle}
\item{legend.loc}{legend location}
\item{legend.title}{title}
\item{add}{add to current plor}
\item{...}{additional parameters for plot}}
