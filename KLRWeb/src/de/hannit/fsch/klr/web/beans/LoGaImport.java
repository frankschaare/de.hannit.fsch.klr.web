package de.hannit.fsch.klr.web.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.ListDataModel;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.UploadedFile;

import de.hannit.fsch.klr.dataservice.mssql.MSSQLDataService;
import de.hannit.fsch.klr.model.loga.LoGaDatei;
import de.hannit.fsch.klr.model.loga.LoGaDatensatz;
import de.hannit.fsch.klr.model.organisation.Organisation;

@ManagedBean
@SessionScoped
public class LoGaImport implements Serializable 
{
private static final long serialVersionUID = -15869998202315851L;
@ManagedProperty (value = "#{dataService}")
private MSSQLDataService dataService;
private UploadedFile file = null;
private LoGaDatei logaDatei = new LoGaDatei("");
private final static Logger log = Logger.getLogger(LoGaImport.class.getSimpleName());	
private String logPrefix = null;	
private FacesContext fc = null;
private FacesMessage msg = null;
private String detail = null;
private ListDataModel<LoGaDatensatz> daten = null;
private LoGaDatensatz selectedRow = null;



	public LoGaImport() 
	{
	fc = FacesContext.getCurrentInstance();
	dataService = dataService != null ? dataService : fc.getApplication().evaluateExpressionGet(fc, "#{dataService}", MSSQLDataService.class);
	}
	
	public UploadedFile getFile() 
	{
	return file;
	}

	public void setFile(UploadedFile file) 
	{
	this.file = file;
	logaDatei = new LoGaDatei(file.getFileName());
	logaDatei.hasHeader(true);
	logaDatei.setDelimiter(";");
	logaDatei.read(file);
	checkLogaData(logaDatei);
	daten = new ListDataModel<LoGaDatensatz>(new ArrayList<LoGaDatensatz>(logaDatei.getDaten().values()));
	}	
	
    public void upload(FileUploadEvent event) 
    {
    setFile(event.getFile());	
    FacesMessage message = new FacesMessage("Datei verarbeitet", "Datei " + event.getFile().getFileName() + " wurde erfolgreich verarbeitet.");
    FacesContext.getCurrentInstance().addMessage(null, message);
    }
    
    public void onRowSelect(SelectEvent event) 
    {
    LoGaDatensatz row =  (LoGaDatensatz) event.getObject();
    //RequestContext.getCurrentInstance().execute("updateButtons();");
System.out.println("Ole !");
    }
    
    public void deleteRow() 
    {
    logaDatei.getDaten().remove(selectedRow).getLineNumber();	
	daten = new ListDataModel<LoGaDatensatz>(new ArrayList<LoGaDatensatz>(logaDatei.getDaten().values()));
    selectedRow = null;
    }
    
    public LoGaDatensatz getSelectedRow() 
    {
	return selectedRow;
	}

	public void setSelectedRow(LoGaDatensatz selectedRow) 
	{
	this.selectedRow = selectedRow;
	System.out.println("ole !");
	}

	public ListDataModel<LoGaDatensatz> getDaten()
    {
    return daten;
    }
    
	/*
	 * Versuche eventuell fehlende Daten nachzutragen
	 */
	private void checkLogaData(LoGaDatei logaDatei)
	{
	fc = FacesContext.getCurrentInstance();
	boolean exists = false;
   	logPrefix = this.getClass().getName() + ".checkLogaData(LoGaDatei logaDatei): ";

	Organisation hannit = dataService.getHannit();	
	int pnrVorstand = hannit.getVorstand().getPersonalNR();
			
		for (LoGaDatensatz ds : logaDatei.getDaten().values())
		{
		exists = dataService.existsMitarbeiter(ds.getPersonalNummer());
		ds.setMitarbeiterChecked(true);
		ds.setexistsMitarbeiter(exists);
			if (! exists)
			{
			logaDatei.setErrors(true);	
			ds.setErrors(true);
			detail = "Personalnummer " + ds.getPersonalNummer() + " wurde nicht in Tabelle Mitarbeiter gefunden !"; 
			msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Personalnummer nicht gefunden !", detail);			
			log.log(Level.WARNING, logPrefix + detail);	
			fc.addMessage(null, msg);
			}			
			
			// Ist keine Tarifgruppe angegeben, handelt es sich möglicherweise um eine Aushilfe, Praktikant oder studentische Hilfskraft
			if (ds.getTarifGruppe().trim().length() == 0)
			{
			log.log(Level.WARNING, logPrefix + "Die Importdatei einhält keine Tarifgruppe für Mitarbeiter " + ds.getPersonalNummer() + " !");	
			ds.setWarnings(true);
				try
				{
				String tarifGruppe = dataService.getTarifgruppeAushilfen(ds.getPersonalNummer());
				ds.setTarifGruppe(tarifGruppe);
				ds.setWarnings(false);
				ds.setRowStyle("rowStyleOrange");
				msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Tarifgruppe nachgetragen", "Die Tarifgruppe für Mitarbeiter " + ds.getPersonalNummer() + " (Zeile " + ds.getLineNumber() +")" + " wurde aus der Tabelle Aushilfen nachgetragen.");			
				fc.addMessage(null, msg);				
				}
				catch (NullPointerException e)
				{
				ds.setErrors(true);
				
				detail = "Für Mitarbeiter " + ds.getPersonalNummer() + " konnte keine Tarifgruppe ermittelt werden !"; 
				msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tarifgruppe nicht gefunden !", detail);			
				fc.addMessage(null, msg);				
				log.log(Level.SEVERE, logPrefix + detail);	
				}
			}			
			
			if (ds.getPersonalNummer() == pnrVorstand)
			{
			ds.setTarifGruppe(hannit.getVorstand().getTarifGruppe());
			ds.setStellenAnteil(1);
			}
			// Sonderfall Schnese
			if (ds.getPersonalNummer() == 120025)
			{
			ds.setTarifGruppe("A13");
			ds.setStellenAnteil(1);
			}
			
			// Stellenanteil unbekannt ?
			if (ds.getStellenAnteil() == 999999)
			{
			ds.setStellenAnteil(dataService.getLetzterStellenanteil(ds.getPersonalNummer()));	
			}
		}
	logaDatei.setChecked(true);	
	}

	public MSSQLDataService getDataService() {
		return dataService;
	}

	public void setDataService(MSSQLDataService dataService) {
		this.dataService = dataService;
	}  
	
}
