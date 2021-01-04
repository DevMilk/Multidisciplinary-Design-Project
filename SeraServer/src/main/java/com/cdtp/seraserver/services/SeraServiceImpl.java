package com.cdtp.seraserver.services;

import com.cdtp.seraserver.Exceptions.GreenHouseNotFoundException;
import com.cdtp.seraserver.domain.GreenHouse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Service
public class SeraServiceImpl implements SeraService{
    private final int CLIENT_CAPACITY = 4;


    private Map<String,GreenHouse> clients = new HashMap<String, GreenHouse>(CLIENT_CAPACITY);
    private Map<String, Map<String,String>> commands = new HashMap<>(CLIENT_CAPACITY);

    public SeraServiceImpl() {}

    @Override
    public int getCapacity() {
        return CLIENT_CAPACITY;
    }

    public GreenHouse getClientByIp(String ip) throws GreenHouseNotFoundException {
        GreenHouse client = clients.get(ip);
        if(client == null) throw new GreenHouseNotFoundException("No Client found with ip:",ip);
        return client;
    }
    public void addClient(Map<String,String> valueMap, String ip){
        GreenHouse newSera = new GreenHouse(valueMap,ip);
        newSera.setName("Sera#"+clients.size());
        clients.put(ip,newSera);
    }
    public void addClient(Map<String,String> valueMap, String ip,String Name){
        GreenHouse newSera = new GreenHouse(valueMap,ip);
        newSera.setName(Name);
        clients.put(ip,newSera);
    }
    public void changeClientValue(String ip, Map<String, String> valueMap){
        try {
            getClientByIp(ip).setValues(valueMap);

        }catch (GreenHouseNotFoundException e){
            addClient(valueMap,ip);
        }
    }
    public void addCommand(String employee, Map<String,String> command){
        commands.put(employee,command);
    }
    public synchronized void addCommand(String employee, String valueName,String value){

        try {
            getClientByIp(employee);
        } catch(GreenHouseNotFoundException e){
            e.printStackTrace();
        }

        Map<String,String> param = commands.get(employee);
        if(param == null) {
            commands.put(employee, new HashMap<String, String>());
            param = commands.get(employee);
        }
        param.put(valueName,value);
        commands.put(employee,param);
    }

    @Override
    public void addCommandToAllEmployees(String valueName, String value) {
        for(Map.Entry<String,GreenHouse> client : clients.entrySet())
                addCommand(client.getKey(),valueName,value);
    }

    public Map<String,String> getCommand(String employee) throws GreenHouseNotFoundException {
        getClientByIp(employee);
        Map<String,String> aliveCopy = commands.get(employee);
        return aliveCopy;
    }
    public void controlConnection(float total){

        for(Map.Entry<String,GreenHouse> client : clients.entrySet()) {
            long next = client.getValue().getNextNotificationTime() + (int)total;
            long now = TimeService.getTime();
            client.getValue().setIsDown(now > next);
        }
    }
    public String getIpFromName(String name){
        for(Map.Entry<String,GreenHouse> client : clients.entrySet()){
            if(client.getValue().getName().equals(name))
                return client.getValue().getIp();
        }
        return null;
    }
}
