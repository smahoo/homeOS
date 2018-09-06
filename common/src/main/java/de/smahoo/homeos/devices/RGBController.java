package de.smahoo.homeos.devices;

public interface RGBController {
	public void setRGB(int red, int green, int blue);
	public long getRGB();
	public int getRed();
	public int getGreen();
	public int getBlue();
}
