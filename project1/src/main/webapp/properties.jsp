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
		
		<div id="create-data-property-modal" class="w3-modal" style="background-color: rgba(0,0,0,0);">
			<div class="w3-modal-content w3-center" style="width:425px; border: 1.5px solid #000000; border-radius: 10px;">
				<span id="create-data-property-span" style="position:absolute; right:15px ; color: #aaaaaa; font-size: 30px; font-weight: bold; cursor: pointer;">&times;</span>
				<h2>Create Data Property</h2>
				<form action="script.php" method="post">
					<div style="margin-top: 20px; margin-bottom: 15px">
						<label for="data-property-input" style="margin-right: 12px">Data Property Name:</label>
			      		<input type="text" placeholder="Enter Data Property Name" name="data-property-input" required>
					</div>
					<div style="margin-bottom: 25px">
						<label for="email-input" style="margin-right: 68px">User Email:</label>
				      	<input type="email" placeholder="Enter Email" name="email-input" required>
					</div>
			      	<button type="submit" style="margin-bottom: 25px">Send Request</button>
		      	</form>
			</div>
		</div>
		
		<div id="edit-data-property-modal" class="w3-modal" style="background-color:rgba(0,0,0,0);">
			<div class="w3-modal-content w3-center" style="width:450px; border: 1.5px solid #000000; border-radius: 10px; ">
				<span id="edit-data-property-span" style="position:absolute; right:15px ; color: #aaaaaa; font-size: 30px; font-weight: bold; cursor: pointer;">&times;</span>
				<h2>Edit Data Property</h2>
				<form action="script.php" method="post">
			  		<div style="margin-top: 20px; margin-bottom: 15px">
						<label for="old-data-property-input" style="margin-right: 10px">Old Data Property Name:</label>
			      		<input id="edited-data-property" type="text" name="old-data-property-input" readonly>
					</div>
					<div style="margin-bottom: 15px">
						<label for="new-data-property-input" style="margin-right: 4px">New Data Property Name:</label>
			      		<input type="text" placeholder="Enter Data Property Name" name="new-data-property-input" required>
					</div>
					<div style="margin-bottom: 25px">
						<label for="email-input" style="margin-right:92px">User Email:</label>
				      	<input type="email" placeholder="Enter Email" name="email-input" required>
					</div>
			      	<button type="submit" style="margin-bottom: 25px">Send Request</button>
		      	</form>
			</div>
		</div>
		
		<div id="delete-data-property-modal" class="w3-modal" style="background-color:rgba(0,0,0,0);">
			<div class="w3-modal-content w3-center" style="width:425px; border: 1.5px solid #000000; border-radius: 10px; ">
				<span id="delete-data-property-span" style="position:absolute; right:15px ; color: #aaaaaa; font-size: 30px; font-weight: bold; cursor: pointer;">&times;</span>
				<h2>Delete Data Property</h2>
				<form action="script.php" method="post">
			  		<div style="margin-top: 20px; margin-bottom: 15px">
						<label for="data-property-input" style="margin-right: 12px">Data Property Name:</label>
			      		<input id="deleted-data-property" type="text" name="data-property-input" readonly>
					</div>
					<div style="margin-bottom: 25px">
						<label for="email-input" style="margin-right: 68px">User Email:</label>
				      	<input type="email" placeholder="Enter Email" name="email-input" required>
					</div>
			      	<button type="submit" style="margin-bottom: 25px">Send Request</button>
		      	</form>
			</div>
		</div>
		
		<div id="create-object-property-modal" class="w3-modal" style="background-color: rgba(0,0,0,0);">
			<div class="w3-modal-content w3-center" style="width:425px; border: 1.5px solid #000000; border-radius: 10px;">
				<span id="create-object-property-span" style="position:absolute; right:15px ; color: #aaaaaa; font-size: 30px; font-weight: bold; cursor: pointer;">&times;</span>
				<h2>Create Object Property</h2>
				<form action="script.php" method="post">
					<div style="margin-top: 20px; margin-bottom: 15px">
						<label for="object-property-input" style="margin-right: 12px">Object Property Name:</label>
			      		<input type="text" placeholder="Enter Object Property Name" name="object-property-input" required>
					</div>
					<div style="margin-bottom: 25px">
						<label for="email-input" style="margin-right: 80px">User Email:</label>
				      	<input type="email" placeholder="Enter Email" name="email-input" required>
					</div>
			      	<button type="submit" style="margin-bottom: 25px">Send Request</button>
		      	</form>
			</div>
		</div>
		
		<div id="edit-object-property-modal" class="w3-modal" style="background-color:rgba(0,0,0,0);">
			<div class="w3-modal-content w3-center" style="width:450px; border: 1.5px solid #000000; border-radius: 10px; ">
				<span id="edit-object-property-span" style="position:absolute; right:15px ; color: #aaaaaa; font-size: 30px; font-weight: bold; cursor: pointer;">&times;</span>
				<h2>Edit Object Property</h2>
				<form action="script.php" method="post">
			  		<div style="margin-top: 20px; margin-bottom: 15px">
						<label for="old-object-property-input" style="margin-right: 10px">Old Object Property Name:</label>
			      		<input id="edited-object-property" type="text" name="old-object-property-input" readonly>
					</div>
					<div style="margin-bottom: 15px">
						<label for="new-object-property-input" style="margin-right: 4px">New Object Property Name:</label>
			      		<input type="text" placeholder="Enter Class Name" name="new-object-property-input" required>
					</div>
					<div style="margin-bottom: 25px">
						<label for="email-input" style="margin-right: 104px">User Email:</label>
				      	<input type="email" placeholder="Enter Email" name="email-input" required>
					</div>
			      	<button type="submit" style="margin-bottom: 25px">Send Request</button>
		      	</form>
			</div>
		</div>
		
		<div id="delete-object-property-modal" class="w3-modal" style="background-color:rgba(0,0,0,0);">
			<div class="w3-modal-content w3-center" style="width:425px; border: 1.5px solid #000000; border-radius: 10px; ">
				<span id="delete-object-property-span" style="position:absolute; right:15px ; color: #aaaaaa; font-size: 30px; font-weight: bold; cursor: pointer;">&times;</span>
				<h2>Delete Object Property</h2>
				<form action="script.php" method="post">
			  		<div style="margin-top: 20px; margin-bottom: 15px">
						<label for="object-property-input" style="margin-right: 12px">Object Property Name:</label>
			      		<input id="deleted-object-property" type="text" name="object-property-input" readonly>
					</div>
					<div style="margin-bottom: 25px">
						<label for="email-input" style="margin-right: 80px">User Email:</label>
				      	<input type="email" placeholder="Enter Email" name="email-input" required>
					</div>
			      	<button type="submit" style="margin-bottom: 25px">Send Request</button>
		      	</form>
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
		var obj_properties_data = <% out.print(JSONHandler.convertJSONToString(application.getResourceAsStream("object_properties.json"))); %>
		
		var dt_properties_table = new Tabulator("#data-properties-table", {
			layout:"fitDataStretch",
		    height:"450px",
		    data:dt_properties_data,
		    selectable:1,
		    columns:[
		    {title:"Data Properties", field:"data property", responsive:0, headerFilter:true},
		    ],
		});
		
		var obj_properties_table = new Tabulator("#object-properties-table", {
			layout:"fitDataStretch",
		    height:"450px",
		    data:obj_properties_data,
		    selectable:1,
		    columns:[
		    {title:"Object Properties", field:"object property", responsive:0, headerFilter:true},
		    ],
		});
		
		function data_properties_load(element) {
			var selectedcell = dt_properties_table.getSelectedRows()[0];
			if(element.id == "create-data-property") {
				document.getElementById("create-data-property-modal").style.display = "block";
			} else if(element.id == "edit-data-property") {
				if(selectedcell) {
					document.getElementById("edited-data-property").defaultValue = selectedcell.getCells()[0].getValue();
					document.getElementById("edit-data-property-modal").style.display = "block";
				}
			} else if(element.id == "delete-data-property") {
				if(selectedcell) {
					document.getElementById("deleted-data-property").defaultValue = selectedcell.getCells()[0].getValue();
					document.getElementById("delete-data-property-modal").style.display = "block";
				}
			}
		}
		
		function object_properties_load(element) {
			var selectedcell = obj_properties_table.getSelectedRows()[0];
			if(element.id == "create-object-property") {
				document.getElementById("create-object-property-modal").style.display = "block";
			} else if(element.id == "edit-object-property") {
				if(selectedcell) {
					document.getElementById("edited-object-property").defaultValue = selectedcell.getCells()[0].getValue();
					document.getElementById("edit-object-property-modal").style.display = "block";
				}
			} else if(element.id == "delete-object-property") {
				if(selectedcell) {
					document.getElementById("deleted-object-property").defaultValue = selectedcell.getCells()[0].getValue();
					document.getElementById("delete-object-property-modal").style.display = "block";
				}
			}
		}
		
		document.getElementById("create-data-property-span").onclick = function() {
			document.getElementById("create-data-property-modal").style.display = "none";
		}
		document.getElementById("edit-data-property-span").onclick = function() {
			document.getElementById("edit-data-property-modal").style.display = "none";
		}
		document.getElementById("delete-data-property-span").onclick = function() {
			document.getElementById("delete-data-property-modal").style.display = "none";
		}
		document.getElementById("create-object-property-span").onclick = function() {
			document.getElementById("create-object-property-modal").style.display = "none";
		}
		document.getElementById("edit-object-property-span").onclick = function() {
			document.getElementById("edit-object-property-modal").style.display = "none";
		}
		document.getElementById("delete-object-property-span").onclick = function() {
			document.getElementById("delete-object-property-modal").style.display = "none";
		}
		
		window.onclick = function(event) {
			if (event.target == document.getElementById("create-data-property-modal"))
				document.getElementById("create-data-property-modal").style.display = "none";
			if (event.target == document.getElementById("edit-data-property-modal"))
				document.getElementById("edit-data-property-modal").style.display = "none";
			if (event.target == document.getElementById("delete-data-property-modal"))
				document.getElementById("delete-data-property-modal").style.display = "none";
			if (event.target == document.getElementById("create-object-property-modal"))
				document.getElementById("create-object-property-modal").style.display = "none";
			if (event.target == document.getElementById("edit-object-property-modal"))
				document.getElementById("edit-object-property-modal").style.display = "none";
			if (event.target == document.getElementById("delete-object-property-modal"))
				document.getElementById("delete-object-property-modal").style.display = "none";
		}
			
	</script>
</body>

</html>