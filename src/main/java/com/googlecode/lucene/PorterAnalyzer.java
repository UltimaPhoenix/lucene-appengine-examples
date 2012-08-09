package com.googlecode.lucene;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.PorterStemFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
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
	public TokenStream tokenStream(String fieldName, Reader reader) {
		return new PorterStemFilter(new StandardFilter(version, new StopFilter(version, new LowerCaseFilter(version, new StandardTokenizer(version, reader)), StandardAnalyzer.STOP_WORDS_SET)));
	}

}
