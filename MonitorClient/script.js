
const PERIOD = 1; //Saniye cinsinden refresh
function getAll(){
	const xhr = new XMLHttpRequest();
	xhr.open("GET", "http://192.168.1.38:8080/sera/");
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
	xhr.open("POST", "http://127.0.0.1:8080/sera");
	xhr.setRequestHeader("Content-Type", "application/json");
    xhr.send(JSON.stringify(requestBody));
	
	
}
function handle(result){
	//Bu Json değerini html'de göstergelerle göster
		console.log(result);
		for (var key in result) 
			console.log(result[key].values["temperature"]);

	  document.getElementById("result").innerHTML= result["127.0.0.1"].values["temperature"];
}



function httpGet(theUrl)
{
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.open( "GET", theUrl, false ); // false for synchronous request
    xmlHttp.send( null );
    return xmlHttp.responseText;
}
getAll();
setInterval(getAll,PERIOD*1000);