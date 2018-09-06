/*
 * AirControl Constants class. Provides general access to 
 * application constants.
 *
 * kaiser@smahoo.de - Apr 2014
 *
 */


function HomeOSServer() {
    console.log("DEBUG: Starting HomeOS Server Access.");
}

/* create static instance */
window.hoss =  new HomeOSServer();
window.hoss.curdevices = [];
window.hoss.curlocations = [];


HomeOSServer.prototype.getLastActivityForDevice = function (device) {
    try {
        if ((device.nodeName !== "#text") && (device.childNodes.length > 0)) {
            return device.attributes.getNamedItem("lastActivity").value;
        }
    } catch (e) {
        return "";
    }
};


HomeOSServer.prototype.getReachableForDevice = function (device) {
    var lastactivity = window.hoss.getLastActivityForDevice(device);
    if ((lastactivity !== undefined) && (lastactivity !== null) && (lastactivity !== "")) {
        return true;
    }
    return false;
};


HomeOSServer.prototype.getLocationForDevice = function (device) {
    try {
        return device.attributes.getNamedItem("location").value;
    } catch (e) {
        return "";
    }
};


HomeOSServer.prototype.getIdForDevice = function (device) {
    try {
        return device.attributes.getNamedItem("id").value;
    } catch (e) {
        return "";
    }
};


HomeOSServer.prototype.getNameForDevice = function (device) {
    try {
        return device.attributes.getNamedItem("name").value;
    } catch (e) {
        return "";
    }
};


HomeOSServer.prototype.getLocationNameForDevice = function (device) {
    var loc = window.hoss.getLocationForDevice(device);
    var loclist = window.hoss.curlocations;
    var i = 0;
    for (i = loclist.length - 1; i >= 0; i--) {
        if (loclist[i].attributes.getNamedItem("id").value === loc) {
            return loclist[i].attributes.getNamedItem("name").value;
        }
    }
    console.log("WARNING: Could not retrieve location name for location " + loc);
    return "";
};



HomeOSServer.prototype.showDeviceList = function () {
    if (xmlHttpObject.readyState === 4 && xmlHttpObject.status === 200) {
        var xmlDoc = xmlHttpObject.responseXML;
        console.log("DEBUG: Data found is " + (new XMLSerializer()).serializeToString(xmlDoc));
        var deviceList = $(xmlDoc).find("sensorclimate");
        console.log("DEBUG: Devicelist found has length " + deviceList.length);
        window.hoss.curdevices = deviceList;
        var locationslist = $(xmlDoc).find("location");
        console.log("DEBUG: Locations list found has length " + locationslist.length);
        window.hoss.curlocations = locationslist;
        $(window.eventbus).trigger("dataHasRefreshed");
        console.log("DEBUG: 'dataHasRefreshed' event has been triggered.");
    }
};


HomeOSServer.prototype.loadDeviceList = function () {
    xmlHttpObject = new XMLHttpRequest();
    if (xmlHttpObject === null) {
        alert("xmlHttpObject is null");
    } else {
        /* var command = '<?xml version="1.0" encoding="UTF-8" ?><request><devicelist/></request>'; */
        var command = '<?xml version="1.0" encoding="UTF-8" ?><request requestType="COMPLETE"></request>';
        xmlHttpObject.open("POST", '', true);
        xmlHttpObject.onreadystatechange = window.hoss.showDeviceList;
        xmlHttpObject.send(command);
        console.log("DEBUG: Did send command: " + command);
    }
};


/**
* Parses a date time string from the HomeOs data base format (yyyy-mm-dd hh:mm:ss)
*
* @param mydatestring The string from the HomeOs data source
* @returns A Javascript Date() object containing the value represented in mydatestring
**/
HomeOSServer.prototype.parseDateString = function (mydatestring) {
    if (mydatestring.length > 0) {
        var date_time_seg = mydatestring.split(" ");
        var dateparts = date_time_seg[0].split("-");
        var timeparts = date_time_seg[1].split(":");
        return new Date(
            dateparts[0],
            dateparts[1] - 1,
            dateparts[2],
            timeparts[0],
            timeparts[1],
            timeparts[2]
        );
    }
    return null;
};


/**
* Checks if a given time string defines a time older than the given time frame.
*
* @param datetimestring The String containing the time to be checked
* @param outdatingtime The time frame to be checked against
* @returns True if datetimestring value is older than allowed by outdatingtime, false otherwise
**/
HomeOSServer.prototype.isOlderThan = function (datetimestring, outdatingtime) {
    if (datetimestring.length === 0) {
        return true;
    }
    var compdate = window.hoss.parseDateString(datetimestring);
    console.log("DEBUG: checking timestamp " + datetimestring + " . Conversion resulted in " + compdate.toString());
    if (compdate >= new Date() - (outdatingtime * 60000)) {
        return false;
    }
    return true;
};


HomeOSServer.prototype.removeDevice = function (deviceid) {
    xmlHttpObject = new XMLHttpRequest();
    if (xmlHttpObject === null) {
        alert("xmlHttpObject is null, cannot create request to delete device.");
    } else {
        /* var command = '<?xml version="1.0" encoding="UTF-8" ?><request><devicelist/></request>'; */
        var command = '<?xml version="1.0" encoding="UTF-8" ?><cmd><delete><device id="'
            + deviceid + '"/></delete></cmd>';

        xmlHttpObject.open("POST", '', true);
        xmlHttpObject.onreadystatechange = function () {
            location.reload(true);
        };
        xmlHttpObject.send(command);
        console.log("DEBUG: Did send command: " + command);
    }
};


HomeOSServer.prototype.saveLocationForDevice = function (deviceid, locationid) {
    xmlHttpObject = new XMLHttpRequest();
    if (xmlHttpObject === null) {
        alert("xmlHttpObject is null, cannot create request to save location for device.");
    } else {
        /* var command = '<?xml version="1.0" encoding="UTF-8" ?><request><devicelist/></request>'; */
        var command = '<?xml version="1.0" encoding="UTF-8" ?><cmd><change><device id="'
            + deviceid
            + '"> <property name="location" value="'
            + locationid
            + '"/></device></change></cmd>';

        xmlHttpObject.open("POST", '', true);
        xmlHttpObject.onreadystatechange = function () {
            location.reload(true);
        };
        xmlHttpObject.send(command);
        console.log("DEBUG: Did send command: " + command);
    }
};


HomeOSServer.prototype.saveNewLocation = function (newloc, newltype) {
    xmlHttpObject = new XMLHttpRequest();
    if (xmlHttpObject === null) {
        alert("xmlHttpObject is null, cannot create request to store new location device.");
    } else {
        /* var command = '<?xml version="1.0" encoding="UTF-8" ?><request><devicelist/></request>'; */
        var command = '<?xml version="1.0" encoding="UTF-8" ?><cmd><add><location name="'
            + newloc + '" type="' + newltype + '"/></add></cmd>';

        xmlHttpObject.open("POST", '', true);
        xmlHttpObject.onreadystatechange = function () {
            location.reload(true);
        };
        xmlHttpObject.send(command);
        console.log("DEBUG: Did send command: " + command);
    }
};
