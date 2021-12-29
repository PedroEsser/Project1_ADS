<!DOCTYPE html>
<%@page import="logic.GitHandler"%>
<%@page import="logic.JSONHandler"%>
<html>
<title>Ontology Editor</title>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
<link href="https://unpkg.com/tabulator-tables@5.0.7/dist/css/tabulator.min.css" rel="stylesheet">
<script type="text/javascript" src="https://unpkg.com/tabulator-tables@5.0.7/dist/js/tabulator.min.js"></script>
<style>button,body,h1,h2,h3,h4,h5,h6 {font-family: "Times New Roman"}</style>

<body>

	<div class="w3-top">
	  <div class="w3-bar w3-white w3-border w3-large">
	  	<input id="editor" type="button" value="Ontology Editor" onclick="window.location.href='taxonomy.jsp'" style="cursor: pointer;" class="w3-bar-item w3-hide-small w3-padding-large w3-white"/>
	  	<input id="vis" type="button" value="WebVOWL" onclick="window.open('https://webvowl.blackglacier-3bc0d68a.northeurope.azurecontainerapps.io/#iri=https://raw.githubusercontent.com/ADSDummyUser/Knowledge_Base/master/ontology.owl')" style="cursor: pointer;" class="w3-bar-item w3-hide-small w3-padding-large w3-white"/>
	  	<input id="log" type="button" value="Request Manager" onclick="window.location.href='login.jsp'" style="cursor: pointer;" class="w3-bar-item w3-hide-small w3-padding-large w3-white"/>
	  </div>
	</div>
	
	<div style="height: 640px; padding:100px 50px 0px 50px;">
		<h1 class="w3-center" style="margin-bottom: 30px"></h1>
		<div style="float: left; width: 48%;">
			<div class="w3-center">
				<form action="query.jsp" method="post">
					<div style="width: 100%; height: 125px; margin-bottom: 50px">
						<div style="margin-bottom: 10px; float: left">SQWRL Query:</div>
						<textarea id="query" name="query" placeholder="Enter SQWRL Query" style="width: 100%; height: 90%" required></textarea>
						<button type="submit" style="float: right">Execute</button>
					</div>
					<div style="width: 100%; height: 175px; margin-bottom: 8px">
						<div style="margin-bottom: 10px; float: left">Raw Result:</div>
						<textarea id="result" name="result" style="width: 100%; height: 90%" readonly></textarea>
					</div>
				</form>
   	 		</div>
		</div>
		
		<div style="float: right; width: 48%;">
			<div style="margin-bottom: 10px">Table Result:</div>
			<div id="result-table"></div>
			<div id="webvowl-result" style="float: right; margin-top: 5px">
				<button onclick="alert('Hello WebVOWL!')">Visualize in WebVOWL</button>
			</div>
		</div>
	</div>
	

	<div class="w3-center" style="padding:0px 0px 50px 0px;">
		<a style="margin-right: 10px;" href="taxonomy.jsp">&laquo;</a>
	  	<a style="margin-right: 10px;" href="taxonomy.jsp">Classes & Individuals</a>
	  	<a style="margin-right: 10px;" href="properties.jsp">Properties</a>
	  	<a style="margin-right: 10px;">Query Console</a>
	  	<a style="margin-right: 10px;">&raquo;</a>
	</div>
	
	<script>
		
		var result_table = new Tabulator("#result-table",{
			layout:"fitColumns",
			height:"332px",
			selectable:1,
		    data:[{"No result":''}], //assign data to table
		    autoColumns:true, //create columns from data field names
		    
		});
		
		window.onload = function() {
			var query = `<% if(request.getParameter("query") != null) out.print(request.getParameter("query")); %>`;
			var result = `<% if(request.getParameter("query") != null) out.print(GitHandler.getDefault().getOWLHandler().runSQWRLQuery(request.getParameter("query"))); %>`;
			var result_data = `<% if(request.getParameter("query") != null) out.print(GitHandler.getDefault().getOWLHandler().runJSONSQWRLQuery(request.getParameter("query"))); %>`;
			document.getElementById("query").value = query;
			document.getElementById("result").value = result;
			result_table.setData(result_data);
		};

	</script>

</body>
</html>