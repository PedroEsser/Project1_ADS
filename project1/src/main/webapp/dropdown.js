function createDropdown(buttonText){
    let dropdown_div = document.createElement("div")
    dropdown_div.className = "dropdown"

    let drop_button = document.createElement("button")
    drop_button.className = "dropbtn"
	if(buttonText)
    	drop_button.textContent = buttonText
    dropdown_div.appendChild(drop_button)
    
    let dropdown = document.createElement("div")
    dropdown.className = "dropdown-content"
    drop_button.onclick = () => toggleDropdown(dropdown_div)
    dropdown_div.appendChild(dropdown)
		
    return dropdown_div
}

function setButtonText(dropdown_div, text){
	getButton(dropdown_div).textContent = text
}

function getSelected(dropdown_div){
	return getButton(dropdown_div).textContent
}

function setSelectFunction(dropdown_div, selectFunction){
    getOptions(dropdown_div).forEach(a => a.onclick = () => {
        setButtonText(dropdown_div, a.innerHTML)
        toggleDropdown(dropdown_div)
        selectFunction(a.innerHTML)
		queryCheck()
    })
}

function getButton(dropdown_div){
	return dropdown_div.getElementsByTagName("button")[0]
}

function getDropdown(dropdown_div){
	return dropdown_div.getElementsByTagName("div")[0]
}

function getOptions(dropdown_div){
    return Array.from(getDropdown(dropdown_div).getElementsByTagName("a"))
}

function addOptions(dropdown_div, options, withSearch, noClear){
	let dropdown = getDropdown(dropdown_div)
	if(!noClear)
		dropdown.innerHTML = ""
	if(withSearch){
        let input = document.createElement("input")
        input.type = "text"
        input.placeholder = "Search.."
        input.onkeyup = () => filterFunction(input, dropdown)
        dropdown.appendChild(input)
    }
    options.forEach(b => {
        let a = document.createElement("a")
        a.textContent = b
        dropdown.appendChild(a)
    })
}

function clearOptions(dropdown_div){
	getDropdown(dropdown_div).innerHTML = ""
}

function setVisible(dropdown_div, visible){
	if(visible)
		dropdown_div.display = "inline-block"
	else
		dropdown_div.hide()
}

function toggleDropdown(dropdown_div) {
	getDropdown(dropdown_div).classList.toggle("show");
}

function filterFunction(input, dropdown) {
  var input, filter, a, i;
  filter = input.value.toUpperCase();
  a = dropdown.getElementsByTagName("a");
  for (i = 0; i < a.length; i++) {
    txtValue = a[i].textContent || a[i].innerText;
    if (txtValue.toUpperCase().indexOf(filter) > -1) {
      a[i].style.display = "";
    } else {
      a[i].style.display = "none";
    }
  }
}