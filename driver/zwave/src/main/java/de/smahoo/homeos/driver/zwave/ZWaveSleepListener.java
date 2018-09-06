package de.smahoo.homeos.driver.zwave;

public interface ZWaveSleepListener {
	abstract void onWakeUpIntervalSet(long seconds);
	abstract void onSleep();
	abstract void onWakeUp();
}
