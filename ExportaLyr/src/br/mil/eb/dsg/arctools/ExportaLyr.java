package br.mil.eb.dsg.arctools;

import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.esri.arcgis.addins.desktop.Button;
import com.esri.arcgis.arcmapui.IMxDocument;
import com.esri.arcgis.carto.IEnumLayer;
import com.esri.arcgis.carto.ILayer;
import com.esri.arcgis.carto.ILayerFile;
import com.esri.arcgis.carto.LayerFile;
import com.esri.arcgis.framework.IApplication;
import com.esri.arcgis.interop.AutomationException;

public class ExportaLyr extends Button {

	IApplication app;
	IMxDocument mxDoc;
	/**
	 * Called when the button is clicked.
	 * 
	 * @exception java.io.IOException if there are interop problems.
	 * @exception com.esri.arcgis.interop.AutomationException if the component throws an ArcObjects exception.
	 */
	@Override
	public void onClick() throws IOException, AutomationException {
		
		JFileChooser fileChooser = new JFileChooser();
		ILayerFile lyrFile = new LayerFile();
		IEnumLayer allLayers = mxDoc.getFocusMap().getLayers(null, true);
		
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setDialogTitle("Selecionar diretório de exportacao");
		
		int returnVal = fileChooser.showOpenDialog(null);
		int totalFL = 0;
		
		if(returnVal == JFileChooser.APPROVE_OPTION)
		{
			ILayer tempLayer;
			while((tempLayer = allLayers.next()) != null)
			{
				if(tempLayer.getClass().getSimpleName().equals("FeatureLayer"))
				{
					totalFL++;
					String path = fileChooser.getSelectedFile().getAbsolutePath();
					path += "\\";
					path += tempLayer.getName();
					path += ".lyr";
					lyrFile.esri_new(path);
					lyrFile.replaceContents(tempLayer);
					lyrFile.save();
					lyrFile.close();
				}
			}
		}
		
		JOptionPane.showMessageDialog(null, totalFL + " camadas exportadas com sucesso!");
	}
	
	@Override
	public void init(IApplication app) throws AutomationException, IOException{
		this.app = app;
		mxDoc = (IMxDocument) app.getDocument();
	}
}
