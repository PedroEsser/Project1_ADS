<!DOCTYPE html>
<%@page import="logic.GitHandler"%>
<html>
<title>Ontology Editor</title>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
<style>button,body,h1,h2,h3,h4,h5,h6 {font-family: "Times New Roman"}</style>

<body>

	<div class="w3-top">
	  <div class="w3-bar w3-white w3-border w3-large">
	  	<input id="editor" type="button" value="Ontology Editor" onclick="window.location.href='taxonomy.jsp'" style="cursor: pointer;" class="w3-bar-item w3-hide-small w3-padding-large w3-white"/>
	  	<input id="log" type="button" value="Request Manager" onclick="window.location.href='login.jsp'" style="cursor: pointer;" class="w3-bar-item w3-hide-small w3-padding-large w3-white"/>
	  </div>
	</div>
	
	<div class="w3-center" style="height: 640px; margin-left: 25%; margin-right: 25%; padding:100px 50px 0px 50px;">
		<h1 style="margin-bottom: 50px">Console</h1>
		<form action="query.jsp" method="post">
			<div style="width: 100%; height: 125px; margin-bottom: 20px">
				<textarea id="query" name="query" placeholder="Enter SQWRL Query" style="width: 100%; height: 90%" required></textarea>
				<button type="submit" style="float: right">Execute</button>
			</div>
			<div style="width: 100%; height: 175px; margin-bottom: 8px">
				<div style="margin-bottom: 10px; float: left">Result:</div>
				<textarea id="result" name="result" style="width: 100%; height: 90%" readonly></textarea>
			</div>
		</form>
    </div>

	<div class="w3-center" style="padding:0px 0px 50px 0px;">
		<a style="margin-right: 10px;" href="taxonomy.jsp">&laquo;</a>
	  	<a style="margin-right: 10px;" href="taxonomy.jsp">Classes & Individuals</a>
	  	<a style="margin-right: 10px;" href="properties.jsp">Properties</a>
	  	<a style="margin-right: 10px;">Query Console</a>
	  	<a style="margin-right: 10px;">&raquo;</a>
	</div>
	
	<script>
		window.onload = function() {
			var query = `<% if(request.getParameter("query") != null) out.print(request.getParameter("query")); %>`;
			var result = `<% if(request.getParameter("query") != null) out.print(GitHandler.getDefault().getOWLHandler().runSQWRLQuery(request.getParameter("query"))); %>`;
			document.getElementById("query").value = query;
			document.getElementById("result").value = result;
		};
	</script>

</body>
</html>