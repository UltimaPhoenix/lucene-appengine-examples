<%@page import="org.apache.commons.lang3.StringEscapeUtils"%>
<%@page import="org.apache.lucene.document.Document"%>
<%@page import="org.apache.lucene.search.IndexSearcher"%>
<%@page import="org.apache.lucene.search.ScoreDoc"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="org.apache.lucene.index.Term"%>
<%@page import="com.googlecode.luceneappengine.LuceneIndex"%>
<%@page import="com.googlecode.luceneappengine.GaeDirectory"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<!DOCTYPE html>
<html>
<head>
	<title>Lucene AppEngine 2.0.0 Demo</title>
	<link href="//netdna.bootstrapcdn.com/twitter-bootstrap/2.3.2/css/bootstrap-combined.min.css" rel="stylesheet" media="screen">
	<link href="/css/docs.css" rel="stylesheet">
	<link href="//cdnjs.cloudflare.com/ajax/libs/prettify/r298/prettify.css" type="text/css">
	<script src="//cdnjs.cloudflare.com/ajax/libs/prettify/r298/prettify.js" type="text/javascript"></script>
	<script type="text/javascript" src="//netdna.bootstrapcdn.com/twitter-bootstrap/2.3.2/js/bootstrap.min.js"></script>
	
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
	<div class="container">
	<h1>Welcome to Lucene AppEngine demo application!</h1>
	<br />
	<%
		String muted = (String) request.getAttribute("muted");
		String error = (String) request.getAttribute("error");
		String info = (String) request.getAttribute("info");
		String indexName = request.getParameter("indexName");
		String query = request.getParameter("query");
	%>
	<p class="muted"><%= muted != null ? StringEscapeUtils.escapeHtml4(muted) : "" %></p>
	<p class="text-error"><%= error != null ? StringEscapeUtils.escapeHtml4(error) : "" %></p>
	<p class="text-info"><%= info != null ? StringEscapeUtils.escapeHtml4(info) : "" %></p>
		<%
			ScoreDoc[] hits = (ScoreDoc[]) request.getAttribute("hits");
			if(hits != null) {
				IndexSearcher searcher = (IndexSearcher) request.getAttribute("searcher");
		%>
	<p class="text-success">Found <%= hits.length %> hits.</p>
	<p class="text-info"><b><%= hits.length == 0 && "*".equals(query) ? 
			"Index is empty; try to index something filling the blue input box" : 
				hits.length == 0 ? " Try to index something filling the blue input box" : "" %></b></p>
		<ol>
<%
				for(int i=0;i<hits.length;++i) {
				    Document d = searcher.doc(hits[i].doc);
				    String docId = URLEncoder.encode(d.get("id"), "UTF-8");
%>
				<li title="id=<%= docId %>">
					<textarea rows="1" class="input-xxlarge" readonly="readonly"><%= d.get("title") %></textarea> -- 
				  	<a href="deindex.do?action=deindex&docId=<%= docId %>&indexName=<%= indexName %>&query=<%= query %>">Deindex</a>
				</li>
			<% } %> 
			</ol>
		<% } %>
	<br />
	<div class="tabbable">
		<ul class="nav nav-tabs">
			<li class="active"><a href="#interaction">Index Interaction</a></li>
		</ul>
		<div class="tab-content">
			<div class="tab-pane active" id="#interaction">
				<form action="search.do" method="get" class="form-search">
					Query: 
					<select name="indexName">
					<%for (LuceneIndex index : GaeDirectory.getAvailableIndexes()) {%>
						<option value="<%= index.getName() %>" <%= index.getName().equals(request.getParameter("indexName")) ? "selected='selected'" : "" %>>
							<%= index.getName() %>
						</option>
					<%}%>
					</select>
					with
					<div class="input-append control-group <%= error != null && query != null ? "error" : "" %>">
						<input type="text" size="100" name="query" placeholder="Fill with query '*' means all"  value="<%= request.getParameter("query") != null ? StringEscapeUtils.escapeHtml4(request.getParameter("query")) : "" %>" class="input-large search-query"/>
						<input type="submit" name="action" value="search" class="btn" />
					</div>
				</form>
				<form action="index.do" method="post" class="form-inline">
					Index:
					<select name="indexName">
					<%for (LuceneIndex index : GaeDirectory.getAvailableIndexes()) {%>
						<option value="<%= index.getName() %>" <%= index.getName().equals(request.getParameter("indexName")) ? "selected='selected'" : "" %>>
							<%= index.getName() %>
						</option>
					<%}%>
					</select>
					text
					<div class="input-append control-group <%= hits != null && hits.length == 0 ? "info" : "" %>">
						<input type="text" size="100" name="text" placeholder="Fill with text to index" value="<%= request.getParameter("text") != null ? StringEscapeUtils.escapeHtml4(request.getParameter("text")) : "" %>" class="input-large"/>
						<input type="submit" name="action" value="index" class="btn" />
					</div>
				</form>
			</div>
		</div>
		<br />
		<ul class="nav nav-tabs">
			<li class="active"><a href="#management">Index Management</a></li>
		</ul>
		<div>
			<div class="tab-pane active" id="#management">
				<p class="muted">
				Available indexes: 
				<%for (LuceneIndex index : GaeDirectory.getAvailableIndexes()) {%>
					'<i><%= index.getName() %></i>'
				<%} %>
				</p>
				<form action="addIndex.do" class="form-inline">
					Create index: 
					<input type="text" name="indexName" placeholder="Enter index name" value="" size="40"/>
					<input type="submit" name="action" value="add" class="btn" />
				</form>
				<form action="deleteIndex.do" method="post" class="form-inline">
					Delete index: 
					<select name="indexName">
					<%for (LuceneIndex index : GaeDirectory.getAvailableIndexes()) {%>
						<option value="<%= index.getName() %>" <%= index.getName().equals(request.getParameter("indexName")) ? "selected='selected'" : "" %>>
							<%= index.getName() %>
						</option>
					<%}%>
					</select>
					<input type="submit" name="action" value="clear" class="btn" />
					<input type="submit" name="action" value="delete" class="btn" />
				</form>
			</div>
		</div>
	</div>
</div>
<footer class="footer">
  <div class="container">
  	<p class="muted credit">Created by: Fabio Grucci</p>
	<p class="muted credit">Powered by: Google App Engine (GAE), LuceneAppEngine (LAE) and Apache Lucene, Bootstrap</p>
    <p>Code licensed under <a href="http://www.apache.org/licenses/LICENSE-2.0" target="_blank">Apache License v2.0</a>.</p>
    <ul class="footer-links">
      <li><a href="http://code.google.com/p/lucene-appengine-examples">Source</a></li>
      <li class="muted">·</li>
      <li><a href="http://code.google.com/p/luceneappengine">LuceneAppEngine project (LAE)</a></li>
      <li class="muted">·</li>
      <li><a href="http://co-de-generation.blogspot.it">Author Blog</a></li>
    </ul>
  </div>
</footer>
</body>
</html>