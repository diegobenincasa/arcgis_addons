package br.mil.eb.dsg.arctools;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.esri.arcgis.addins.desktop.Button;
import com.esri.arcgis.arcmapui.IMxDocument;
import com.esri.arcgis.carto.IAnnotateLayerPropertiesCollection;
import com.esri.arcgis.carto.IEnumLayer;
import com.esri.arcgis.carto.IFeatureRenderer;
import com.esri.arcgis.carto.IGeoFeatureLayer;
import com.esri.arcgis.carto.ILayer;
import com.esri.arcgis.carto.ILayerFile;
import com.esri.arcgis.carto.LayerFile;
import com.esri.arcgis.framework.IApplication;
import com.esri.arcgis.interop.AutomationException;

public class LoadStyles extends Button {

	static IApplication app;
	IMxDocument mxDoc;
	public LLExtension llext;
	
	@Override
	public void init(IApplication app) throws IOException, AutomationException{
		try{
			LoadStyles.app = app;
			mxDoc = (IMxDocument) app.getDocument();
			llext = LLExtension.getExtension();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * Called when the button is clicked.
	 * 
	 * @exception java.io.IOException if there are interop problems.
	 * @exception com.esri.arcgis.interop.AutomationException if the component throws an ArcObjects exception.
	 */
	@Override
	public void onClick() throws IOException, AutomationException {
		if(mxDoc.getFocusMap().getLayerCount() == 0)
		{
			JOptionPane.showMessageDialog(null, "Mapa não contém camadas.\nAbra um mapa ou carregue camadas e tente novamente.");
			return;
		}
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setDialogTitle("Selecionar diretório com arquivos .lyr");
		
		int returnVal = fileChooser.showOpenDialog(null);
		
		if(returnVal == JFileChooser.APPROVE_OPTION)
		{
			File folder = new File(fileChooser.getSelectedFile().getAbsolutePath());
			FileFilter filter = new FileFilter(){
				@Override
				public boolean accept(File arg0) {
					return arg0.getAbsolutePath().toLowerCase().endsWith(".lyr");
				}
			};
			File[] fileList = folder.listFiles(filter);
			
			if(fileList.length == 0)
			{
				JOptionPane.showMessageDialog(null, "Não há arquivos .lyr no diretório selecionado.\nSelecione outro diretório.");
			}
			else
			{
				carregarEstilos(fileChooser.getSelectedFile().getAbsolutePath());
			}
		}
	}
	
	private void carregarEstilos(String dir){
		try{
			File folder = new File(dir);
			FileFilter filter = new FileFilter(){
				@Override
				public boolean accept(File arg0) {
					return arg0.getAbsolutePath().toLowerCase().endsWith(".lyr");
				}
			};
			File[] fileList = folder.listFiles(filter);
			
			List<String> filenameList = new ArrayList<String>();
			for(File f: fileList)
			{
				filenameList.add(f.getAbsolutePath());
			}
			
			
															//	  Layer   	  Renderer
			Map<String, IFeatureRenderer> renderers = new HashMap<String, IFeatureRenderer>();
			Map<String, IAnnotateLayerPropertiesCollection> annotations = new HashMap<String, IAnnotateLayerPropertiesCollection>();
			for(String fn: filenameList)
			{
				String layerName = fn.substring(fn.lastIndexOf("\\")+1, fn.lastIndexOf("."));
				ILayerFile lyrLayer = new LayerFile();
				lyrLayer.open(fn);
				IGeoFeatureLayer lyrSymbols = (IGeoFeatureLayer) lyrLayer.getLayer();
				IFeatureRenderer lyrRenderer = lyrSymbols.getRenderer();
				renderers.put(layerName, lyrRenderer);
				if(lyrSymbols.isDisplayAnnotation())
				{
					IAnnotateLayerPropertiesCollection lyrAnnot = lyrSymbols.getAnnotationProperties();
					annotations.put(layerName,lyrAnnot);
				}
				lyrLayer.close();
			}
			JOptionPane.showMessageDialog(null, "Total de arquivos .lyr: " + renderers.size());
			
			List<String> noLyrFound = new ArrayList<String>();
			IEnumLayer allLayers = mxDoc.getFocusMap().getLayers(null, true);
			int totalLayers = 0;
			while(allLayers.next() != null)
			{
				totalLayers++;
			}
			allLayers.reset();
			
			for(int i = 0; i < totalLayers; i++)
			{
				ILayer temp = allLayers.next();
				String tempType = temp.getClass().getSimpleName();
				
				if(tempType.equals("FeatureLayer"))
				{
					IGeoFeatureLayer layerSymbols = (IGeoFeatureLayer) temp;
				
					if(renderers.get(temp.getName()) != null)
					{
						layerSymbols.setRendererByRef(renderers.get(temp.getName()));
						if(annotations.containsKey(temp.getName()))
						{
							layerSymbols.setAnnotationProperties(annotations.get(temp.getName()));
							layerSymbols.setDisplayAnnotation(true);
						}
					}
					
					else
					{
						noLyrFound.add(temp.getName());
					}
				}
			}
			
			mxDoc.getActiveView().refresh();

			if(noLyrFound.size() > 0)
			{
				String output = "Estilos carregados para todas as camadas, exceto:";
				for(int j = 0; j < noLyrFound.size(); j++)
				{
					output += "\n - ";
					output += noLyrFound.get(j);
				}
				JOptionPane.showMessageDialog(null, output);
			}
			else
			{
				JOptionPane.showMessageDialog(null, "Estilos carregados para todas as camadas!");
			}
		} catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, e.toString());
		}
	}	
}
