const IP_ADDRESS = "192.168.1.38"
const PORT = "8080"
const root_endpoint = "http://"+IP_ADDRESS+":"+PORT+"/sera";
const PERIOD = 1; //Saniye cinsinden refresh
function getAll(){
	const xhr = new XMLHttpRequest();
	xhr.open("GET", root_endpoint);
    xhr.send();
    xhr.onreadystatechange = function () {
      if (this.readyState === 4   && 
		  this.status     ==  200 &&
  	      this.status < 300) {
			  if(this.responseText=="")
				  return
			  result = JSON.parse(this.responseText);
			  handle(result);
		  }
    }
	
}

function change(ip,valueName,value){
	const xhr = new XMLHttpRequest();   // new HttpRequest instance 
	var requestBody = {"ip":ip,"valueName":valueName,"value":value};
	xhr.open("POST", root_endpoint);
	xhr.setRequestHeader("Content-Type", "application/json");
    xhr.send(JSON.stringify(requestBody));
	
	
}
function configure(period,timeout){
	if((isNaN(timeout)) && (isNaN(period)))
		return;
	
	const xhr = new XMLHttpRequest();   // new HttpRequest instance 
	var requestBody = {}

	if(!isNaN(period) && period!="")
		requestBody["period"] = period;
		
	if(!isNaN(timeout) && timeout!="")
		requestBody["timeout"] = timeout;
		
	console.log(requestBody);
	xhr.open("POST", root_endpoint+"/configure");
	xhr.setRequestHeader("Content-Type", "application/json");
    xhr.send(JSON.stringify(requestBody));
	
}
function handle(result){
	//Bu Json değerini html'de göstergelerle göster
		console.log(result);
		for (var key in result){ 
			for(valueName in result[key]){
				let value = result[key][valueName];
				console.log(key,"temperature",value);
				addOrChangeSera(key,toPascalCase(valueName),value);
			}
		}
}



function httpGet(theUrl)
{
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.open( "GET", theUrl, false ); // false for synchronous request
    xmlHttp.send( null );
    return xmlHttp.responseText;
}
/*getAll();
setInterval(getAll,PERIOD*1000);*/


console.log("set");