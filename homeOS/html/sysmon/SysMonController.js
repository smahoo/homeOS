/*
 * AirControl Constants class. Provides general access to 
 * application constants.
 *
 * kaiser@smahoo.de - Apr 2014
 *
 */


function SysMonController() {
    console.log("DEBUG: Starting SysMonController.");
}

window.uicontroller = new SysMonController();
window.eventbus = {};

SysMonController.prototype.getLocationSelector = function (device, preselect) {
    console.log("Creating location list with default location " + preselect);
    var data = window.hoss.curlocations;
    var selectorstring = "";
    var deviceid = device.attributes.getNamedItem("id").value;
    selectorstring = selectorstring + "<select id='locationselector"
        + deviceid + "'"
        + " deviceid='" + deviceid
        + "' onChange=\"$(window.eventbus).trigger('storeNewLocation', '"
        + deviceid + "');\"" + ">";
    selectorstring = selectorstring + "<option value='-'> </option>";
    var i;
    for (i = data.length - 1; i >= 0; i--) {
        if (data[i].attributes.getNamedItem("name").value === preselect) {
            selectorstring = selectorstring + "<option value='"
                + data[i].attributes.getNamedItem("id").value
                + "' selected='selected'>"
                + data[i].attributes.getNamedItem("name").value
                + "</option>";
        } else {
            selectorstring = selectorstring + "<option value='"
                + data[i].attributes.getNamedItem("id").value
                + "'>" + data[i].attributes.getNamedItem("name").value
                + "</option>";
        }
    }
    selectorstring = selectorstring + "</select>";
    return selectorstring;
};


SysMonController.prototype.removeDevice = function (deldevice) {
    if (confirm("Do you really want to remove " + deldevice + "?")) {
        console.log("DEBUG: Deleting device " + deldevice);
        window.hoss.removeDevice(deldevice);
    }
};


SysMonController.prototype.drawSysMon = function () {
    console.log("DEBUG: Drawing sensors table.");
    var tbod = document.getElementById("sensortablebody");
    var data = window.hoss.curdevices;
    var tablestring = "";
    var x = document.getElementById('refresh');
    var time = x.options[x.selectedIndex].value;
    var i = 0;
    for (i = data.length - 1; i >= 0; i--) {
        tablestring = tablestring + "<tr>";
        tablestring = tablestring + "<th" + " id='"
            + window.hoss.getIdForDevice(data[i]) + "'" + ">"
            + window.hoss.getIdForDevice(data[i])
            + "&nbsp;&nbsp;"
            + "<img onclick='window.uicontroller.removeDevice(\""
            + window.hoss.getIdForDevice(data[i])
            + "\");' src='./delete-black.png'></img>" + "</th>";
        tablestring = tablestring + "<td>"
            + window.uicontroller.getLocationSelector(data[i], window.hoss.getLocationNameForDevice(data[i]))
            + "</td>";

        if (window.hoss.getReachableForDevice(data[i])) {
            tablestring = tablestring + "<td>" + window.hoss.getReachableForDevice(data[i]) + "</td>";
        } else {
            tablestring = tablestring + "<td class='nocontact'>" + window.hoss.getReachableForDevice(data[i]) + "</td>";
        }

        if (window.hoss.isOlderThan(window.hoss.getLastActivityForDevice(data[i]), time)) {
            tablestring = tablestring + "<td class='issue'>" + window.hoss.getLastActivityForDevice(data[i]) + "</td>";
        } else {
            tablestring = tablestring + "<td>" + window.hoss.getLastActivityForDevice(data[i]) + "</td>";
        }
        tablestring = tablestring + "</tr>";
    }
    tbod.innerHTML = tablestring;
};


SysMonController.prototype.storeNewLocation = function (devid) {
    var locselect = document.getElementById("locationselector" + devid);
    var newselection = locselect.options[locselect.selectedIndex].value;
    if (newselection !== "-") {
        if (confirm("Store new location " + newselection + " for " + devid + " ?")) {
            window.hoss.saveLocationForDevice(devid, newselection);
        }
    }
    location.reload(true);
};


SysMonController.prototype.saveNewLocation = function () {
    var newloc = document.getElementById("newLocationInput").value;
    var newltype = document.getElementById("LocationTypeList").value;
    newloc = $.trim(newloc);
    if (newloc === "") {
        return;
    }
    if (newloc === "new location") {
        return;
    }
    window.hoss.saveNewLocation(newloc, newltype);
};

$(window.eventbus).bind("dataHasRefreshed", function () {
    console.log("DEBUG: 'dataHasRefreshed' event has been received.");
    window.uicontroller.drawSysMon();
});

$(window.eventbus).bind("storeNewLocation", function (e, devid) {
    console.log("DEBUG: 'storeNewLocation' event has been received with param: " + devid);
    window.uicontroller.storeNewLocation(devid);
});

$(window.eventbus).bind("saveNewLocation", function () {
    console.log("DEBUG: 'saveNewLocation' event has been received.");
    window.uicontroller.saveNewLocation();
});
