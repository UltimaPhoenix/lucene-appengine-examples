package com.googlecode.lucene;

import com.googlecode.luceneappengine.GaeDirectory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.LockObtainFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

import static com.googlecode.luceneappengine.GaeLuceneUtil.getIndexWriterConfig;

/**
 * Servlet implementation class IndexServlet
 */
public class IndexServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 453189771475098636L;

	private static final Logger log = LoggerFactory.getLogger(IndexServlet.class);

	public static final int MAX_RESULTS = 200;
	public static final int MAX_PER_PAGE = 10;
	public static final String ENCODING = "UTF-8";
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final String indexName = request.getParameter("indexName") == null ? "defaultIndex" : request.getParameter("indexName");
		final String action = request.getParameter("action");
		
		if ("".equals(indexName)) {
		    request.setAttribute("error", "Index name should be not empty, Operation " + action + " not executed.");
		    getServletConfig().getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);
		    return;
		}
		try (GaeDirectory directory = new GaeDirectory(indexName); Analyzer analyzer = customAnalyzer()){
			final String text = request.getParameter("text");
			final String query = request.getParameter("query");
			final String page = request.getParameter("page");
			
			request.setAttribute("index", indexName);
			
			if("index".equalsIgnoreCase(action)) {
				long start = System.currentTimeMillis();
				boolean operationSuccess = true;
				try (IndexWriter w = new IndexWriter(directory, getIndexWriterConfig(analyzer))) {
    				request.setAttribute("info", "Indexed in index '" + indexName + "' string: " + text.substring(0, Math.min(70, text.length())) + (text.length() > 70 ? "..." : ""));
					addDoc(w, text);
				} catch(LockObtainFailedException e) {
					operationSuccess = false;
					request.setAttribute("error", e.getMessage() + ". System is under heavy load, please try again later");
					log.error("Error indexing text {}.", text, e);
				} catch(Exception e) {
					operationSuccess = false;
					request.setAttribute("error", e.getMessage() + "Try again later");
					log.error("Error indexing text {}.", text, e);
				}
				long end = System.currentTimeMillis();
				log.info("Index completed:{} elapsed: {} millis.", operationSuccess, end - start);
			} else if("delete".equalsIgnoreCase(action)) {
				try {
					directory.delete();
					request.setAttribute("info", "Successfully deleted index:'" + indexName + "'.");
				} catch (RuntimeException e) {
					request.setAttribute("error", "Error during delete index:'" + indexName + "' cause:" + e.getMessage());
					log.error("Error during delete index '{}'.", indexName, e);
				}
			} else if("clear".equalsIgnoreCase(action)) {
				try (IndexWriter w = new IndexWriter(directory, getIndexWriterConfig(analyzer))) {
					w.deleteAll();
					request.setAttribute("info", "Successfully cleared index:'" + indexName + "'.");
				} catch (Exception e) {
					request.setAttribute("error", "Error during clear index:'" + indexName + "' cause:" + e.getMessage());
					log.error("Error during clear index '{}'.", indexName, e);
				}
			} else if("deindex".equalsIgnoreCase(action)) {
				final String docId = request.getParameter("docId");
				try (IndexWriter w = new IndexWriter(directory, getIndexWriterConfig(analyzer))){
					w.deleteDocuments(new Term("id", docId.intern()));
					request.setAttribute("info", "Successfully deindexed doc:'" + docId + "' in index:'" + indexName + "'.");
					request.setAttribute("muted", "Successfully deindexed doc:'" + docId + "' in index:'" + indexName + "'.");
				} catch (Exception e) {
					request.setAttribute("error", "Error during deindex doc:'" + docId + "' in index:' " + indexName + "' cause:"+ e.getMessage());
					log.error("Error during deindex doc:'" + docId + "' in index:'" + indexName + "'.", e);
				}
			} else if("add".equalsIgnoreCase(action)) {//do creating an empty index
				IndexWriter w = new IndexWriter(directory, getIndexWriterConfig(analyzer));
				w.close();
			}
			if("search".equalsIgnoreCase(action) || "deindex".equalsIgnoreCase(action)) {
				IndexReader reader = null;
				IndexSearcher searcher = null;
				try {
				    QueryParser queryParser = new QueryParser("title", analyzer);
                    queryParser.setAllowLeadingWildcard(true);
                    Query q = queryParser.parse(query);
				
					request.setAttribute("info", "Result for index '" + indexName + "' query '" + query + "'");
					int currentPage;
					if (page == null)
					    currentPage = 1;
					else
					    currentPage = Integer.parseInt(page);
					reader = DirectoryReader.open(directory);
					long start = System.currentTimeMillis();
					searcher = new IndexSearcher(reader);
					TopScoreDocCollector collector = TopScoreDocCollector.create(MAX_RESULTS);
					searcher.search(q, collector);
					long end = System.currentTimeMillis();
					log.info("Search: {} millis.", end - start);
					ScoreDoc[] hits = collector.topDocs((currentPage-1)*MAX_PER_PAGE, MAX_PER_PAGE).scoreDocs;
					
					request.setAttribute("collector", collector);
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
			getServletConfig().getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);
		} catch (LockObtainFailedException e){
		    request.setAttribute("error", "Cannot acquire lock to the :'" + indexName + "' for operation '" + action + "'.Other indexing operation are executing please try again later.");
		    getServletConfig().getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);
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
		idType.setIndexOptions(IndexOptions.DOCS);
		idType.setStored(true);
		idType.setTokenized(false);
		return idType;
	}

	private static FieldType titleType() {
		FieldType titleType = new FieldType();
		titleType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
		titleType.setStored(true);
		return titleType;
	}

	private static CustomAnalyzer customAnalyzer() throws IOException {
		return CustomAnalyzer.builder()
				.withTokenizer("standard")
				.addTokenFilter("standard")
				.addTokenFilter("lowercase")
				.addTokenFilter("stop")
				.addTokenFilter("snowballporter")
				.build();
	}

}
