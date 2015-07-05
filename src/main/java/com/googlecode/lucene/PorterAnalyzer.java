package com.googlecode.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

public class PorterAnalyzer extends Analyzer {

	@Override
	protected TokenStreamComponents createComponents(String fieldName) {
		final StandardTokenizer src = new StandardTokenizer();
	    TokenStream tok = new StandardFilter(src);
	    tok = new LowerCaseFilter(tok);
	    tok = new StopFilter(tok, StandardAnalyzer.STOP_WORDS_SET);
	    tok = new PorterStemFilter(tok);
	    return new TokenStreamComponents(src, tok);
	}

}
