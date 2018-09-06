package de.smahoo.homeos.testing.gui;


import de.smahoo.homeos.automation.Rule;
import de.smahoo.homeos.automation.RuleGroup;
import de.smahoo.homeos.device.Device;
import de.smahoo.homeos.device.DeviceType;
import de.smahoo.homeos.driver.Driver;
import de.smahoo.homeos.location.Location;

public interface SelectionListener {

	void onDeviceSelected(Device device);
	void onDriverSelected(Driver driver);
	void onLocationSelected(Location location);
	void onDeviceTypeSelected(DeviceType deviceType);
	void onRuleSelected(Rule rule);
	void onRuleGroupSelected(RuleGroup group);
}
