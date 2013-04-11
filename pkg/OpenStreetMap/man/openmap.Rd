\name{openmap}
\alias{openmap}
\title{get a map based on lat long coordinates}
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
  get a map based on lat long coordinates
}
\examples{
\dontrun{
#Korea
map <- openmap(c(43.46886761482925,119.94873046875),
				c(33.22949814144951,133.9892578125),type='osm')
plot(map)
}
}

