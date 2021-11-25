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
	  	<input id="editor" type="button" value="Ontology Editor" style="cursor: pointer;" class="w3-bar-item w3-hide-small w3-padding-large w3-white"/>
	  	<input id="log" type="button" value="Login as Curator" onclick="window.location.href='login.jsp'" style="cursor: pointer;" class="w3-bar-item w3-hide-small w3-padding-large w3-white w3-right"/>
	  </div>
	</div>
	
	<div style="position:relative; padding:100px 50px 0px 50px;">
		<div style="float: left; width: 48%;">
			<div id="data-properties-table"></div>
			<div class="w3-bar w3-large" style="padding-top: 5px;">
				<input id="create-data-property" type="button" value="Create" onclick="data_properties_load(this)" style="font-size: 15px; cursor: pointer;"/>
				<input id="edit-data-property" type="button" value="Edit" onclick="data_properties_load(this)" style="font-size: 15px; cursor: pointer;"/>
				<input id="delete-data-property" type="button" value="Delete" onclick="data_properties_load(this)" style="font-size: 15px; cursor: pointer;"/>
			</div>
		</div>
		<div style="float: right; width: 48%;">
			<div id="object-properties-table"></div>
			<div class="w3-bar w3-large" style="padding-top: 5px;">
				<input id="create-object-property" type="button" value="Create" onclick="object_properties_load(this)" style="font-size: 15px; cursor: pointer;"/>
				<input id="edit-object-property" type="button" value="Edit" onclick="object_properties_load(this)" style="font-size: 15px; cursor: pointer;"/>
				<input id="delete-object-property" type="button" value="Delete" onclick="object_properties_load(this)" style="font-size: 15px; cursor: pointer;"/>
			</div>
		</div>
	</div>
	
	<div class="w3-bottom w3-center" style="padding:0px 0px 50px 0px;">
		<a style="margin-right: 10px;" href="taxonomy.jsp">&laquo;</a>
	  	<a style="margin-right: 10px;" href="taxonomy.jsp">Classes & Individuals</a>
	  	<a style="margin-right: 10px;" href="properties.jsp">Properties</a>
	  	<a style="margin-right: 10px;" href="properties.jsp">&raquo;</a>
	</div>
	
	<script id="taxonomy" type="application/json" src="taxonomy.json"></script>
	<script>
		var dt_properties_data = <% out.print(JSONHandler.convertJSONToString(application.getResourceAsStream("data_properties.json"))); %>
		
		var dt_properties_table = new Tabulator("#data-properties-table", {
			layout:"fitDataStretch",
		    height:"450px",
		    data:dt_properties_data,
		    selectable:1,
		    columns:[
		    {title:"Data Property", field:"data property", responsive:0},
		    ],
		});
		
		var obj_properties_data = <% out.print(JSONHandler.convertJSONToString(application.getResourceAsStream("object_properties.json"))); %>
		
		var obj_properties_table = new Tabulator("#object-properties-table", {
			layout:"fitDataStretch",
		    height:"450px",
		    data:obj_properties_data,
		    selectable:1,
		    columns:[
		    {title:"Object Property", field:"object property", responsive:0},
		    ],
		});
			
	</script>
</body>

</html>