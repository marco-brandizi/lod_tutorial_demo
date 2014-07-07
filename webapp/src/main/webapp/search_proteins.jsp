<jsp:directive.page import="uk.ac.ebi.rdf.demo.web.ProteinSearch.ProteinSearchResult" />
<jsp:directive.page import="java.util.List"/>
<jsp:directive.page import="uk.ac.ebi.rdf.demo.web.ProteinSearch"/>
<%
	ProteinSearch search = new ProteinSearch ();
	List<ProteinSearchResult> results = search.searchProteins ( request.getParameter ( "query" ) );
%>
<html>
<head>
<title>Insert title here</title>
</head>
<body>

  <table border = "1">
  <% for ( ProteinSearchResult result: results ) { %>
  
    <tr>
    	<td><a href = "<%= result.proteinUri %>"><%= result.uniProtId %></a></td>
    	<td><a href = "<%= result.conditionTermUri %>"><%= result.conditionLabel %></a></td>
    </tr>

	<% } %>   
	</table>

</body>
</html>
