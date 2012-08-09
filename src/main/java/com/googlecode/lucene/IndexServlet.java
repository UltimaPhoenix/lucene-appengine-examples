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
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexNotFoundException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
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
	private static final long serialVersionUID = 4758577562823785272L;
	
	private static final Logger log = LoggerFactory.getLogger(IndexServlet.class);

	private static final Version LUCENE_VERSION = Version.LUCENE_36;
	
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
				
				request.setAttribute("message", "Indexed in index '" + indexName + "' string: " + text);
				try {
					addDoc(w, text);
				} catch(Exception e) {
					request.setAttribute("message", e.getMessage());
					log.error("Error indexing text {}.", text, e);
				} finally {
					w.close();
				}
			} else if("search".equalsIgnoreCase(action)) {
				IndexReader reader = null;
				IndexSearcher searcher = null;
				try {
					Query q = new QueryParser(LUCENE_VERSION, "title", analyzer).parse(query);
				
					request.setAttribute("message", "Result for index '" + indexName + "' query '" + query + "'");
					int hitsPerPage = 10;
					reader = IndexReader.open(directory);
					searcher = new IndexSearcher(reader);
					TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
					searcher.search(q, collector);
					ScoreDoc[] hits = collector.topDocs().scoreDocs;
					
					request.setAttribute("searcher", searcher);
					request.setAttribute("hits", hits);
				} catch (ParseException e) {
					request.setAttribute("message", "Query parse exception:'" + indexName + "' '" + query + "', cause:" + e.getMessage());
				} catch (IndexNotFoundException e) {
					request.setAttribute("message", "Cannot find index:'" + indexName + "'.");
				}
			} else if("delete".equalsIgnoreCase(action)) {
				directory.delete();
			} else if("clear".equalsIgnoreCase(action)) {
				final IndexWriterConfig config = GaeLuceneUtil.getIndexWriterConfig(LUCENE_VERSION, analyzer);
				final IndexWriter w = new IndexWriter(directory, config);
				try {
					w.deleteAll();
					request.setAttribute("message", "Successfull cleared index:'" + indexName + "'.");
				} catch (Exception e) {
					request.setAttribute("message", "Error during clear index:'" + indexName + "' cause:" + e.getMessage());
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
					w.deleteDocuments(new Term("id", docId));
					request.setAttribute("message", "Successfull deindexed doc:'" + docId + "' in index:'" + indexName + "'.");
				} catch (Exception e) {
					request.setAttribute("message", "Error during deindex doc:'" + docId + "' in index:' " + indexName + "' cause:"+ e.getMessage());
					log.error("Error during deindex doc:'" + docId + "' in index:'" + indexName + "'.", e);
				} finally {
					if(w != null) w.close();
				}
			} else if("add".equalsIgnoreCase(action)) {
				//nothing to do created during new GaeDirectory()
			}
			
			getServletConfig().getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);
		} finally {
			directory.close();
		}
	}

	private static void addDoc(IndexWriter w, String value) throws CorruptIndexException, IOException {
		Document doc = new Document();
		doc.add(new Field("id", UUID.randomUUID().toString(), Field.Store.YES, Field.Index.NO));
	    doc.add(new Field("title", value, Field.Store.YES, Field.Index.ANALYZED));
	    w.addDocument(doc);
	}
	
}
