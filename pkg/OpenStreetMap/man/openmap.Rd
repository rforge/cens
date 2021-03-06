\name{openmap}
\alias{openmap}
\title{Get a map based on lat long coordinates}
\usage{
  openmap(upperLeft, lowerRight, zoom = NULL,
    type = c("osm", "osm-bw", "maptoolkit-topo", "waze", "mapquest", "mapquest-aerial", "bing", "stamen-toner", "stamen-terrain", "stamen-watercolor", "osm-german", "osm-wanderreitkarte", "mapbox", "esri", "esri-topo", "nps", "apple-iphoto", "skobbler", "cloudmade-<id>", "hillshade", "opencyclemap", "osm-transport", "osm-public-transport", "osm-bbike", "osm-bbike-german"),
    minNumTiles = 9L, mergeTiles = TRUE)
}
\arguments{
  \item{upperLeft}{the upper left lat and long}

  \item{lowerRight}{the lower right lat and long}

  \item{zoom}{the zoom level. If null, it is determined
  automatically}

  \item{type}{the tile server from which to get the map}

  \item{minNumTiles}{If zoom is null, zoom will be chosen
  such that the number of map tiles is greater than or
  equal to this number.}

  \item{mergeTiles}{should map tiles be merged into one
  tile}
}
\description{
  Get a map based on lat long coordinates
}
\examples{
\dontrun{
#show some of the maps available
nm <- c("osm", "maptoolkit-topo", "mapquest",
		"mapquest-aerial", "bing", "stamen-toner",
		"stamen-watercolor", "esri", "esri-topo",
		"nps", "apple-iphoto", "skobbler")
par(mfrow=c(3,4))
#Korea
for(i in 1:length(nm)){
	map <- openmap(c(43.46886761482925,119.94873046875),
			c(33.22949814144951,133.9892578125),
			minNumTiles=3,type=nm[i])
	plot(map)
}

#cloudMade has thousands of map types, and requires a key.
#A default key is provided with the package, but you
#should get your own at http://ww.cloudmade.com and
#apply it with:
#setCloudMadeKey("< your key >")

#plot Korea with a cloudmade map and ggplot2.
map <- openmap(c(43.46886761482925,119.94873046875),
		c(33.22949814144951,133.9892578125),
		minNumTiles=4,type="cloudmade-1960")
autoplot(map)
}
}

