\name{openproj.osmtile}
\alias{openproj.osmtile}
\title{Projects the open street map to an alternate coordinate system}
\usage{
  \method{openproj}{osmtile} (x,
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

