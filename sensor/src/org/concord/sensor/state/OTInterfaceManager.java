/*
 * Created on Jan 12, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.concord.sensor.state;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectList;
import org.concord.framework.otrunk.OTResourceSchema;
import org.concord.sensor.DeviceConfig;
import org.concord.sensor.device.impl.InterfaceManager;

/**
 * @author Informaiton Services
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class OTInterfaceManager extends InterfaceManager 
	implements OTObject 
{
	public static interface ResourceSchema extends OTResourceSchema 
	{
		OTObjectList getDeviceConfigs(); 
	}
	private ResourceSchema resources;
	/**
	 * @param h
	 */
	public OTInterfaceManager(ResourceSchema resources) {
		// FIXME we need a message hander to pass to this
		// interface manager
		super(new PrintUserMessageHandler());
		
		this.resources = resources;
	}

	/* (non-Javadoc)
	 * @see org.concord.framework.otrunk.OTObject#getGlobalId()
	 */
	public OTID getGlobalId() {
		return resources.getGlobalId();
	}

	/* (non-Javadoc)
	 * @see org.concord.framework.otrunk.OTObject#getName()
	 */
	public String getName() {
		return resources.getName();
	}

	/* (non-Javadoc)
	 * @see org.concord.framework.otrunk.OTObject#setName(java.lang.String)
	 */
	public void setName(String name) {
		resources.setName(name);
	}

	/* (non-Javadoc)
	 * @see org.concord.framework.otrunk.OTObject#init()
	 */
	public void init() 
	{
		OTObjectList deviceConfigList = resources.getDeviceConfigs();
		DeviceConfig [] configs = new DeviceConfig [deviceConfigList.size()];
		for(int i=0; i<configs.length; i++) {
			configs [i] = (DeviceConfig)deviceConfigList.get(i);
		}
		setDeviceConfigs(configs);
	}
}
