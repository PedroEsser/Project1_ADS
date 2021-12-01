<!DOCTYPE html>
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
	  	<input id="editor" type="button" value="Request Management" onclick="" style="cursor: pointer;" class="w3-bar-item w3-hide-small w3-padding-large w3-white"/>
	  	<input id="log" type="button" value="Logout" onclick="window.location.href='login.jsp'" style="cursor: pointer;" class="w3-bar-item w3-hide-small w3-padding-large w3-white w3-right"/>
	  </div>
	</div>
	
	<div style="padding:100px 50px 0px 50px;">
		<div style="float: left; width: 20%;">
			<div id="branches-table"></div>
		</div>
	</div>
	
	<script>
		var branches_data = <%= JSONHandler.convertJSONToString("src/main/webapp/resources/branches.json") %>
		
		var branches_table = new Tabulator("#branches-table", {
			layout:"fitDataStretch",
		    height:"500px",
		    data:branches_data,
		    selectable:1,
		    columns:[
		    {title:"Branches", field:"branch", responsive:0, headerFilter:true, headerFilterPlaceholder:"Filter..."},
		    ],
		});
			
	</script>
</body>

</html>