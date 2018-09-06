package de.smahoo.homeos.kernel.remote.result.cmd;

public class RemoteCmdException extends Exception {

	public RemoteCmdException(String cmd, String description){
		super(description);
	}
	
}
