/*
 * Created on Jan 11, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.concord.sensor;

/**
 * @author scytacki
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface DeviceConfig {
	public void setDeviceId(int id);

	public int getDeviceId();

	public void setConfigString(String config);

	public String getConfigString();
}