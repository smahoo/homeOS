# Configuration File


## Common Structure

All driver share the same configuration structure. Each driver will be initialized with the XML-Element ```<driver>```. All child
nodes of ```<driver>``` free of choice, except ```<connection>```. A connection will always be declared in a common way.
The connection itself will be established by the ```IOManger``` which could be received from the running ```homeOs instance```. 

        <driver class="de.smahoo.homeos.driver.cul.CulDriver" autoAddNewDevice="true">
            <connection baudrate="115200" portname="/dev/tty.usbserial-A70251V0" type="IO_TYPE_SERIAL"/>            
        </driver>
        
        
### Node ```<driver>```        
        
| Attribute         |M/O | Description  | 
|-------------------|--- |---           |
| class             |  M |              | 
| autoAddNewDevice  |  M |              |


### Node ```<connection>```

A connection is optional. If a driver needs a connection (e.g. a serial connection), it should not establish the connection
itself. 

| Attribute         |M/O | Description  | 
|-------------------|--- |---           |
| type              | M  | Connection Type| 
| baudrate          | C  | Defining the baudrate (eg. 9600, 115200, etc). Mendatory for IO_TYPE_SERIAL |
| portname          | C  | Defining the portname (eg. "COM7" or "/dev/tty.usbserial". Mendatory for IO_TYPE_SERIAL |
 