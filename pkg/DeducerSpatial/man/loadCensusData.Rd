\name{loadCensusData}
\alias{loadCensusData}
\title{load 200 or 2010 census data}
\usage{
loadCensusData(state,level=c("county","tract","blkgrp","blk","cdp"),
		year=c("2010","2000"), verbose=TRUE, osmTransform=TRUE)
}
\arguments{
  \item{state}{the name of the state (lower case)}
  
  \item{level}{the detail level of the dataset. note that blkgrp and blk yield very large datasets}

  \item{year}{census year}
  
  \item{verbose}{how verbose to be}
  
  \item{osmTransform}{should data be loaded in the OpenStreetMaps mercator projection}

}
\description{
  Loads SpatialPolygonDataFrames representing the 2000 or 2010 census. If the dataset packages
  are not present they are installed (via the internet).
}
\examples{
\dontrun{
loadCensusData(state = 'california')
plot(california.county10)
}
}

