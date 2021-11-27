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
	
	<div style="padding:100px 50px 0px 50px;">
		<div style="float: left; width: 48%;">
			<div id="taxonomy-table"></div>
			<div class="w3-bar w3-large" style="padding-top: 5px;">
				<input id="create-class" type="button" value="Create" onclick="taxonomy_load(this)" style="font-size: 15px; cursor: pointer;"/>
				<input id="edit-class" type="button" value="Edit" onclick="taxonomy_load(this)" style="font-size: 15px; cursor: pointer;"/>
				<input id="delete-class" type="button" value="Delete" onclick="taxonomy_load(this)" style="font-size: 15px; cursor: pointer;"/>
				<div id="class-warning" style="float: right; font-size: 15px; color:red;"></div>
			</div>
		</div>
		
		<div style="float: right; width: 48%;">
			<div id="individuals-table"></div>
			<div class="w3-bar w3-large" style="padding-top: 5px;">
				<input id="create-individual" type="button" value="Create" onclick="taxonomy_load(this)" style="font-size: 15px; cursor: pointer;"/>
				<input id="edit-individual" type="button" value="Edit" onclick="individuals_load(this)" style="font-size: 15px; cursor: pointer;"/>
				<input id="delete-individual" type="button" value="Delete" onclick="individuals_load(this)" style="font-size: 15px; cursor: pointer;"/>
				<input id="individual-details" type="button" value="See Details" onclick="individuals_load(this)" style="font-size: 15px; cursor: pointer;"/>
				<div id="individual-warning" style="float: right; font-size: 15px; color:red;"></div>
			</div>
		</div>
		
		<div id="create-class-modal" class="w3-modal" style="background-color: rgba(0,0,0,0);">
			<div class="w3-modal-content w3-center" style="width:375px; border: 1.5px solid #000000; border-radius: 10px;">
				<span id="create-class-span" style="position:absolute; right:15px ; color: #aaaaaa; font-size: 30px; font-weight: bold; cursor: pointer;">&times;</span>
				<h2>Create Class</h2>
				<form action="create_class" method="post">
			  		<div style="margin-top: 20px; margin-bottom: 15px">
						<label for="super-class-input" style="margin-right: 13px">Super Class:</label>
			      		<input id="super-class" type="text" placeholder="Select a Class (Optional)" name="super-class-input" readonly>
					</div>
					<div style="margin-bottom: 15px">
						<label for="class-input" style="margin-right: 12px">Class Name:</label>
			      		<input type="text" placeholder="Enter Class Name" name="class-input" required>
					</div>
					<div style="margin-bottom: 25px">
						<label for="email-input" style="margin-right: 17px">User Email:</label>
				      	<input type="email" placeholder="Enter Email" name="email-input" required>
					</div>
			      	<button type="submit" style="margin-bottom: 25px">Send Request</button>
		      	</form>
			</div>
		</div>
		
		<div id="edit-class-modal" class="w3-modal " style="background-color:rgba(0,0,0,0);">
			<div class="w3-modal-content w3-center" style="width:400px; border: 1.5px solid #000000; border-radius: 10px; ">
				<span id="edit-class-span" style="position:absolute; right:15px ; color: #aaaaaa; font-size: 30px; font-weight: bold; cursor: pointer;">&times;</span>
				<h2>Edit Class</h2>
				<form action="script.php" method="post">
			  		<div style="margin-top: 20px; margin-bottom: 15px">
						<label for="old-class-input" style="margin-right: 10px">Old Class Name:</label>
			      		<input id="edited-class" type="text" name="old-class-input" readonly>
					</div>
					<div style="margin-bottom: 15px">
						<label for="new-class-input" style="margin-right: 4px">New Class Name:</label>
			      		<input type="text" placeholder="Enter Class Name" name="new-class-input" required>
					</div>
					<div style="margin-bottom: 25px">
						<label for="email-input" style="margin-right: 41px">User Email:</label>
				      	<input type="email" placeholder="Enter Email" name="email-input" required>
					</div>
			      	<button type="submit" style="margin-bottom: 25px">Send Request</button>
		      	</form>
			</div>
		</div>
		
		<div id="delete-class-modal" class="w3-modal " style="background-color:rgba(0,0,0,0);">
			<div class="w3-modal-content w3-center" style="width:375px; border: 1.5px solid #000000; border-radius: 10px; ">
				<span id="delete-class-span" style="position:absolute; right:15px ; color: #aaaaaa; font-size: 30px; font-weight: bold; cursor: pointer;">&times;</span>
				<h2>Delete Class</h2>
				<form action="script.php" method="post">
			  		<div style="margin-top: 20px; margin-bottom: 15px">
						<label for="class-input" style="margin-right: 12px">Class Name:</label>
			      		<input id="deleted-class" type="text" name="class-input" readonly>
					</div>
					<div style="margin-bottom: 25px">
						<label for="email-input" style="margin-right: 17px">User Email:</label>
				      	<input type="email" placeholder="Enter Email" name="email-input" required>
					</div>
			      	<button type="submit" style="margin-bottom: 25px">Send Request</button>
		      	</form>
			</div>
		</div>
		
		<div id="create-individual-modal" class="w3-modal" style="background-color: rgba(0,0,0,0);">
			<div class="w3-modal-content w3-center" style="width:400px; border: 1.5px solid #000000; border-radius: 10px; ">
				<span id="create-individual-span" style="position:absolute; right:15px ; color: #aaaaaa; font-size: 30px; font-weight: bold; cursor: pointer;">&times;</span>
				<h2>Create Individual</h2>
				<form action="script.php" method="post">
			  		<div style="margin-top: 20px; margin-bottom: 15px">
						<label for="class-input" style="margin-right: 42px">Class Name:</label>
			      		<input id="class" type="text" name="class-input" readonly>
					</div>
					<div style="margin-bottom: 15px">
						<label for="individual-input" style="margin-right: 12px">Individual Name:</label>
			      		<input type="text" placeholder="Enter Individual Name" name="class-input" required>
					</div>
					<div style="margin-bottom: 25px">
						<label for="email-input" style="margin-right: 46px">User Email:</label>
				      	<input type="email" placeholder="Enter Email" name="email-input" required>
					</div>
			      	<button type="submit" style="margin-bottom: 25px">Send Request</button>
		      	</form>
			</div>
		</div>
			
		<div id="edit-individual-modal" class="w3-modal " style="background-color:rgba(0,0,0,0);">
			<div class="w3-modal-content w3-center" style="width:425px; border: 1.5px solid #000000; border-radius: 10px; ">
				<span id="edit-individual-span" style="position:absolute; right:15px ; color: #aaaaaa; font-size: 30px; font-weight: bold; cursor: pointer;">&times;</span>
				<h2>Edit Individual</h2>
				<form action="script.php" method="post">
			  		<div style="margin-top: 20px; margin-bottom: 15px">
						<label for="old-individual-input" style="margin-right: 10px">Old Individual Name:</label>
			      		<input id="edited-individual" type="text" name="old-individual-input" readonly>
					</div>
					<div style="margin-bottom: 15px">
						<label for="new-individual-input" style="margin-right: 4px">New Individual Name:</label>
			      		<input type="text" placeholder="Enter Individual Name" name="new-individual-input" required>
					</div>
					<div style="margin-bottom: 25px">
						<label for="email-input" style="margin-right: 70px">User Email:</label>
				      	<input type="email" placeholder="Enter Email" name="email-input" required>
					</div>
			      	<button type="submit" style="margin-bottom: 25px">Send Request</button>
		      	</form>
			</div>
		</div>
			
		<div id="delete-individual-modal" class="w3-modal " style="background-color:rgba(0,0,0,0);">
			<div class="w3-modal-content w3-center" style="width:400px; border: 1.5px solid #000000; border-radius: 10px; ">
				<span id="delete-individual-span" style="position:absolute; right:15px ; color: #aaaaaa; font-size: 30px; font-weight: bold; cursor: pointer;">&times;</span>
				<h2>Delete Individual</h2>
				<form action="script.php" method="post">
			  		<div style="margin-top: 20px; margin-bottom: 15px">
						<label for="individual-input" style="margin-right: 12px">Individual Name:</label>
			      		<input id="deleted-individual" type="text" name="individual-input" readonly>
					</div>
					<div style="margin-bottom: 25px">
						<label for="email-input" style="margin-right: 45px">User Email:</label>
				      	<input type="email" placeholder="Enter Email" name="email-input" required>
					</div>
			      	<button type="submit" style="margin-bottom: 25px">Send Request</button>
		      	</form>
			</div>
		</div>
		
		<div id="individual-details-modal" class="w3-modal " style="background-color:rgba(0,0,0,0);">
			<div class="w3-modal-content" style="width:765px; border: 1.5px solid #000000; border-radius: 10px; ">
				<span id="individual-details-span" style="position:absolute; right:15px ; color: #aaaaaa; font-size: 30px; font-weight: bold; cursor: pointer;">&times;</span>
				<h2 id="individual-name" class="w3-center"></h2>
				<p style="margin-left: 40px"><i><u>Class</u></i></p>
				<div id="individual-class" style="margin-left: 40px"></div>
				<p style="margin-left: 40px"><i><u>Object Properties</u></i></p>
				<div id="object-properties-list"></div>
				<form action="script.php" method="post" style="margin-left: 40px">
			      	<select id="object-property" name="object-property-input" style="width: 191px; height: 28px"></select>
			      	<select id="linked-individual" name="object-property-value" style="width: 191px; height: 28px"></select>
				    <input type="email" placeholder="Enter Email" name="email-input" required>
			      	<button type="submit" style="margin-bottom: 25px">Send Request</button>
		      	</form>
				<p style="margin-left: 40px"><i><u>Data Properties</u></i></p>
				<div id="data-properties-list"></div>
				<form action="script.php" method="post" style="margin-left: 40px">
			      	<select id="data-property" name="data-property-input" style="width: 191px; height: 28px"></select>
			      	<input type="text" placeholder="Data Property Value" name="data-property-value" required>
				    <input type="email" placeholder="Enter Email" name="email-input" required>
			      	<button type="submit" style="margin-bottom: 40px">Send Request</button>
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
	
	<script>
		var taxonomy_data = <% out.print(JSONHandler.convertJSONToString(application.getResourceAsStream("taxonomy.json"))); %>
		var individuals_data = <% out.print(JSONHandler.convertJSONToString(application.getResourceAsStream("individuals.json"))); %>
		var dt_properties_data = <% out.print(JSONHandler.convertJSONToString(application.getResourceAsStream("data_properties.json"))); %>
		var obj_properties_data = <% out.print(JSONHandler.convertJSONToString(application.getResourceAsStream("object_properties.json"))); %>
		
		var taxonomy_table = new Tabulator("#taxonomy-table", {
			layout:"fitDataStretch",
		    height:"450px",
		    data:taxonomy_data,
		    dataTree:true,
		    dataTreeStartExpanded:true,
		    dataTreeChildIndent:15,
		    selectable:1,
		    columns:[
		    {title:"Classes (Taxonomy)", field:"class", responsive:0},
		    ],
		});
		
		taxonomy_table.on("cellClick", function(e, cell){
			if(cell.getRow().isSelected()) {
				individuals_table.setFilter([{field:"class", type:"=", value:cell.getValue()}]);
			} else {
				individuals_table.clearFilter();
			}
		});
		
		var individuals_table = new Tabulator("#individuals-table", {
			layout:"fitDataStretch",
		    height:"450px",
		    data:individuals_data,
		    selectable:1,
		    columns:[
		    {title:"Individuals", field:"individual", responsive:0, headerFilter:true, headerFilterPlaceholder:"Filter..."},
		    ],
		});
		
		function taxonomy_load(element) {
			var selectedcell = taxonomy_table.getSelectedRows()[0];
			if(element.id == "create-class") {
				document.getElementById("super-class").defaultValue = selectedcell ? selectedcell.getCells()[0].getValue() : "";
				document.getElementById("create-class-modal").style.display = "block";
				document.getElementById("class-warning").textContent = "";
			} else if(element.id == "create-individual") {
				if(selectedcell) {
					document.getElementById("class").defaultValue = selectedcell.getCells()[0].getValue();
					document.getElementById("create-individual-modal").style.display = "block";
					document.getElementById("individual-warning").textContent = "";
				} else {
					document.getElementById("individual-warning").textContent = "Select a Class!";
				}
			} else if(element.id == "edit-class") {
				if(selectedcell) {
					document.getElementById("edited-class").defaultValue = selectedcell.getCells()[0].getValue();
					document.getElementById("edit-class-modal").style.display = "block";
					document.getElementById("class-warning").textContent = "";
				} else {
					document.getElementById("class-warning").textContent = "Select a Class!";
				}
			} else if(element.id == "delete-class") {
				if(selectedcell) {
					document.getElementById("deleted-class").defaultValue = selectedcell.getCells()[0].getValue();
					document.getElementById("delete-class-modal").style.display = "block";
					document.getElementById("class-warning").textContent = "";
				} else {
					document.getElementById("class-warning").textContent = "Select a Class!";
				}
			}
		}
		
		function individuals_load(element) {
			var selectedcell = individuals_table.getSelectedRows()[0];
			if(element.id == "edit-individual") {
				if(selectedcell) {
					document.getElementById("edited-individual").defaultValue = selectedcell.getCells()[0].getValue();
					document.getElementById("edit-individual-modal").style.display = "block";
					document.getElementById("individual-warning").textContent = "";
				} else {
					document.getElementById("individual-warning").textContent = "Select an Individual!";
				}
			} else if(element.id == "delete-individual") {
				if(selectedcell) {
					document.getElementById("deleted-individual").defaultValue = selectedcell.getCells()[0].getValue();
					document.getElementById("delete-individual-modal").style.display = "block";
					document.getElementById("individual-warning").textContent = "";
				} else {
					document.getElementById("individual-warning").textContent = "Select an Individual!";
				}
			} else if(element.id == "individual-details") {
				if(selectedcell) {
					document.getElementById("individual-name").textContent = selectedcell.getCells()[0].getValue();
					document.getElementById("individual-class").textContent = selectedcell.getData()["class"];
					clear_element_children(document.getElementById("object-properties-list"));
					clear_element_children(document.getElementById("data-properties-list"));
					clear_element_children(document.getElementById('data-property'))
					clear_element_children(document.getElementById('object-property'))
					clear_element_children(document.getElementById('linked-individual'))
					append_children_to_element(document.getElementById("object-properties-list"), selectedcell.getData()["object properties"]);
					append_children_to_element(document.getElementById("data-properties-list"), selectedcell.getData()["data properties"]);
					set_property_options_lists();
					set_linked_individual_options_list(selectedcell.getCells()[0].getValue());
					document.getElementById("individual-details-modal").style.display = "block";
					document.getElementById("individual-warning").textContent = "";
				} else {
					document.getElementById("individual-warning").textContent = "Select an Individual!";
				}
			}
		}
		
		function clear_element_children(element) {
			while (element.hasChildNodes()) {  
				element.removeChild(element.firstChild);
			}
		}
		
		function append_children_to_element(element, list) {
			for(var i = 0; i < list.length; i++) {
				var aux = document.createElement('div');
				aux.textContent = list[i];
				aux.style.marginLeft = "40px";
				aux.style.marginRight = "40px";
				element.appendChild(aux);
			}
		}
		
		function set_property_options_lists() {
			var sel_dt = document.getElementById('data-property');
			for(var i = 0; i < dt_properties_data.length; i++) {
			    var opt = document.createElement('option');
			    opt.innerHTML = dt_properties_data[i]['data property'];
			    sel_dt.appendChild(opt);
			}
			var sel_obj = document.getElementById('object-property');
			for(var i = 0; i < obj_properties_data.length; i++) {
			    var opt = document.createElement('option');
			    opt.innerHTML = obj_properties_data[i]['object property'];
			    sel_obj.appendChild(opt);
			}
		}
		
		function set_linked_individual_options_list(selectedIndividual) {
			var sel_ind = document.getElementById('linked-individual');
			for(var i = 0; i < individuals_data.length; i++) {
				console.log(individuals_data[i]['individual']);
				console.log(selectedIndividual);
				if(individuals_data[i]['individual'] != selectedIndividual) {
					var opt = document.createElement('option');
					opt.innerHTML = individuals_data[i]['individual'];
					sel_ind.appendChild(opt);
				}
			}
		}
		
		document.getElementById("create-class-span").onclick = function() {
			document.getElementById("create-class-modal").style.display = "none";
		}
		document.getElementById("edit-class-span").onclick = function() {
			document.getElementById("edit-class-modal").style.display = "none";
		}
		document.getElementById("delete-class-span").onclick = function() {
			document.getElementById("delete-class-modal").style.display = "none";
		}
		document.getElementById("create-individual-span").onclick = function() {
			document.getElementById("create-individual-modal").style.display = "none";
		}
		document.getElementById("edit-individual-span").onclick = function() {
			document.getElementById("edit-individual-modal").style.display = "none";
		}
		document.getElementById("delete-individual-span").onclick = function() {
			document.getElementById("delete-individual-modal").style.display = "none";
		}
		document.getElementById("individual-details-span").onclick = function() {
			document.getElementById("individual-details-modal").style.display = "none";
		}
		
		window.onclick = function(event) {
			if (event.target == document.getElementById("create-class-modal"))
				document.getElementById("create-class-modal").style.display = "none";
			if (event.target == document.getElementById("edit-class-modal"))
				document.getElementById("edit-class-modal").style.display = "none";
			if (event.target == document.getElementById("delete-class-modal"))
				document.getElementById("delete-class-modal").style.display = "none";
			if (event.target == document.getElementById("create-individual-modal"))
				document.getElementById("create-individual-modal").style.display = "none";
			if (event.target == document.getElementById("edit-individual-modal"))
				document.getElementById("edit-individual-modal").style.display = "none";
			if (event.target == document.getElementById("delete-individual-modal"))
				document.getElementById("delete-individual-modal").style.display = "none";
			if (event.target == document.getElementById("individual-details-modal"))
				document.getElementById("individual-details-modal").style.display = "none";
		}
			
	</script>
</body>

</html>