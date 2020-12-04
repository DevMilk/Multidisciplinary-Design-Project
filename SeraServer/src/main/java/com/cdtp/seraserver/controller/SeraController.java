package com.cdtp.seraserver.controller;

import com.cdtp.seraserver.Exceptions.GreenHouseNotFoundException;
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
    private int notificationPeriod = 5;
    private int TIME_OUT_S = 3;
    private Boolean ONLY_MONITOR_READS = true;

    @Autowired
    private SeraService seraService;


    //TODO:
    @GetMapping("/")
    public ResponseEntity<Map<String,GreenHouse>> getAll(HttpServletRequest request) {
        if(ONLY_MONITOR_READS){
            try{
                seraService.getClientByIp(getIpOfRequest(request));
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            } catch(GreenHouseNotFoundException e){}
        }
        seraService.controlConnection(TIME_OUT_S*1000);
        return ResponseEntity.ok(seraService.getClients());
    }

    //Change valueName field of greenhouse where the ip address belongs with the value that given by the monitor client
    @PostMapping("/")
    public ResponseEntity<Void> changeValue(@RequestParam String ip, @RequestParam String valueName, @RequestParam float value) {
        //Clientlerle haberleşip onlara değiştirmelerini söylemeli.
        seraService.addCommand(ip,valueName,Float.toString(value));
        return ResponseEntity.ok().build();
    }

    //Get status information about clients
    @PostMapping("/notify")
    public ResponseEntity<Void> getNotifiedFromClients(HttpServletRequest request, @RequestBody Map<String,String> valueMap) throws GreenHouseNotFoundException {
        String ip_address = getIpOfRequest(request);
        seraService.changeClientValue(ip_address,valueMap);

        seraService.getClientByIp(ip_address).setNextNotificationTime(TimeService.getTime()+notificationPeriod*1000);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/notify")
    public ResponseEntity notifyToClients(HttpServletRequest request) {

        Map<String,String> response = new HashMap<>(2);
        try {
            response = seraService.removeCommand(getIpOfRequest(request));

        } catch(GreenHouseNotFoundException e){

            //Bulunamamışsa ve kapasite dolu değilse senkronizasyon yapılır.
            if(seraService.getClients().size()<seraService.getCapacity())
                response.put("sync_period",Integer.toString(this.notificationPeriod));
            else
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Sera Client Capacity Reached. Cannot add another Sera Client.");
        }

        return ResponseEntity.ok(response);
    }



    @GetMapping("/server_configure/{period}{timeout}{readAuth}")
    public ResponseEntity<Void> configure(@PathVariable Optional<Integer> period, @PathVariable Optional<Integer> timeout, @PathVariable Optional<Boolean> readAuth){
        this.notificationPeriod = period.isPresent() ? period.get() : this.notificationPeriod;
        this.TIME_OUT_S = timeout.isPresent() ? timeout.get() : this.TIME_OUT_S;
        this.ONLY_MONITOR_READS = readAuth.isPresent() ? readAuth.get() : this.ONLY_MONITOR_READS;

        seraService.addCommandToAllEmployees("sync_period",Integer.toString(this.notificationPeriod));

        return ResponseEntity.ok().build();
    }


    private String getIpOfRequest(HttpServletRequest request) {
        //{sec-fetch-mode=cors, content-length=95, referer=http://127.0.0.1:8080/swagger-ui.html, sec-fetch-site=same-origin, accept-language=tr-TR,tr;q=0.9,en-US;q=0.8,en;q=0.7, origin=http://127.0.0.1:8080, dnt=1, accept=*/*, host=127.0.0.1:8080, connection=keep-alive, content-type=application/json, accept-encoding=gzip, deflate, br, user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36, sec-fetch-dest=empty}
        return request.getRemoteAddr();
    }


}
