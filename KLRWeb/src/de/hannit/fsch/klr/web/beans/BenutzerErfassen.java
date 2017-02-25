package de.hannit.fsch.klr.web.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.application.ProjectStage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.ListDataModel;

import org.primefaces.event.SelectEvent;

import de.hannit.fsch.klr.dataservice.mssql.MSSQLDataService;
import de.hannit.fsch.klr.model.mitarbeiter.Mitarbeiter;

@ManagedBean
@ViewScoped
public class BenutzerErfassen implements Serializable
{
private static final long serialVersionUID = 8287257676826361362L;

@ManagedProperty (value = "#{dataService}")
private MSSQLDataService dataService;

private final static Logger log = Logger.getLogger(BenutzerErfassen.class.getSimpleName());	
private String logPrefix = null;	
private FacesContext fc = null;
private FacesMessage msg = null;
private String detail = null;

private ListDataModel<Mitarbeiter> mitarbeiterModel = null;
private ArrayList<Mitarbeiter> mitarbeiter = null;
private Mitarbeiter selectedMitarbeiter = null;
private boolean btnBenutzerAktualisierenDisabled = true;
private boolean btnBenutzerSpeichernDisabled = true;
private boolean btnBenutzerResetDisabled = true;

	public BenutzerErfassen() 
	{
	fc = FacesContext.getCurrentInstance();
	dataService = dataService != null ? dataService : fc.getApplication().evaluateExpressionGet(fc, "#{dataService}", MSSQLDataService.class);

	loadData();
	}
	
    public void onRowSelect(SelectEvent event) 
    {
    Mitarbeiter m = (Mitarbeiter) event.getObject();
    setSelectedMitarbeiter(m);
    btnBenutzerAktualisierenDisabled = false;
    btnBenutzerResetDisabled = false;
    FacesMessage msg = new FacesMessage("Mitarbeiter ", m.getPersonalNR() + " ausgewählt.");
    FacesContext.getCurrentInstance().addMessage(null, msg);
    }	
	
	/*
	 * Lädt Daten aus der DB
	 */
	public void loadData()
	{
	fc = FacesContext.getCurrentInstance();	
	logPrefix = this.getClass().getName() + ".loadData(): ";
	
	if (fc.isProjectStage(ProjectStage.Development)) {log.log(Level.INFO, logPrefix + "Fordere Mitarbeiterliste vom DataService an.");}
	mitarbeiter = dataService.getMitarbeiterOhneAZV();
	mitarbeiterModel = new ListDataModel<>(mitarbeiter);
	detail = "Es wurden " + mitarbeiter.size() + " Mitarbeiter aus der Datenbank geladen.";
	msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Mitarbeiter geladen.", detail);
	fc.addMessage(null, msg);
	}	
	
	public void reset()
	{
	selectedMitarbeiter = selectedMitarbeiter != null ? null : selectedMitarbeiter;	
    btnBenutzerAktualisierenDisabled = true;
    btnBenutzerResetDisabled = true;	
	}
	
	public ListDataModel<Mitarbeiter> getMitarbeiterModel() {return mitarbeiterModel;}
	public void setMitarbeiterModel(ListDataModel<Mitarbeiter> mitarbeiterModel) {this.mitarbeiterModel = mitarbeiterModel;}
	public MSSQLDataService getDataService() {return dataService;}
	public void setDataService(MSSQLDataService dataService) {this.dataService = dataService;}
	public Mitarbeiter getSelectedMitarbeiter() {return selectedMitarbeiter;}
	public void setSelectedMitarbeiter(Mitarbeiter selectedMitarbeiter) {this.selectedMitarbeiter = selectedMitarbeiter;}
	public boolean getBtnBenutzerAktualisierenDisabled() {return btnBenutzerAktualisierenDisabled;}
	public void setBtnBenutzerAktualisierenDisabled(boolean btnBenutzerAktualisierenDisabled) {this.btnBenutzerAktualisierenDisabled = btnBenutzerAktualisierenDisabled;}
	public boolean getBtnBenutzerSpeichernDisabled() {return btnBenutzerSpeichernDisabled;}
	public void setBtnBenutzerSpeichernDisabled(boolean btnBenutzerSpeichernDisabled) {this.btnBenutzerSpeichernDisabled = btnBenutzerSpeichernDisabled;}
	public boolean isBtnBenutzerResetDisabled() {return btnBenutzerResetDisabled;}
	public void setBtnBenutzerResetDisabled(boolean btnBenutzerResetDisabled) {this.btnBenutzerResetDisabled = btnBenutzerResetDisabled;}
	
}
