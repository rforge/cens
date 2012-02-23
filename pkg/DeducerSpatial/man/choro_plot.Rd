\name{choro_plot}
\alias{choro_plot}
\title{lifted from choropleth in the USCensus2000 package}
\usage{
  choro_plot(sp, dem,
    cuts = list("quantile", seq(0, 1, 0.25)), alpha = 0.5,
    main = NULL, sub = "", legend.loc = "bottomleft",
    legend.title = "", add = TRUE, ...)
}
\arguments{
  \item{sp}{a SpatialPolygonsDataFrame}

  \item{dem}{the variable to map to color}

  \item{cuts}{how to cut dem}

  \item{alpha}{transparency}

  \item{main}{title}

  \item{sub}{subtitle}

  \item{legend.loc}{legend location}

  \item{legend.title}{title}

  \item{add}{add to current plor}

  \item{...}{additional parameters for plot}
}
\description{
  lifted from choropleth in the USCensus2000 package
}
\examples{
\dontrun{
library(UScensus2000)

lat <- c(43.834526782236814,30.334953881988564)
lon <- c(-131.0888671875  ,-107.8857421875)
southwest <- openmap(c(lat[1],lon[1]),c(lat[2],lon[2]),5,'bing')
data(california.tract)
california.tract <- spTransform(california.tract,osm())

plot(southwest,removeMargin=TRUE)
choro_plot(california.tract,dem = slot(california.tract,"data")[,'med.age'],
		legend.title = 'Median Age',alpha=1)
}
}

