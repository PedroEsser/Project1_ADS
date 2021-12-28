<!DOCTYPE html>
<%@page import="logic.JSONHandler"%>
<%@page import="logic.CuratorHandler"%>
<%@page import="logic.GitHandler"%>
<html>
<title>Request Manager</title>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/9.13.1/styles/github.min.css"/>
<link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/diff2html/bundles/css/diff2html.min.css"/>
<script type="text/javascript" src="https://cdn.jsdelivr.net/npm/diff2html/bundles/js/diff2html-ui.min.js"></script>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js" type="text/javascript"></script>
<style>
	button,body,h1,h2,h3,h4,h5,h6 {
		font-family: "Times New Roman"
	}
	.d2h-file-diff {
	    overflow: scroll;
	    height: 463px;
	}
	.d2h-code-wrapper {
	    position: relative;
	}
</style>

<body>

	<div class="w3-top">
	  <div class="w3-bar w3-white w3-border w3-large">
	  	<input id="log" type="button" value="Ontology Editor" style="cursor: pointer;" class="w3-bar-item w3-hide-small w3-padding-large w3-white"/>
	  	<input id="vis" type="button" value="WebVOWL" style="cursor: pointer;" class="w3-bar-item w3-hide-small w3-padding-large w3-white"/>
	  	<input id="editor" type="button" value="Request Manager" style="cursor: pointer;" class="w3-bar-item w3-hide-small w3-padding-large w3-white"/>
	  </div>
	</div>
	
	<div id="div" class="w3-center" style="display: none; padding:100px 50px 0px 50px;">
		<div id="diff" style="float: left; width: 48%;"></div>
		<form action="conflict" method="post">
			<div style="float: right; width: 48%; margin-bottom: 20px;">
				<input id="email" type="hidden" name="email"/>
				<input id="password" type="hidden" name="password"/>
				<textarea id="file" name="file" style="width: 100%; height: 500px; margin-bottom: 10px" required></textarea>
				<button type="submit">Confirm</button>
			</div>
		</form>
	</div>
	
	<script>
		var authorized = <% String email = request.getParameter("email");
							String password = request.getParameter("password");
							out.println(CuratorHandler.authenticateCurator(email, password)); %>
	
		if(authorized) {
			var branches_data = <%= JSONHandler.convertJSONToString("src/main/webapp/resources/branches.json") %>
			var branch = "<%= request.getParameter("branch") %>";
			
			window.onload = function() {
				$.post("revert", {email: "<%= email %>", password: "<%= password %>"});
				
				var file = `<% GitHandler handler = GitHandler.getDefault();
							  out.println(handler.getOntologyFileContent()); %>`;
				document.getElementById("file").value = file;
				document.getElementById("div").style.display = "block";
				
				var targetElement = document.getElementById('diff');
				var diffString = branches_data.find(item=>item.branch==branch)["diff"];
				var configuration = {
				  drawFileList: false,
				  matching: 'lines',
				  synchronisedScroll: true,
				  highlight: true,
				  renderNothingWhenEmpty: false,
				};
				var diff2htmlUi = new Diff2HtmlUI(targetElement, diffString, configuration);
				diff2htmlUi.draw();
				diff2htmlUi.highlightCode();
			};
			
			$(document).on("submit", "form", function (e) {
				if(document.getElementById("file").value.match("[<=>]{7}") == null) {
					document.getElementById("email").value = "<%= email %>";
					document.getElementById("password").value = "<%= password %>";
					var form = $(this);
				    $.ajax({
						type: "POST",
				      	url: form.attr('name'),
				      	data: form.serialize()
				    });
				} else {
					e.preventDefault();
					alert("Resolve the conflict first!");
				}
			});
		} else {
			alert("Acess Denied!");
		}
	
	</script>
</body>

</html>