package de.hannit.fsch.klr.web.beans;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.application.ProjectStage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.ListDataModel;
import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.primefaces.event.SelectEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.hannit.fsch.klr.dataservice.mssql.MSSQLDataService;
import de.hannit.fsch.klr.model.Datumsformate;
import de.hannit.fsch.klr.model.azv.AZVDaten;
import de.hannit.fsch.klr.model.azv.AZVDatensatz;
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
private String detail = null;
private FacesContext fc = null;

private IAZVClient webService = null;
private String connectionInfo = "Nicht verbunden";
private AZVDaten azvDaten = null;
private ArrayList<AZVDatensatz> azvMeldungen = null;
private ArrayList<LocalDate> azvBerichtsMonate = null;
private LocalDate maxAZVDate = null;
private LocalDate requestAZVDate = null;
private Date selectedDate = null;
private int anzahlDaten = 0;
private int anzahlFehler = 0;
private long anfrageDauer = 0;

private ListDataModel<AZVDatensatz> daten = null;
private AZVDatensatz selectedRow = null;

private boolean btnAZVAnfrageDisbled = true;
private String btnAZVAnfrageTTT = null;
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
	azvDaten = new AZVDaten();
	setMaxBerichtsmonat();
	}
	
    public void onDateSelect(SelectEvent event) 
    {
    Date selected = (Date) event.getObject();	
    setSelectedDate(selected);
    }
	
	public void setSelectedDate(Date selectedDate) 
	{
	logPrefix = this.getClass().getName() + "setSelectedDate(Date selectedDate): ";
		
	this.selectedDate = selectedDate;
	requestAZVDate = DateUtility.asLocalDate(getSelectedDate());
	azvDaten.setBerichtsMonat(requestAZVDate);
	log.log(Level.INFO, logPrefix + "AZV Anfrage wurde für den Monat " + Datumsformate.DF_MONATJAHR.format(requestAZVDate) + " initialisiert");
	
		if (selectedDate != null && maxAZVDate.isBefore(requestAZVDate)) 
		{
		setBtnAZVAnfrageDisbled(false);
		setBtnAZVAnfrageTTT("Startet die Abfrage des AZV Webservices, sofern die aktuellste AZV vor dem ausgewähltem Anfragedatum liegt.");	
		} 
		else 
		{
		setBtnAZVAnfrageDisbled(true);
		setBtnAZVAnfrageTTT("Derzeit nicht verfügbar, da das ausgewählte Anfragedatum " + Datumsformate.DF_MONATJAHR.format(requestAZVDate) + " vor der aktuellsten, in der Datenbank gespeicherten, AZV für den Monat " + Datumsformate.DF_MONATJAHR.format(maxAZVDate) + " liegt.");	
		}
	setBtnAZVResetDisabled(true);
	setBtnAZVSpeichernDisbled(true);
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
	connectionInfo = "Nicht verbunden";	
	setAnzahlDaten(0);
	azvDaten = new AZVDaten();
	azvDaten.setBerichtsMonat(DateUtility.asLocalDate(getSelectedDate()));
	daten = new ListDataModel<AZVDatensatz>();
	setBtnAZVAnfrageDisbled(false);
	setBtnAZVResetDisabled(true);
	setBtnAZVSpeichernDisbled(true);
	}
	
	public void save() 
	{
	SQLException e = dataService.insertAZVDaten(azvDaten.getAzvMeldungen());
	
		if (e != null) 
		{
		detail = e.getLocalizedMessage();
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler beim Speichern", detail);
		FacesContext.getCurrentInstance().addMessage(null, message);			
		} 
		else 
		{
		detail = "Es wurden " + azvDaten.getAzvMeldungen().size() + " AZV Datensätze erfolgreich in der Datenbank gespeichert.";
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Erfolgreich gepeichert !", detail);
		FacesContext.getCurrentInstance().addMessage(null, message);	
		reset();
		}
	}
	
    public void onRowSelect(SelectEvent event) 
    {
    AZVDatensatz row =  (AZVDatensatz) event.getObject();
    //RequestContext.getCurrentInstance().execute("updateButtons();");
    }
	
	public ListDataModel<AZVDatensatz> getDaten()
    {
    return daten;
    }	
	
	/*
	 * Verarbeitet die nun fertigen AZV-Datensätze zu einer sortierbaren Liste
	 */
	public void setDaten(ArrayList<AZVDatensatz> toSet) 
	{
	check();	
	this.daten = new ListDataModel<AZVDatensatz>(toSet);
	setBtnAZVAnfrageDisbled(true);
	setBtnAZVResetDisabled(false);
	setBtnAZVSpeichernDisbled(false);
	}
	
	private void check() 
	{
	fc = FacesContext.getCurrentInstance();
	FacesMessage message = null;
	setAnzahlFehler(0);
	
		for (AZVDatensatz azv : azvDaten.getAzvMeldungen()) 
		{
			if (azv.getPersonalNummer() == 0) 
			{
			setAnzahlFehler((getAnzahlFehler() + 1));	
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Personalnummer fehlt !", "Personalnummer für AZV-Meldung: Benutzer = " + azv.getUserName() + ", Nachname = " + azv.getNachname() + ", Vorname = " + azv.getVorname() + ", eMail = " + azv.getEMail() + " wurde nicht gefunden.");	
			fc.addMessage(null, message);
			}
		}
	}

	public void execute() 
	{
   	logPrefix = this.getClass().getName() + ".execute(): ";
	fc = FacesContext.getCurrentInstance();
   	
	webService = new AZVClient();
	setConnectionInfo("Verbunden mit OS/ECM Web Service an IP: " + webService.getServerInfo());
	
	Date start = new Date();	
	log.log(Level.INFO, logPrefix + "Starte Anfrage an den OS/ECM Webservice für den Berichtsmonat " + azvDaten.getRequestedMonthFromLocalDate() + " " + azvDaten.getRequestedYearFromLocalDate());	
	
	e = webService.setAZVRequest(azvDaten.getRequestedMonthFromLocalDate(), azvDaten.getRequestedYearFromLocalDate());
		if (e != null)
		{
		detail = e.getLocalizedMessage();	
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler bei der Anfrage !", detail);
		fc.addMessage(null, message);	
		e.printStackTrace();
		}
		else
		{
		doc = webService.getResultList();
		// write(doc);
		Date end = new Date();
		setAnfrageDauer((end.getTime() - start.getTime()));
		detail = "Anfrage an den OS/ECM Webservice wurde in " + String.valueOf(anfrageDauer) + " Millisekunden abgeschlossen.";	
		log.log(Level.INFO, logPrefix + detail);	
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Anfrage erfolgreich !", detail);
		fc.addMessage(null, message);	
		
		parseDocument();

		azvDaten.setAzvMeldungen(azvMeldungen);
		azvDaten.setErrors(false);
		azvDaten.setChecked(false);
		azvDaten.setRequestComplete(true);
		
		setAnzahlDaten(azvDaten.getAzvMeldungen().size());
		setDaten(azvDaten.getAzvMeldungen());
		}
	}	
	
	@SuppressWarnings("unused")
	private Document read() 
	{
	ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
	String dateiName = "responseUTF8.xml";
	String dateiPfad = servletContext.getRealPath("/tmp");
	Document result = null;
	
		try 
		{
		result = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(dateiPfad + "/" + dateiName));
		} 
		catch (SAXException | IOException | ParserConfigurationException e) 
		{
		e.printStackTrace();
		}
	return result;
	}

	
	@SuppressWarnings("unused")
	private void write(Document toWrite) 
	{
	DOMSource source = new DOMSource(doc);
	ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
	String dateiName = "responseUTF8" + Datumsformate.DF_JAHRMONAT.format(azvDaten.getBerichtsMonat()) + ".xml";
	String dateiPfad = servletContext.getRealPath("/tmp");

		try 
		{
		OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(new File(dateiPfad + "/" + dateiName)), StandardCharsets.UTF_8);	
		StreamResult result = new StreamResult(writer);
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.transform(source, result);
		} 
		catch (IOException e) 
		{
		e.printStackTrace();
		} 
		catch (TransformerConfigurationException e) 
		{
		e.printStackTrace();
		} 
		catch (TransformerException e) 
		{
		e.printStackTrace();
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
    int rowCount = 1;
    String strNachname = null;
    String strVorname = null;
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
							
							case "Vorname":
							strVorname = field.getTextContent();	
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
				            azvMeldung.setRowCount(rowCount);
				            azvMeldung.setNachname(strNachname);
				            azvMeldung.setVorname(strVorname);
				            azvMeldung.setTeam(strTeam);
				            azvMeldung.setUserName(strBenutzername);
				            azvMeldung.setEMail(strEMail);
				            
				            azvMeldung.setPersonalNummer(dataService.getPersonalnummer(azvMeldung));
				            
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
			            	rowCount++;
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
	
	public String getFormattedMaxAZVDate() {return Datumsformate.DF_MONATJAHR.format(maxAZVDate);}

	public AZVDatensatz getSelectedRow() {return selectedRow;}
	public void setSelectedRow(AZVDatensatz selectedRow) {this.selectedRow = selectedRow;}

	public MSSQLDataService getDataService() {return dataService;}
	public void setDataService(MSSQLDataService dataService) {this.dataService = dataService;}
	public Date getSelectedDate() {return selectedDate;}
	
	public int getAnzahlDaten() {return anzahlDaten;}
	public void setAnzahlDaten(int anzahlDaten) {this.anzahlDaten = anzahlDaten;}

	public boolean getBtnAZVResetDisabled() {return btnAZVResetDisabled;}
	public void setBtnAZVResetDisabled(boolean btnAZVResetDisabled) {this.btnAZVResetDisabled = btnAZVResetDisabled;}
	public boolean getBtnAZVSpeichernDisbled() {return btnAZVSpeichernDisbled;}
	public void setBtnAZVSpeichernDisbled(boolean btnAZVSpeichernDisbled) {this.btnAZVSpeichernDisbled = btnAZVSpeichernDisbled;}
	public boolean getBtnAZVAnfrageDisbled() {return btnAZVAnfrageDisbled;}
	public void setBtnAZVAnfrageDisbled(boolean btnAZVAnfrageDisbled) {this.btnAZVAnfrageDisbled = btnAZVAnfrageDisbled;}
	public String getBtnAZVAnfrageTTT() {return btnAZVAnfrageTTT;}
	public void setBtnAZVAnfrageTTT(String btnAZVAnfrageTTT) {this.btnAZVAnfrageTTT = btnAZVAnfrageTTT;}
	public String getConnectionInfo() {return connectionInfo;}
	public void setConnectionInfo(String connectionInfo) {this.connectionInfo = connectionInfo;}
	public int getAnzahlFehler() {return anzahlFehler;}
	public void setAnzahlFehler(int anzahlFehler) {this.anzahlFehler = anzahlFehler;}
	public long getAnfrageDauer() {return anfrageDauer;}
	public void setAnfrageDauer(long anfrageDauer) {this.anfrageDauer = anfrageDauer;}
		
}
