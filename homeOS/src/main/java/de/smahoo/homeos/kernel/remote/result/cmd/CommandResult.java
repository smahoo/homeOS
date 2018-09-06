package de.smahoo.homeos.kernel.remote.result.cmd;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.smahoo.homeos.kernel.remote.result.RemoteResult;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;


public class CommandResult extends RemoteResult{

	private String cmd;

	
	private boolean success = true;
	
	public CommandResult(){
		
	}
	
	public CommandResult(String cmd){
		
		this.cmd = cmd;
	}
	
		
	public CommandResult(String cmd, String msg){
		this(cmd);		
		setMessage(msg);
	}
	
	public CommandResult(String cmd, boolean success, String msg){
		this(cmd,msg);
		this.success = success;
	}
	
	
	public String getCommand(){
		return cmd;
	}	
	
	public String toString(){
		return getCommand() + super.toString();
	}
	
	
	
	public Document toXmlDocument(){
		Document result = null;
		DocumentBuilder docBuilder;
		DocumentBuilderFactory docBFac;
	
		try {
			//docBFac.setAttribute(name, value)
			docBFac = DocumentBuilderFactory.newInstance();
			docBuilder = docBFac.newDocumentBuilder();
			result = docBuilder.newDocument();
			
		} catch (Exception exc){
			exc.printStackTrace();
		}
		result.setXmlVersion("1.1");
		Element root = result.createElement("cmdresult");
		if (success){
			root.setAttribute("success","true");	
		} else {
			root.setAttribute("success","false");
		}
		
		if (hasMessage()){
			root.setAttribute("message",getMessage());
		}
		
		appendRemoteResultItems(result, root);
		result.appendChild(root);
		return result;
	}
	
	
	
	
	
	
	
	
	/*
	 * protected Element generateSensorUpdateElement(Document doc, ClimaSensor sensor){
		if (doc == null) return null;
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		Element tmp = doc.createElement("climasensor");
		String timeStamp =formatter.format(sensor.getCurrentDataSet().getDate());
		String strTemp = ""+sensor.getCurrentDataSet().getTemperature();
		String strHum  = "" + sensor.getCurrentDataSet().getHumidity();
		
		tmp.setAttribute("id", ""+sensor.getID());
		tmp.setAttribute("name",sensor.getName());
		tmp.setAttribute("date",timeStamp);
		tmp.setAttribute("temperature",strTemp);
		tmp.setAttribute("humidity",strHum);
		tmp.setAttribute("available",""+sensor.isAvailable());
		tmp.setAttribute("trendHumidity",""+ sensor.getHumidityTrend());
		tmp.setAttribute("trendTemperature",""+ sensor.getTemperatureTrend());
				
		return tmp;
	}
	 */
	
	
}
