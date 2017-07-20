package br.mil.eb.dsg.arctools;

import java.io.IOException;

import com.esri.arcgis.addins.desktop.Extension;
import com.esri.arcgis.arcmapui.IMxDocument;
import com.esri.arcgis.framework.IApplication;
import com.esri.arcgis.geodatabase.IWorkspace;
import com.esri.arcgis.interop.AutomationException;
import com.esri.arcgis.system.UID;

public class LLExtension extends Extension {

	private static IApplication app;
	public IApplication appl;
	private static LLExtension s_extension;
	public boolean isMDBSelected;
	public IWorkspace mdbWorkspace;
	public IMxDocument mxDoc;
	public String currentMDB;
	public String csvOrdem;
	public boolean isCSVSelected;
	
	/**
	 * Initializes this application extension with the ArcMap application instance it is hosted in.
	 * 
	 * This method is automatically called by the host ArcMap application.
	 * It marks the start of the dockable window's lifecycle.
	 * Clients must not call this method.
	 * 
	 * @param app is a reference to ArcMap's IApplication interface
	 * @exception java.io.IOException if there are interop problems.
	 * @exception com.esri.arcgis.interop.AutomationException if the component throws an ArcObjects exception.
	 */
	@Override
	public void init(IApplication app)
			throws IOException, AutomationException {
		LLExtension.app = app;
		this.appl = app;
		s_extension = this;
		isMDBSelected = false;
		isCSVSelected = false;
		mxDoc = (IMxDocument) app.getDocument();
	}
	
	public static LLExtension getExtension() throws Exception{
		if(s_extension == null){
				UID pUID = new UID();
				pUID.setValue("br.mil.eb.dsg.arctools.loadlayers");
				app.findExtensionByCLSID(pUID);
		}
		return s_extension;
	}
}
