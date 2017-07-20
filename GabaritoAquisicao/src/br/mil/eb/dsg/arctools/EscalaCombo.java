package br.mil.eb.dsg.arctools;

import java.io.IOException;
import java.net.UnknownHostException;

//import javax.swing.JOptionPane;

import com.esri.arcgis.addins.desktop.ComboBox;
import com.esri.arcgis.framework.IApplication;
import com.esri.arcgis.interop.AutomationException;
import com.esri.arcgis.system.UID;

public class EscalaCombo extends ComboBox {

	/**
	 * Called when the combo box is initialized. Subclasses must implement this method
	 * to add entries to the combo box using the add() method.
	 * 
	 * @exception java.io.IOException if there are interop problems.
	 * @exception com.esri.arcgis.interop.AutomationException if the component throws an ArcObjects exception.
	 */
	private int[] options = new int[4];
	private int curSelect;
	private static EscalaCombo s_extension;
	static IApplication app;
	GabExtension gabExt;

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		s_extension = this;
		try{
			gabExt = GabExtension.getExtension();
			EscalaCombo.app = gabExt.appl;
			
			options[0] = add("1:25.000");
			options[1] = add("1:50.000");
			options[2] = add("1:100.000");
			options[3] = add("1:250.000");
			
			select(options[0]);
			
			curSelect = getSelected();
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
	public void onEditChange(String arg0) throws IOException,
			AutomationException {
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
	public void onFocus(boolean setFocus) throws IOException,
			AutomationException {
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
	public void onSelChange(int cookie) throws IOException, AutomationException {
		curSelect = cookie;
	}
	
	public static EscalaCombo getExtension() throws UnknownHostException, IOException{
		if(s_extension == null){
			UID pUID = new UID();
			pUID.setValue("br.mil.eb.dsg.arctools.escalacombo");
			app.findExtensionByCLSID(pUID);
		}
		return s_extension;
	}
	
	public double getEscala() throws Exception{
		double escala = 0;

		if(curSelect == options[0])
			escala = 25000;
		else if(curSelect == options[1])
			escala = 50000;
		else if(curSelect == options[2])
			escala = 100000;
		else if(curSelect == options[3])
			escala = 250000;

		return escala;
	}
}
