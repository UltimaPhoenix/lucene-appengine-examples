package com.googlecode.lucene;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexNotFoundException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.lucene.appengine.GaeDirectory;
import com.googlecode.lucene.appengine.GaeLuceneUtil;

/**
 * Servlet implementation class IndexServlet
 */
public class IndexServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 453189771475098636L;

	private static final Logger log = LoggerFactory.getLogger(IndexServlet.class);

	private static final Version LUCENE_VERSION = Version.LUCENE_43;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final String indexName = request.getParameter("indexName") == null ? "defaultIndex" : request.getParameter("indexName");
		final GaeDirectory directory = new GaeDirectory(indexName);
		try {
			final String text = request.getParameter("text");
			final String query = request.getParameter("query");
			final Analyzer analyzer = new PorterAnalyzer(LUCENE_VERSION);
			
			request.setAttribute("index", indexName);
			
			final String action = request.getParameter("action");
			if("index".equalsIgnoreCase(action)) {
				final IndexWriterConfig config = GaeLuceneUtil.getIndexWriterConfig(LUCENE_VERSION, analyzer);
				final IndexWriter w = new IndexWriter(directory, config);
				
				request.setAttribute("info", "Indexed in index '" + indexName + "' string: " + text.substring(0, Math.min(70, text.length())) + (text.length() > 70 ? "..." : ""));
				long start = System.currentTimeMillis();
				try {
					addDoc(w, text);
				} catch(Exception e) {
					request.setAttribute("message", e.getMessage());
					log.error("Error indexing text {}.", text, e);
				} finally {
					w.close();
				}
				long end = System.currentTimeMillis();
				log.info("Search: {} millis.", end - start);
			} else if("delete".equalsIgnoreCase(action)) {
				try {
					directory.delete();
					request.setAttribute("info", "Successfull deleted index:'" + indexName + "'.");
				} catch (RuntimeException e) {
					request.setAttribute("error", "Error during delete index:'" + indexName + "' cause:" + e.getMessage());
					log.error("Error during delete index '{}'.", indexName, e);
				}
			} else if("clear".equalsIgnoreCase(action)) {
				final IndexWriterConfig config = GaeLuceneUtil.getIndexWriterConfig(LUCENE_VERSION, analyzer);
				final IndexWriter w = new IndexWriter(directory, config);
				try {
					w.deleteAll();
					request.setAttribute("info", "Successfull cleared index:'" + indexName + "'.");
				} catch (Exception e) {
					request.setAttribute("error", "Error during clear index:'" + indexName + "' cause:" + e.getMessage());
					log.error("Error during clear index '{}'.", indexName, e);
				} finally {
					w.close();
				}
			} else if("deindex".equalsIgnoreCase(action)) {
				final String docId = request.getParameter("docId");
				final IndexWriterConfig config = GaeLuceneUtil.getIndexWriterConfig(LUCENE_VERSION, analyzer);
				IndexWriter w = null;
				try {
					w = new IndexWriter(directory, config);
					w.deleteDocuments(new Term("id", docId.intern()));
					request.setAttribute("info", "Successfull deindexed doc:'" + docId + "' in index:'" + indexName + "'.");
					request.setAttribute("muted", "Successfull deindexed doc:'" + docId + "' in index:'" + indexName + "'.");
				} catch (Exception e) {
					request.setAttribute("error", "Error during deindex doc:'" + docId + "' in index:' " + indexName + "' cause:"+ e.getMessage());
					log.error("Error during deindex doc:'" + docId + "' in index:'" + indexName + "'.", e);
				} finally {
					if(w != null) w.close();
				}
			} else if("add".equalsIgnoreCase(action)) {
				//nothing to do created during new GaeDirectory()
			}
			if("search".equalsIgnoreCase(action) || "deindex".equalsIgnoreCase(action)) {
				IndexReader reader = null;
				IndexSearcher searcher = null;
				try {
					Query q = new QueryParser(LUCENE_VERSION, "title", analyzer).parse(query);
				
					request.setAttribute("info", "Result for index '" + indexName + "' query '" + query + "'");
					int hitsPerPage = 10;
					reader = DirectoryReader.open(directory);
					long start = System.currentTimeMillis();
					searcher = new IndexSearcher(reader);
					TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
					searcher.search(q, collector);
					long end = System.currentTimeMillis();
					log.info("Search: {} millis.", end - start);
					ScoreDoc[] hits = collector.topDocs().scoreDocs;
					
					request.setAttribute("searcher", searcher);
					request.setAttribute("hits", hits);
				} catch (ParseException e) {
					request.setAttribute("error", "Query parse exception:'" + indexName + "' '" + query + "', cause:" + e.getMessage());
				} catch (IndexNotFoundException e) {
					if("defaultIndex".equals(indexName)) {
						request.setAttribute("searcher", null);
						request.setAttribute("hits", new ScoreDoc[0]);
					} else {
						request.setAttribute("error", "Cannot find index:'" + indexName + "'.");
					}
				}
			}
			analyzer.close();
			getServletConfig().getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);
		} finally {
			directory.close();
		}
	}

	private static void addDoc(IndexWriter w, String value) throws CorruptIndexException, IOException {
		Document doc = new Document();
		doc.add(new Field("id", UUID.randomUUID().toString(), idType()));
	    doc.add(new Field("title", value, titleType()));
	    w.addDocument(doc);
	}

	private static FieldType idType() {
		FieldType idType = new FieldType();
		idType.setIndexed(true);
		idType.setStored(true);
		idType.setTokenized(false);
		return idType;
	}

	private static FieldType titleType() {
		FieldType titleType = new FieldType();
		titleType.setIndexed(true);
		titleType.setStored(true);
		return titleType;
	}
	
}
