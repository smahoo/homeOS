package de.smahoo.homeos.automation;


import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.smahoo.homeos.common.Function;
import de.smahoo.homeos.device.Device;
import de.smahoo.homeos.device.ParameterizedDeviceFunction;
import de.smahoo.homeos.device.PhysicalDevice;
import de.smahoo.homeos.kernel.HomeOs;
import de.smahoo.homeos.property.Property;
import de.smahoo.homeos.utils.AttributeValuePair;

public class RuleFactory {
	
	private static RuleFactory instance = null;
	private RuleEngine ruleEngine;
	
	protected RuleFactory(){
		ruleEngine = HomeOs.getInstance().getRuleEngine();
	}
	
	public static RuleFactory getInstance(){
		if (instance == null){
			instance = new RuleFactory();
		}
		return instance;
	}
		
	public void checkForApplicability(Rule rule){		
		boolean conditionCheck = this.createConditions(rule,rule.getXmlSource());
		boolean actionCheck    = this.createActions(rule, rule.getXmlSource());
		rule.setApplicable(conditionCheck && actionCheck);
	}
	
	public void initRuleEngine(Document document){
		if (document == null) return;		
		NodeList nodelist = document.getElementsByTagName("ruleengine");		
		for (int i = 0; i< nodelist.getLength(); i++){
			if (nodelist.item(i) instanceof Element){
				initRuleEngine((Element)nodelist.item(i));				
			}
		}
	}
	
	
	
	public void initRuleEngine(Element element){
		if (element == null) return;
		if (!(element.getTagName().equalsIgnoreCase("ruleengine"))) return;
		deleteAllRules();
		createRules(element.getChildNodes());
		
		
		
	}
	
	private void createRules(NodeList nodelist){
		if (nodelist == null) return;
		if (nodelist.getLength() == 0) return;
		Element tmpElem;		
		for (int i= 0; i< nodelist.getLength(); i++){
			if (nodelist.item(i) instanceof Element){
				tmpElem = (Element)nodelist.item(i);
				if (tmpElem.getTagName().equalsIgnoreCase("rulegroup")){
					createRuleGroup(tmpElem);
				}
				if (tmpElem.getTagName().equalsIgnoreCase("rule")){
					createRule(tmpElem);					
				}
			}
		}
	}
	
	private void createRuleGroup(Element element){
		RuleGroup group = new RuleGroup();
		if (element.hasAttribute("name")){
			group.setName(element.getAttribute("name"));			
		} else {
			group.setName("RuleGroup_"+(ruleEngine.ruleGroups.size() +1 ));
		}
		if (element.hasAttribute("description")){
			group.setDescription(element.getAttribute("description"));
		}
		NodeList list = element.getChildNodes();
		Element tmpElem;
		Rule tmpRule;
		for (int i = 0; i<list.getLength(); i++){
			if (list.item(i) instanceof Element){
				tmpElem = (Element)list.item(i);
				if (tmpElem.getTagName().equalsIgnoreCase("rule")){
					tmpRule = createRule(tmpElem);
					if (tmpRule != null){
						group.addRule(tmpRule);
					}
				}
			}
		}
		ruleEngine.ruleGroups.add(group);
	}
	
	private boolean createConditions(Rule rule, Element element){
		rule.removeConditions();
		boolean result = true;
		NodeList nodelist = element.getChildNodes();
		Element elem;
		Condition tmp;
		for (int i=0; i<nodelist.getLength(); i++){
			if (nodelist.item(i) instanceof Element){
				elem = (Element)nodelist.item(i);
				if (elem.getTagName().equalsIgnoreCase("condition")){
					tmp = createCondition(elem);
					if (tmp == null){
						result = false;
					} else {
						rule.addCondition(createCondition(elem));
					}
				}				
			}
		}
		return result;
	}
	
	private boolean createActions(Rule rule, Element element){
		rule.removeActions();
		boolean result = true;
		NodeList nodelist = element.getChildNodes();
		Element elem;		
		for (int i=0; i<nodelist.getLength(); i++){
			if (nodelist.item(i) instanceof Element){
				elem = (Element)nodelist.item(i);
				
				if (elem.getTagName().equalsIgnoreCase("action")){
					RuleAction action =createRuleAction(elem); 
					if (action == null){
						result = false;
					} else {
						rule.addRuleAction(action);
					}
				}
			}
		}
		return result;
	}
	
	private Rule createRule(Element element){
		Rule rule = new Rule();
		rule.setXmlSource(element);
		if (element.hasAttribute("name")){
			rule.setName(element.getAttribute("name"));
		} else {
			rule.setName("Rule_"+(ruleEngine.rules.size()+1));
		}
		if (element.hasAttribute("description")){
			rule.setDescription(element.getAttribute("description"));
		}
		if (element.hasAttribute("enabled")){
			rule.setEnabled(element.getAttribute("enabled").equalsIgnoreCase("true"));
		} else {
			rule.setEnabled(false);
		}		
		checkForApplicability(rule);
		ruleEngine.addRule(rule);
		return rule;
	}
	
	protected RuleAction createRuleAction(Element elem){
		if (elem == null) return null;
		RuleAction action = null;
		
		if (elem.getElementsByTagName("execute").getLength() >= 1){
			action = createFunctionExecutionAction(elem.getElementsByTagName("execute"));
		}
		if (elem.getElementsByTagName("send").getLength() >=1 ){
			action = createSendAction(elem.getElementsByTagName("send"));
		}
		
		return action;
	}
	
	protected SendAction createSendAction(NodeList nodeList){
		SendAction action = null;
		
		Element element = null;
		
		for (int i=0; i<nodeList.getLength(); i++){
			if (nodeList.item(i) instanceof Element){
				element = (Element)nodeList.item(i);
			}
		}
		
		if (element.hasAttribute("type")){
			String type = element.getAttribute("type");
			if ("MT_EMAIL".equalsIgnoreCase(type)){
				action = generateSendEmailAction(element);
			}
		}
		
		return action;
	}
	
	protected SendEmailAction generateSendEmailAction(Element elem){
		SendEmailAction action = null;
		
		String address = null;
		String subject = null;
		String text = null;
		
		NodeList nodeList = elem.getChildNodes();
		Element tmp = null;
		for (int i = 0; i<nodeList.getLength(); i++){
			if (nodeList.item(i) instanceof Element){
				tmp = (Element)nodeList.item(i);
				if ("property".equalsIgnoreCase(tmp.getTagName())){
					if ("address".equalsIgnoreCase(tmp.getAttribute("name"))){
						address = tmp.getAttribute("value");
					}
					if ("subject".equalsIgnoreCase(tmp.getAttribute("name"))){
						subject = tmp.getAttribute("value");
					}
					if ("text".equalsIgnoreCase(tmp.getAttribute("name"))){
						text = tmp.getAttribute("value");
					}
				}
			}
		}
		
		if ((address != null) && (subject != null) && (text != null)){
			action = new SendEmailAction(address, subject, text);
		}
		
		return action;
	}
	
	protected FunctionExecutionAction createFunctionExecutionAction(NodeList nodeList){
		FunctionExecutionAction action = null;
		String deviceId = null;
		String functionName = null;
		Element element = null;
		
		for (int i=0; i<nodeList.getLength(); i++){
			if (nodeList.item(i) instanceof Element){
				element = (Element)nodeList.item(i);
			}
		}
		if (element == null) return null;
		if (element.hasAttribute("deviceId")){
			deviceId = element.getAttribute("deviceId");
		}
		if (element.hasAttribute("function")){
			functionName = element.getAttribute("function");
		}
		
		Device device = HomeOs.getInstance().getDeviceManager().getDevice(deviceId);
		Function function = null;
		if (device != null){
			if (device instanceof PhysicalDevice){
				function = ((PhysicalDevice)device).getFunction(functionName);
			}
		}
		if (function != null){
			if (function instanceof ParameterizedDeviceFunction){
				if (element.hasChildNodes()){
					action = new FunctionExecutionAction(function, generateParameter(element.getChildNodes()));
				}
			} else {
				action = new FunctionExecutionAction(function);
			}
		}		
		return action;
	}
	
	
	protected List<AttributeValuePair> generateParameter(NodeList nodeList){
		List<AttributeValuePair> avList = new ArrayList<AttributeValuePair>();
		Element elem;
		for (int i=0; i< nodeList.getLength(); i++){
			if (nodeList.item(i) instanceof Element){
				elem = (Element)nodeList.item(i);
				if (elem.hasAttribute("name")){
					if (elem.hasAttribute("value")){
						avList.add(new AttributeValuePair(elem.getAttribute("name"), elem.getAttribute("value")));
					}
				}
			}
		}
		
		return avList;
	}
	
	protected Condition createCondition(Element elem){
		if (elem == null) return null;
		Condition condition = null;
		if (elem.getElementsByTagName("time").getLength() > 0){
			condition =  createTimeCondition(elem.getElementsByTagName("time"));
		}
		if (elem.getElementsByTagName("property").getLength() > 0){
			condition =  createDevicePropertyCondition(elem.getElementsByTagName("property"));
		}
		if (condition != null){
			if (elem.hasAttribute("description")){
				condition.setDescription(elem.getAttribute("description"));
			}
		}
		return condition;
	}
	
	protected PropertyCondition createTimeCondition(NodeList nodeList){
		return null;
	}
	
	protected PropertyCondition createDevicePropertyCondition(NodeList nodelist){
		PropertyCondition condition = null;
		if (nodelist.getLength() <= 0) return null;
		Element elem;
		String strDevice = null;	
		String strPropertyName = null;		
		String strOperantType = null;		
		String strValue = null;
		
		for (int i=0; i< nodelist.getLength(); i++){
			if (nodelist.item(i) instanceof Element){
				elem = (Element)nodelist.item(i);
				if (elem.getTagName().equalsIgnoreCase("property")){
					strDevice = elem.getAttribute("deviceId");
					strOperantType = elem.getAttribute("operantType");
					strPropertyName = elem.getAttribute("name");
					strValue = elem.getAttribute("value");
				}
			}
		}
		
		if ((strDevice == null) || (strPropertyName == null) || (strOperantType == null) || (strValue == null)){
			return null;
		}
		
		Device device = HomeOs.getInstance().getDeviceManager().getDevice(strDevice);
		if (device == null){
			return null;
		}
		Property property = null;
		if (device instanceof PhysicalDevice){
			property = ((PhysicalDevice)device).getProperty(strPropertyName);
			if (property != null){
				condition = new PropertyCondition(property,strValue,strOperantType);
			}
		}
		return condition;
	}
	
	protected void deleteAllRules(){	
		RuleGroup tmpGroup;
		while(!ruleEngine.ruleGroups.isEmpty()){
			tmpGroup = ruleEngine.ruleGroups.get(0);
			ruleEngine.ruleGroups.remove(tmpGroup);
			for (Rule rule : tmpGroup.getRules()){
				tmpGroup.removeRule(rule);
			}
		}
		Rule tmp;
		while (!ruleEngine.rules.isEmpty()){
			tmp = ruleEngine.rules.get(0);
			ruleEngine.rules.remove(tmp);
			deleteRule(tmp);
		}
	}
	
	protected void deleteRule(Rule rule){
		
	}
}
