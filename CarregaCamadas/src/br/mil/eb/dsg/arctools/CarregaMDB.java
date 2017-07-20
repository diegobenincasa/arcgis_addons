package br.mil.eb.dsg.arctools;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.esri.arcgis.addins.desktop.Button;
import com.esri.arcgis.datasourcesGDB.AccessWorkspaceFactory;
import com.esri.arcgis.framework.IApplication;
import com.esri.arcgis.geodatabase.IWorkspace;
import com.esri.arcgis.geodatabase.IWorkspaceFactory;

public class CarregaMDB extends Button {

	JFileChooser fileChooser;
	String mdb;
	IWorkspace mdbWorkspace;
	LLExtension llext;
	IApplication app;
	LoadLayers loadLayers;
	/**
	 * Called when the button is clicked.
	 * 
	 * @exception java.io.IOException if there are interop problems.
	 * @exception com.esri.arcgis.interop.AutomationException if the component throws an ArcObjects exception.
	 */
	@Override
	public void init(IApplication app){
		try {
			llext = LLExtension.getExtension();
			this.app = llext.appl;
			loadLayers = new LoadLayers();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onClick() {
		try{
			fileChooser = new JFileChooser();
			FileFilter aFilter = new FileNameExtensionFilter("ESRI Geodatabase File (*.mdb)","mdb");
			fileChooser.setFileFilter(aFilter);
			fileChooser.setAcceptAllFileFilterUsed(false);
			fileChooser.setMultiSelectionEnabled(false);
			
			int returnVal = fileChooser.showOpenDialog(null);
			
			if(returnVal == JFileChooser.APPROVE_OPTION){
				mdb = fileChooser.getSelectedFile().getAbsolutePath();
				mdb = mdb.replace("\\", "\\\\");
	
				IWorkspaceFactory workspaceFactory = new AccessWorkspaceFactory();
				mdbWorkspace = workspaceFactory.openFromFile(mdb, 0);
				llext.isMDBSelected = true;
				llext.mdbWorkspace = mdbWorkspace;
				llext.currentMDB = fileChooser.getSelectedFile().getName();
				llext.currentMDB = llext.currentMDB.substring(0, llext.currentMDB.length()-4);
				loadLayers.load();
			}
		}
		
		catch(Exception e){
			JOptionPane.showMessageDialog(null, e.toString());
		}
	}
	
	@Override
	public boolean isEnabled(){
		return llext.isCSVSelected;
	}
}
