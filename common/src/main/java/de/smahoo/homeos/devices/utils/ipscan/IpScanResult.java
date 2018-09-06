package de.smahoo.homeos.utils.ipscan;

public class IpScanResult {

    private String ip; 
    private int port;
    private boolean available;
    
    public IpScanResult(String ip, int port, boolean available){
            this.ip= ip;
            this.port= port;
            this.available= available;
    }
    
    public boolean isAvailable(){
            return available;
    }
    
    public int getPort(){
            return port;
    }
    
    public String getIp(){
            return ip;
    }
    
    
}
