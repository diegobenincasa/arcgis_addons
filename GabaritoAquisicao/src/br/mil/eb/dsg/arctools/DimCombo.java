package br.mil.eb.dsg.arctools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import com.esri.arcgis.addins.desktop.ComboBox;
import com.esri.arcgis.framework.IApplication;
import com.esri.arcgis.interop.AutomationException;
import com.esri.arcgis.system.UID;

public class DimCombo extends ComboBox {

	private int curSelect;
	private static DimCombo s_extension;
	static IApplication app;
	private List<Criterio> criterios = new ArrayList<Criterio>();
	GabExtension gabExt;

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
		
	    try {
			gabExt = GabExtension.getExtension();
			DimCombo.app = gabExt.appl;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Called by system when the edit box is typed into (if editable)
	 * 
	 * @param editString the String typed into the edit box
	 * @exception java.io.IOException if there are interop problems.
	 * @exception com.esri.arcgis.interop.AutomationException if the component throws an ArcObjects exception.
	 */
	
	public void loadGabaritoFromCSV(String csvPath){
		try{
			BufferedReader br = new BufferedReader(new FileReader(csvPath)); // ABRE ARQUIVO
			String line = br.readLine(); // LÊ A PRIMEIRA LINHA E IGNORA (CABEÇALHO)
			line = br.readLine(); // LÊ A SEGUNDA LINHA
			String[] fields;
	    	
			while(line != null){
	        	fields = line.split(";"); // QUEBRA A LINHA EM VÁRIOS CAMPOS
	        	if(fields.length != 3)
	        	{
	        		JOptionPane.showMessageDialog(null, "Arquivo CSV inválido.Verifique:\n> Se o arquivo selecionado é o correto\n> Se o arquivo selecionado segue a formatação adequada.");
	        		br.close();
	        		gabExt.setCSVPath("");
	        		break;
	        	}
	        	Criterio crit = new Criterio();
	        	crit.setNome(fields[0]); // CAMPO 1: NOME
	        	crit.setTipo(Integer.parseInt(fields[1])); // CAMPO 2: TIPO (1 - ÁREA; 2 - DISTÂNCIA)
	        	crit.setTamanho(Double.parseDouble(fields[2])); // CAMPO 3: TAMANHO
	        	criterios.add(crit); // ADICIONA O CRITÉRIO À LISTA DE CRITÉRIOS
	        	line = br.readLine(); // LÊ A PRÓXIMA LINHA
	    	}
	    	
	    	for(Criterio c: criterios){
	    		c.setIndex(add(c.getNome())); // SETAR O INDEX PARA O MESMO DA COMBO, E ADICIONAR NA COMBO
	    	}
	    	select(criterios.get(0).getIndex()); // SELECIONAR O PRIMEIRO CRITÉRIO NA COMBO
	    	curSelect = getSelected(); // DEFINIR O INDEX DO ITEM SELECIONADO
	    	br.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e){
			JOptionPane.showMessageDialog(null, "O CSV selecionado é inválido. Repita a operação.");
			e.printStackTrace();
		}
	}
	
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
	public void onSelChange(int cookie){
		try {
			curSelect = cookie; // SETAR O ITEM CORRENTE PARA O INDEX DA COMBO ATUAL
			Criterio crit = new Criterio();
			for(Criterio c: criterios){ // ENCONTRAR O CRITÉRIO, SELECIONADO NA COMBO, NA LISTA DE CRITÉRIOS
				if(c.getIndex() == curSelect){
					crit = c;
					break;
				}
			}
			
			gabExt.formaComboClear(); // RESETA A COMBO DE FORMAS DE GABARITO
			int const_circ = gabExt.addFormaComboItem("Circulo");
			gabExt.setFormaComboConstants(1, const_circ); // DEFINE A VARIÁVEL CONST_CIRCULO PARA O INDEX DA OPÇÃO CÍRCULO APÓS ADICIONÁ-LA NA COMBO DE FORMAS
			if(crit.getTipo() == 2){ // SE O CRITÉRIO SELECIONADO FOR DE ÁREA, REPETIR O PROCEDIMENTO PARA CONST_QUADRADO
				gabExt.setFormaComboConstants(2, gabExt.addFormaComboItem("Quadrado"));
			}
			gabExt.setFormaComboCirculo();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, e.toString());
			e.printStackTrace();
		}
	}
	
	
	
	// --- AUXILIARY METHODS --- //
	
	public List<Criterio> getCriterios(){
		return criterios;
	}
	
	public static DimCombo getExtension() throws UnknownHostException, IOException{
		if(s_extension == null){
			UID pUID = new UID();
			pUID.setValue("br.mil.eb.dsg.arctools.dimcombo");
			app.findExtensionByCLSID(pUID);
		}
		return s_extension;
	}
}
