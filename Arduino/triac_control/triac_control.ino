#include <WiFi.h>
#include <WiFiClient.h>
#include <WiFiServer.h>
#include <WiFiUdp.h>

WiFiClient client;
// Emulate Serial1 on pins 6/7 if not present
#ifndef HAVE_HWSERIAL1
#include "SoftwareSerial.h"
SoftwareSerial Serial1(6, 7); // RX, TX
#endif
#include <ArduinoJson.h>


unsigned long previousMillis = 0;
unsigned long currentMillis = 0;
int temp_read_Delay = 100;

//PID variables
float elapsedTime, Time, timePrev;
float errorR=0;
float errorPR=0;
float IntegralToplamR=0;
float DerivR=0;
float ProportionalPartR=0;
float IntegralPartR=0;
float DerivativePartR=0;
float OutR1=0;
int OutR=0;
float KP = 3000;
float KI = 800;
float KD = 100;
int pwm = 3;
float sys_out = A0;

float real_temperature = 0;
int setpoint = 30;


const String SERVER = "http://25.45.128.215:8080/sera/notify";
const int PORT = 8080;
int commRate = 5; //Saniye cinsinden servis ile haberleşme aralığı
  
void communicateWithService(){
  if (client.connect(SERVER, PORT)) {
    Serial.println("Connected to server");
    // Make a HTTP request
    String content = "Hey, just testing a post request.";
    client.println("POST YOUR_RESOURCE_URI HTTP/1.1");
    client.println("Host: SERVER:PORT");
    client.println("Accept: */*");
    client.println("Content-Length: " + content.length());
    client.println("Content-Type: application/x-www-form-urlencoded");
    client.println();
    client.println(content);
  }
}


}

void setup() {
  Serial.begin(9600);
  pinMode(sys_out, INPUT);
  pinMode(pwm, OUTPUT);
  attachInterrupt(0, zero_crossing, RISING);
  WiFi.init(&Serial1);

  // check for the presence of the shield
  if (WiFi.status() == WL_NO_SHIELD) {
    Serial.println("WiFi shield not present");
    // don't continue
    while (true);
  }

  // attempt to connect to WiFi network
  while ( status != WL_CONNECTED) {
    Serial.print("Attempting to connect to WPA SSID: ");
    Serial.println(ssid);
    // Connect to WPA/WPA2 network
    status = WiFi.begin(ssid, pass);
  }
  
  
}
void loop() {
  currentMillis = millis();           //Save the value of time before the loop

  if(currentMillis-previousMillis>=commRate){
    communicateWithService();
  }
  
  if (currentMillis - previousMillis >= temp_read_Delay) {
    previousMillis += temp_read_Delay;              //Increase the previous time for next loop
    float sys_out = analogRead(A0);  //get the real temperature in Celsius degrees
    real_temperature = 25+sys_out / 29.42;

    errorR = setpoint - real_temperature;    //Calculate the pid ERROR
    timePrev = Time;                    // the previous time is stored before the actual time read
    Time = millis();                    // actual time read
    elapsedTime = (Time - timePrev) / 1000;

    IntegralToplamR = IntegralToplamR + elapsedTime * (errorR + errorPR) / 2.00;
    if (IntegralToplamR >= 10000.0) {
      IntegralToplamR = 10000.0;
    }
    if (IntegralToplamR <= -10000.0) {
      IntegralToplamR = -10000.0;
    }

    DerivR = (errorR - errorPR) / elapsedTime;

    ProportionalPartR = KP * errorR;

    IntegralPartR = KI * IntegralToplamR;

    DerivativePartR = KD * DerivR;

    OutR1 = ProportionalPartR + IntegralPartR + DerivativePartR;

    if (OutR1 >= 7500) {
      OutR = 7500;
    }
    else if (OutR1 <= 1000) {
      OutR = 1000;
    }
    else {
      OutR = OutR1;
    }

    errorPR = errorR;
    Serial.println(real_temperature);

  }
}
void zero_crossing(){
    delayMicroseconds(10000 - OutR); //This delay controls the power
      digitalWrite(3,HIGH);         
      delayMicroseconds(100);
      digitalWrite(3,LOW);
}
