/*
 *  Copyright (C) 2004  The Concord Consortium, Inc.,
 *  10 Concord Crossing, Concord, MA 01742
 *
 *  Web Site: http://www.concord.org
 *  Email: info@concord.org
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * END LICENSE */

/*
 * Last modification information:
 * $Revision: 1.3 $
 * $Date: 2007-03-09 17:51:59 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor.view;

import javax.swing.JComponent;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectService;
import org.concord.framework.otrunk.view.OTActionView;
import org.concord.framework.otrunk.view.OTJComponentView;
import org.concord.framework.otrunk.view.OTViewFactory;
import org.concord.framework.otrunk.view.OTViewFactoryAware;
import org.concord.sensor.state.OTLoggingRequest;
import org.concord.sensor.state.OTSetupLogger;

public class OTLoggingRequestView
    implements OTJComponentView, OTViewFactoryAware
{
    protected OTViewFactory viewFactory;
    protected OTLoggingRequest request;
    
    /**
     * This should be replaced by a facility 
     * for creating views from otrunk objects.  This would
     * be a new type of OTViewEntry that instead of taking 
     * view class it would take an OTObject and a perhaps a 
     * path to set a part of that object with the input object
     */
    public JComponent getComponent(OTObject otObject, boolean editable)
    {
        request = (OTLoggingRequest)otObject;

        try {
            OTObjectService otService = request.getOTObjectService();
            ClassLoader cLoader = getClass().getClassLoader();
            Class otButtonClass =
            	cLoader.loadClass("org.concord.otrunk.control.OTButton");
            OTObject buttonObj = otService.createObject(otButtonClass);

            OTSetupLogger setupObj = (OTSetupLogger)
                otService.createObject(OTSetupLogger.class);
            
            setupObj.setRequest(request);

            ((OTActionView)buttonObj).setAction(setupObj);
            
            return viewFactory.getComponent(buttonObj, null, editable);            
        } catch (Exception e){
            
        }
        
        return null;
    }

    public void viewClosed()
    {
        // TODO Auto-generated method stub
        
    }

	public void setViewFactory(OTViewFactory factory) {
		viewFactory = factory;		
	}
}
