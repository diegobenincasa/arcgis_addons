package br.mil.eb.dsg.arctools;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.JFileChooser;
//import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.esri.arcgis.addins.desktop.Button;
import com.esri.arcgis.framework.IApplication;
import com.esri.arcgis.interop.AutomationException;

public class AbreCSV extends Button {

	/**
	 * Called when the button is clicked.
	 * 
	 * @exception java.io.IOException if there are interop problems.
	 * @exception com.esri.arcgis.interop.AutomationException if the component throws an ArcObjects exception.
	 */
	JFileChooser fileDialog;
	IApplication app;
	GabExtension gabExt;

	@Override
	public void init(IApplication app) throws UnknownHostException, IOException{
		this.app = app;
		gabExt = GabExtension.getExtension();
	}
	
	@Override
	public void onClick() throws IOException, AutomationException {
		// TODO Auto-generated method stub
		try {
			gabExt = GabExtension.getExtension();
			fileDialog = new JFileChooser();
			FileFilter aFilter = new FileNameExtensionFilter("Comma-Separated Values File (*.csv)","csv");
			fileDialog.setFileFilter(aFilter);
			
			int returnVal = fileDialog.showOpenDialog(null);
			
			if(returnVal == JFileChooser.APPROVE_OPTION){
				String filename = fileDialog.getSelectedFile().getAbsolutePath().toString();
				gabExt.setCSVPath(filename.replace("\\","\\\\"));
				gabExt.loadCSV();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
