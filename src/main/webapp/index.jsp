	<%@page import="org.apache.lucene.document.Document"%>
<%@page import="org.apache.lucene.search.IndexSearcher"%>
<%@page import="org.apache.lucene.search.ScoreDoc"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="org.apache.lucene.index.Term"%>
<%@page import="com.googlecode.lucene.appengine.LuceneIndex"%>
<%@page import="com.googlecode.lucene.appengine.GaeDirectory"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<!DOCTYPE html>
<html>
<head>
<title>Lucene AppEngine 4.2.0-SNAPSHOT Demo</title>
<script type="text/javascript">

  var _gaq = _gaq || [];
  _gaq.push(['_setAccount', 'UA-34007514-3']);
  _gaq.push(['_trackPageview']);

  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();

</script>
</head>
<body>
	<h1>Welcome to Google App Engine Lucene demo application!</h1>
	<h3><%= request.getAttribute("message") != null ? request.getAttribute("message") : "" %></h3>
	<ol>
		<%
			ScoreDoc[] hits = (ScoreDoc[]) request.getAttribute("hits");
			if(hits != null) {
				IndexSearcher searcher = (IndexSearcher) request.getAttribute("searcher");
				out.write("Found " + hits.length + " hits. <br />");
				String indexName = request.getParameter("indexName");
				String query = request.getParameter("query");
				for(int i=0;i<hits.length;++i) {
				    Document d = searcher.doc(hits[i].doc);
				    String docId = URLEncoder.encode(d.get("id"), "UTF-8");
				%>
				<li title="id=<%= docId %>">
				  	<b><%= d.get("title") %></b> --
				  	<a href="deindex.do?action=deindex&docId=<%= docId %>&indexName=<%= indexName %>&query=<%= query %>">Deindex</a>
				</li>
				<%
				}
			}
		%>
	</ol>
	<div>
		<h3>Index Interaction</h3>
		<form action="search.do" method="get">
			Query: 
			<select name="indexName">
			<%for (LuceneIndex index : GaeDirectory.getAvailableIndexes()) {%>
				<option value="<%= index.getName() %>" <%= index.getName().equals(request.getParameter("indexName")) ? "selected='selected'" : "" %>>
					<%= index.getName() %>
				</option>
			<%}%>
			</select>
			with
			<input type="text" size="100" name="query" placeholder="Fill with query '*' means all"  value="<%= request.getParameter("query") != null ? request.getParameter("query") : "" %>"/>
			<input type="submit" name="action" value="search" />
		</form>
		<form action="index.do" method="post">
			Index:
			<select name="indexName">
			<%for (LuceneIndex index : GaeDirectory.getAvailableIndexes()) {%>
				<option value="<%= index.getName() %>" <%= index.getName().equals(request.getParameter("indexName")) ? "selected='selected'" : "" %>>
					<%= index.getName() %>
				</option>
			<%}%>
			</select>
			text
			<input type="text" size="100" name="text" placeholder="Fill with text to index" value="<%= request.getParameter("text") != null ? request.getParameter("text") : "" %>"/>
			<input type="submit" name="action" value="index" />
	</form>
	</div>
	<div>
		<h3>Index Management</h3>
		<div>
			Available indexes: 
			<%for (LuceneIndex index : GaeDirectory.getAvailableIndexes()) {%>
				'<i><%= index.getName() %></i>'
			<%} %>
		</div>
		<form action="addIndex.do">
			Create index: 
			<input type="text" name="indexName" placeholder="Enter index name" value="" size="40"/>
			<input type="submit" name="action" value="add" />
		</form>
		<form action="deleteIndex.do" method="post">
			Delete index: 
			<select name="indexName">
			<%for (LuceneIndex index : GaeDirectory.getAvailableIndexes()) {%>
				<option value="<%= index.getName() %>" <%= index.getName().equals(request.getParameter("indexName")) ? "selected='selected'" : "" %>>
					<%= index.getName() %>
				</option>
			<%}%>
			</select>
			<input type="submit" name="action" value="clear" />
			<input type="submit" name="action" value="delete" />
		</form>
	</div>
	<br />
	<div>
		For details see <a href="http://code.google.com/p/lucene-appengine">Lucene-AppEngine project (LAE)</a>
	</div>
	<div>
		Source code of this demo <a href="http://code.google.com/p/lucene-appengine-examples">Lucene-AppEngine Example project (LAE)</a>
	</div>
</body>
	<footer>
		<p>Created by: Fabio Grucci</p>
		<p>Powered by: Google App Engine (GAE), Lucene-AppEngine (LAE) and Apache Lucene</p>
	</footer>
</html>
