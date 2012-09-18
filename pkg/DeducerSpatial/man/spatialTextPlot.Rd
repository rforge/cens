\name{spatialTextPlot}
\alias{spatialTextPlot}
\title{Plot text}
\usage{
spatialTextPlot(x,text,...)
}
\arguments{
  \item{x}{a spatial data frame (points or polygon}

  \item{text}{the name of a variable in x or a vector representing the labels}

  \item{...}{additional parameters for text}
}
\description{
  Plot text
}
\examples{
\dontrun{

data(LA_places)
plot.new()
par(mar=c(.5,.5,2.25,.5), oma=c(1,1,1,1))
plot.window(c(-1.3160249151515616E7,-1.3155204307648793E7),c(3992993.9205893227,3996691.5618326175), xaxs = 'i', yaxs = 'i')
plot(openmap(c(33.760525217369974,-118.22052955627441),c(33.73290566922855,-118.17521095275879),14,'bing'),add=TRUE)
plot(x = LA_places,add = TRUE,pch = 16.0,col = '#e00700')
spatialTextPlot(LA_places,text = slot(LA_places,"data")[,'NAME'],adj = 0.0,col = '#fff7f9')
title('Long Beach Terminal')
}
}

