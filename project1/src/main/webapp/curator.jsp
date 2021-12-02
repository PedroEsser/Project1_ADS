<!DOCTYPE html>
<%@page import="logic.JSONHandler"%>
<html>
<title>Ontology Editor</title>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
<link href="https://unpkg.com/tabulator-tables@5.0.7/dist/css/tabulator.min.css" rel="stylesheet">
<script type="text/javascript" src="https://unpkg.com/tabulator-tables@5.0.7/dist/js/tabulator.min.js"></script>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/9.13.1/styles/github.min.css"/>
<link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/diff2html/bundles/css/diff2html.min.css"/>
<script type="text/javascript" src="https://cdn.jsdelivr.net/npm/diff2html/bundles/js/diff2html-ui.min.js"></script>
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
	  	<input id="editor" type="button" value="Request Management" onclick="" style="cursor: pointer;" class="w3-bar-item w3-hide-small w3-padding-large w3-white"/>
	  	<input id="log" type="button" value="Logout" onclick="window.location.href='login.jsp'" style="cursor: pointer;" class="w3-bar-item w3-hide-small w3-padding-large w3-white w3-right"/>
	  </div>
	</div>
	
	<div style="padding:100px 50px 0px 50px;">
		<div style="float: left; width: 20%;">
			<div id="branches-table"></div>
		</div>
		<div id="diff-container" style="display: none; float: right; width: 76%; margin-bottom: 3%;">
			<div id="diff" style="width: 100%;"></div>
			<form action="script.php" method="post">
				<textarea id="comment" name="comment" placeholder="Leave a comment... (Optional)" style="width: 100%; height: 13vh"></textarea>
				<input id="accept-request" type="submit" name="decision" value="Accept" onclick="taxonomy_load(this)" style="font-size: 15px; cursor: pointer;"/>
				<input id="decline-request" type="submit" name="decision" value="Decline" onclick="taxonomy_load(this)" style="font-size: 15px; cursor: pointer;"/>
			</form>
		</div>
	</div>
	
	<script>
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
//				console.log(cell.getValue())
				document.getElementById('diff-container').style.display = "block"
			} else {
				document.getElementById('diff-container').style.display = "none"
			}
		});
		
		const diffString = `diff --git a/ontology.owl b/ontology.owl
index e3c262f..9e816ac 100644
--- a/ontology.owl
+++ b/ontology.owl
@@ -17,6 +17,7 @@
 Declaration(Class(:G))
 Declaration(Class(:H))
 Declaration(Class(:I))
+Declaration(Class(:New_Class))
 Declaration(ObjectProperty(:Friends))
 Declaration(DataProperty(:maxObjectivesAlgorithmIsAbleToDealWith))
 Declaration(DataProperty(:minObjectivesAlgorithmIsAbleToDealWith))
@@ -59,6 +60,14 @@
 
 SubClassOf(:I :G)
 
+# Class: :New_Class (:New_Class)
+
+SubClassOf(:New_Class :B)
+SubClassOf(:New_Class :D)
+SubClassOf(:New_Class :G)
+SubClassOf(:New_Class :H)
+SubClassOf(:New_Class :I)
+
 
 ############################
 #   Named Individuals`;
		
		document.addEventListener('DOMContentLoaded', function () {
		      var targetElement = document.getElementById('diff');
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
		    });
			
	</script>
</body>

</html>