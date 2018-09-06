package de.smahoo.homeos.kernel.remote;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.smahoo.homeos.common.Function;
import de.smahoo.homeos.common.FunctionParameter;
import de.smahoo.homeos.device.Device;
import de.smahoo.homeos.device.DeviceImpl;
import de.smahoo.homeos.device.DeviceManager;
import de.smahoo.homeos.device.DeviceType;
import de.smahoo.homeos.device.ParameterizedDeviceFunction;
import de.smahoo.homeos.device.PhysicalDevice;
import de.smahoo.homeos.device.role.DeviceRole;
import de.smahoo.homeos.driver.Driver;
import de.smahoo.homeos.driver.DriverMode;
import de.smahoo.homeos.kernel.HomeOs;
import de.smahoo.homeos.kernel.remote.result.DeleteDeviceResultItem;
import de.smahoo.homeos.kernel.remote.result.DeviceRequestResultItem;
import de.smahoo.homeos.kernel.remote.result.ExecutionResultItem;
import de.smahoo.homeos.kernel.remote.result.FunctionExecutor;
import de.smahoo.homeos.kernel.remote.result.RemoteResult;
import de.smahoo.homeos.kernel.remote.result.RemoteResultItem;
import de.smahoo.homeos.kernel.remote.result.cmd.AddCmdResultItem;
import de.smahoo.homeos.kernel.remote.result.cmd.ChangeDeviceResultItem;
import de.smahoo.homeos.kernel.remote.result.cmd.ChangeLocationResultItem;
import de.smahoo.homeos.kernel.remote.result.cmd.CmdSaveResultItem;
import de.smahoo.homeos.kernel.remote.result.cmd.CommandResult;
import de.smahoo.homeos.kernel.remote.result.cmd.DeleteCmdResultItem;
import de.smahoo.homeos.kernel.remote.result.cmd.DriverCommandResultItem;
import de.smahoo.homeos.kernel.remote.result.cmd.SetCmdResultItem;
import de.smahoo.homeos.kernel.remote.result.request.DeviceHistoryResultItem;
import de.smahoo.homeos.kernel.remote.result.request.DeviceListRequestResultItem;
import de.smahoo.homeos.kernel.remote.result.request.HistoryResultListItem;
import de.smahoo.homeos.kernel.remote.result.request.LocationListRequestResultItem;
import de.smahoo.homeos.kernel.remote.result.request.LocationRequestResultItem;
import de.smahoo.homeos.kernel.remote.result.request.PhysicalDeviceItem;
import de.smahoo.homeos.kernel.remote.result.request.PhysicalDeviceListItem;
import de.smahoo.homeos.kernel.remote.result.request.RequestResult;
import de.smahoo.homeos.kernel.remote.result.request.SystemConfigurationResultItem;
import de.smahoo.homeos.kernel.remote.result.request.SystemInfoResultItem;
import de.smahoo.homeos.location.Location;
import de.smahoo.homeos.location.LocationType;
import de.smahoo.homeos.utils.AttributeValuePair;


public class RemoteProcessor {	
	protected final static String SINGLE_ATTRIBUTE_INDICATOR= "#";	

	public RemoteResult process(Document xmlDoc){	
		Element elem = xmlDoc.getDocumentElement();
		
		if ("cmd".equals(elem.getTagName())){
			return processCmd(elem);
		}
		
		if (elem.getTagName().equals("request")){
			return processRequest(elem);
		}
		
		CommandResult result = new CommandResult("Unknown Command Sequence \""+elem.getTagName()+"\"");
		result.setSuccess(false);		
		return result;
	}
	
	private RemoteResult processRequest(Element element){
		RequestResult result = new RequestResult();
	
		result.setSuccess(false);
		if (element.hasAttribute("requestType")){
			if (element.getAttribute("requestType").equalsIgnoreCase("COMPLETE")){
				result.addResultItem(processDevicelistRequest(element));
				result.addResultItem(processLocationListRequest(element));
				return result;
			}
			if (element.getAttribute("requestType").equalsIgnoreCase("HISTORY")){								
				result.addResultItem(processHistoryRequest(element));
				return result;
			}
			if (element.getAttribute("requestType").equals("PHYSICAL")){
				result.addResultItem(processPhysicalDeviceRequest(element));
				return result;
			}
			if (element.getAttribute("requestType").equals("SYSTEM_INFO")){
				result.addResultItem(processSystemInfoRequest(element));
				return result;
			}
			if (element.getAttribute("requestType").equals("SYSTEM_CONFIGURATION")){
				result.addResultItem(processSystemConfigurationRequest(element));
				return result;
			}
		}
		Element elem;
		NodeList nList = element.getChildNodes();
		if (nList.getLength() > 0){
			for(int i = 0; i<nList.getLength(); i++){
				if (nList.item(i) instanceof Element){
					elem = (Element)nList.item(i);
					
					if (elem.getTagName().equalsIgnoreCase("devicelist")){
						result.addResultItem(processDevicelistRequest(elem));
					}					
					if (elem.getTagName().equalsIgnoreCase("device")){
						result.addResultItem(processDeviceRequest(elem));
					}					
					if (elem.getTagName().equalsIgnoreCase("locationlist")){
						result.addResultItem(processLocationListRequest(elem));
					}				
					if (elem.getTagName().equalsIgnoreCase("location")){						
						result.addResultItem(processLocationRequest(elem));
					}
				}
			}
		}		
		return result;
	}
	
	// ---------------------------- System Info --------------------------------
	
	private SystemInfoResultItem processSystemInfoRequest(Element elem){
		SystemInfoResultItem item = new SystemInfoResultItem();
		return item;
	}
	
	private SystemConfigurationResultItem processSystemConfigurationRequest(Element elem){
		SystemConfigurationResultItem item = new SystemConfigurationResultItem();
		return item;
	}
	
	
	
	
	
	
	// ---------------------------- Phyiscal --------------------------------
	
	private PhysicalDeviceListItem processPhysicalDeviceRequest(Element elem){
		PhysicalDeviceListItem item = new PhysicalDeviceListItem();
		
		if (elem.hasChildNodes()){
			NodeList nodeList = elem.getChildNodes();
			String deviceId;
			DeviceManager devManager = HomeOs.getInstance().getDeviceManager();
			for (int i = 0; i<nodeList.getLength(); i++){
				if (nodeList.item(i) instanceof Element){					
					Element tmp = (Element)nodeList.item(i);
					if (tmp.hasAttribute("deviceId")){
						deviceId = tmp.getAttribute("deviceId");
						PhysicalDevice device = devManager.getPhysicalDevice(deviceId);
						if (device != null){							
							item.add(new PhysicalDeviceItem(device));
						}
					}	
				}
			}
		}		
		return item;
	}
	
	
	// ----------------------------  History --------------------------------
	
	private HistoryResultListItem processHistoryRequest(Element elem){	
		HistoryResultListItem result = new HistoryResultListItem();
		result.setSuccess(true);
		if (!elem.hasAttribute("start")){
			result.setSuccess(false);
			result.setMessage("Parameter 'start' not given!");
			return result;
		}
		if (!elem.hasAttribute("end")){
			result.setSuccess(false);
			result.setMessage("Parameter 'end' not given!");
			return result;
		}
		
		DateFormat formatter = new SimpleDateFormat("dd.MM.yy HH:mm:ss");		    
		
		String startStr = elem.getAttribute("start");
		String stopStr  = elem.getAttribute("end");
		
		Date start = null;
		Date end = null;		
		
		try {
			 start = (Date)formatter.parse(startStr);
			 end = (Date)formatter.parse(stopStr);
		} catch (Exception exc){
			result.setSuccess(false);
			result.setMessage("Unable to parse start and stop attributes ("+exc.getMessage()+"). Use 'dd.MM.yy HH:mm:ss'!");
			return result;
		}
		
		result.setStartTime(start);
		result.setEndTime(end);
		
		NodeList list = elem.getChildNodes();
		Element tmp;		
		for (int i = 0; i<list.getLength(); i++){
			if (list.item(i) instanceof Element){
				tmp = (Element)list.item(i);
				if (tmp.getTagName().equalsIgnoreCase("device")){					
					result.addResultItem(processDeviceHistoryRequest(start,end,tmp));
				}
			}
		}
		return result;
	}
	
	private DeviceHistoryResultItem processDeviceHistoryRequest(Date start, Date end, Element elem){
		DeviceHistoryResultItem result;
		if (!elem.hasAttribute("deviceId")){
			result = new DeviceHistoryResultItem();
			result.setSuccess(false);
			result.setMessage("No parameter 'deviceId' given!");
			return result;
		}
		String deviceStr = elem.getAttribute("deviceId");
		Device device = HomeOs.getInstance().getDeviceManager().getDevice(deviceStr);
		
				
		result = new DeviceHistoryResultItem(device, device.getHistoryData(start, end));
		result.setSuccess(true);
		
		return result;
		
		
	}
	
	private LocationListRequestResultItem processLocationListRequest(Element elem){
		LocationListRequestResultItem result = new LocationListRequestResultItem();
		LocationRequestResultItem item;
		if (elem.hasAttribute("start")){
			Location location = HomeOs.getInstance().getLocationManager().getLocation(elem.getAttribute("start"));
			if (location != null){
				item = new LocationRequestResultItem(location);
				result.addLocationRequestResultItem(item);
			}
			return result;
		}
		
		List<Location> locList =  HomeOs.getInstance().getLocationManager().getLocations();				
		for (Location loc : locList){
			item = new LocationRequestResultItem(loc);
			result.addLocationRequestResultItem(item);
		}
		return result;
	}
	
	
	
	private LocationRequestResultItem processLocationRequest(Element elem){
		String locId = elem.getAttribute("id");
		if (locId == null){
			LocationRequestResultItem result = new LocationRequestResultItem("no location found with id =\""+locId+"\"");
			return result;
		}		
		Location location = HomeOs.getInstance().getLocationManager().getLocation(locId);
		LocationRequestResultItem result = new LocationRequestResultItem(location);
		return result;
	}
	
	private DeviceListRequestResultItem processDevicelistRequest(Element elem){
		DeviceListRequestResultItem result = new DeviceListRequestResultItem();
		DeviceType deviceType = null;
		Location location = null;
		if (elem.hasAttribute("type")){
			String deviceTypeStr = elem.getAttribute("type");
			deviceType = DeviceType.valueOf(deviceTypeStr.toUpperCase());
			result.setDeviceType(deviceType);
			
		}
		if (elem.hasAttribute("location")){
			String locStr = elem.getAttribute("location");
			location = HomeOs.getInstance().getLocationManager().getLocation(locStr);
			result.setLocation(location);
		}
		
		List<Device> devList = null;
		if ((deviceType != null)&&(location == null)){
			devList = HomeOs.getInstance().getDeviceManager().getDevices(deviceType);
		}else if ((deviceType == null)&&(location != null)){
			devList = HomeOs.getInstance().getDeviceManager().getDevices(location);
		}else if ((deviceType != null)&&(location != null)){
			devList = HomeOs.getInstance().getDeviceManager().getDevices(deviceType,location);
		}			
		if (devList == null){
			devList = HomeOs.getInstance().getDeviceManager().getDevices();
		}
		
		if (devList.isEmpty()){
			
		}
		
		for (Device device : devList){
			if (!device.isHidden()){
				try {
					DeviceRequestResultItem requestItem;
					if (deviceType != null){
						requestItem = new DeviceRequestResultItem(device,deviceType);
					} else {
						requestItem = new DeviceRequestResultItem(device);
					}
					if (requestItem != null){
						result.addDeviceRequestResultItem(requestItem);
					} else {
						System.out.println("ERROR generating RequestResultItem for device "+device.getDeviceId());
					}
				} catch (Exception exc){
					exc.printStackTrace();
				}
				
			}
		}
		return result;
	}
	
	private DeviceRequestResultItem processDeviceRequest(Element elem){
		String deviceId = null;
		Device device;
		if (elem.hasAttribute("id")){
			deviceId = elem.getAttribute("id");
		}
		if (deviceId == null){
			
		}
		
		device = HomeOs.getInstance().getDeviceManager().getDevice(deviceId);
		if (device.isHidden()) return null;
		DeviceRequestResultItem item = new DeviceRequestResultItem(device);
		return item;
	}
	
	private CommandResult processCmd(Element elem){
		CommandResult result = new CommandResult();
		result.setSuccess(true);
		RemoteResultItem resultItem = null;
		NodeList nList = elem.getChildNodes();
		if (nList.getLength() > 0){
			for(int i = 0; i<nList.getLength(); i++){
				if (nList.item(i) instanceof Element){
					elem = (Element)nList.item(i);
					if (elem.getTagName().equalsIgnoreCase("add")){
						resultItem = processAddCmd(elem);
					}
					if (elem.getTagName().equalsIgnoreCase("execute")){
						resultItem = processExecutionCmd(elem);						
					}	
					if (elem.getTagName().equalsIgnoreCase("shutdown")){
						resultItem = processShutdownCommand(elem);
					}
					if (elem.getTagName().equalsIgnoreCase("change")){
						resultItem = processChangeCmd(elem);
					}
					if (elem.getTagName().equalsIgnoreCase("delete")){
						resultItem = processDeleteCmd(elem);
					}
					if ("save".equalsIgnoreCase(elem.getTagName())){
						resultItem = processSaveCmd(elem);
					}
					if ("driver".equalsIgnoreCase(elem.getTagName())){
						resultItem = processDriverCmd(elem);
					}
					if (resultItem != null){
						result.setSuccess(result.isSuccess()&&resultItem.isSuccess());
						result.addResultItem(resultItem);
						result.setSuccess(result.isSuccess()&&resultItem.isSuccess());
						return result;
					}
					
					result.setSuccess(false);
					result.setMessage("Unknown command \'"+elem.getTagName()+"\'");
					return result;				
				}
			}
		}
		return result;
	}
	
	
	private RemoteResultItem processDriverCmd(Element elem){
		DriverCommandResultItem item = new DriverCommandResultItem();
		item.setSuccess(false);
		if ("driver".equalsIgnoreCase(elem.getTagName())){
			if (elem.hasAttribute("class")){
				String name = elem.getAttribute("class");
				Driver driver = HomeOs.getInstance().getDriverManager().getDriverByClassName(name);
				if (driver == null){
					item.setSuccess(false);
					item.setMessage("Unable to find driver "+name);
					return item;
				}
				return driver.processCmd(elem);
			}
		} 
		return item;
		
	}
	
	
	private CmdSaveResultItem processSaveCmd(Element elem){
		String name = null;
		String filename = null;
		
		if (!elem.hasAttribute("name")){
			return new CmdSaveResultItem(false, "attribute 'name' missing. Unclear what to save.");
		}
		
		name = elem.getAttribute("name");
		if ("SYSTEM_CONFIGURATION".equalsIgnoreCase(name)){
			if (elem.hasAttribute("filename")){
				filename = elem.getAttribute("filename");
			} else {
				filename = HomeOs.getInstance().getConfigFileName();
			}
			try {
				HomeOs.getInstance().saveConfiguration(filename);
				return new CmdSaveResultItem(name, filename);
			} catch (Exception exc){
				exc.printStackTrace();
				return new CmdSaveResultItem(false,exc.getMessage());
			}
		}
		
		return new CmdSaveResultItem(false,"Unknown option '"+name+"'");
	}
	
	private DeleteCmdResultItem processDeleteCmd(Element elem){
		DeleteCmdResultItem result = new DeleteCmdResultItem();		
		NodeList nodelist = elem.getChildNodes();
		Element tmp = null;
		
		for (int i = 0; i<nodelist.getLength(); i++){
			if (nodelist.item(i) instanceof Element){
				tmp = (Element)nodelist.item(i);
				if (tmp.getTagName().equalsIgnoreCase("device")){
					result.addResultItem(processDeleteDeviceCmd(tmp));
				}
			}
		}
		
		return result;
	}
	
	private DeleteDeviceResultItem processDeleteDeviceCmd(Element elem){
		DeleteDeviceResultItem item = new DeleteDeviceResultItem();
		if (elem.hasAttribute("id")){
			Device dev = HomeOs.getInstance().getDeviceManager().getDevice(elem.getAttribute("id"));
			if (dev != null){
				item.setDeviceId(dev.getDeviceId());
				HomeOs.getInstance().getDeviceManager().deleteDevice(dev);
			} else {
				item.setSuccess(false);
				item.setMessage("Unable to delete Device. No device with deviceId=\""+elem.getAttribute("id")+"\" found.");
			}
		} else {
			item.setSuccess(false);
			item.setMessage("Unable to delete device. No attribute 'id' given.");
		}
		return item;
	}
	
	private SetCmdResultItem processChangeCmd(Element elem){
		// FIXME check wether id contains @!!!
		NodeList nodelist = elem.getChildNodes();
		SetCmdResultItem resultItem = new SetCmdResultItem();
		Element tmp = null;
		for (int i = 0; i<nodelist.getLength(); i++){
			if (nodelist.item(i) instanceof Element){
				tmp = (Element)nodelist.item(i);
				if (tmp.getTagName().equalsIgnoreCase("device")){
					resultItem.addChangeCmdResultItem(processChangeDeviceCmd(tmp));
				}
				if (tmp.getTagName().equalsIgnoreCase("location")){
					resultItem.addChangeCmdResultItem(processChangeLocationCmd(tmp));
				}
			}
		}
		return resultItem;
	}
	
	
	
	private void changeDeviceProperty(ChangeDeviceResultItem resultItem, Device device, String name, String value){			
		if (name.equalsIgnoreCase("name")){
			device.setName(value);
			resultItem.addProperty(name, value, true);
			return;
		}
		if (name.equalsIgnoreCase("hidden")){		
			if ((value.equalsIgnoreCase("true"))||(value.equalsIgnoreCase("true"))) {
				((DeviceImpl)device).setHidden(Boolean.parseBoolean(value));
				resultItem.addProperty(name,value,true);
				return;
			}else {
				resultItem.addProperty(name,value,false,"\""+value+"\" is not a valid value (must be \"true\" or \"false\")");
				return;
			}
		}
		if (name.equalsIgnoreCase("location")){
			if (value.equals("NONE")){
				if (device.getLocation() != null){
					device.getLocation().removeDevice(device);
				}
				resultItem.addProperty(name,value,true);
				return;
			}
			Location location = HomeOs.getInstance().getLocationManager().getLocation(value);
			if (location != null){
				device.assignLocation(location);
				resultItem.addProperty(name,value,true);
				return;
			} else {
				resultItem.addProperty(name, value, false, "no location with id \""+value+"\" found");
				return;
			}
		}		
		resultItem.addProperty(name, value, false, "unknown property ("+value+")");
	}
	
	private ChangeDeviceResultItem processChangeDeviceCmd(Element elem){
		String deviceId = null;
		if (elem.hasAttribute("id")){
			deviceId = elem.getAttribute("id");
		} else {
			// ...
			return null; // FIX ME
		}
		Device device = null;
		if (deviceId.contains("@")){			
			String[] ids = deviceId.split("@");			
			String deviceIdStr = ids[1];
			device = HomeOs.getInstance().getDeviceManager().getDevice(deviceIdStr);
		} else {
			device = HomeOs.getInstance().getDeviceManager().getDevice(deviceId);
		}
		if (device == null){
			return null; // FIX ME
		}
		ChangeDeviceResultItem resultItem = new ChangeDeviceResultItem(device);
		NodeList nodelist = elem.getChildNodes();
		Element tmp = null;
		
		for (int i = 0; i < nodelist.getLength(); i++){
			if (nodelist.item(i) instanceof Element){				
				tmp = (Element)nodelist.item(i);
				if (tmp.getTagName().equalsIgnoreCase("property")){
					String name = null;
					String value = null;		
					name = tmp.getAttribute("name");
					value = tmp.getAttribute("value");
					changeDeviceProperty(resultItem, device, name, value);
				};				
			}
		}		
		return resultItem;
	}
	
	
	private ChangeLocationResultItem processChangeLocationCmd(Element elem){
		
		if (!elem.hasAttribute("id")){
			ChangeLocationResultItem item = new ChangeLocationResultItem((Location)null);
			item.setSuccess(false);
			item.setMessage("No location id given. Unable to process change command.");
			return item;			
		} 
		
		String id = elem.getAttribute("id");
		Location location = HomeOs.getInstance().getLocationManager().getLocation(id);
		if (location == null){
			ChangeLocationResultItem item = new ChangeLocationResultItem((Location)null);
			item.setSuccess(false);
			item.setMessage("Unable to find location with id '"+id+"'");
			return item;	
		}	
		
		ChangeLocationResultItem item = new ChangeLocationResultItem(location);
		
		NodeList nodeList = elem.getChildNodes();
		Element tmp;
		for (int i = 0; i<nodeList.getLength(); i++){
			if (nodeList.item(i) instanceof Element){
				tmp = (Element)nodeList.item(i);
				if (tmp.getTagName().equalsIgnoreCase("property")){
					if (!tmp.hasAttribute("name")){					
						item.setSuccess(false);
						item.setMessage("Property element does not contains attribute 'name'");
					} else {
						if (!tmp.hasAttribute("value")){
							item.setSuccess(false);
							item.setMessage("Property element does not contains attribute 'value'");
						} else {
							item.setSuccess(true);
							if (tmp.getAttribute("name").equalsIgnoreCase("name")){
								location.setName(tmp.getAttribute("value"));
								item.addProperty("name",tmp.getAttribute("value"), true);
							}
							if (tmp.getAttribute("name").equalsIgnoreCase("type")){
								location.setLocationType(LocationType.valueOf(tmp.getAttribute("value")));
								item.addProperty("type",tmp.getAttribute("value"), true);
							}
						}
					}
					
				}
			}
		}
		
		return item;
	}
	
	private ShutdownResultItem processShutdownCommand(Element elem){
		String password;
		ShutdownResultItem sri = new ShutdownResultItem();
		if (elem.hasAttribute("password")){
			sri.setSuccess(true);
			HomeOs.getInstance().shutdown();
		} else {
			sri.setSuccess(false);
			sri.setMessage("Access denied, no attribute password given!");
		}
		return sri;
	}
	
	private AddCmdResultItem processAddCmd(Element elem){
		AddCmdResultItem resultItem = new AddCmdResultItem();
		NodeList list = elem.getChildNodes();
		
		if (elem.hasAttribute("mode")){
			if (elem.getAttribute("mode").equalsIgnoreCase("CANCEL")){
				HomeOs.getInstance().getDriverManager().setMode(DriverMode.DRIVER_MODE_NORMAL);
				return resultItem;
			}
		}
		
		Element tmp;		
		for (int i = 0; i<list.getLength(); i++){
			if (list.item(i) instanceof Element){
				tmp = (Element)list.item(i);
				if (tmp.getTagName().equalsIgnoreCase("location")){
					resultItem.addResultItem(createLocation(tmp));					
				}
				if (tmp.getTagName().equalsIgnoreCase("device")){
					HomeOs.getInstance().getDriverManager().setMode(DriverMode.DRIVER_MODE_ADD_DEVICE);
				}
			}
		}
		return resultItem;
	}
	
	private RemoteResultItem addDevice(Element elem){
		
		/*HomeOs.getInstance().getDriverManager().setAddDeviceMode();
		
		resultItem.setSuccess(true);
		resultItem.setMessage("All Drivers are set to learn mode.");*/
		return null;
	}
	
	private LocationRequestResultItem createLocation(Element elem){
		String name = "new Location";
		String type ="LT_NOT_GIVEN";
		
		if (elem.hasAttribute("name")){
			name = elem.getAttribute("name");
		}
		if (elem.hasAttribute("type")){
			type = elem.getAttribute("type");
		}		
		Location location = HomeOs.getInstance().getLocationManager().generateLocation(name, type);		
		if (location != null){
			HomeOs.getInstance().getLocationManager().addLocation(location);
		}
		LocationRequestResultItem locItem = new LocationRequestResultItem(location);
		locItem.setSuccess(location != null);
		return locItem;
	}
	
	private ExecutionResultItem processExecutionCmd(Element elem){
		String errMsg = "";
		int missCnt = 0;
		if (!elem.hasAttribute("device")){
			errMsg = "device";
			missCnt++;
		}
		if (!elem.hasAttribute("function")){
			if (missCnt > 0) {
				errMsg = errMsg+",function";
				missCnt++;
			}
		}
		if (missCnt > 0){
			if (missCnt > 1){
				errMsg = "Missing attributes ("+errMsg+")";
			} else {
				errMsg = "Missing attribute ("+errMsg+")";
			}
			return new ExecutionResultItem(errMsg);
		}		
		
		List<AttributeValuePair> avList = null;
		
		if (elem.hasChildNodes()){
			avList = generateAvList(elem.getChildNodes());
		}
		
		return processExecutionCmd(elem.getAttribute("device"), elem.getAttribute("function"), avList);				
	}
	
	
	private List<AttributeValuePair> generateAvList(NodeList nList){
		List<AttributeValuePair> avList = new ArrayList<AttributeValuePair>();
		Element tmp;
		String name;
		String value;
		for (int i = 0; i< nList.getLength(); i++){
			if (nList.item(i) instanceof Element){
				tmp = (Element)nList.item(i);
				value = tmp.getAttribute("value");
				if (tmp.hasAttribute("name")){
					name = tmp.getAttribute("name");
				} else {
					name = SINGLE_ATTRIBUTE_INDICATOR;
				}
				avList.add(new AttributeValuePair(name,value));
			}
		}		
		return avList;
	}
	
	/*
	 * <deviceID>.<functionName>(<parameterName>=<parameterValue>;<parameterName>=<parameterValue>)
	 */
	public CommandResult processExecutionCmd(String cmd){
		
		cmd = eraseSpaces(cmd);
		
		//System.out.println("Parsing "+cmd);
		/**
		 * Superficial syntax check whether cmd is constructed like <something>.<something>(  
		 */
		int pIndex = cmd.indexOf(".");
		int bIndex = cmd.indexOf("(");
		if ((pIndex <= 0)||(bIndex <= 0)){
			return new CommandResult(cmd,"Unexpected command format. Command is not of format \'<device>.<function>()\'");
		}		
		if (bIndex < pIndex){
			return new CommandResult(cmd,"Unexpected command format. Command is not of format \'<device>.<function>()\'");
		}
		
		/**
		 * Parsing device and function name
		 */
		
		String deviceId;
		String functionName;
		
		try {		
			deviceId = cmd.substring(0,cmd.indexOf("."));		
			functionName = cmd.substring(cmd.indexOf(".")+1,cmd.indexOf("("));		
		} catch (Exception exc){
			return new CommandResult(cmd,false,exc.getMessage());			
		}	
		
		CommandResult cmdResult = new CommandResult(cmd);
		RemoteResultItem item = process(deviceId, functionName,cmd.substring(bIndex+1,cmd.length()-1));
		cmdResult.setSuccess(item.isSuccess());
		cmdResult.addResultItem(item);
		return cmdResult;
	}
	

	protected ExecutionResultItem processExecutionCmd(DeviceType type, Device device, String function, List<AttributeValuePair> avList ) {		
		FunctionExecutor fe = new FunctionExecutor();
		return fe.processExecutionCmd(type, device, function, avList);
	}
	
	public ExecutionResultItem processExecutionCmd(String deviceId, String function, List<AttributeValuePair> avList ) {		
		if (deviceId.contains("@")){			
			String[] ids = deviceId.split("@");
			String deviceTypeStr = ids[0];
			String deviceIdStr = ids[1];			
			DeviceType type = DeviceType.valueOf(deviceTypeStr);			
			Device device = HomeOs.getInstance().getDeviceManager().getDevice(deviceIdStr);
			if (device == null){
				return new ExecutionResultItem(deviceIdStr, function, false,"no device found with id "+deviceIdStr);
			}
			if (type == null){
				return new ExecutionResultItem(deviceIdStr, function, false,"no device type found ("+deviceTypeStr+")");
			}

			return processExecutionCmd(type, device, function, avList);			 
		}
		
		
		Device device = HomeOs.getInstance().getDeviceManager().getDevice(deviceId);		
		if (device == null){
			return new ExecutionResultItem(deviceId,function,false,"Device with ID \'"+deviceId+"\' was not found.");
		}
		Function devFunction = null;
		if (device instanceof PhysicalDevice){
		  devFunction = ((PhysicalDevice)device).getFunction(function);
			
		}
		if (device instanceof DeviceRole){
			devFunction = ((DeviceRole)device).getRoleFunction(function);
		}
		
		if (devFunction == null){
			return new ExecutionResultItem(deviceId,function,false,"Device with ID \'"+deviceId+"\' has no function \'"+function+"\'");
		}		
		return processExecutionCmd(device,devFunction,avList);
	
	}
	
	private ExecutionResultItem processExecutionCmd(Device device, Function function, List<AttributeValuePair> avList){
				
		if (function instanceof ParameterizedDeviceFunction){			
				return process(device,(ParameterizedDeviceFunction)function, avList);			
		} else {
			ExecutionResultItem exeResult = new ExecutionResultItem(device.getDeviceId(),function.getName());
			try {
				function.execute();
			} catch (Exception exc){				
				exeResult.setMessage(exc.getMessage());
				exeResult.setSuccess(false);
				return exeResult;			
			}
			exeResult.setSuccess(true);
			return exeResult;
		}
	}
	
	
	protected ExecutionResultItem process(Device device,ParameterizedDeviceFunction function, List<AttributeValuePair> avList){	
		ExecutionResultItem exeResult = new ExecutionResultItem(device.getDeviceId(),function.getName());		
		if ((function).getNeededParameter().size() == 0){
			try {
				function.execute();
			} catch (Exception exc){
				exeResult.setMessage(exc.getMessage());
				exeResult.setSuccess(false);
				return exeResult;
			}
			exeResult.setSuccess(true);
			return exeResult;
		}
		
		List<FunctionParameter> lstParameter = function.getNeededParameter();	
		if (avList == null){			
			exeResult.setMessage("Parameters are needed but not given!");
			exeResult.setSuccess(false);
			return exeResult;
		}
		
		if (avList.size()==0){
			exeResult.setMessage("Parameters are needed but not given!");
			exeResult.setSuccess(false);
			return exeResult;
		}
		
				
		try {
			setFunctionParameterValues(lstParameter,avList);		
			function.execute(lstParameter);
		} catch (Exception exc){
			exeResult.setMessage(exc.getMessage());
			exeResult.setSuccess(false);
			return exeResult;
		}
		exeResult.setSuccess(true);
		return exeResult;
		
	}
	
	
	
	private ExecutionResultItem process(String deviceId, String functionName, String strParameter){
		/*
		 * Get parameter as list of attributes and values
		 * Examples
		 * 	   <device>.<function>()
		 *     <device>.<function>([value])  -> valid if function only needs one parameter
		 *     <device>.<function>([parameter=<value]>])
		 *     <device>.<function>([parameter=<value]>];[parameter=<value]>])
		 *     <device>.<function>(<device>.<parameter>)
		 */
		List<AttributeValuePair> avList = null;
		if (strParameter != null){
			if (strParameter.length() > 0) {
				try {
					avList = parseParameter(strParameter.substring(0,strParameter.length()));
			
					eraseSpaces(avList);			
					printAvList(avList);			
				} catch (Exception exc){
					ExecutionResultItem exeResult = new ExecutionResultItem(deviceId, functionName);
					exeResult.setMessage(exc.getMessage());
					exeResult.setSuccess(false);
					return exeResult;
				}
			}
		}
		return processExecutionCmd(deviceId, functionName, avList);
	}
	
	protected void setFunctionParameterValues(List<FunctionParameter> lstParameter, List<AttributeValuePair> lstAv) throws MissingParameterException, NumberFormatException{
		if ((lstParameter.size()==1)&&(lstAv.size() == 1) && (lstAv.get(0).getAttribute().equals(SINGLE_ATTRIBUTE_INDICATOR))){
			try {
				lstParameter.get(0).setValue(lstAv.get(0).getValue());
			} catch (NumberFormatException exc){
				throw new NumberFormatException("\'"+lstAv.get(0).getValue()+"\' is not a valid format for "+lstParameter.get(0).getValueClass().getSimpleName());
			}
			return;
		}
		List<String> missingParameters = new ArrayList<String>();
		AttributeValuePair av = null;
		
		for (FunctionParameter parameter : lstParameter){
			av = getAvPair(parameter.getName(),lstAv);
			if (av == null){
				missingParameters.add(parameter.getName());
			}			
		}
		
		
		
		if (missingParameters.size() != 0){
			throw new MissingParameterException(missingParameters);
		}
		
		for (FunctionParameter parameter : lstParameter){
			av = getAvPair(parameter.getName(),lstAv);
			parameter.setValue(""+av.getValue());	
		}
		
	}
	
	protected AttributeValuePair getAvPair(String parameter, List<AttributeValuePair> lstAv){
		for (AttributeValuePair av : lstAv){
			if (av.getAttribute().toLowerCase().equals(parameter.toLowerCase())){
				return av;
			}
		}
		return null;
	}
	
	protected void eraseSpaces(List<AttributeValuePair> avList){
		for (AttributeValuePair av : avList){
			av.setAttribute(eraseSpaces(av.getAttribute()));
			av.setValue(eraseSpaces(av.getValue()));
		}
	}
	
	protected String eraseSpaces(String attr){
		String res = attr;
		
		while (res.startsWith(" ") && res.length() > 1){
			res = res.substring(1,res.length());
		}
		
		while (res.endsWith(" ") && res.length() > 1){
			res = res.substring(0,res.length()-1);
		}
		
		return res;
	}
	
	private void printAvList(List<AttributeValuePair> avList){
		for (AttributeValuePair av : avList){
			System.out.println("        => "+av.toString());
		}
	}
	
	
	/**
	 * Parsing the parameter to a list of attributes and values
	 * @param strParam <device>.<function>(strParam)
	 * @return List of found attributes and values
	 */
	protected List<AttributeValuePair> parseParameter(String strParam){
		//System.out.println("    strParam = "+strParam);
		int length = strParam.length();
		int begin = 0;
		int  end = length;
		String strTmp = "";
		List<AttributeValuePair> avList = new ArrayList<AttributeValuePair>();
		AttributeValuePair av=null;
		
		while (end > 0){
			end = getNextExpressionIndex(strParam.substring(begin,length))+begin;			
			strTmp = strParam.substring(begin,end);
			
				
			if (end < length){
				if (strParam.charAt(end) == '='){
					av = new AttributeValuePair(strTmp);
				}
				if (strParam.charAt(end) == ';'){
					if (av != null){
						av.setValue(strTmp);
						//System.out.println("    adding \""+av.toString()+"\" to list.");
						avList.add(av);
						av = null;
					}
				}							
			} else {
				if (av != null){
					av.setValue(strTmp);
					//System.out.println("    adding \""+av.toString()+"\" to list.");
					avList.add(av);
					av = null;
				} else {
					if ((strTmp != null)&&(strTmp.length() > 0)){
						av = new AttributeValuePair(SINGLE_ATTRIBUTE_INDICATOR,strTmp);
						avList.add(av);
						//System.out.println("    adding \""+av.toString()+"\" to list.");
						av = null;
					}
				}
			}
			
			
			if (end < length) {
			  end++;
			  begin = end;			 
			} else end = -1;
			
		}	
		return avList;
	}
	
	protected int getNextExpressionIndex(String str){
		if (str.length() <=1) return str.length();	
		int length = str.length();		
		boolean waitForQuotes = false;
		
		for ( int i=0; i<length; i++){
			if (str.charAt(i)=='\"'){
				waitForQuotes = !waitForQuotes;				
			}
			if (!waitForQuotes){
				if (str.charAt(i)=='=') return i;
				if (str.charAt(i)==';') return i;
			}			
		}
		return length;
	}	
	
}
