
cens.term_freq <- function(
		d, 
		percent=0,
		topN=0, #only include the first 'topN' terms
		sorted=c("none", "alpha", "freq"),
		minFreq=1,
        decreasing=FALSE) {
	
	dtm <- DocumentTermMatrix(d, control = list(tolower=FALSE, minWordLength=1));
	
	#remove terms whose total ocurrences < minFreq
	dtm <- dtm[ ,findFreqTerms(dtm, minFreq)];
	
  #could clamp freqs of dtm between 0 and 1 for doc. frequency
	x <- apply(dtm,2,sum);

  sorted <- match.arg(sorted);

  if(percent > 0) { #use percentage of terms
    o <- order(x, decreasing=TRUE)[1:(length(x) * percent / 100)];
    x <- x[o];
  } else if (topN > 0) { #use absolute number of terms
	  o <- order(x, decreasing=TRUE)[1:topN];
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

cens.getCorpusNames <- function() {
  names(which(unlist(eapply(.GlobalEnv, function(n) "Corpus" %in% class(n)))));
}

cens.word_cloud <- function(words){
  require(snippets);
  cloud(words, col = col.bbr(words, fit=TRUE));
}

cens.txt_barplot <- function(words){
  barplot(words, las=2);
}

cens.viewer <- function(words){
  x <- .jnew(J("edu.cens.text.CorpusViewer"), .jarray(cens.getCorpusNames()));
  x$setVisible(TRUE);
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




