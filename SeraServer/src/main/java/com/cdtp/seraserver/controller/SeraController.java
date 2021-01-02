package com.cdtp.seraserver.controller;
import org.springframework.stereotype.Controller;

import com.cdtp.seraserver.Exceptions.GreenHouseNotFoundException;
import com.cdtp.seraserver.contexts.CommandContext;
import com.cdtp.seraserver.contexts.ConfigContext;
import com.cdtp.seraserver.domain.GreenHouse;
import com.cdtp.seraserver.services.SeraService;
import com.cdtp.seraserver.services.SeraServiceImpl;
import com.cdtp.seraserver.services.TimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/sera")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class SeraController {
    // 1000 ms = 1 s
    private float notificationPeriod = 1;
    private float TIME_OUT_S = 3;
    private Boolean ONLY_MONITOR_READS = false;

    @Autowired
    private SeraService seraService;

    @GetMapping("")
    public ResponseEntity<Map<String,GreenHouse>> getAll(HttpServletRequest request) {
        if(ONLY_MONITOR_READS){
            try{
                seraService.getClientByIp(getIpOfRequest(request));
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            } catch(GreenHouseNotFoundException e){}
        }
        seraService.controlConnection((int)(TIME_OUT_S*1000));
        return ResponseEntity.ok(seraService.getClients());
    }

    //Change valueName field of greenhouse where the ip address belongs with the value that given by the monitor client
    @PostMapping("")
    public ResponseEntity changeValue(@RequestBody(required = true) CommandContext requestBody) throws GreenHouseNotFoundException {
        //Clientlerle haberleşip onlara değiştirmelerini söylemeli.
        if(requestBody.getName()== null ||  requestBody.getValuename()==null)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not All Parameters not provided");
        String ip = seraService.getIpFromName(requestBody.getName());
        seraService.addCommand(ip,requestBody.getValuename(),Float.toString(requestBody.getValue()));
        System.out.println("Monitor requested "+requestBody.getValuename()+" as "+requestBody.getValue()+" for " +requestBody.getName());
        return ResponseEntity.ok().build();
    }

    //Get status information about clients
    @PostMapping("/notification")
    public ResponseEntity notification(HttpServletRequest request, @RequestBody(required = false) Map<String,String> valueMap) {
        String ip_address = getIpOfRequest(request);
        Map<String,String> response = new HashMap<>(2);
        GreenHouse client = null;

        try {
            //Kayıtlıysa:

            client = seraService.getClientByIp(ip_address); // client'ı al
            Map<String,String> newCommand = seraService.getCommand(ip_address); //client'a komutu bildir
            if(newCommand!=null)
                response.putAll(newCommand);
        } catch(GreenHouseNotFoundException e){
            //Kayıtlı Değilse:

            if(seraService.getClients().size()<seraService.getCapacity()){
                seraService.addClient(new HashMap<>(),ip_address);
                client = seraService.getClients().get(ip_address);
            }
            else
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Sera Client Capacity Reached. Cannot add another Sera Client.");
        }
        if(valueMap!=null && !valueMap.isEmpty())
            seraService.changeClientValue(ip_address,valueMap); //valueMap sağlanmışsa değişimi gerçekleştir

        client.setNextNotificationTime(TimeService.getTime()+(int)(notificationPeriod*1000));
        response.put("sync_period",Float.toString(this.notificationPeriod)); //Eğer yer varsa client'ı kayıt et ve senkron et
        return ResponseEntity.ok(response);
    }



    @PostMapping("/server_configure")
    public ResponseEntity<Void> configure(@RequestBody ConfigContext requestBody){
        this.notificationPeriod = requestBody.getPeriod()!=-1 ? requestBody.getPeriod() : this.notificationPeriod;
        this.TIME_OUT_S = requestBody.getTimeout()!=-1 ? requestBody.getTimeout()  : this.TIME_OUT_S;

        seraService.addCommandToAllEmployees("sync_period",Float.toString(this.notificationPeriod));

        System.out.println("Monitor requested "+requestBody.getPeriod()+" with "+requestBody.getTimeout());

        return ResponseEntity.ok().build();
    }

    @GetMapping("/server_configure")
    public ResponseEntity configure(){
        return ResponseEntity.ok(new float[]{this.notificationPeriod,this.TIME_OUT_S});
    }
    private String getIpOfRequest(HttpServletRequest request) {
        //{sec-fetch-mode=cors, content-length=95, referer=http://127.0.0.1:8080/swagger-ui.html, sec-fetch-site=same-origin, accept-language=tr-TR,tr;q=0.9,en-US;q=0.8,en;q=0.7, origin=http://127.0.0.1:8080, dnt=1, accept=*/*, host=127.0.0.1:8080, connection=keep-alive, content-type=application/json, accept-encoding=gzip, deflate, br, user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36, sec-fetch-dest=empty}
        return request.getRemoteAddr();
    }


}
