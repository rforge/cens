\name{plot.OpenStreetMap}
\alias{plot.OpenStreetMap}
\title{plot the map in mercator coordinates. see osm().}
\usage{
  \method{plot}{OpenStreetMap} (x, y = NULL, add = FALSE,
    removeMargin = FALSE, ...)
}
\arguments{
  \item{x}{the OpenStreetMap}

  \item{y}{ignored}

  \item{add}{add to current plot}

  \item{removeMargin}{remove margins from plotting device}

  \item{...}{additional parameters to be passed to plot}
}
\description{
  plot the map in mercator coordinates. see osm().
}
\examples{
\dontrun{
library(rgdal)
m <- c(25.7738889,-80.1938889)
j <- c(58.3019444,-134.4197222)
miami <- project_mercator(25.7738889,-80.1938889)
jun <- project_mercator(58.3019444,-134.4197222)
data(states)
map <- openmap(j,m,4)
plot(map,removeMargin=TRUE)
plot(states,add=TRUE)

}
}

