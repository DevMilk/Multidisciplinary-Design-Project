var monitor = document.querySelector("body > div:nth-child(1)");
monitor.innerHTML = "";
function getInput(valueName){
	do{
		var value = prompt("Enter the requested value of "+valueName+":");
		if(value==null)
		  return

		if (value != parseInt(value, 10)|| value <=0)
		  alert("Only enter numbers");
	  
	}while(value != parseInt(value, 10) || value <=0);

	return value;
  
}


function monitorClickFunc(event) {
	let valueName = event.target.innerHTML;
	let targetName = event.target.parentElement.getElementsByClassName("def")[0].innerText;
	let value = getInput(valueName);
	valueName = valueName.toLowerCase();
	change(targetName,valueName,value);
}

function configClickFunc(event){
	let valueName = event.target.innerHTML;
	let targetName = event.target.parentElement.getElementsByClassName("def")[0].innerText;
	let value = getInput(valueName);
	valueName = valueName.toLowerCase();
	configure(valueName,value);
}


/*SERVER FUNCTIONS*/
function toPascalCase(str) {
    var arr = str.split(" ");
    for(var i=0,l=arr.length; i<l; i++) {
        arr[i] = arr[i].substr(0,1).toUpperCase() + 
                 (arr[i].length > 1 ? arr[i].substr(1).toLowerCase() : "");
    }
    return arr.join(" ");
}
function changeVal(indicatorList,valueName,value){
	let indicators = indicatorList.getElementsByClassName("indicator");
	for(var i=0;i<indicators.length;i++){
		let cells = indicators[i].getElementsByTagName("div");
		for(var j = 0; j<cells.length; j++){
				if(cells[j].innerText==toPascalCase(valueName)){
					cells[j+1].innerText = value;
					return true;
				}
		}
	}
	return false;
}
function changeValue(indicatorElement,valueName,value){
	let cells = indicatorElement.getElementsByTagName("div");
	for(var j = 1; j<cells.length; j+=2){
			if(cells[j].innerText==toPascalCase(valueName)){
				
				cells[j+1].innerText = value+"C";
				return true;
			}
	}
	return false;
}
function changeIfSeraExists(name,valueName,value){
	var indicators = monitor.getElementsByClassName("indicator");
	for(var i =0;i<indicators.length; i++){
		if(indicators[i].getElementsByTagName("div")[0].innerText==name){
			changeValue(indicators[i],valueName,value);
			return true;
		}
	}
	return false;
}
function addOrChangeSera(name,valueName,value,isDown=false){

	
	if(true){
		let Down = isDown ? " Down" : "";
		let html = "<div> \
						<div class='indicator grid-container hover"+Down+"'> \
							<div class='def'>"+name+"</div> \
							<div>"+toPascalCase(valueName)+"</div> \
							<div>"+value+"C</div> \
						</div> \
					</div>";
							
		monitor.innerHTML+=html;
	}
	
	
	var indicators = document.getElementsByClassName("indicators")[0].getElementsByClassName("indicator");
	for(var i=0;i<indicators.length;i++){
		let cells = indicators[i].getElementsByTagName("div");
		for(var j = 1; j<cells.length; j+=2){
			if(cells[j].onclick==null){
				cells[j].onclick = monitorClickFunc;
				cells[j].classList.add("attribHover");
			}
		}
	}
}

document.querySelector("#periodIndicator > div.def").onclick = configClickFunc;
document.querySelector("#timeoutIndicator > div.def").onclick = configClickFunc;
document.querySelector("#periodIndicator > div.def").classList.add("attribHover");
document.querySelector("#timeoutIndicator > div.def").classList.add("attribHover");