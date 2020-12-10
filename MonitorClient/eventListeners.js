
function rightClickFunction(event) {
	if(event.which!=3)
		return
  var txt;
  do{
	  
  var value = prompt("Enter the requested value of "+event.target.innerHTML+":");
  if(value==null)
	  return
  if (value != parseInt(value, 10)){
	  alert("Only enter numbers");
  }
  }while(value != parseInt(value, 10));

  alert("Request Send");
}
var monitor = document.querySelector("body > div:nth-child(1)");
var indicators = monitor.getElementsByClassName("indicator");
for(var i =0;i<indicators.length; i++){
	let cells = indicators[i].getElementsByTagName("div");
	for(var j = 1; j<cells.length; j+=2){
		cells[j].onclick = rightClickFunction;
	}
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
	var monitor = document.querySelector("body > div:nth-child(1)");
	var indicators = monitor.getElementsByClassName("indicator");
	for(var i =0;i<indicators.length; i++){
		if(indicators[i].getElementsByTagName("div")[0].innerText==name){
			console.log("BE");
			changeValue(indicators[i],valueName,value);
			return true;
		}
	}
	return false;
}
function addOrChangeSera(name,valueName,value){
	var monitor = document.querySelector("body > div:nth-child(1)");
	if(changeIfSeraExists(name,valueName,value)==false){
		monitor.insertAdjacentHTML("beforeend","<div> \
								<div class='indicator grid-container hover'> \
									<div class='def'>"+name+"</div> \
									<div>"+toPascalCase(valueName)+"</div> \
									<div>"+value+"C</div> \
							</div> \
							");
	}
	
}


function requestSettings(){
	let inputs = document.getElementsByTagName("input");
	
	let period = parseFloat(inputs[0].value);
	let timeout = parseFloat(inputs[1].value);
	let notifyPeriod = parseFloat(inputs[2].value);
	configure(notifyPeriod,timeout);
	
}
var btn = document.getElementsByClassName("submit")[0];
btn.onmousedown = requestSettings;