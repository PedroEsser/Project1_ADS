<!DOCTYPE html>
<html>
<title>Login</title>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
<style>
	button,body,h1,h2,h3,h4,h5,h6 {font-family: "Times New Roman"}
</style>

<body>
	<div class="w3-top">
	  <div class="w3-bar w3-white w3-border w3-large">
	  	<input id="editor" type="button" value="Ontology Editor" onclick="window.location.href='taxonomy.jsp'" style="cursor: pointer;" class="w3-bar-item w3-hide-small w3-padding-large w3-white"/>
	  	<input id="log" type="button" value="Request Manager" style="cursor: pointer;" class="w3-bar-item w3-hide-small w3-padding-large w3-white"/>
	  </div>
	</div>

	<div id="div" class="w3-center" style="position:absolute; top:0px; right:0px; bottom:0px; left:0px; padding:100px 50px 0px 50px;">
		<h1 style="margin-bottom: 30px">Welcome</h1>
		<form action="login" method="post">
			<div style="margin-bottom: 15px">
				<label for="email" style="margin-right: 32px">Email:</label>
		      	<input type="text" placeholder="Enter Email" name="email" required>
			</div>
	  		<div style="margin-bottom: 25px">
				<label for="password" style="margin-right: 10px">Password:</label>
	      		<input type="password" placeholder="Enter Password" name="password" required>
			</div>
	      	<button type="submit">Access</button>
      	</form>
	</div>
</body>
</html>