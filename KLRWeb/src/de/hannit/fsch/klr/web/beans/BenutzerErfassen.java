package de.hannit.fsch.klr.web.beans;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.application.ProjectStage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.ListDataModel;

import org.primefaces.event.SelectEvent;

import de.hannit.fsch.klr.dataservice.mssql.MSSQLDataService;
import de.hannit.fsch.klr.model.mitarbeiter.Mitarbeiter;

@ManagedBean
@ViewScoped
public class BenutzerErfassen implements Serializable
{
private static final long serialVersionUID = 8287257676826361362L;
private static final String ERFASSEN = "Mitarbeiter erfassen:";
private static final String BEARBEITEN = "Mitarbeiter bearbeiten:";

@ManagedProperty (value = "#{dataService}")
private MSSQLDataService dataService;
@ManagedProperty (value = "#{menuBar}")
private MenuBar menuBar;
private final static Logger log = Logger.getLogger(BenutzerErfassen.class.getSimpleName());	
private String logPrefix = null;	
private FacesContext fc = null;
private FacesMessage msg = null;
private String detail = null;

private ListDataModel<Mitarbeiter> mitarbeiterModel = null;
private TreeMap<Integer, Mitarbeiter> mitarbeiter = null;
private Mitarbeiter selectedMitarbeiter = new Mitarbeiter();
private boolean btnBenutzerAktualisierenDisabled = true;
private boolean btnBenutzerSpeichernDisabled = true;
private boolean btnBenutzerResetDisabled = true;
private boolean inputPersonalnummerDisabled = false;
private String gridHeaderText = BenutzerErfassen.ERFASSEN; 


	public BenutzerErfassen() 
	{
	fc = FacesContext.getCurrentInstance();
	dataService = dataService != null ? dataService : fc.getApplication().evaluateExpressionGet(fc, "#{dataService}", MSSQLDataService.class);
	menuBar = menuBar != null ? menuBar : fc.getApplication().evaluateExpressionGet(fc, "#{menuBar}", MenuBar.class);

	load();

	setSelectedMitarbeiter(menuBar.getCreateMitarbeiterCommand().getMitarbeiter() != null ? menuBar.getCreateMitarbeiterCommand().getMitarbeiter() : new Mitarbeiter());
	}
	
    public void onRowSelect(SelectEvent event) 
    {
    Mitarbeiter m = (Mitarbeiter) event.getObject();
    setSelectedMitarbeiter(m);
    setGridHeaderText(BenutzerErfassen.BEARBEITEN);
    btnBenutzerSpeichernDisabled = true;
    btnBenutzerAktualisierenDisabled = false;
    btnBenutzerResetDisabled = false;
    inputPersonalnummerDisabled = true;
    FacesMessage msg = new FacesMessage("Mitarbeiter ", m.getPersonalNR() + " ausgewählt.");
    FacesContext.getCurrentInstance().addMessage(null, msg);
    }	
	
	/*
	 * Lädt Daten aus der DB
	 */
	public void load()
	{
	fc = FacesContext.getCurrentInstance();	
	logPrefix = this.getClass().getName() + ".loadData(): ";
	
	if (fc.isProjectStage(ProjectStage.Development)) {log.log(Level.INFO, logPrefix + "Fordere Mitarbeiterliste vom DataService an.");}
	mitarbeiter = dataService.getMitarbeiterOhneAZV();
	mitarbeiterModel = new ListDataModel<>(mitarbeiter.descendingMap().values().stream().collect(Collectors.toCollection(ArrayList<Mitarbeiter>::new)));
	detail = "Es wurden " + mitarbeiter.size() + " Mitarbeiter aus der Datenbank geladen.";
	msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Mitarbeiter geladen.", detail);
	fc.addMessage(null, msg);
	}	
	
	/*
	 * Validiert, ob die Personalnummer noch frei ist.
	 * Die Validierung erfolgt nur, wenn das Element nicht disabled ist,
	 * ansonsten wird daven ausgegangen, dass aktualisiert werden soll.
	 */
	public void validatePNR(FacesContext context, UIComponent comp,	Object value) 
	{
	int pnr =  (int) value;	
	HtmlInputText input = (HtmlInputText) comp;
		
		if (! input.isDisabled() && dataService.existsMitarbeiter(pnr)) 
		{
		input.setValid(false);
		detail = "Die Personalnummer in der Datenbank kann nicht überschrieben werden. Wenn Sie einen vorhandenen Mitarbeiter bearbeiten wollen, klicken Sie auf den entsprechenden Eintrag in der Mitarbeitertabelle.";
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "PNR existiert bereits !", null);
		context.addMessage(comp.getClientId(context), message);
		message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "PNR existiert bereits !", detail);
		context.addMessage(null, message);			
		}

	}

	public void validatePNRAjax(AjaxBehaviorEvent event) 
	{
	selectedMitarbeiter = selectedMitarbeiter != null ? selectedMitarbeiter : new Mitarbeiter();	
	fc = FacesContext.getCurrentInstance();	
	HtmlInputText input = (HtmlInputText) event.getComponent();
	int pnr = (int) input.getValue();	

		
		if (! input.isDisabled() && dataService.existsMitarbeiter(pnr)) 
		{
		input.setValid(false);
		
		detail = "Die Personalnummer in der Datenbank kann nicht überschrieben werden. Wenn Sie einen vorhandenen Mitarbeiter bearbeiten wollen, klicken Sie auf den entsprechenden Eintrag in der Mitarbeitertabelle.";
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "PNR existiert bereits !", "Die Personalnummer in der Datenbank kann nicht überschrieben werden. Wenn Sie einen vorhandenen Mitarbeiter bearbeiten wollen, klicken Sie auf den entsprechenden Eintrag in der Mitarbeitertabelle.");
		fc.addMessage(input.getClientId(fc), message);		
		}

	}
	
	public void validateNachnameAjax(AjaxBehaviorEvent event) 
	{
	selectedMitarbeiter = selectedMitarbeiter != null ? selectedMitarbeiter : new Mitarbeiter();	
	fc = FacesContext.getCurrentInstance();	
	HtmlInputText input = (HtmlInputText) event.getComponent();
	String strName = (String) input.getValue();	

		
		if (strName.length() > 0) 
		{
			if (dataService.existsMitarbeiter(selectedMitarbeiter.getPersonalNR())) 
			{
			setBtnBenutzerSpeichernDisabled(true);
			setBtnBenutzerAktualisierenDisabled(false);
			setBtnBenutzerResetDisabled(false);
			} 
			else 
			{
			setBtnBenutzerSpeichernDisabled(false);
			setBtnBenutzerAktualisierenDisabled(true);
			setBtnBenutzerResetDisabled(false);
			}
		}
		else
		{
		input.setValid(false);
			
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Name ist leer !", null);
		fc.addMessage(input.getClientId(fc), message);			
		}

	}	

	public void save()
	{
	SQLException e = dataService.setMitarbeiter(selectedMitarbeiter);
	fc = FacesContext.getCurrentInstance();
	
		if (e != null) 
		{
		detail = e.getLocalizedMessage();
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler beim Speichern", detail);
		fc.addMessage(null, message);			
		} 
		else 
		{
		detail = "Mitarbeiter " + selectedMitarbeiter.getNachname() + " wurde unter der Personalnummer " + selectedMitarbeiter.getPersonalNR() + " erfolgreich in der Datenbank gespeichert.";
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Erfolgreich gepeichert !", detail);
		fc.addMessage(null, message);	
		
		mitarbeiter = dataService.getMitarbeiterOhneAZV();
		mitarbeiterModel = new ListDataModel<>(mitarbeiter.descendingMap().values().stream().collect(Collectors.toCollection(ArrayList<Mitarbeiter>::new)));		
		
		selectedMitarbeiter = new Mitarbeiter();	
		setGridHeaderText(BenutzerErfassen.ERFASSEN);
		btnBenutzerSpeichernDisabled = true;
		btnBenutzerAktualisierenDisabled = true;
		btnBenutzerResetDisabled = true;
		inputPersonalnummerDisabled = false;
		}			
	}

	public void delete()
	{
	SQLException e = dataService.deleteMitarbeiter(selectedMitarbeiter);
	fc = FacesContext.getCurrentInstance();
	
		if (e != null) 
		{
		detail = e.getLocalizedMessage();
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler beim Löschen", detail);
		fc.addMessage(null, message);			
		} 
		else 
		{
		detail = "Mitarbeiter " + selectedMitarbeiter.getNachname() + " wurde mit der Personalnummer " + selectedMitarbeiter.getPersonalNR() + " erfolgreich gelöscht.";
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Erfolgreich aktualisiert !", detail);
		fc.addMessage(null, message);	
		
		mitarbeiter = dataService.getMitarbeiterOhneAZV();
		mitarbeiterModel = new ListDataModel<>(mitarbeiter.descendingMap().values().stream().collect(Collectors.toCollection(ArrayList<Mitarbeiter>::new)));		
		
		selectedMitarbeiter = new Mitarbeiter();	
		setGridHeaderText(BenutzerErfassen.ERFASSEN);
		btnBenutzerSpeichernDisabled = true;
		btnBenutzerAktualisierenDisabled = true;
		btnBenutzerResetDisabled = true;
		inputPersonalnummerDisabled = false;
		}			
	}		
	
	public void update()
	{
	SQLException e = dataService.updateMitarbeiter(selectedMitarbeiter);
	fc = FacesContext.getCurrentInstance();
	
		if (e != null) 
		{
		detail = e.getLocalizedMessage();
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler beim Aktualisieren", detail);
		fc.addMessage(null, message);			
		} 
		else 
		{
		detail = "Mitarbeiter " + selectedMitarbeiter.getNachname() + " wurde mit der Personalnummer " + selectedMitarbeiter.getPersonalNR() + " erfolgreich in der Datenbank aktualisiert.";
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Erfolgreich aktualisiert !", detail);
		fc.addMessage(null, message);	
		
		mitarbeiter = dataService.getMitarbeiterOhneAZV();
		mitarbeiterModel = new ListDataModel<>(mitarbeiter.descendingMap().values().stream().collect(Collectors.toCollection(ArrayList<Mitarbeiter>::new)));		
		
		selectedMitarbeiter = new Mitarbeiter();	
		setGridHeaderText(BenutzerErfassen.ERFASSEN);
		btnBenutzerSpeichernDisabled = true;
		btnBenutzerAktualisierenDisabled = true;
		btnBenutzerResetDisabled = true;
		inputPersonalnummerDisabled = false;
		}			
	}	
	
	public void reset()
	{
	selectedMitarbeiter = new Mitarbeiter();	
	setGridHeaderText(BenutzerErfassen.ERFASSEN);
    btnBenutzerAktualisierenDisabled = true;
    btnBenutzerResetDisabled = true;
    inputPersonalnummerDisabled = false;
	}
	
	public ListDataModel<Mitarbeiter> getMitarbeiterModel() {return mitarbeiterModel;}
	public void setMitarbeiterModel(ListDataModel<Mitarbeiter> mitarbeiterModel) {this.mitarbeiterModel = mitarbeiterModel;}
	public MSSQLDataService getDataService() {return dataService;}
	public void setDataService(MSSQLDataService dataService) {this.dataService = dataService;}
	public MenuBar getMenuBar() {return menuBar;}
	public void setMenuBar(MenuBar menuBar) {this.menuBar = menuBar;}	
	public Mitarbeiter getSelectedMitarbeiter() {return selectedMitarbeiter;}
	public void setSelectedMitarbeiter(Mitarbeiter selectedMitarbeiter) {this.selectedMitarbeiter = selectedMitarbeiter;}
	public boolean getBtnBenutzerAktualisierenDisabled() {return btnBenutzerAktualisierenDisabled;}
	public void setBtnBenutzerAktualisierenDisabled(boolean btnBenutzerAktualisierenDisabled) {this.btnBenutzerAktualisierenDisabled = btnBenutzerAktualisierenDisabled;}
	public boolean getBtnBenutzerSpeichernDisabled() {return btnBenutzerSpeichernDisabled;}
	public void setBtnBenutzerSpeichernDisabled(boolean btnBenutzerSpeichernDisabled) {this.btnBenutzerSpeichernDisabled = btnBenutzerSpeichernDisabled;}
	public boolean isBtnBenutzerResetDisabled() {return btnBenutzerResetDisabled;}
	public void setBtnBenutzerResetDisabled(boolean btnBenutzerResetDisabled) {this.btnBenutzerResetDisabled = btnBenutzerResetDisabled;}
	public boolean isInputPersonalnummerDisabled() {return inputPersonalnummerDisabled;}
	public void setInputPersonalnummerDisabled(boolean inputPersonalnummerDisabled) {this.inputPersonalnummerDisabled = inputPersonalnummerDisabled;}
	public String getGridHeaderText() {return gridHeaderText;}
	public void setGridHeaderText(String gridHeaderText) {this.gridHeaderText = gridHeaderText;}
	
	
}
