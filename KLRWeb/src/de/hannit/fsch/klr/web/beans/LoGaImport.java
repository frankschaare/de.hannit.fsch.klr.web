package de.hannit.fsch.klr.web.beans;

import java.io.Serializable;
import java.sql.SQLException;
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

import de.hannit.fsch.common.Dezimalformate;
import de.hannit.fsch.klr.dataservice.mssql.MSSQLDataService;
import de.hannit.fsch.klr.model.Constants;
import de.hannit.fsch.klr.model.Datumsformate;
import de.hannit.fsch.klr.model.loga.LoGaDatei;
import de.hannit.fsch.klr.model.loga.LoGaDatensatz;
import de.hannit.fsch.klr.model.mitarbeiter.Mitarbeiter;
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
private int anzahlDaten = 0;
private int anzahlDatenInDB = 0;
private int anzahlErrors = 0;
private int anzahlWarnungen = 0;
private boolean btnLoGaResetDisabled = true;
private boolean btnLoGaUpdateDisabled = true;
private boolean btnLoGaSpeichernDisbled = true;


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
    
	public void update()
	{
	SQLException e = dataService.updateLoGaDaten(logaDatei);
	fc = FacesContext.getCurrentInstance();
	
		if (e != null) 
		{
		detail = e.getLocalizedMessage();
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler beim Aktualisieren", detail);
		fc.addMessage(null, message);			
		} 
		else 
		{
		detail = "LoGa Daten wurden erfolgreich in der Datenbank aktualisiert.";
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Erfolgreich aktualisiert !", detail);
		fc.addMessage(null, message);	
		reset();
		}			
	}    
    
	public void save()
	{
	SQLException e = dataService.setLoGaDaten(logaDatei.getDaten());
	fc = FacesContext.getCurrentInstance();
	
		if (e != null) 
		{
		detail = e.getLocalizedMessage();
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler beim Speichern", detail);
		fc.addMessage(null, message);			
		} 
		else 
		{
		detail = "LoGa Daten wurden erfolgreich in der Datenbank gespeichert.";
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Erfolgreich gepeichert !", detail);
		fc.addMessage(null, message);	
		reset();
		}			
	}
    
	public void reset()
	{
	file = null;	
	daten = new ListDataModel<LoGaDatensatz>();
	logaDatei.setAbrechnungsMonat(null);
	logaDatei.setSummeBrutto(0);
	logaDatei.setSummeStellen(0);
	anzahlDaten = 0;
	anzahlErrors = 0;
	anzahlWarnungen = 0;
	setBtnLoGaResetDisabled(true);	
	setBtnLoGaUpdateDisabled(true);
	setBtnLoGaSpeichernDisbled(true);
    }
    
    public void onRowSelect(SelectEvent event) 
    {
    LoGaDatensatz row =  (LoGaDatensatz) event.getObject();
    //RequestContext.getCurrentInstance().execute("updateButtons();");
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
	setAnzahlDaten(0);
	setAnzahlErrors(0);
	setAnzahlWarnungen(0);
	setAnzahlDatenInDB(0);
	
		for (LoGaDatensatz ds : logaDatei.getDaten().values())
		{
		anzahlDaten++;	
		
		exists = dataService.existsLoGaDatensatz(ds);
			if (exists)
			{
			ds.setErrors(true);
			ds.setRowStyle(Constants.CSS.ROWSTYLE_RED);
			setAnzahlDatenInDB((getAnzahlWarnungen() + 1));
			setAnzahlErrors((getAnzahlErrors() + 1));
			}	
		
		exists = dataService.existsMitarbeiter(ds.getPersonalNummer());
		ds.setMitarbeiterChecked(true);
		ds.setexistsMitarbeiter(exists);
			if (! exists)
			{
			ds.setWarnings(true);
			Mitarbeiter neu = new Mitarbeiter();
			neu.setPersonalNR(ds.getPersonalNummer());
			neu.setBenutzerName(Constants.DUMMIES.DUMMY_USERNAME);
			neu.setNachname(Constants.DUMMIES.DUMMY_NACHNAME);
			neu.setVorname(Constants.DUMMIES.DUMMY_VORNAME);
			SQLException e = dataService.setMitarbeiter(neu);
			
				if (e == null) 
				{
				detail = "Personalnummer " + ds.getPersonalNummer() + " wurde nicht in Tabelle Mitarbeiter gefunden ! Es wurde ein Dummy Eintrag in die Mitarbeitertabelle geschrieben, bitte die fehlenden Daten umgehend ergänzen !"; 
				msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Personalnummer nicht gefunden !", detail);			
				ds.setMitarbeiterChecked(true);
				ds.setexistsMitarbeiter(true);
				} 
				else 
				{
				detail = e.getLocalizedMessage(); 
				msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Datenbankfehler beim Anlegen eines Dummy Eintrages !", detail);
				ds.setexistsMitarbeiter(false);
				ds.setRowStyle(Constants.CSS.ROWSTYLE_RED);
				setAnzahlErrors((getAnzahlErrors() + 1));
				}
			
			log.log(Level.SEVERE, logPrefix + detail);	
			fc.addMessage(null, msg);
			}			
			
			// Ist keine Tarifgruppe angegeben, handelt es sich möglicherweise um eine Aushilfe, Praktikant oder studentische Hilfskraft
			if (ds.getTarifGruppe().trim().length() == 0)
			{
			log.log(Level.WARNING, logPrefix + "Die Importdatei einhält keine Tarifgruppe für Mitarbeiter " + ds.getPersonalNummer() + " !");	
			ds.setWarnings(true);
			setAnzahlWarnungen((getAnzahlWarnungen() + 1));
				try
				{
				String tarifGruppe = dataService.getTarifgruppeAushilfen(ds.getPersonalNummer());
				ds.setTarifGruppe(tarifGruppe);
				ds.setWarnings(false);
				setAnzahlWarnungen((getAnzahlWarnungen() - 1));
				ds.setRowStyle(Constants.CSS.ROWSTYLE_ORANGE);
				msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Tarifgruppe nachgetragen", "Die Tarifgruppe für Mitarbeiter " + ds.getPersonalNummer() + " (Zeile " + ds.getLineNumber() +")" + " wurde aus der Tabelle Aushilfen nachgetragen.");			
				fc.addMessage(null, msg);				
				}
				catch (NullPointerException e)
				{
				ds.setErrors(true);
				setAnzahlErrors((getAnzahlErrors() + 1));
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
		switch (anzahlErrors) 
		{
		case 0:
		setBtnLoGaResetDisabled(false);	
		setBtnLoGaUpdateDisabled(true);
		setBtnLoGaSpeichernDisbled(false);
		break;

		default:
		setBtnLoGaResetDisabled(false);	
		setBtnLoGaUpdateDisabled(false);
		setBtnLoGaSpeichernDisbled(true);			
		break;
		}

		if (getAnzahlDatenInDB() > 0) 
		{
		detail = "Es sind bereits Daten in der Datenbank vorhanden ! Die Daten können nicht importiert werden, Sie können die vorhandenen Daten lediglich überschreiben. Dabei werden die in der Datenbank vorhandenen Daten gelöscht und die Daten aus der ausgewählten Datei neu eingefügt."; 
		msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Daten bereits gespeichert !", detail);			
		fc.addMessage(null, msg);				
		log.log(Level.SEVERE, logPrefix + detail);	
		setBtnLoGaResetDisabled(false);	
		setBtnLoGaUpdateDisabled(false);
		setBtnLoGaSpeichernDisbled(true);	
		}
	logaDatei.setChecked(true);
	}

	public String getFormattedSummeBrutto()
	{
	return Dezimalformate.DFBRUTTO.format(logaDatei.getSummeBrutto());	
	}
	
	public String getFormattedSummeStellen()
	{
	return Dezimalformate.KOMMAZAHL.format(logaDatei.getSummeStellen());	
	}
	
	public String getFormattedAbrechnungsMonat()
	{
	return logaDatei.getAbrechnungsMonat() != null ? Datumsformate.DF_MONATJAHR.format(logaDatei.getAbrechnungsMonat()) : "";	
	}

	public MSSQLDataService getDataService() {return dataService;}
	public void setDataService(MSSQLDataService dataService) {this.dataService = dataService;}
	
	public int getAnzahlDaten() {return anzahlDaten;}
	public int getAnzahlErrors() {return anzahlErrors;}
	public int getAnzahlWarnungen() {return anzahlWarnungen;}
	public int getAnzahlDatenInDB() {return anzahlDatenInDB;}
	public void setAnzahlDatenInDB(int anzahlDatenInDB) {this.anzahlDatenInDB = anzahlDatenInDB;}
	public void setAnzahlDaten(int anzahlDaten) {this.anzahlDaten = anzahlDaten;}
	public void setAnzahlErrors(int anzahlErrors) {this.anzahlErrors = anzahlErrors;}
	public void setAnzahlWarnungen(int anzahlWarnungen) {this.anzahlWarnungen = anzahlWarnungen;}
	
	public boolean getBtnLoGaResetDisabled() {return btnLoGaResetDisabled;}
	public void setBtnLoGaResetDisabled(boolean btnLoGaResetDisabled) {this.btnLoGaResetDisabled = btnLoGaResetDisabled;}
	public boolean getBtnLoGaUpdateDisabled() {return btnLoGaUpdateDisabled;}
	public void setBtnLoGaUpdateDisabled(boolean btnLoGaUpdateDisabled) {this.btnLoGaUpdateDisabled = btnLoGaUpdateDisabled;}

	public boolean getBtnLoGaSpeichernDisbled() {return btnLoGaSpeichernDisbled;}
	public void setBtnLoGaSpeichernDisbled(boolean btnLoGaSpeichernDisbled) {this.btnLoGaSpeichernDisbled = btnLoGaSpeichernDisbled;}
	
}
