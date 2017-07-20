package br.mil.eb.dsg.arctools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.esri.arcgis.arcmapui.IMxDocument;
import com.esri.arcgis.carto.FeatureLayer;
import com.esri.arcgis.carto.GroupLayer;
import com.esri.arcgis.carto.IFeatureLayer;
import com.esri.arcgis.carto.IGroupLayer;
import com.esri.arcgis.framework.IApplication;
import com.esri.arcgis.geodatabase.IDataset;
import com.esri.arcgis.geodatabase.IEnumDataset;
import com.esri.arcgis.geodatabase.IFeatureClass;
import com.esri.arcgis.geodatabase.IFeatureWorkspace;
import com.esri.arcgis.geodatabase.IWorkspace;
import com.esri.arcgis.geodatabase.esriDatasetType;
import com.esri.arcgis.interop.AutomationException;


public class LoadLayers{

	static IApplication app;
	LLExtension llext;
	IWorkspace ws;
	IMxDocument mxDoc;
	JFileChooser fileChooser;
	int layersComFeicoes = 0, layersSemFeicoes = 0;
	
	/**
	 * Called when the button is clicked.
	 * 
	 * @exception java.io.IOException if there are interop problems.
	 * @exception com.esri.arcgis.interop.AutomationException if the component throws an ArcObjects exception.
	 */
	
	public LoadLayers(){
		try {
			llext = LLExtension.getExtension();
			LoadLayers.app = llext.appl;
			mxDoc = (IMxDocument) app.getDocument();
			ws = llext.mdbWorkspace;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void load() throws IOException, AutomationException {
		layersComFeicoes = 0;
		layersSemFeicoes = 0;
		
		Map<String, IFeatureClass> featureClasses = new LinkedHashMap<String, IFeatureClass>();
		Map<String, IFeatureClass> featureClassesComFeicoes = new LinkedHashMap<String, IFeatureClass>();
		
		IEnumDataset datasets = llext.mdbWorkspace.getDatasets(esriDatasetType.esriDTAny);
		IDataset dataset = null;
		datasets.reset();
		
		while((dataset = datasets.next()) != null)
		{
			if(dataset.getType() == esriDatasetType.esriDTFeatureDataset)
			{
				IEnumDataset subdatasets = dataset.getSubsets();
				IDataset subdataset = null;
				while((subdataset = subdatasets.next()) != null)
				{
					IFeatureWorkspace fws = (IFeatureWorkspace) llext.mdbWorkspace;
					IFeatureClass fc = fws.openFeatureClass(subdataset.getName());
					
					if(fc.featureCount(null) > 0)
					{
						layersComFeicoes++;
						featureClassesComFeicoes.put(subdataset.getName(), fc);
					}
					else
					{
						layersSemFeicoes++;
					}
					featureClasses.put(subdataset.getName(), fc);
				}
			}
		}
		// Tenho a lista de FeatureClass para carregar.
		// Preciso montar a árvore das camadas em memória.
		int outOfList = createGroups(featureClasses, featureClassesComFeicoes);
		JOptionPane.showMessageDialog(null, "Layers com feições (carregados): " + layersComFeicoes + "\n"
				+ "- Layers do MDB ausentes no CSV (grupo NAO_DEFINIDO): " + outOfList + "\n"
				+ "- Layers sem feições (ignorados): " + layersSemFeicoes);
	}
	
	private int createGroups(Map<String, IFeatureClass> fc, Map<String, IFeatureClass> fc2){
		int outOfList = 0;
		try{
			// Lista dos grupos para posterior adição ao mapa
			Map<String, IGroupLayer> groupsToAdd = new LinkedHashMap<String, IGroupLayer>(); // Nomes de grupo e GroupLayers correspondentes
			
			String filename = llext.csvOrdem;
			BufferedReader br = new BufferedReader(new FileReader(filename));
			
			String line;
			
			// Checar cada layer do CSV se existe no MDB.
			// Se existir, verificar a quantidade de feições.
			List<String> layersAdded = new ArrayList<String>();
			
			while((line = br.readLine()) != null)
			{
				String[] lineSplit = line.split(";");
				if(!(groupsToAdd.containsKey(lineSplit[0]))) // Se for a primeira inclusão no grupo...
				{
					IGroupLayer gpLayer = new GroupLayer(); // Cria o grupo
					gpLayer.setName(lineSplit[0]); // Define o nome do grupo conforme lido do CSV
					groupsToAdd.put(lineSplit[0], gpLayer); // Adiciona o grupo ao Map de grupos
				}
				if(fc.containsKey(lineSplit[1])) // Se na lista de FeatureClass tiver a lida do CSV...
				{
					IFeatureClass fclass = fc.get(lineSplit[1]); // Pegar a FeatureClass
					if(fclass.featureCount(null) > 0) // Se a FeatureClass tem feições, adicionar ao grupo correspondente
					{
						IFeatureLayer layer = new FeatureLayer();
						layer.setName(lineSplit[1]);
						layer.setFeatureClassByRef(fclass);
						groupsToAdd.get(lineSplit[0]).add(layer); // Adiciona o layer recém criado ao grupo correto
						layersAdded.add(lineSplit[1]);
					}
				}
			}
			br.close();
			
			for(String fcn: fc2.keySet()) // Para cada FeatureClass do vetor das que devem ser adicionadas...
			{
				if(!(layersAdded.contains(fcn))) // ... testar se na lista de layers já adicionados têm a FeatureClass em questão. Se não...
				{
					outOfList++;
					IFeatureClass fclass = fc2.get(fcn); // Pegar a FeatureClass
					IFeatureLayer layer = new FeatureLayer();
					layer.setName(fcn);
					layer.setFeatureClassByRef(fclass);
					String genericGroup = "NAO_DEFINIDO"; // Definir nome do grupo genérico
					if(!(groupsToAdd.containsKey(genericGroup))) // Se for a primeira inclusão no grupo genérico...
					{
						IGroupLayer gpLayer = new GroupLayer(); // Cria o grupo genérico
						gpLayer.setName(genericGroup); // Define o nome do grupo genérico
						groupsToAdd.put(genericGroup, gpLayer); // Adiciona o grupo genérico ao Map de grupos
					}
					groupsToAdd.get(genericGroup).add(layer); // Adiciona o layer recém criado ao grupo genérico
				}
			}
			
			// Adicionar cada grupo criado (e seus layers) à árvore do mapa
			IGroupLayer mdbGroup = new GroupLayer();
			mdbGroup.setName(llext.currentMDB);
			
			for(IGroupLayer gl: groupsToAdd.values())
			{
				gl.setExpanded(false);
				mdbGroup.add(gl);
			}
			mxDoc.addLayer(mdbGroup);
			
			mxDoc.getActiveView().refresh();
			llext.isMDBSelected = false;
			
		} catch(Exception e){
			e.printStackTrace();
		}
		return outOfList;
	}
}