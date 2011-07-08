
#words: the words
#
#freq: their frequency
#
#scale: the range of the size of the words
#
#min.freq: words with frequency below min.freq will not be plotted
#
#max.words: Maximum number of words to be plotted. least frequent terms dropped
#
#random.order: plot words in random order. If false, 
#              they will be plotted in decreasing frequency
#
#rot.per: % of words with 90 degree rotation
#
#colors: color words from least to most frequent
#
#...: Additional parameters to be passed to text (and strheight,strwidth).
#	  e.g. control font with vfont.

cloud <- function(words,freq,scale=c(4,.5),min.freq=3,max.words=Inf,random.order=TRUE,
					rot.per=.1,colors="black",...){
	tails <- "g|j|p|q|y"
	last <- 1
	nc<- length(colors)
	overlap <- function(x1, y1, sw1, sh1) {
		s <- 0
		if (length(boxes) == 0) 
			return(FALSE)
		#if(exists(".overlap"))
		#	return(.overlap(x1,y1,sw1,sh1,boxes))
		for (i in c(last,1:length(boxes))) {
			bnds <- boxes[[i]]
			x2 <- bnds[1]
			y2 <- bnds[2]
			sw2 <- bnds[3]
			sh2 <- bnds[4]
			if (x1 < x2) 
				overlap <- x1 + sw1 > x2-s
			else 
				overlap <- x2 + sw2 > x1-s
			
			if (y1 < y2) 
				overlap <- overlap && (y1 + sh1 > y2-s)
			else 
				overlap <- overlap && (y2 + sh2 > y1-s)
			if(overlap){
				last <<- i
				return(TRUE)
			}
		}
		FALSE
	}
	
	ord <- order(freq,decreasing=TRUE)
	words <- words[ord<=max.words]
	freq <- freq[ord<=max.words]
	
	if(random.order)
		ord <- sample.int(length(words))
	else
		ord <- order(freq,decreasing=TRUE)
	words <- words[ord]
	freq <- freq[ord]
	words <- words[freq>=min.freq]
	freq <- freq[freq>=min.freq]
	thetaStep <- .1
	rStep <- .05
	plot.new()
	op <- par("mar")
	par(mar=c(0,0,0,0))
	plot.window(c(0,1),c(0,1),asp=1)
	normedFreq <- freq/max(freq)
	#logs can lessen the size gap between extremely frequent words, and less frequent words.
	#Didn't look too great in practice.
	#normedFreq <- log2(2 + freq)/log2(max(2 + freq))
	size <- (scale[1]-scale[2])*normedFreq + scale[2]
	boxes <- list()

					   
	for(i in 1:length(words)){
		rotWord <- runif(1)<rot.per
		r <-0
		theta <- runif(1,0,2*pi)
		x1<-.5
		y1<-.5
		wid <- strwidth(words[i],cex=size[i],...)
		ht <- strheight(words[i],cex=size[i],...)
		#mind your ps and qs
		if(grepl(tails,words[i]))
			ht <- ht + ht*.2
		if(rotWord){
			tmp <- ht
			ht <- wid
			wid <- tmp	
		}
		isOverlaped <- TRUE
		while(isOverlaped){
			if(!overlap(x1-.5*wid,y1-.5*ht,wid,ht) &&
				x1-.5*wid>0 && y1-.5*ht>0 &&
				x1+.5*wid<1 && y1+.5*ht<1){
				cc <- which((0:nc)/nc>normedFreq[i])
				if(length(cc)>0)
					cc <- colors[cc[1]]
				else
					cc <- colors[1]
				text(x1,y1,words[i],cex=size[i],offset=0,srt=rotWord*90,
					col=cc,...)
				#rect(x1-.5*wid,y1-.5*ht,x1+.5*wid,y1+.5*ht)
				boxes[[length(boxes)+1]] <- c(x1-.5*wid,y1-.5*ht,wid,ht)
				isOverlaped <- FALSE
			}else{
				if(r>sqrt(.5)){
					warning(paste(words[i],
						"could not be fit on page. It will not be plotted."))
					isOverlaped <- FALSE
				}
				theta <- theta+thetaStep
				r <- r + rStep*thetaStep/(2*pi)
				x1 <- .5+r*cos(theta)
				y1 <- .5+r*sin(theta)
			}
		}
	}
	par(mar=op)
	invisible()
}

#library(inline)
#library(Rcpp)
src  <- '
		double x1 = as<double>(x11);
		double y1 =as<double>(y11);
		double sw1 = as<double>(sw11);
		double sh1 = as<double>(sh11);
		Rcpp::List boxes(boxes1);
		Rcpp::NumericVector bnds;
		double x2, y2, sw2, sh2;
		bool overlap= true;
		for (int i=0;i < boxes.size();i++) {
		bnds = boxes(i);
		x2 = bnds(0);
		y2 = bnds(1);
		sw2 = bnds(2);
		sh2 = bnds(3);
		if (x1 < x2) 
		overlap = (x1 + sw1) > x2;
		else 
		overlap = (x2 + sw2) > x1;
		
		
		if (y1 < y2) 
		overlap = (overlap && ((y1 + sh1) > y2));
		else 
		overlap = (overlap && ((y2 + sh2) > y1));
		
		if(overlap)
		return Rcpp::wrap(true);
		}
		
		return Rcpp::wrap(false);
		'

#.overlap <- cxxfunction(
#		signature(x11 = "numeric", y11 = "numeric", sw11 = "numeric", sh11 = "numeric", boxes1="list"), 
#		src, plugin = "Rcpp")
rm("src")

##### 			Example 			#####

#set up data
#library(tm)
#data(crude)
#crude <- tm_map(crude, stemDocument)
#crude <- tm_map(crude, removePunctuation)
#crude <- tm_map(crude, function(x)removeWords(x,stopwords()))
#tdm <- TermDocumentMatrix(crude)
#m <- as.matrix(tdm)
#v <- sort(rowSums(m),decreasing=T)
#d <- data.frame(word = names(v),freq=v)

#a cloud of words with a minimum freqency of 5
#cloud(d$word,d$freq,,5,,,.5)











