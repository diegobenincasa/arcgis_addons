package br.mil.eb.dsg.arctools;

import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.esri.arcgis.addins.desktop.Button;
import com.esri.arcgis.framework.IApplication;
import com.esri.arcgis.interop.AutomationException;

public class SelectCSV extends Button {
	LLExtension llext;
	IApplication app;

	/**
	 * Called when the button is clicked.
	 * 
	 * @exception java.io.IOException if there are interop problems.
	 * @exception com.esri.arcgis.interop.AutomationException if the component throws an ArcObjects exception.
	 */
	@Override
	public void onClick() throws IOException, AutomationException {
		JFileChooser fileChooser = new JFileChooser();
		FileFilter aFilter = new FileNameExtensionFilter("Comma-separated values file (.csv)","csv");
		fileChooser.setFileFilter(aFilter);
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setDialogTitle("Selecionar arquivo de ordem de camadas");
		int returnVal = fileChooser.showOpenDialog(null);
		if(returnVal == JFileChooser.APPROVE_OPTION)
		{
			llext.csvOrdem = fileChooser.getSelectedFile().getAbsolutePath();
			JOptionPane.showMessageDialog(null, "Arquivo selecionado!");
			llext.isCSVSelected = true;
		}

	}
	
	@Override
	public void init(IApplication app){
		try {
			llext = LLExtension.getExtension();
			this.app = llext.appl;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
