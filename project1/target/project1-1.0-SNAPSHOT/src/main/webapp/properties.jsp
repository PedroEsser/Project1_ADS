<!DOCTYPE html>
<%@page import="logic.JSONHandler"%>
<% JSONHandler.updateJSONs(); %>
<html>
<title>Ontology Editor</title>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
<link href="https://unpkg.com/tabulator-tables@5.0.7/dist/css/tabulator.min.css" rel="stylesheet">
<script type="text/javascript" src="https://unpkg.com/tabulator-tables@5.0.7/dist/js/tabulator.min.js"></script>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js" type="text/javascript"></script>
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
		<div style="float: left; width: 48%;">
			<div id="data-properties-table"></div>
			<div class="w3-bar w3-large" style="padding-top: 5px;">
				<input id="create-data-property" type="button" value="Create" onclick="data_properties_load(this)" style="font-size: 15px; cursor: pointer;"/>
				<input id="edit-data-property" type="button" value="Edit" onclick="data_properties_load(this)" style="font-size: 15px; cursor: pointer;"/>
				<input id="delete-data-property" type="button" value="Delete" onclick="data_properties_load(this)" style="font-size: 15px; cursor: pointer;"/>
				<div id="data-properties-warning" style="float: right; font-size: 15px; color:red;"></div>
			</div>
		</div>
		
		<div style="float: right; width: 48%;">
			<div id="object-properties-table"></div>
			<div class="w3-bar w3-large" style="padding-top: 5px;">
				<input id="create-object-property" type="button" value="Create" onclick="object_properties_load(this)" style="font-size: 15px; cursor: pointer;"/>
				<input id="edit-object-property" type="button" value="Edit" onclick="object_properties_load(this)" style="font-size: 15px; cursor: pointer;"/>
				<input id="delete-object-property" type="button" value="Delete" onclick="object_properties_load(this)" style="font-size: 15px; cursor: pointer;"/>
				<div id="object-properties-warning" style="float: right; font-size: 15px; color:red;"></div>
			</div>
		</div>
	</div>
	
	<div id="create-data-property-modal" class="w3-modal" style="background-color: rgba(0,0,0,0);">
		<div class="w3-modal-content w3-center" style="width:425px; border: 1.5px solid #000000; border-radius: 10px;">
			<span id="create-data-property-span" style="position:absolute; right:15px ; color: #aaaaaa; font-size: 30px; font-weight: bold; cursor: pointer;">&times;</span>
			<h2>Create Data Property</h2>
			<form name="create_data_property" method="post">
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
			<form name="edit_data_property" method="post">
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
			<form name="delete_data_property" method="post">
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
			<form name="create_object_property" method="post">
				<div style="margin-top: 20px; margin-bottom: 15px">
					<label for="object-property-input" style="margin-right: 12px">Object Property Name:</label>
		      		<input type="text" placeholder="Enter Object Property Name" name="object-property-input" required>
				</div>
				<div style="text-align: left; margin-bottom: 15px; margin-left: 39px">
					<label>Characteristics:</label>
				</div>
				<div style="margin-left: 40px; margin-right: 10px; margin-bottom: 7px; display: grid; grid-template-columns: auto auto auto; grid-template-rows: 30px 30px 30px; justify-items: start;">
	  				<div><input type="checkbox" name="functional" style="margin-right: 5px"><label for="functional">Functional</label></div>
					<div><input type="checkbox" name="inverse-functional" style="margin-right: 5px"><label for="inverse-functional">Inverse Functional</label></div>
  					<div><input type="checkbox" name="transitive" style="margin-right: 5px"><label for="transitive">Transitive</label></div>
  					<div><input type="checkbox" name="symmetric" style="margin-right: 5px"><label for="symmetric">Symmetric</label></div>
  					<div><input type="checkbox" name="asymmetric" style="margin-right: 5px"><label for="asymmetric">Asymmetric</label></div>
  					<div><input type="checkbox" name="reflexive" style="margin-right: 5px"><label for="reflexive">Reflexive</label></div>
  					<div><input type="checkbox" name="irreflexive" style="margin-right: 5px"><label for="irreflexive">Irreflexive</label></div>
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
			<form name="edit_object_property" method="post">
		  		<div style="margin-top: 20px; margin-bottom: 15px">
					<label for="old-object-property-input" style="margin-right: 10px">Old Object Property Name:</label>
		      		<input id="edited-object-property" type="text" name="old-object-property-input" readonly>
				</div>
				<div style="margin-bottom: 15px">
					<label for="new-object-property-input" style="margin-right: 4px">New Object Property Name:</label>
		      		<input type="text" placeholder="Enter Class Name (Optional)" name="new-object-property-input">
				</div>
				<div style="text-align: left; margin-bottom: 15px; margin-left: 39px">
					<label>Characteristics:</label>
				</div>
				<div style="margin-left: 40px; margin-right: 10px; margin-bottom: 7px; display: grid; grid-template-columns: auto auto auto; grid-template-rows: 30px 30px 30px; justify-items: start;">
	  				<div><input id="Functional" type="checkbox" name="functional" style="margin-right: 5px"><label for="functional">Functional</label></div>
					<div><input id="InverseFunctional" type="checkbox" name="inverse-functional" style="margin-right: 5px"><label for="inverse-functional">Inverse Functional</label></div>
  					<div><input id="Transitive" type="checkbox" name="transitive" style="margin-right: 5px"><label for="transitive">Transitive</label></div>
  					<div><input id="Symmetric" type="checkbox" name="symmetric" style="margin-right: 5px"><label for="symmetric">Symmetric</label></div>
  					<div><input id="Asymmetric" type="checkbox" name="asymmetric" style="margin-right: 5px"><label for="asymmetric">Asymmetric</label></div>
  					<div><input id="Reflexive" type="checkbox" name="reflexive" style="margin-right: 5px"><label for="reflexive">Reflexive</label></div>
  					<div><input id="Irrefexive" type="checkbox" name="irreflexive" style="margin-right: 5px"><label for="irreflexive">Irreflexive</label></div>
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
			<form name="delete_object_property" method="post">
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
	
	<div class="w3-center" style="padding:0px 0px 50px 0px;">
		<a style="margin-right: 10px;" href="taxonomy.jsp">&laquo;</a>
	  	<a style="margin-right: 10px;" href="taxonomy.jsp">Classes & Individuals</a>
	  	<a style="margin-right: 10px;">Properties</a>
	  	<a style="margin-right: 10px;" href="query.jsp">Query Console</a>
	  	<a style="margin-right: 10px;" href="query.jsp">&raquo;</a>
	</div>
	
	<script>
		var dt_properties_data = <%= JSONHandler.convertJSONToString("src/main/webapp/resources/data_properties.json") %>
		var obj_properties_data = <%= JSONHandler.convertJSONToString("src/main/webapp/resources/object_properties.json") %>
		
		var dt_properties_table = new Tabulator("#data-properties-table", {
			layout:"fitDataStretch",
		    height:"450px",
		    data:dt_properties_data,
		    selectable:1,
		    columns:[
		    {title:"Data Properties", field:"data property", responsive:0, headerFilter:true, headerFilterPlaceholder:"Filter..."},
		    ],
		});
		
		var obj_properties_table = new Tabulator("#object-properties-table", {
			layout:"fitDataStretch",
		    height:"450px",
		    data:obj_properties_data,
		    selectable:1,
		    columns:[
		    {title:"Object Properties", field:"object property", responsive:0, headerFilter:true, headerFilterPlaceholder:"Filter..."},
		    ],
		});
		
		function data_properties_load(element) {
			var selectedcell = dt_properties_table.getSelectedRows()[0];
			if(element.id == "create-data-property") {
				document.getElementById("create-data-property-modal").style.display = "block";
				document.getElementById("data-properties-warning").textContent = "";
			} else if(element.id == "edit-data-property") {
				if(selectedcell) {
					document.getElementById("edited-data-property").defaultValue = selectedcell.getCells()[0].getValue();
					document.getElementById("edit-data-property-modal").style.display = "block";
					document.getElementById("data-properties-warning").textContent = "";
				} else {
					document.getElementById("data-properties-warning").textContent = "Select a Data Property!";
				}
			} else if(element.id == "delete-data-property") {
				if(selectedcell) {
					document.getElementById("deleted-data-property").defaultValue = selectedcell.getCells()[0].getValue();
					document.getElementById("delete-data-property-modal").style.display = "block";
					document.getElementById("data-properties-warning").textContent = "";
				} else {
					document.getElementById("data-properties-warning").textContent = "Select a Data Property!";
				}
			}
		}
		
		function object_properties_load(element) {
			var selectedcell = obj_properties_table.getSelectedRows()[0];
			if(element.id == "create-object-property") {
				document.getElementById("create-object-property-modal").style.display = "block";
				document.getElementById("object-properties-warning").textContent = "";
			} else if(element.id == "edit-object-property") {
				if(selectedcell) {
					document.getElementById("edited-object-property").defaultValue = selectedcell.getCells()[0].getValue();
					for(var i = 0; i < obj_properties_data.length; i++) {
						if(obj_properties_data[i]["object property"] == selectedcell.getCells()[0].getValue()) {
							for(var j = 0; j < obj_properties_data[i]["characteristics"].length; j++) {
								document.getElementById(obj_properties_data[i]["characteristics"][j]).checked = true;
							}
						}
					}
					document.getElementById("edit-object-property-modal").style.display = "block";
					document.getElementById("object-properties-warning").textContent = "";
				} else {
					document.getElementById("object-properties-warning").textContent = "Select an Object Property!";
				}
			} else if(element.id == "delete-object-property") {
				if(selectedcell) {
					document.getElementById("deleted-object-property").defaultValue = selectedcell.getCells()[0].getValue();
					document.getElementById("delete-object-property-modal").style.display = "block";
					document.getElementById("object-properties-warning").textContent = "";
				} else {
					document.getElementById("object-properties-warning").textContent = "Select an Object Property!";
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
		
		$(document).on("submit", "form", function (e) {
			e.preventDefault();
			var form = $(this);
		    $.ajax({
				type: "POST",
		      	url: form.attr('name'),
		      	data: form.serialize(),
		      	success: function () {
		    		location.reload();
		      	}
		    });
		});
			
	</script>
</body>

</html>