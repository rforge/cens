\name{openproj.OpenStreetMap}
\alias{openproj.OpenStreetMap}
\title{Projects the open street map to an alternate coordinate system}
\usage{
  \method{openproj}{OpenStreetMap} (x,
    projection = "+proj=longlat", ...)
}
\arguments{
  \item{x}{an OpenStreetMap object}

  \item{projection}{a proj4 character string or CRS object}

  \item{...}{additional parameters for projectRaster}
}
\description{
  Projects the open street map to an alternate coordinate
  system
}

