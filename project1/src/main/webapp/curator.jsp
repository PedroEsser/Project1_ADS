<!DOCTYPE html>
<%@page import="logic.JSONHandler"%>
<%@page import="logic.CuratorHandler"%>
<% JSONHandler.updateJSONs(); %>
<html>
<title>Request Manager</title>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
<link href="https://unpkg.com/tabulator-tables@5.0.7/dist/css/tabulator.min.css" rel="stylesheet">
<script type="text/javascript" src="https://unpkg.com/tabulator-tables@5.0.7/dist/js/tabulator.min.js"></script>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/9.13.1/styles/github.min.css"/>
<link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/diff2html/bundles/css/diff2html.min.css"/>
<script type="text/javascript" src="https://cdn.jsdelivr.net/npm/diff2html/bundles/js/diff2html-ui.min.js"></script>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js" type="text/javascript"></script>
<style>
	button,body,h1,h2,h3,h4,h5,h6 {
		font-family: "Times New Roman"
	}
	.d2h-file-side-diff {
	    overflow: scroll;
	    height: 55vh;
	}
	.d2h-code-wrapper {
	    position: relative;
	}
</style>

<body>

	<div class="w3-top">
	  <div class="w3-bar w3-white w3-border w3-large">
	  	<input id="log" type="button" value="Ontology Editor" onclick="window.location.href='taxonomy.jsp'" style="cursor: pointer;" class="w3-bar-item w3-hide-small w3-padding-large w3-white"/>
	  	<input id="vis" type="button" value="WebVOWL" onclick="window.open('https://webvowl.blackglacier-3bc0d68a.northeurope.azurecontainerapps.io/#iri=https://raw.githubusercontent.com/ADSDummyUser/Knowledge_Base/master/ontology.owl')" style="cursor: pointer;" class="w3-bar-item w3-hide-small w3-padding-large w3-white"/>
	  	<input id="editor" type="button" value="Request Manager" style="cursor: pointer;" class="w3-bar-item w3-hide-small w3-padding-large w3-white"/>
	  	<input id="import" type="button" value="Import Ontology" onclick="importOntology()" style="float: right; cursor: pointer;" class="w3-bar-item w3-hide-small w3-padding-large w3-white"/>
	  </div>
	</div>
	
	<div style="padding:100px 50px 0px 50px;">
		<div style="float: left; width: 20%;">
			<div id="branches-table"></div>
		</div>
		<div id="diff-container" style="display: none; float: right; width: 76%; margin-bottom: 3%;">
			<div id="diff" style="width: 100%;"></div>
			<form action="curator" method="post">
				<input id="email" type="hidden" name="email"/>
				<input id="password" type="hidden" name="password"/>
				<input id="branch" type="hidden" name="branch"/>
				<textarea id="comment" name="comment" placeholder="Leave a comment... (Optional)" style="width: 100%; height: 13vh"></textarea>
				<input id="accept-request" type="submit" name="decision" value="Accept" style="font-size: 15px; cursor: pointer;"/>
				<input id="decline-request" type="submit" name="decision" value="Decline" style="font-size: 15px; cursor: pointer;"/>
			</form>
		</div>
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
	
		var authorized = <% String email = request.getParameter("email");
							String password = request.getParameter("password");
							out.println(CuratorHandler.authenticateCurator(email, password)); %>
		
		if(authorized) {
			var branches_data = <%= JSONHandler.convertJSONToString("src/main/webapp/resources/branches.json") %>
			
			var branches_table = new Tabulator("#branches-table", {
				layout:"fitDataStretch",
			    height:"80vh",
			    data:branches_data,
			    selectable:1,
			    columns:[
			    {title:"Branches", field:"branch", responsive:0, headerFilter:true, headerFilterPlaceholder:"Filter..."},
			    ],
			});
			
			branches_table.on("cellClick", function(e, cell){
				if(cell.getRow().isSelected()) {
					var targetElement = document.getElementById('diff');
					var diffString = branches_data.find(item=>item.branch==cell.getValue())["diff"];
					var configuration = {
					  drawFileList: false,
					  matching: 'lines',
					  outputFormat: 'side-by-side',
					  synchronisedScroll: true,
					  highlight: true,
					  renderNothingWhenEmpty: false,
					};
					var diff2htmlUi = new Diff2HtmlUI(targetElement, diffString, configuration);
					diff2htmlUi.draw();
					diff2htmlUi.highlightCode();
					document.getElementById('diff-container').style.display = "block";
				} else {
					document.getElementById('diff-container').style.display = "none";
				}
			});
			
			$(document).on("submit", "form", function (e) {
				document.getElementById("branch").value = branches_table.getSelectedRows()[0].getCells()[0].getValue();
				document.getElementById("email").value = "<%= email %>";
				document.getElementById("password").value = "<%= password %>";
				var form = $(this);
			    $.ajax({
					type: "POST",
			      	url: form.attr('name'),
			      	data: form.serialize()
			    });
			});
		} else {
			alert("Acess Denied!");
		}
		
	</script>
</body>

</html>