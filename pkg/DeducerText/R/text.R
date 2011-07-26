
cens.term_freq <- function(
		d, 
		percent=0,
		topN=0, #only include the first 'topN' terms
		sorted=c("none", "alpha", "freq"),
		minFreq=1,
        decreasing=FALSE,
		useDocFreq=FALSE) {
	
	dtm <- DocumentTermMatrix(d, control = list(tolower=FALSE, minWordLength=1));
	
	#remove terms whose total ocurrences < minFreq
	dtm <- dtm[ ,findFreqTerms(dtm, minFreq)];
	
  #clamp freqs of dtm between 0 and 1 for doc. frequency
	if (useDocFreq == TRUE) 
	{
		dtm <- dtm > 0
	}
	x <- apply(dtm,2,sum);

  sorted <- match.arg(sorted);

  if(percent > 0) { #use percentage of terms
    o <- order(x, decreasing=TRUE)[1:(length(x) * percent / 100)];
    x <- x[o];
  } else if (topN > 0) { #use absolute number of terms
	  o <- order(x, decreasing=TRUE)[1:min(topN, length(x))];
	  x <- x[o];
  }

  if(sorted == "alpha") {
    x <- x[order(names(x), decreasing=decreasing)];
  }
  else if (sorted == "freq") {
    x <- sort(x, decreasing=decreasing);
  }

  return(x);
}

cens.getCorpusNames <- function() 
{
	get.objects("Corpus")
  #names(which(unlist(eapply(.GlobalEnv, function(n) "Corpus" %in% class(n)))));
}

make.color.scale<- function(aColor, bColor, steps, gradientExp=.5){
	len <- steps-1
	ret <- 0
	for (i in 0:len)
	{
		alpha = (i/len)^gradientExp # doing this yields a better spread of color
		ct = (1 - alpha) * aColor + alpha * bColor
		ret[i+1] <- rgb(ct[1], ct[2], ct[3])
	}
	return(ret);
}

cens.choose_corpus <- function(){
  require(rJava);
  corpuses <- cens.getCorpusNames();
  x <- .jnew(J("edu.cens.text.TermFreqOptionsDialog"), .jarray(corpuses));
  x$setVisible(TRUE);



  corpus <-  x$getCorpus();
  percent <- x$getPercent();
  sorted <- x$getSorted();
  decreasing <- ! x$getAsc();

  if(x$isOk())
    return(cens.term_freq(get(corpus), percent, sorted, decreasing));
}

cens.choose_and_do <- function(f, ...) {
  x <- cens.choose_corpus();
  if(!is.null(x)) f(x, ...);
}




