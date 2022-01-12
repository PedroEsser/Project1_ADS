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
<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js" type="text/javascript"></script>
<script type="text/javascript" src="dropdown.js"></script>
<style>button,body,h1,h2,h3,h4,h5,h6 {font-family: "Times New Roman"}</style>
<style>

.dropbtn {
  min-width: 140px;
  font-size: 16px;
  background: #bbf;
  border: solid 2px black;
  cursor: pointer;
}

.dropdown {
  position: relative;
  display: inline-block;
  margin-left: auto;
}

.dropdown-content {
  display: none;
  position: absolute;
  background-color: #f6f6f6;
  overflow: auto;
  overflow-x: hidden;
  border: 3px solid #ddd;
  width: auto;
  max-height: 250px;
  z-index: 1;
}

.dropdown-content a {
  color: black;
  text-align: left;
  border: solid 1px #ddd;
  display: block;
  padding: 2px 20px 2px 5px;
}

.dropdown-content input {
  color: black;
  text-align: left;
  border: solid 1px #ddd;
  display: block;
  padding: 3px 0 0 10px;
}

.dropdown a:hover {background-color: #ddd;}

.show {
  display: grid;
  grid-auto-flow: row;
}

.query-grid {
  display: grid;
  grid-auto-flow: row;
  gap: 10px;
}

</style>

<body>

	<div class="w3-top">
	  <div class="w3-bar w3-white w3-border w3-large">
	  	<input id="editor" type="button" value="Ontology Editor" onclick="window.location.href='taxonomy.jsp'" style="cursor: pointer;" class="w3-bar-item w3-hide-small w3-padding-large w3-white"/>
	  	<input id="vis" type="button" value="WebVOWL" onclick="window.open('https://webvowl.blackglacier-3bc0d68a.northeurope.azurecontainerapps.io/#iri=https://raw.githubusercontent.com/ADSDummyUser/Knowledge_Base/master/ontology.owl')" style="cursor: pointer;" class="w3-bar-item w3-hide-small w3-padding-large w3-white"/>
	  	<input id="log" type="button" value="Request Manager" onclick="window.location.href='login.jsp'" style="cursor: pointer;" class="w3-bar-item w3-hide-small w3-padding-large w3-white"/>
	  	<input id="import" type="button" value="Import Ontology" onclick="importOntology()" style="float: right; cursor: pointer;" class="w3-bar-item w3-hide-small w3-padding-large w3-white"/>
	  </div>
	</div>
	
	<div style="height: 640px; padding:100px 50px 0px 50px;">
		<h1 class="w3-center" style="margin-bottom: 30px"></h1>
		<div style="float: left; width: 48%;">
			<h2>Dynamic Query</h2>
			<div class="query-grid" id="query_div" style="width: 100%; "></div>
			<div id="select_div" style="width: 100%; margin:30px 0 0 0"></div>
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
				<button onclick="view_webvowl()">Visualize in WebVOWL</button>
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
		
		function importOntology() {
		    let input = document.createElement('input');
		    input.type = 'file';
		    input.onchange = e => {
	            var file = e.target.files[0];
	            var reader = new FileReader();
	            reader.readAsText(file, "UTF-8");
	            reader.onload = function (evt) {
	          		$.post("import", {content: evt.target.result, page: "login.jsp"})
	          		.done(function() {
	          			location.reload();
	          	    });
	            }
	        };
		    input.click();
		}
		
		window.onload = function() {
			var query = `<% if(request.getParameter("query") != null) out.print(request.getParameter("query")); %>`;
			var result = `<% if(request.getParameter("query") != null) out.print(GitHandler.getDefault().getOWLHandler().runSQWRLQuery(request.getParameter("query"))); %>`;
			var result_data = `<% if(request.getParameter("query") != null) out.print(GitHandler.getDefault().getOWLHandler().runJSONSQWRLQuery(request.getParameter("query"))); %>`;
			document.getElementById("query").value = query;
			document.getElementById("result").value = result;
			result_table.setData(result_data);
			var cols = result_table.getColumns();
			cols.forEach(function(col){
				col.updateDefinition({headerFilter:true, headerFilterPlaceholder:"Filter..."})
			});
		};
		var result_table = new Tabulator("#result-table",{
			layout:"fitColumns",
			height:"332px",
			selectable:1,
		    data:[], //assign data to table
		    autoColumns:true, //create columns from data field names
		});
		function view_webvowl() {
			$.post("result_visualization", {result: document.getElementById("query").value });
		}
		
	</script>
	
	<!-- 
	Algorithm(?alg) ^ maxObjectivesAlgorithmIsAbleToDealWith(?alg,?max) ^ swrlb:greaterThanOrEqual(?max,2) minObjectivesAlgorithmIsAbleToDealWith(?alg,?min) ^ swrlb:lessThanOrEqual(?min,2) -> sqwrl:select(?alg, ?max, ?min) ^ sqwrl:orderBy(?alg)
	 -->
	<!-- Dynamic Query -->
	<script>
		
		var classes_data = <%= JSONHandler.convertJSONToString("src/main/webapp/resources/taxonomy.json") %>
		classes_data = getAllClasses(classes_data)
		var dt_properties_data = <%= JSONHandler.convertJSONToString("src/main/webapp/resources/data_properties.json") %>
		dt_properties_data = dt_properties_data.map(x => x["data property"])
		var obj_properties_data = <%= JSONHandler.convertJSONToString("src/main/webapp/resources/object_properties.json") %>
		obj_properties_data = obj_properties_data.map(x => x["object property"])
		var swrl_built_ins = ["swrlb:equal", "swrlb:notEqual", "swrlb:lessThan", "swrlb:lessThanOrEqual", "swrlb:greaterThan", "swrlb:greaterThanOrEqual"]
		var types = ["Class", "Data-property", "Object-property", "Comparison"]
		
		function getAllClasses(taxonomy_data){
			function getSubClasses(super_class){
				
				let subclasses = [super_class["class"]]
				if("_children" in super_class)
					super_class._children.forEach(child => subclasses = subclasses.concat(getSubClasses(child)))
				return subclasses;
			}
			let all_classes = [];
			taxonomy_data.forEach(c => all_classes = all_classes.concat(getSubClasses(c)))
			return all_classes
		}
		
		function fillTypeDropdown(type_dropdown, type){
			switch(type){
				case "Class":addOptions(type_dropdown, classes_data, true)
					break;
				case "Data-property":addOptions(type_dropdown, dt_properties_data, true)
					break;
				case "Object-property":addOptions(type_dropdown, obj_properties_data, true)
					break;
				case "Comparison":addOptions(type_dropdown, swrl_built_ins, true)
					break;
				default:
			}
		}
		
		function addAtom(){
			let query_div = document.getElementById("query_div")
			let atom_div = document.createElement("div")
			atom_div.className = "grid-item"
			query_div.appendChild(atom_div)
			
			let type_select_dropdown = createDropdown("[Select type]")
			addOptions(type_select_dropdown, types, false)
			atom_div.appendChild(type_select_dropdown)
			
			let type_dropdown = createDropdown()
			let variables_dropdown = [createArgumentsDropdown(), createArgumentsDropdown()]
			
			setSelectFunction(type_select_dropdown, choice => {
				if(atom_div.children.length === 1)
					addAtom()
					
				setButtonText(type_dropdown, "[Select " + choice + "]")
				fillTypeDropdown(type_dropdown, choice)
				atom_div.appendChild(type_dropdown)
				
				variables_dropdown.forEach(d => {
					if([...atom_div.children].includes(d)){
						setButtonText(d, "[arg name]")
						atom_div.removeChild(d)
					}
						
				})
				if(choice === "Class")
					setSelectFunction(type_dropdown, c => atom_div.appendChild(variables_dropdown[0]))
				else
					setSelectFunction(type_dropdown, c => variables_dropdown.forEach(d => atom_div.appendChild(d)))
			})
		}
		
		function createArgumentsDropdown(){
			let arguments_dropdown = createDropdown("[arg name]")
			getButton(arguments_dropdown).onclick = () => {
				fillArgumentsDropdown(arguments_dropdown)
				toggleDropdown(arguments_dropdown);
			}
			return arguments_dropdown
		}
		
		function fillArgumentsDropdown(variables_dropdown){
			clearOptions(variables_dropdown)
			let input = document.createElement("input")
	        input.type = "text"
	        input.placeholder = "Argument name"
	        input.onkeypress = (e) => {
	        	if(e.keyCode == 13){			// "Enter" key pressed
	        		setButtonText(variables_dropdown, input.value)
	        		toggleDropdown(variables_dropdown)
	        		queryCheck()
	        	}
	        }
	        getDropdown(variables_dropdown).appendChild(input)
			
			addOptions(variables_dropdown, getUsedArguments(), false, true)
			setSelectFunction(variables_dropdown, a => {})
		}
		
		
		
		function getAtoms(){
			return [...document.getElementById("query_div").children]
		}
		
		function getUsedArguments(){
			let usedVariables = []
			getAtoms().forEach(a => {
				for(let i = 2 ; i < a.children.length ; i++){
					let v = getSelected(a.children[i])
					if(v !== "[arg name]" && !usedVariables.includes(v) && isNaN(v))
						usedVariables.push(v)
				}
			})
			return usedVariables
		}
		
		
		function atomComplete(atom_div){
			if(atom_div.children.length < 3)
				return false
			for(let i = 2 ; i < atom_div.children.length ; i++){
				let v = getSelected(atom_div.children[i])
				if(v === "[arg name]")
					return false
			}
			return true
		}
		
		function generateAtomText(atom_div){
			let atom_text = getSelected(atom_div.children[1]) + "(" + getArgText(atom_div.children[2])
			if(atom_div.children.length === 4)
				atom_text += ", " + getArgText(atom_div.children[3])
			return atom_text + ")"
		}
		
		function getArgText(arg_dropdown){
			let arg = getSelected(arg_dropdown)
			if(isNaN(arg))
				return "?" + arg
			return arg
		}
		
		function generateQueryText(){
			let atoms = getAtoms()
			atoms = atoms.filter(a => atomComplete(a))
			
			let query_text = ""
			if(atoms.length > 0){
				query_text = generateAtomText(atoms[0])
				for(let i = 1 ; i < atoms.length ; i++){
					query_text += " ^ " + generateAtomText(atoms[i])
				}
			}
			if(selectValid()){
				query_text += " -> sqwrl:select("
				let usedArgs = [...document.getElementById("select_div").children]
				for(let i = 1 ; i < usedArgs.length ; i++){
					let v = getSelected(usedArgs[i])
					if(v !== "[arg name]")
						query_text += "?" + getSelected(usedArgs[i]) + ", "
				}
				query_text = query_text.substring(0, query_text.length - 2) + ")"
			}
			
					
			document.getElementById("query").value = query_text
		}
		
		function displayState(dropdown, state){
			let button = getButton(dropdown)
			switch(state){
				case 0:
					button.style.background = "#fbb"
					button.style.border = "dashed 2px black"
					break;
				case 1:
					button.style.background = "#bbf"
						button.style.border = "solid 2px black"
					break;
				case 2:
					button.style.background = "#bfb"
					button.style.border = "solid 2px black"
					break;
				default:
					button.style.background = "#ffb"
					button.style.border = "dashed 2px black"
			}
		}
		
		function selectValid(){
			let select_args = [...document.getElementById("select_div").children]
			for(let i = 1 ; i < select_args.length ; i++)
				if(getSelected(select_args[i]) !== "[arg name]")
					return true
			return false
		}
		
		function queryCheck(){
			generateQueryText()
			let atoms = getAtoms()
			atoms.forEach(a => {
				let complete = atomComplete(a)
				for(let i = 0 ; i < a.children.length ; i++){
					let v = getSelected(a.children[i])
					if(v.startsWith("[") && v.endsWith("]"))
						displayState(a.children[i], 0)
					else
						displayState(a.children[i], complete ? 2 : 1)
				}
			})
			
			let select_args = [...document.getElementById("select_div").children]
			for(let i = 1 ; i < select_args.length ; i++){
				if(getSelected(select_args[i]) !== "[arg name]"){
					displayState(select_args[i], 2)
				}else{
					displayState(select_args[i], 0)
				}
			}
			if(selectValid()){
				displayState(select_args[0], 2)
			}else{
				displayState(select_args[0], 4)
			}
		}
		
		function createSelectDropdown(){
			let select_div = document.getElementById("select_div")
			
			let select_dropdown = createDropdown("Select")
			getButton(select_dropdown).onclick = () => {}
			select_div.appendChild(select_dropdown)
			
			createSelectArgDropdown()
		}
		
		function createSelectArgDropdown(){
			let select_div = document.getElementById("select_div")
			let arg_dropdown = createDropdown("[arg name]")
			getButton(arg_dropdown).onclick = () => {
				clearOptions(arg_dropdown)
				let available_args = getAvaliableArgs()
				available_args.push("[arg name]")
				addOptions(arg_dropdown, available_args, false, true)
				setSelectFunction(arg_dropdown, a => {
					if(a !== "[arg name]" && select_div.lastChild === arg_dropdown){
						createSelectArgDropdown()
					}
					queryCheck()
				})
				toggleDropdown(arg_dropdown);
			}
			select_div.appendChild(arg_dropdown)
		}
		
		function getAvaliableArgs(){
			let available_args = getUsedArguments()
			let usedArgs = [...document.getElementById("select_div").children]
			for(let i = 1 ; i < usedArgs.length ; i++){
				let arg = getSelected(usedArgs[i])
				let index = available_args.indexOf(arg);
				if (index > -1) 
					available_args.splice(index, 1);
			}
			return available_args
		}
		
		addAtom()
		createSelectDropdown()
		queryCheck()
	</script>

</body>
</html>