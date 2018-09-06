package de.smahoo.homeos.db;

import de.smahoo.homeos.common.Event;
import de.smahoo.homeos.common.EventType;

public class DataBaseManagerEvent extends Event {
	public DataBaseManagerEvent(EventType type, String description){
		super(type,description);
	}
}
