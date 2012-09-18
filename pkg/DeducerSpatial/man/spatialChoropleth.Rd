\name{spatialChoropleth}
\alias{spatialChoropleth}
\title{lifted from choropleth in the USCensus2000 package}
\usage{
spatialChoropleth(sp, color ,quantileBin=FALSE, palette, alpha=1,
		main = NULL, sub = "", legend.loc = "bottomleft", 
		legend.title ,add=TRUE,border="transparent", ...) 
}
\arguments{
  \item{sp}{a SpatialPolygonsDataFrame}

  \item{color}{the variable to map to color (either the name in x or a vector)}

  \item{quantileBin}{should color be devided into quantile bins. If true, 5 bins are useds. alternatively 
  					quantileBin can be set to the number of desired bins}

  \item{palette}{A color scale (See the scales package)}
  
  \item{alpha}{transparency}

  \item{main}{title}

  \item{sub}{subtitle}

  \item{legend.loc}{legend location}

  \item{legend.title}{title}

  \item{add}{add to current plot}
  
  \item{border}{polygon border type}

  \item{...}{additional parameters for plot}
}
\description{
produces a choropleth plot for a SpatialPolygonsDataFrame
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
spatialChoropleth(california.tract,'med.age',
		legend.title = 'Median Age',alpha=1)
}
}

