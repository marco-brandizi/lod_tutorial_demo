<%@page import="java.net.URLEncoder"%>
<%@page import="uk.ac.ebi.rdf.demo.web.search.condition.Protein"%>
<%@page import="uk.ac.ebi.rdf.demo.web.search.condition.Condition"%>
<jsp:directive.page import="java.util.List"/>
<jsp:directive.page import="uk.ac.ebi.rdf.demo.web.search.condition.ConditionSearch"/>
<%
	ConditionSearch search = new ConditionSearch ();
	List<Condition> results = search.searchConditions ( request.getParameter ( "query" ) );
%>
<html>
<head>
<title>Condition Search</title>
</head>
<body>

	<h2>Resulting Conditions</h2>
	
  <table border = "0">
  <% for ( Condition result: results ) { %>
    
    <tr>
    
      <% String queryUrl = "search_pathways.jsp?termUri=" + URLEncoder.encode ( result.conditionTermUri ) 
      	 	+ "&termLabel=" + URLEncoder.encode ( result.conditionLabel ); %>
      	 	
    	<td><a href = "<%= queryUrl %>"><%= result.conditionLabel %></a></td>
    	<td>
    	  <b><%= result.proteinCount %> proteins associated to this condition</b>
    	  <p>
    	  <% for ( Protein protein: result.proteins ) { %>
    		  <a href = "<%= protein.proteinUri %>"><%= protein.uniProtId %></a>&nbsp;
    		<% } %>
    		</p>
    	</td>
    </tr>

	<% } %>   
	</table>

</body>
</html>
