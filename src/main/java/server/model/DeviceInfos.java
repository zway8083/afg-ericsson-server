package server.model;

import server.database.model.Device;
import server.database.model.User;

public class DeviceInfos {
    private Long id;
    private String date;
    private Device Device;
    private User user;

    public DeviceInfos(){
    }

    public DeviceInfos(Long id,String date){
        this.id=id;
        this.date=date;
    }

    public Long getId() { return id;}

    public void setId(Long id) { this.id = id; }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public Device getDevice() { return Device; }

    public void setDevice(Device device) { Device = device; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }


}
