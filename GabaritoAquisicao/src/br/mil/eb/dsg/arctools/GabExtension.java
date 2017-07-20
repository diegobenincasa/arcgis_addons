package br.mil.eb.dsg.arctools;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import javax.swing.JOptionPane;

//import javax.swing.JOptionPane;

import com.esri.arcgis.addins.desktop.Extension;
import com.esri.arcgis.framework.IApplication;
import com.esri.arcgis.system.UID;

public class GabExtension extends Extension {

	private static GabExtension gabExtension;
	private static IApplication app;
	public IApplication appl;
	public FormaCombo formaCombo;
	public DimCombo dimCombo;
	public EscalaCombo escalaCombo;
	public List<Criterio> criterios;
	public String csvPath = "";

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
	public void init(IApplication app) {
		GabExtension.app = app;
		this.appl = app;
	}
	
	public GabExtension(){
		gabExtension = this;
	}
	
	protected static GabExtension getExtension() throws UnknownHostException, IOException{
		if(gabExtension == null){
			UID extID = new UID();
			extID.setValue(GabExtension.class);
			app.findExtensionByCLSID(extID);
		}
		
		return gabExtension;
	}
	
	public void formaComboClear(){
		try{
			formaCombo = FormaCombo.getExtension();
			formaCombo.clear();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public int addFormaComboItem(String s){
		int cookie = -1;
		try {
			formaCombo = FormaCombo.getExtension();
			cookie = formaCombo.add(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cookie;
	}
	
	public void setFormaComboConstants(int variant, int value){
		try{
			formaCombo = FormaCombo.getExtension();
			if(variant == 1)
				formaCombo.CONST_CIRCULO = value;
			else if(variant == 2)
				formaCombo.CONST_QUADRADO = value;
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void setFormaComboCirculo(){
		try{
			formaCombo = FormaCombo.getExtension();
			formaCombo.select(formaCombo.CONST_CIRCULO);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public List<Criterio> getCriterios(){
		List<Criterio> crit = null;
		
		try{
			dimCombo = DimCombo.getExtension();
			crit = dimCombo.getCriterios();
		} catch(Exception e){
			e.printStackTrace();
		}
		
		return crit;
	}
	
	public int formaSelected(){
		int forma = 0;
		try{
			formaCombo = FormaCombo.getExtension();
			forma = formaCombo.getSelected();
		} catch(Exception e){
			e.printStackTrace();
		}
		return forma;
	}
	
	public int dimSelected(){
		int dim = 0;
		try{
			dimCombo = DimCombo.getExtension();
			dim = dimCombo.getSelected();
		} catch(Exception e){
			e.printStackTrace();
		}
		return dim;
	}
	
	public double escalaSelected(){
		double escala = -1;
		try{
			escalaCombo = EscalaCombo.getExtension();
			escala = escalaCombo.getEscala();
		}catch (Exception e){
			e.printStackTrace();
		}
		return escala;
	}
	
	public int cookieCirculo(){
		int cookie = 0;
		try{
			formaCombo = FormaCombo.getExtension();
			cookie = formaCombo.CONST_CIRCULO;
		} catch(Exception e){
			e.printStackTrace();
		}
		return cookie;
	}

	public int cookieQuadrado(){
		int cookie = 0;
		try{
			formaCombo = FormaCombo.getExtension();
			cookie = formaCombo.CONST_QUADRADO;
		} catch(Exception e){
			e.printStackTrace();
		}
		return cookie;
	}
	
	public void setCSVPath(String path){
		csvPath = path;
	}
	
	public String getCSVPath(){
		return csvPath;
	}
	
	public void loadCSV() {
		try{
			dimCombo = DimCombo.getExtension();
		
			JOptionPane.showMessageDialog(null, "Arquivo carregado!");
			dimCombo.loadGabaritoFromCSV(csvPath);
		} catch(UnknownHostException e){
			JOptionPane.showMessageDialog(null, "1\n" + e.toString());
		} catch(IOException e){
			JOptionPane.showMessageDialog(null, "2\n" + e.toString());
		}
	}
}
