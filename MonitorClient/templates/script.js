
function getAll(){
	const xhr = new XMLHttpRequest();
	xhr.open("GET", "http://192.168.1.38:8080/sera");
    xhr.send();
    xhr.onreadystatechange = function () {
      if (this.readyState === 4   && 
		  this.status     ==  200 &&
  	      this.status < 300) {
			  result = JSON.parse(this.responseText);
			  document.getElementById("result").innerHTML= this.responseText;
			  handle(result);
			  console.log(result[0]["ip"]);
		  }
    }
	
}

function change(index,valueName,value){
	var xmlhttp = new XMLHttpRequest();   // new HttpRequest instance 
	xmlhttp.open("POST", "http://127.0.0.1:8080/sera?index="+index+"&value="+value+"&valueName="+valueName);
	xmlhttp.send();
	
	
}
function handle(result){
	//Bu Json değerini html'de göstergelerle göster
}



function httpGet(theUrl)
{
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.open( "GET", theUrl, false ); // false for synchronous request
    xmlHttp.send( null );
    return xmlHttp.responseText;
}
getAll();
setInterval(getAll,5000);
change(0,"dazes",6);