/* Copyright 2012 ESRI
* 
* All rights reserved under the copyright laws of the United States
* and applicable international laws, treaties, and conventions.
* 
* You may freely redistribute and use this sample code, with or
* without modification, provided you include the original copyright
* notice and use restrictions.
* 
* See the use restrictions.
* 
*/
package br.mil.eb.dsg.arctools;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;
import javax.swing.JOptionPane;
import com.esri.arcgis.addins.desktop.Tool;
import com.esri.arcgis.arcmapui.IMxDocument;
import com.esri.arcgis.carto.IElement;
import com.esri.arcgis.carto.IFillShapeElement;
import com.esri.arcgis.carto.IGraphicsContainer;
import com.esri.arcgis.carto.PolygonElement;
import com.esri.arcgis.carto.esriViewDrawPhase;
import com.esri.arcgis.display.IColor;
import com.esri.arcgis.display.IDisplayTransformation;
import com.esri.arcgis.display.IRgbColor;
import com.esri.arcgis.display.IScreenDisplay;
import com.esri.arcgis.display.ISimpleFillSymbol;
import com.esri.arcgis.display.ISimpleLineSymbol;
import com.esri.arcgis.display.ISymbol;
import com.esri.arcgis.display.RgbColor;
import com.esri.arcgis.display.SimpleFillSymbol;
import com.esri.arcgis.display.SimpleLineSymbol;
import com.esri.arcgis.framework.IApplication;
import com.esri.arcgis.geometry.GeometryEnvironment;
import com.esri.arcgis.geometry.IConstructPoint;
import com.esri.arcgis.geometry.IGeometry;
import com.esri.arcgis.geometry.IGeometryBridge2;
import com.esri.arcgis.geometry.IPoint;
import com.esri.arcgis.geometry.IPointCollection4;
import com.esri.arcgis.geometry.Point;
import com.esri.arcgis.geometry.Polygon;
import com.esri.arcgis.interop.AutomationException;

public class AcquisitionTool extends Tool {
  IScreenDisplay screenDisplay;
  IGeometry gabPolygon, newPolygon;
  IApplication app;
  IMxDocument mxDoc;
  GabExtension gabExt;
  boolean drawn = false;
  
  List<Criterio> criterios;
  
//Initializes this tool with the ArcGIS application it is hosted in
 @Override
 public void init(IApplication app) {
	  try {
		  this.app = app;
		  mxDoc = (IMxDocument) app.getDocument();
		  gabExt = GabExtension.getExtension();
	  } catch (Exception e) {
		  e.printStackTrace();
	  }
 }

 @Override
 public boolean isEnabled(){
	 if(gabExt.getCSVPath().isEmpty())
		 return false;
	 else
		 return true;
 }
 
 // Called when the tool is activated by clicking it
  @Override
  public void activate() {
	  try{
		  screenDisplay = mxDoc.getActiveView().getScreenDisplay();
	  }
	  catch(Exception e){
		  e.printStackTrace();
	  }
  }

  //Called when a mouse button is pressed while the tool is active
  @Override
  public void mousePressed(MouseEvent me) {
	 try
     {
		if(drawn == false)
		{
			drawn = true;
			IGraphicsContainer graphicsContainer = (IGraphicsContainer) mxDoc.getFocusMap();
			graphicsContainer.deleteAllElements();
			gabPolygon = new Polygon();
			gabPolygon = getPolygon(me);
			
			IFillShapeElement polygonElement = new PolygonElement();			
			polygonElement.setSymbol((ISimpleFillSymbol) getSymbol());
			IElement element = (IElement) polygonElement;
			element.setGeometry(gabPolygon);
			
			graphicsContainer.deleteAllElements();
			graphicsContainer.addElement(element, 0);
			mxDoc.getActiveView().partialRefresh(esriViewDrawPhase.esriViewGraphics, null, element.getGeometry().getEnvelope());
		}
		else
		{
			drawn = false;
			IGraphicsContainer graphicsContainer = (IGraphicsContainer) mxDoc.getFocusMap();
			graphicsContainer.reset();
			IElement el = graphicsContainer.next();
			mxDoc.getActiveView().partialRefresh(esriViewDrawPhase.esriViewGraphics, el, null);
			graphicsContainer.deleteAllElements();
		}
     }
     catch(Exception ex)
     {
       ex.printStackTrace();
     }
  }
  
  // Called when the mouse is moved while the tool is active
  @Override
  public void mouseMoved(MouseEvent me) {
  	  try
  	  {
  		  if(drawn)
  		  {
		  	  IGraphicsContainer graphicsContainer = (IGraphicsContainer) mxDoc.getFocusMap();
	  		  graphicsContainer.deleteAllElements();
	  		  //if(me.getButton() != 0)
	  		  {
				  gabPolygon = new Polygon();
	  			  gabPolygon = getPolygon(me);
	
	  			  IFillShapeElement polygonElement = new PolygonElement();
	  			  polygonElement.setSymbol((ISimpleFillSymbol) getSymbol());
	  			  IElement element = (IElement) polygonElement;
	  			  element.setGeometry(gabPolygon);
	  			  
	  			  graphicsContainer.addElement(element, 0);
	  			  mxDoc.getActiveView().partialRefresh(esriViewDrawPhase.esriViewGraphics, null, null);
	 		  }
  		  }
  	  }
  	  catch(Exception e){
  		  e.printStackTrace();
  	  }
  }  
  
  // --- AUXILIARY METHODS --- //
  
  // Define drawing style.
  public ISymbol getSymbol(){
		try{
			IRgbColor rgbColor = new RgbColor();
			rgbColor.setRed(255);
	
			IRgbColor contourColor = new RgbColor();
			contourColor.setRed(255);
			
			IColor color = rgbColor;
			IColor color2 = contourColor;
			color.setTransparency((byte) 0);
			
			ISimpleFillSymbol simpleFillSymbol = new SimpleFillSymbol();
			simpleFillSymbol.setColor(color);
			ISimpleLineSymbol contourSymbol = new SimpleLineSymbol();
			contourSymbol.setColor(color2);
			contourSymbol.setWidth(3);
			simpleFillSymbol.setOutline(contourSymbol);
			ISymbol symbol = (ISymbol)simpleFillSymbol;
			return symbol;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
  }
  
  // Create a square polygon.
  public IGeometry getPolygon(MouseEvent me) throws Exception{
  
	  	IGeometry thePolygon = new Polygon();
	  	IPoint center = new Point();
	  	IDisplayTransformation dTransf = screenDisplay.getDisplayTransformation();
	  	center = dTransf.toMapPoint(me.getX(), me.getY());

	  	// Pegar o Criterio correto de acordo com o tamanho selecionado
	  	criterios = gabExt.getCriterios();
	  	Criterio crit = null; // Criterio selecionado
	  	for(Criterio c: criterios){
	  		if(c.getIndex() == gabExt.dimSelected())
	  		{
	  			crit = c;
	  			break;
	  		}
	  	}
	  	// Crit já é o Criterio correto
	  	
	  	double raio;
	  	double lado;
	  	double escala = gabExt.escalaSelected();
	  	
	  	// Baseado no tamanho e no tipo, criar a geometria
	  	
	  	// CALCULAR RAIO/LARGURA, POIS PODE TER SIDO PASSADA UMA MEDIDA DE ÁREA!!
	  	if(crit.getTipo() == 2) // SE O CRITÉRIO É DE ÁREA
	  	{
	  		if(gabExt.formaSelected() == gabExt.cookieCirculo()) // SE A FORMA SELECIONADA É DE CÍRCULO
	  		{
	  			raio = Math.sqrt(crit.getTamanho()/(Math.PI)); // CALCULA RAIO
	  			thePolygon = getCirculo(center, raio, escala); // CRIA CÍRCULO
	  		}
	  		else
	  		{
	  			lado = Math.sqrt(crit.getTamanho()); // CALCULA LADO DO QUADRADO
	  			thePolygon = getQuadrado(center, lado, escala); // CRIA QUADRADO
	  		}
	  	}
	  	else // SE O CRITÉRIO É DE DISTÂNCIA
	  	{
  			raio = crit.getTamanho() / 2; // RAIO JÁ É A MEDIDA PASSADA DIVIDIDA POR 2
  			thePolygon = getCirculo(center, raio, escala); // CRIA CÍRCULO
	  	}

	  	return thePolygon;
  }

  
  public IGeometry getCirculo(IPoint center, double raio, double escala){
		  IGeometry thePolygon = null;
		  try{
			  thePolygon = new Polygon();
			  IGeometryBridge2 geometryBridge = new GeometryEnvironment();
			  int nPoints = 30; // QUANTIDADE DE PONTOS NO CÍRCULO
		
			  IConstructPoint[] points = new Point[nPoints+1]; // VETOR DE PONTOS QUE DEFINE O CÍRCULO
		
			  for(int i = 0; i <= nPoints; i++)
			  {
				  double scale = escala;

				  points[i] = new Point();				  

				  double raioTer = (raio/1000)*scale;
				  double angle	 = 2*Math.PI*i/nPoints;
				  
				  points[i].constructAngleDistance(center, angle, raioTer);
			  }
			  
			  geometryBridge.addPoints((IPointCollection4)thePolygon, (IPoint[])points); // ADICIONA VETOR DE PONTOS AO POLÍGONO DE RETORNO
		  } catch (Exception e){
			  JOptionPane.showMessageDialog(null, "É necessário que haja dados em tela para a ferramenta Gabarito funcionar!");
		  }
	  return thePolygon; // RETORNA O CÍRCULO
  }
  
  public IGeometry getQuadrado(IPoint center, double lado, double escala) throws AutomationException, IOException{
	  IGeometry thePolygon = null;
	  try{
		  thePolygon = new Polygon();
		  IGeometryBridge2 geometryBridge = new GeometryEnvironment();
	  
		  IConstructPoint[] points = new Point[5]; // VETOR DE PONTOS QUE DEFINE O QUADRADO

		  for(int i = 0; i < 5; i++)
		  {
			  double scale = escala;

			  double diagTer = ((lado/1000)/Math.sqrt(2))*scale;
			  double angle   = (2*i+1)*Math.PI/4;
			  
			  points[i] = new Point();
			  
			  points[i].constructAngleDistance(center, angle, diagTer);
		  }
	
		  geometryBridge.addPoints((IPointCollection4)thePolygon, (IPoint[])points); // ADICIONA VETOR DE PONTOS AO POLÍGONO DE RETORNO
	  } catch (Exception e){
		  JOptionPane.showMessageDialog(null, "É necessário que haja dados em tela para a ferramenta Gabarito funcionar!");
	  }
	  return thePolygon; // RETORNA O QUADRADO
  }
}