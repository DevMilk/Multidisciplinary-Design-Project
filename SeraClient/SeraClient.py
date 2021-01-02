import serial
import time
import requests
import json

#Server Ayarı
server = "http://25.45.128.215:8080/sera/notification"
sync_period = -1
temperature = 0
nextTimeToNotify = 0

def notify(new):
	global sync_period
	global nextTimeToNotify
	nextTimeToNotify = time.time()+sync_period
	try:
		data = requests.post(server, json = {"temperature": str(new))})
		data = data.json()
		sync_period = float(data["sync_period"])
		return data["temperature"]
	except:
		return False
	return False
	
	
#Seri Port ayarları
COM_PORT = 'COM1'
BAUD_RATE = 9600
PARITY = serial.PARITY_NONE
STOP_BITS = serial.STOPBITS_ONE
BYTE_SIZE = serial.EIGHTBITS
TIMEOUT = 0
s = None





while(1):
	lastChange = "0"
	#Döngü
	try:
		s = serial.Serial(
		port=COM_PORT,\
		write_timeout = 4, \
		baudrate=BAUD_RATE,\
		parity=PARITY,\
		stopbits=STOP_BITS,\
		bytesize=BYTE_SIZE,\
			timeout=TIMEOUT)
			
		while(s.isOpen()):
			time.sleep(0.35)
			try:
					temperature = float(s.readline())
					s.reset_output_buffer()
					s.reset_input_buffer()
					
					print(temperature)
					if(nextTimeToNotify<=time.time()):
						isNewRequest = notify(temperature)
						if(isNewRequest and lastChange != isNewRequest):
							time.sleep(0.1)
							print("İstenen Sıcaklık: ",isNewRequest)
							s.reset_output_buffer()
							s.reset_input_buffer()
							cevap = s.write(isNewRequest.encode())
							s.flush()
							time.sleep(0.35)
							lastChange = isNewRequest
			except:
				pass
	except:
		time.sleep(0.1)
		print("COM Port Bağlantısı Bekleniyor...")
		



			

			
    
		
		
		
