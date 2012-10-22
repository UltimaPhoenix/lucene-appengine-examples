package com.googlecode.lucene;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;

public class PorterAnalyzer extends Analyzer {

	private final Version version;
	
	public PorterAnalyzer(Version version) {
		this.version = version;
	}
	
	@Override
	@SuppressWarnings("resource")
	protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
		final StandardTokenizer src = new StandardTokenizer(version, reader);
	    TokenStream tok = new StandardFilter(version, src);
	    tok = new LowerCaseFilter(version, tok);
	    tok = new StopFilter(version, tok, StandardAnalyzer.STOP_WORDS_SET);
	    tok = new PorterStemFilter(tok);
	    return new TokenStreamComponents(src, tok);
	}

}
