<%@page import="uk.ac.ebi.rdf.demo.web.search.pathway.Pathway"%>
<%@page import="uk.ac.ebi.rdf.demo.web.search.pathway.PathwaySearch"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="uk.ac.ebi.rdf.demo.web.search.condition.Protein"%>
<%@page import="uk.ac.ebi.rdf.demo.web.search.condition.Condition"%>
<jsp:directive.page import="java.util.List"/>
<jsp:directive.page import="uk.ac.ebi.rdf.demo.web.search.condition.ConditionSearch"/>
<%
	PathwaySearch search = new PathwaySearch ();
	String conditionUri = request.getParameter ( "termUri" );
	String conditionLabel = request.getParameter ( "termLabel" );

	List<Pathway> results = search.searchPathways ( conditionUri );
%>
<html>
<head>
<title>Pathway Search</title>
</head>
<body>

	<h2>Pathways and targeting compounds associated to <a href = "<%= conditionUri %>"><%= conditionLabel %></a> </h2>
	
  <table border = "1" cellspacing = "0" >
  <% for ( Pathway result: results ) { %>
    
    <tr>
    	<td><a href = "<%= result.pathwayUri %> %>"><%= result.pathwayName %></a></td>
    	<td>
    	  <a href = "<%= result.moleculeUri %>"><%= result.moleculeId %></a><br>
    	  <%= result.moleculeAltLabel %><br>
    	  
    	  <% String moleculeImgUrl = "https://www.ebi.ac.uk/chembl/compound/displayimage_large/" + result.moleculeId; %>
    	  <a href = "<%= result.moleculeUri %>"><img src = "<%= moleculeImgUrl %>" border = "0" width = "60%" height="60%"/></a>
    	</td>
    </tr>

	<% } %>   
	</table>

</body>
</html>
