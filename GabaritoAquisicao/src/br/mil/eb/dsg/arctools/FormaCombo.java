package br.mil.eb.dsg.arctools;

import java.io.IOException;

//import javax.swing.JOptionPane;
import com.esri.arcgis.addins.desktop.ComboBox;
import com.esri.arcgis.framework.IApplication;
import com.esri.arcgis.interop.AutomationException;
import com.esri.arcgis.system.UID;

public class FormaCombo extends ComboBox {

	int CONST_CIRCULO;
	int CONST_QUADRADO;
	int option1;
	int option2;
	int curSelect;
	private static FormaCombo s_extension;
	static IApplication app;
	
	/**
	 * Called when the combo box is initialized. Subclasses must implement this method
	 * to add entries to the combo box using the add() method.
	 * 
	 * @exception java.io.IOException if there are interop problems.
	 * @exception com.esri.arcgis.interop.AutomationException if the component throws an ArcObjects exception.
	 */
	@Override
	public void initialize() {
		s_extension = this;
		try{
			GabExtension gabExt = GabExtension.getExtension();
			FormaCombo.app = gabExt.appl;
		}
		catch(Exception e){}
	}

	/**
	 * Called by system when the edit box is typed into (if editable)
	 * 
	 * @param editString the String typed into the edit box
	 * @exception java.io.IOException if there are interop problems.
	 * @exception com.esri.arcgis.interop.AutomationException if the component throws an ArcObjects exception.
	 */
	@Override
	public void onEditChange(String editString)
			throws IOException, AutomationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEnter() throws IOException, AutomationException {
		// TODO Auto-generated method stub

	}

	/**
	 * Called by system when the combo box gets or loses focus
	 * 
	 * @param setFocus - true when combo box gets focus, false when it loses focus
	 * @exception java.io.IOException if there are interop problems.
	 * @exception com.esri.arcgis.interop.AutomationException if the component throws an ArcObjects exception.
	 */
	@Override
	public void onFocus(boolean setFocus)
			throws IOException, AutomationException {
		// TODO Auto-generated method stub

	}

	/**
	 * Called by system when a selection changes
	 * 
	 * @param cookie the item selected
	 * @exception java.io.IOException if there are interop problems.
	 * @exception com.esri.arcgis.interop.AutomationException if the component throws an ArcObjects exception.
	 */
	@Override
	public void onSelChange(int cookie)
			throws IOException, AutomationException {
		curSelect = getSelected();
	}

	public static FormaCombo getExtension() throws Exception{
		if(s_extension == null){
				UID pUID = new UID();
				pUID.setValue("br.mil.eb.dsg.arctools.formacombo");
				app.findExtensionByCLSID(pUID);
		}
		return s_extension;
	}
}
