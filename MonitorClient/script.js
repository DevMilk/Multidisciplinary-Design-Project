const IP_ADDRESS = "25.45.128.215"
const PORT = "8080"
const root_endpoint = "http://"+IP_ADDRESS+":"+PORT+"/sera";
let PERIOD = 1; //Saniye cinsinden refresh

function GET(enpoint_prefix,handleFunction){
	
	const xhr = new XMLHttpRequest();
	xhr.open("GET", root_endpoint+enpoint_prefix);
    xhr.send();
    xhr.onreadystatechange = function () {
      if (this.readyState === 4   && 
		  this.status     ==  200 &&
  	      this.status < 300) {
			  if(this.responseText=="")
				  return
			  result = JSON.parse(this.responseText);
			  handleFunction(result);
		  }
    }
}
function POST(endpoint_prefix, requestBody){
	const xhr = new XMLHttpRequest();   // new HttpRequest instance 
	xhr.open("POST", root_endpoint+endpoint_prefix);
	xhr.setRequestHeader("Content-Type", "application/json");
    xhr.send(JSON.stringify(requestBody));
}
function log(str){
	let cons = document.getElementById("console");
	var today = new Date();
	var time = today.getHours() + ":" + today.getMinutes();
	cons.innerHTML = cons.innerHTML + time + ": "+str+"<br>";
	
}
function logValueChange(valueName,value, seraName=""){
	
	let str = "Requested value "+value+" for "+valueName;
	let prefix = seraName=="" ? "" : " of "+seraName;
	
	log(str+prefix);
}
function getAll(){
	GET("",handle);
}
function configHandle(result){
	let configMonitor = document.getElementsByClassName("indicators")[1];
	let indicators = configMonitor.getElementsByClassName("indicator");
	document.querySelector("#periodIndicator > div:nth-child(2)").innerText = result[0];
	document.querySelector("#timeoutIndicator > div:nth-child(2)").innerText = result[1];
}
function handle(result){
	//Bu Json değerini html'de göstergelerle göster
		//console.log("Result: ",result);
		monitor.innerHTML = "";
		let valueName = "temperature";
		for (var key in result){ 
			/*for(valueName in result[key]["values"]){
				var value = result[key]["values"][valueName];
				//console.log(key,"temperature",value);
			}*/
			
			addOrChangeSera(result[key]["name"],toPascalCase(valueName),result[key]["values"][valueName],result[key]["isDown"]);
		}
}


function getConfig(){
	GET("/server_configure",configHandle);
}


function change(SeraName,valueName,value){
	
	var requestBody = {"name":SeraName,"valuename":valueName,"value":value};
	POST("",requestBody);
	logValueChange(valueName,value,SeraName);
}
function configure(valueName,value){
	if(valueName.toLowerCase()!="period" && valueName.toLowerCase()!="timeout")
		return;
	
	var requestBody = {};
	requestBody[valueName]=value;
	POST("/server_configure",requestBody);
	logValueChange(valueName,value);
}


getAll();
setInterval(getAll,PERIOD*1000);
getConfig();
setInterval(getConfig,PERIOD*1000);

console.log("set");