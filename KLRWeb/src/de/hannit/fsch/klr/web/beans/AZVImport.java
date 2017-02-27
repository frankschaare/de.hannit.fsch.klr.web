package de.hannit.fsch.klr.web.beans;

import java.io.Serializable;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.ProjectStage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.ListDataModel;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.primefaces.event.SelectEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.hannit.fsch.klr.dataservice.mssql.MSSQLDataService;
import de.hannit.fsch.klr.model.Datumsformate;
import de.hannit.fsch.klr.model.azv.AZVDaten;
import de.hannit.fsch.klr.model.azv.AZVDatensatz;
import de.hannit.fsch.klr.model.loga.LoGaDatensatz;
import de.hannit.fsch.soa.osecm.IAZVClient;
import de.hannit.fsch.soa.osecm.azv.AZVClient;
import de.hannit.fsch.util.DateUtility;

@ManagedBean
@SessionScoped
public class AZVImport implements Serializable 
{
private static final long serialVersionUID = -1303725843784618947L;
@ManagedProperty (value = "#{dataService}")
private MSSQLDataService dataService;
private final static Logger log = Logger.getLogger(AZVImport.class.getSimpleName());	
private String logPrefix = null;	
private FacesContext fc = null;

private IAZVClient webService = null;
private AZVDaten azvDaten = null;
private ArrayList<AZVDatensatz> azvMeldungen = null;
private ArrayList<LocalDate> azvBerichtsMonate = null;
private LocalDate maxAZVDate = null;	
private Date selectedDate = null;
private int anzahlDaten = 0;
private ListDataModel<AZVDatensatz> daten = null;
private AZVDatensatz selectedRow = null;


private boolean btnAZVResetDisabled = true;
private boolean btnAZVSpeichernDisbled = true;

private Exception e = null;
private Document doc = null;
private XPathFactory xpathfactory = XPathFactory.newInstance();
private XPath xpath = xpathfactory.newXPath();




	public AZVImport() 
	{
	fc = FacesContext.getCurrentInstance();
	dataService = dataService != null ? dataService : fc.getApplication().evaluateExpressionGet(fc, "#{dataService}", MSSQLDataService.class);
	//webService = new AZVClient();
	azvDaten = new AZVDaten();
	setMaxBerichtsmonat();
	//TODO
	//azvDaten.setBerichtsMonatSQL(dateTime.getMonth(), dateTime.getYear());
	//log.info("EventBroker versendet AZV-Anfrage für den Berichtsmonat: " + azvDaten.getBerichtsMonatAsString(), plugin);

	}
	
	/*
	 * Ermittelt den letzten Berichtmonat, für den AZV-Daten in der DB gespeichert sind.
	 * Der Kalender wird dann auf den darauf folgenden Monat gestellt
	 */
	private void setMaxBerichtsmonat() 
	{
	logPrefix = this.getClass().getName() + "setMaxBerichtsmonat(): ";
	this.azvBerichtsMonate = dataService.getAzvBerichtsMonate();
					
		for (LocalDate localDate : azvBerichtsMonate) 
		{
			if (maxAZVDate == null)
			{
			maxAZVDate = localDate;	
			}
			else
			{
			maxAZVDate = localDate.isAfter(maxAZVDate) ? localDate : maxAZVDate;	
			}	
		}
	if (fc.isProjectStage(ProjectStage.Development)) {log.log(Level.INFO, logPrefix + "in der Datenbank wurden AZV Daten bis " + Datumsformate.DF_MONATJAHR.format(maxAZVDate) + " gefunden");}	
	
	setSelectedDate(DateUtility.asDate(maxAZVDate.plusMonths(1)));	
	}

	public void reset() 
	{
		
	}
	
	public void save() 
	{
		
	}
	
    public void onRowSelect(SelectEvent event) 
    {
    LoGaDatensatz row =  (LoGaDatensatz) event.getObject();
    //RequestContext.getCurrentInstance().execute("updateButtons();");
    }
	
	public ListDataModel<AZVDatensatz> getDaten()
    {
    return daten;
    }	
	
	public void execute() 
	{
   	logPrefix = this.getClass().getName() + ".execute(): ";
	
	// AZV Request vorbereiten
	Date start = new Date();	
	log.log(Level.INFO, logPrefix + "Starte Anfrage an den OS/ECM Webservice für den Berichtsmonat " + azvDaten.getRequestedMonth() + " " + azvDaten.getRequestedYear());	
	
	e = webService.setAZVRequest(azvDaten.getRequestedMonth(), azvDaten.getRequestedYear());
		if (e != null)
		{
		// log.error(e.getMessage(), plugin, e);	
		}
		else
		{
		doc = webService.getResultList();
		Date end = new Date();
		long anfrageDauer = end.getTime() - start.getTime();
		log.log(Level.INFO, logPrefix + "Anfrage an den OS/ECM Webservice wurde in " + String.valueOf(anfrageDauer) + " Millisekunden abgeschlossen.");	
		
		parseDocument();

		azvDaten.setAzvMeldungen(azvMeldungen);
		azvDaten.setErrors(false);
		azvDaten.setChecked(false);
		azvDaten.setRequestComplete(true);
		}
	}	
	
	/*
	 * Response parsen
	 * Die Rückgabe des Webservices ist richtig kranker Scheiss.
	 * 
	 * Offensichtlich hat niemand ernsthaft damit gerechnet, das jemand diesen Schwachsinn einmal parsen muss
	 * und alles gegeben, um unparsable Markup zu generieren.
	 * 
	 * Beispielsweise gibt es jeweils mindestens Knotentypen mit den Tagnamen ObjectType, Object und Childobjects.
	 * 
	 * Es sind daher einige Verrenkungen notwendig, um die benötigte Information aus diesem Wust herauszufiltern.
	 * 
	 * Benötigt wird 
	 * - der Benutzer: 	//Object/Fields/Field(name=Benutzer)
	 * - der Monat: 	//Object/Fields/Field(name=Monat)
	 * - das Jahr: 		//Object/Fields/Field(name=Jahr)
	 * - KST/KTR:		//Object/TableFields/TableField(name=AZV-Verteilung)/Row/Value[0]
	 * - Prozentanteil:	//Object/TableFields/TableField(name=AZV-Verteilung)/Row/Value[2]
	 * 
	 */
	private void parseDocument()
	{
    AZVDatensatz azvMeldung = null;
    String strNachname = null;
    String strTeam = null;
    String strBenutzername = null;
    String strEMail = null;
    String strKSTKTR = null;
    String strProzentanteil = null;
    XPathExpression xpMitarbeiterdaten = null;
    azvMeldungen = new ArrayList<AZVDatensatz>();
    
    
    	if (doc != null)
		{
    	//Schritt 1: Zuerst wird eine NodeList aller Objekte erstellt
    	NodeList allObjects = doc.getElementsByTagName("Object");
	    	for (int a = 0; a < allObjects.getLength(); a++)
			{
	    	Element mitarbeiterNode = (Element) allObjects.item(a);
	    	
	    		/*
	    		 *  Schritt 2: Dann werden die Object-Nodes heraussortiert, welche einem Mitarbeiter zugeordnet sind.
	    		 *  Aufgrund der kruden Bezeichnung der Tags, welche sich dreimal wiederholen, mache ich das etwas umständlich:
	    		 */
	    		if (mitarbeiterNode.getParentNode().getParentNode().getParentNode().getNodeName().equalsIgnoreCase("Archive"))
				{
	    		/*
	    		 * Es wurde ein Object-Node gefunden, welcher ein Mitarbeiter ist.
	    		 * Nun wird die ID des Knotens ausgelesen und die Kopfdaten des Mitarbeiters ausgelesen:	
	    		 */
	    		String strID = mitarbeiterNode.getAttribute("id");
					try
					{
					xpMitarbeiterdaten = xpath.compile("/DMSContent/Archive/ObjectType/ObjectList/Object[@id='" + strID + "']/Fields/Field");
					NodeList mitarbeiterDaten = (NodeList) xpMitarbeiterdaten.evaluate(doc, XPathConstants.NODESET);
	
				    	for (int m = 0; m < mitarbeiterDaten.getLength(); m++)
						{
				    	Element field = (Element) mitarbeiterDaten.item(m);	
				    		switch (field.getAttribute("name"))
							{
							case "Name":
							strNachname = field.getTextContent();	
							break;
							
							case "Team":
							strTeam = field.getTextContent();	
							break;
							
							case "Benutzername":
							strBenutzername = field.getTextContent();	
							break;	
							
							case "E-Mail":
							strEMail = field.getTextContent();	
							break;								
	
							default:
							break;
							}
				    		
						}
					}
					catch (XPathExpressionException e)
					{
					e.printStackTrace();
					}	    		
	    		
	    		
	    		NodeList tableFields = mitarbeiterNode.getElementsByTagName("TableField");	
	    		
	    			/*
	    			 * Die NodeList tableFields sollte zwei Elemente enthalten,
	    			 * - Attribut name = 'AZV-Verteilung': enthält die benötigten Row-Elemente für die AZV's
	    			 * - Attribut name = 'WF-Protokoll': enthält Workflow-Daten, die hier nicht benötigt werden
	    			 * 
	    			 */
			    	for (int t = 0; t < tableFields.getLength(); t++)
					{
			    	Element tableField = (Element) tableFields.item(t);	
			    		switch (tableField.getAttribute("name"))
						{
						case "AZV-Verteilung":
						NodeList rows = tableField.getElementsByTagName("Row");
							/*
							 * Endlich bei den gesuchten Daten angekommen, werden hier die AZV-Daten ausgelesen. Eine Row hat folgendes Format:
							 * 		<Row id="n">
							 * 			<Value>Kostenstelle</Value>
							 * 			<Value>Kostenträger</Value>
							 * 			<Value>Prozentanteil</Value>
							 * 			<Value>Bemerkung</Value>
							 * 			<Value>Unterschrift</Value>
							 * 		</Row>
							 * Für jedes Row-Element wird ein AZVDatensatz generiert.
							 * 
							 */
					    	for (int r = 0; r < rows.getLength(); r++)
							{
					    	Element azvRow = (Element) rows.item(r);
					    	NodeList azvAnteile = azvRow.getChildNodes();
					    	
				            azvMeldung = new AZVDatensatz();
				            azvMeldung.setNachname(strNachname);
				            azvMeldung.setTeam(strTeam);
				            azvMeldung.setUserName(strBenutzername);
				            azvMeldung.setEMail(strEMail);
				            
				            int iPNR = dataService.getPersonalnummer(strNachname, strBenutzername);
				            
				            	if (iPNR == 0)
								{
								iPNR = dataService.getPersonalnummerbyUserName(strBenutzername);
								}
				            azvMeldung.setPersonalNummer(iPNR);
				            
				            azvMeldung.setBerichtsMonatAsString(azvDaten.getRequestedMonth());
				            azvMeldung.setBerichtsJahrAsString(azvDaten.getRequestedYear());
					            try
								{
								azvMeldung.setBerichtsMonat(Datumsformate.MONATLANG_JAHR.parse(azvDaten.getRequestedMonth() + " " + azvDaten.getRequestedYear()));
								}
								catch (ParseException e)
								{
								e.printStackTrace();
								}						    	
			            	strKSTKTR = azvAnteile.item(0).getTextContent();
			            	strKSTKTR = (strKSTKTR.length() > 0) ? strKSTKTR : azvAnteile.item(1).getTextContent();
			            	strProzentanteil = azvAnteile.item(2).getTextContent();
			            	azvMeldung.setKostenArt(strKSTKTR);
			            	azvMeldung.setProzentanteil(Integer.parseInt(strProzentanteil));	
			            	
			            	azvMeldungen.add(azvMeldung);
							}
						break;					

						default:
						break;
						} 		
					}    					
				}
			}	
		}	
	}	
	
	
	public AZVDatensatz getSelectedRow() {return selectedRow;}
	public void setSelectedRow(AZVDatensatz selectedRow) {this.selectedRow = selectedRow;}

	public MSSQLDataService getDataService() {return dataService;}
	public void setDataService(MSSQLDataService dataService) {this.dataService = dataService;}
	public Date getSelectedDate() {return selectedDate;}
	public void setSelectedDate(Date selectedDate) {this.selectedDate = selectedDate;}
	public int getAnzahlDaten() {return anzahlDaten;}
	public void setAnzahlDaten(int anzahlDaten) {this.anzahlDaten = anzahlDaten;}

	public boolean isBtnAZVResetDisabled() {return btnAZVResetDisabled;}
	public void setBtnAZVResetDisabled(boolean btnAZVResetDisabled) {this.btnAZVResetDisabled = btnAZVResetDisabled;}
	public boolean isBtnAZVSpeichernDisbled() {return btnAZVSpeichernDisbled;}
	public void setBtnAZVSpeichernDisbled(boolean btnAZVSpeichernDisbled) {this.btnAZVSpeichernDisbled = btnAZVSpeichernDisbled;}
	
	
	
	
}
