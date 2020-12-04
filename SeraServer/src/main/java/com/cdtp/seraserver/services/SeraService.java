package com.cdtp.seraserver.services;

import com.cdtp.seraserver.Exceptions.GreenHouseNotFoundException;
import com.cdtp.seraserver.domain.GreenHouse;

import java.util.Map;


public interface SeraService {

    public int getCapacity();
    public Map<String,GreenHouse> getClients();
    public GreenHouse getClientByIp(String ip) throws GreenHouseNotFoundException;
    public void changeClientValue(String ip, Map<String, String> valueMap);
    public void addCommand(String employee, Map<String,String> command);
    public void addCommand(String employee, String valueName,String value);
    public void addCommandToAllEmployees(String valueName,String value);
    public Map<String,String> removeCommand(String employee) throws GreenHouseNotFoundException;
    public void controlConnection(int total);
}
