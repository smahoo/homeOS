package de.smahoo.homeos.home.driver.hue;


import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;



public class HueComm {

	private String hueIp = null;
	private String user = null;
	
	public HueComm(String ip, String user){
		this.hueIp = ip;
		this.user =user;
	}
	
	public synchronized JSONObject getAll() throws IOException{		
        String jsonTxt;
        jsonTxt = get("http://"+hueIp+"/api/"+user+"/");
        return (JSONObject)JSONValue.parse(jsonTxt);        	
                
		
	}
	

	
	public JSONObject getDevices(){		
		return null;
	}
	
	public JSONObject getGroups(){
		return null;
	}
	
	public JSONObject getConfig(){
		return null;
	}
	
	public JSONObject getSchedules(){
		return null;
	}
	
	public void turnOn(HueBulb bulb){
		try {		
			String url = "http://"+hueIp+"/api/"+user+"/lights/"+bulb.getHueId()+"/state";
			String result = put(url,"{\"on\":true}");
			//System.out.println(url);
			//System.out.println("   => "+result);
		} catch (Exception exc){
			exc.printStackTrace();
		}
	}
	
	public void setBrightnes(HueBulb bulb, long brightnes){
		try {			
			String url = "http://"+hueIp+"/api/"+user+"/lights/"+bulb.getHueId()+"/state";
			String result = put(url,"{\"bri\":"+brightnes+"}");
			//System.out.println(url);
			//System.out.println("   => "+result);
		} catch (Exception exc){
			exc.printStackTrace();
		}	
	}
	
	public void setColor(HueBulb bulb, long color){
		try {			
			String url = "http://"+hueIp+"/api/"+user+"/lights/"+bulb.getHueId()+"/state";
			String result = put(url,"{\"hue\":"+color+"}");
			//System.out.println(url);
			//System.out.println("   => "+result);
		} catch (Exception exc){
			exc.printStackTrace();
		}
	}
	
	public void turnOff(HueBulb bulb){
		try {			
			String url = "http://"+hueIp+"/api/"+user+"/lights/"+bulb.getHueId()+"/state";
			String result = put(url,"{\"on\":false}");
			//System.out.println(url);
			//System.out.println("   => "+result);
		} catch (Exception exc){
			exc.printStackTrace();
		}
	}
	
	public JSONObject setState(HueBulb bulb, JSONObject state){
		try {			
			put("http://"+hueIp+"/api/"+user+"/lights/"+bulb.getHueId()+"/state",state.toJSONString());
		} catch (Exception exc){
			exc.printStackTrace();
		}
		return null;
	}
	
	private String put(String address, String body) throws IOException{		
		URL url = new URL(address);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();	
		connection.setRequestMethod("PUT");
		connection.setDoOutput(true);				
		OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
		out.write(body);
		out.flush();
		InputStreamReader in = new InputStreamReader(connection.getInputStream());
		StringBuffer buffer = new StringBuffer();
		int read = 0;
		while ((read = in.read()) !=-1 ){
			buffer.append((char)read);
		}
		in.close();				
		return buffer.toString();
	}
	
	private String get(String address) throws IOException{		
		URL url = new URL(address);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();	
		InputStreamReader in = new InputStreamReader(connection.getInputStream());
		StringBuffer buffer = new StringBuffer();
		int read = 0;
		while ((read = in.read()) !=-1 ){
			buffer.append((char)read);
		}
		in.close();				
		return buffer.toString();
	}
	
}
