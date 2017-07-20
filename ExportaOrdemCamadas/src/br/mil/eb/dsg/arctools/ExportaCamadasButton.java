package br.mil.eb.dsg.arctools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.esri.arcgis.addins.desktop.Button;
import com.esri.arcgis.arcmapui.IMxDocument;
import com.esri.arcgis.carto.ICompositeLayer;
import com.esri.arcgis.carto.IEnumLayer;
import com.esri.arcgis.carto.IGroupLayer;
import com.esri.arcgis.carto.ILayer;
import com.esri.arcgis.framework.IApplication;
import com.esri.arcgis.system.IUID;
import com.esri.arcgis.system.UID;

public class ExportaCamadasButton extends Button {

	public static final String GROUPLAYER = "{EDAD6644-1810-11D1-86AE-0000F8751720}";
	public static final String FEATURELAYER = "{40A9E885-5533-11d0-98BE-00805F7CED21}";
	
	IApplication app;
	
	List<IGroupLayer> groupLayers = new ArrayList<IGroupLayer>();
	List<ICompositeLayer> compositeLayers = new ArrayList<ICompositeLayer>();
	
	@Override
	public void init(IApplication app) throws UnknownHostException, IOException{
		this.app = app;
		IUID UID_GroupLayer = new UID();
		IUID UID_FeatureLayer = new UID();
		UID_GroupLayer.setValue(GROUPLAYER);
		UID_FeatureLayer.setValue(FEATURELAYER);
	}
	
	
	/**
	 * Called when the button is clicked.
	 * 
	 * @exception java.io.IOException if there are interop problems.
	 * @exception com.esri.arcgis.interop.AutomationException if the component throws an ArcObjects exception.
	 */
	@Override
	public void onClick(){
		// TODO Auto-generated method stub
		try
		{
			JFileChooser fileChooser = new JFileChooser(){
				private static final long serialVersionUID = 8634369222564279885L;

				@Override
				public void approveSelection(){
					File f = getSelectedFile();
					if(f.exists() && getDialogType() == SAVE_DIALOG){
						int result = JOptionPane.showConfirmDialog(this, "Sobrescrever arquivo existente?", "Arquivo existe", JOptionPane.YES_NO_CANCEL_OPTION);
						switch(result){
							case JOptionPane.YES_OPTION:
								super.approveSelection();
								return;
							case JOptionPane.NO_OPTION:
								return;
							case JOptionPane.CLOSED_OPTION:
								return;
							case JOptionPane.CANCEL_OPTION:
								cancelSelection();
								return;
						}
					}
					super.approveSelection();
				}
			};
			
			FileFilter aFilter = new FileNameExtensionFilter("Comma-separated values file (*.csv)","csv");
			fileChooser.setFileFilter(aFilter);
			fileChooser.setAcceptAllFileFilterUsed(false);
			fileChooser.setDialogTitle("Salvar arquivo");
			fileChooser.setMultiSelectionEnabled(false);
			fileChooser.setSelectedFile(new File("layerseq.csv"));
			int returnVal = fileChooser.showSaveDialog(null);
			
			if(returnVal == JFileChooser.APPROVE_OPTION)
			{
				String filename = fileChooser.getSelectedFile().getAbsolutePath();
				if(!((filename.toLowerCase()).endsWith(".csv")))
				{
					filename += ".csv";
				}
				
				FileWriter fw = new FileWriter(filename);
				BufferedWriter bw = new BufferedWriter(fw);
				IMxDocument doc = (IMxDocument)app.getDocument();
				IEnumLayer layers = doc.getFocusMap().getLayers(null, false);
				ILayer layer;

				while((layer = layers.next()) != null)
				{
					IGroupLayer groupLayer = (IGroupLayer)layer;
					ICompositeLayer compositeLayer = (ICompositeLayer)layer;
					if(groupLayer != null && compositeLayer != null)
					{
//						JOptionPane.showMessageDialog(null, "7");
						groupLayers.add(groupLayer);
						compositeLayers.add(compositeLayer);
	//					JOptionPane.showMessageDialog(null, groupLayer.getName());
					}
				}
				
				for(int i = 0; i < compositeLayers.size(); i++)
				{
					ICompositeLayer cl = compositeLayers.get(i);
					String clName = groupLayers.get(i).getName();
					for(int j = 0; j < cl.getCount(); j++)
					{
						String line = clName + ";" + cl.getLayer(j).getName();
						bw.write(line);
						if(!(j == cl.getCount() - 1 && i == compositeLayers.size() - 1))
						{
							bw.newLine();
						}
					}
				}
				bw.close();
				groupLayers.clear();
				compositeLayers.clear();
			}
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, e.toString());
		}
	}

}
