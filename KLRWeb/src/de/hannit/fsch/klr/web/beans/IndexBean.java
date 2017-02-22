package de.hannit.fsch.klr.web.beans;

import java.io.Serializable;
import java.text.NumberFormat;
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
import javax.faces.event.ActionEvent;
import javax.faces.model.ListDataModel;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import de.hannit.fsch.common.MonatsSummen;
import de.hannit.fsch.klr.dataservice.mssql.MSSQLDataService;
import de.hannit.fsch.klr.model.Datumsformate;
import de.hannit.fsch.klr.model.azv.Arbeitszeitanteil;
import de.hannit.fsch.klr.model.kostenrechnung.Kostenrechnungsobjekt;
import de.hannit.fsch.klr.model.mitarbeiter.GemeinKosten;
import de.hannit.fsch.klr.model.mitarbeiter.Mitarbeiter;
import de.hannit.fsch.klr.model.mitarbeiter.PersonalDurchschnittsKosten;
import de.hannit.fsch.klr.model.mitarbeiter.Tarifgruppe;
import de.hannit.fsch.klr.model.mitarbeiter.Tarifgruppen;
import de.hannit.fsch.klr.model.organisation.Organisation;
import de.hannit.fsch.klr.model.team.Team;
import de.hannit.fsch.util.DateUtility;

@ManagedBean
@SessionScoped
public class IndexBean implements Serializable
{
private static final long serialVersionUID = 4726044687673797206L;
@ManagedProperty (value = "#{dataService}")
private MSSQLDataService dataService;
@ManagedProperty (value = "#{indexSelectOneController}")
private IndexSelectOneController indexSelectOneController;
private final static Logger log = Logger.getLogger(IndexBean.class.getSimpleName());	
private String logPrefix = null;	
private FacesContext fc = null;
private FacesMessage msg = null;
private String detail = null;
private Organisation hannit = null;
private Tarifgruppen tarifgruppen = null;
private MonatsSummen mSumme = null;
private TreeNode root;
private TreeNode treeName;
private TreeNode treeTeams;
private ListDataModel<Kostenrechnungsobjekt> gesamt = null;
private ListDataModel<Kostenrechnungsobjekt> gesamtKTR = null;
private ListDataModel<Kostenrechnungsobjekt> gesamtKST = null;
private ListDataModel<Tarifgruppe> tarifgruppenListe = null;


/**
 * Wieviel Vollzeitanteile wurden aus den Tarifgruppen verteilt ?
 */
private double vzaeVerteilt = 0;
/**
 * Wie hoch ist die Summe der in den Mitarbeiterdaten gespeicherten Bruttoaufwendungen ?
 */
private double vzaeTotal = 0;	



	public IndexBean() 
	{
	fc = FacesContext.getCurrentInstance();
	dataService = dataService != null ? dataService : fc.getApplication().evaluateExpressionGet(fc, "#{dataService}", MSSQLDataService.class);
	indexSelectOneController = indexSelectOneController != null ? indexSelectOneController : fc.getApplication().evaluateExpressionGet(fc, "#{indexSelectOneController}", IndexSelectOneController.class);

	hannit = new Organisation();
	loadData(DateUtility.asDate(indexSelectOneController.getMaxDate()));
	gesamt = new ListDataModel<Kostenrechnungsobjekt>(new ArrayList<Kostenrechnungsobjekt>(mSumme.getGesamtKosten().values()));
	gesamtKTR = new ListDataModel<Kostenrechnungsobjekt>(new ArrayList<Kostenrechnungsobjekt>(mSumme.getGesamtKostentraeger().values()));
	gesamtKST = new ListDataModel<Kostenrechnungsobjekt>(new ArrayList<Kostenrechnungsobjekt>(mSumme.getGesamtKostenstellen().values()));
	tarifgruppenListe = new ListDataModel<Tarifgruppe>(new ArrayList<Tarifgruppe>(tarifgruppen.getTarifGruppen().values()));

	setTree();
	}
	
	@SuppressWarnings("unused")
	private void setTree() 
	{
    root = new DefaultTreeNode("Root", null);
    treeName = new DefaultTreeNode("Root", null);
    treeTeams = new DefaultTreeNode("Root", null);

		TreeNode tNode = null;
       	TreeNode mNode = null;
    	TreeNode azvNode = null;
		for (Mitarbeiter m : hannit.getMitarbeiterNachPNR().values())
		{
		mNode  = new DefaultTreeNode(m.getTyp(), m, root);
			for (Arbeitszeitanteil	azv : m.getAzvMonat().values()) 
			{
			azvNode = new DefaultTreeNode("AZV", azv, mNode);	
			}
			
		}
		
    	mNode = null;
    	azvNode = null;
		for (Mitarbeiter m : hannit.getMitarbeiterNachName().values())
		{
		mNode  = new DefaultTreeNode(m.getTyp(), m, treeName);
			for (Arbeitszeitanteil	azv : m.getAzvMonat().values()) 
			{
			azvNode = new DefaultTreeNode("AZV", azv, mNode);	
			}
			
		}
		
    	mNode = null;
    	azvNode = null;
		for (Team t : hannit.getTeams().values())
		{
		tNode  = new DefaultTreeNode("Team", t, treeTeams);
			for (Mitarbeiter m : t.getTeamMitglieder().values())
			{
			mNode  = new DefaultTreeNode(m.getTyp(), m, tNode);
				for (Arbeitszeitanteil	azv : m.getAzvMonat().values()) 
				{
				azvNode = new DefaultTreeNode("AZV", azv, mNode);	
				}
			}
		}		
	}
	
    public TreeNode getRoot() {return root;}	
    public TreeNode getTreeName() {return treeName;}	
    public TreeNode getTreeTeams() {return treeTeams;}	


    public void buttonBackAction(ActionEvent actionEvent) 
    {	
   	logPrefix = this.getClass().getName() + ".buttonBackAction(ActionEvent actionEvent): ";
     	
    indexSelectOneController.setAuswertungsMonat(indexSelectOneController.getAuswertungsMonat().minusMonths(1));
    if (FacesContext.getCurrentInstance().isProjectStage(ProjectStage.Development)) {log.log(Level.INFO, logPrefix + "Fordere Mitarbeiterliste und AZV-Daten f�r den Monat " + DateUtility.DF_MONATJAHR.format(indexSelectOneController.getAuswertungsMonat()) + " vom DataService an.");}	
    loadData(DateUtility.asDate(indexSelectOneController.getAuswertungsMonat()));
    }
    
    public void buttonForwardAction(ActionEvent actionEvent) 
    {	
   	logPrefix = this.getClass().getName() + ".buttonForwardAction(ActionEvent actionEvent): ";
     	
    indexSelectOneController.setAuswertungsMonat(indexSelectOneController.getAuswertungsMonat().plusMonths(1));
    if (FacesContext.getCurrentInstance().isProjectStage(ProjectStage.Development)) {log.log(Level.INFO, logPrefix + "Fordere Mitarbeiterliste und AZV-Daten f�r den Monat " + DateUtility.DF_MONATJAHR.format(indexSelectOneController.getAuswertungsMonat()) + " vom DataService an.");}	
    loadData(DateUtility.asDate(indexSelectOneController.getAuswertungsMonat()));
    }

	/*
	 * L�dt Daten aus der DB zur Weiterverwendung im CSVDetailspart.
	 * Wird initial einmal und bei jeder �nderung der MonatsCombo aufgerufen.
	 */
	public void loadData(Date selectedMonth)
	{
	fc = FacesContext.getCurrentInstance();	
	logPrefix = this.getClass().getName() + ".loadData(): ";
	
	if (fc.isProjectStage(ProjectStage.Development)) {log.log(Level.INFO, logPrefix + "Fordere Mitarbeiterliste und AZV-Daten f�r den Monat " + Datumsformate.MONATLANG_JAHR.format(selectedMonth) + " vom DataService an.");}	
	
	hannit.setMitarbeiter(dataService.getAZVMonat(selectedMonth));
	if (fc.isProjectStage(ProjectStage.Development)) {log.log(Level.INFO, logPrefix + "Mitarbeiterliste enth�lt " + hannit.getMitarbeiterNachPNR().size() + " Mitarbeiter");}	

	/*
	 * Pr�fung, ob alle AZV-Anteile des Mitarbeiters zusammengez�hlt 100% ergeben 
	 */
		for (Mitarbeiter m : hannit.getMitarbeiterNachPNR().values())
		{
			if (m.getAzvProzentSumme() != 100)
			{
			log.log(Level.SEVERE, logPrefix + "AZV-Meldungen f�r Mitarbeiter: " + m.getNachname() + ", " + m.getVorname() + " sind ungleich 100% !");				
			}
		}

	if (fc.isProjectStage(ProjectStage.Development)) {log.log(Level.INFO, logPrefix + "Fordere Tarifgruppen f�r den Monat " + Datumsformate.MONATLANG_JAHR.format(selectedMonth) + " vom DataService an.");}	
	tarifgruppen = dataService.getTarifgruppen(selectedMonth);
	tarifgruppen.setAnzahlMitarbeiter(hannit.getMitarbeiterNachPNR().size());
	tarifgruppen.setBerichtsMonat(selectedMonth);
	if (fc.isProjectStage(ProjectStage.Development)) {log.log(Level.INFO, logPrefix + "Es wurden "+ tarifgruppen.getTarifGruppen().size() + " Tarifgruppen geladen.");}	
	
	// TODO Das geht so nicht mehr: Speichert die Tarifgruppen zur initialen Verwendung im Applikationscontext ab:
	//	if (application != null)
	//	{
	//	context = application.getContext();
	//	context.set(AppConstants.CONTEXT_TARIFGRUPPEN, tarifgruppen);
	//	log.info("Tarifgruppen f�r den Monat " + Datumsformate.MONATLANG_JAHR.format(selectedMonth) + ", wurden im Applikationskontext gespeichert.", plugin);
	//	}
	
	// log.info("Eventbroker versendet Tarifgruppen f�r den Monat " + Datumsformate.MONATLANG_JAHR.format(selectedMonth) + ", Topic: Topics.TARIFGRUPPEN", plugin);
	// broker.send(Topics.TARIFGRUPPEN, tarifgruppen);
	
	/*
	 * Nachdem die Tarifgruppen geladen wurden, wird f�r jeden Mitarbeiter
	 * das passende Vollzeit�quivalent gespeichert:
	 */
	vzaeVerteilt = 0;
		Tarifgruppe t = null;
		for (Mitarbeiter m : hannit.getMitarbeiterNachPNR().values())
		{
		t = tarifgruppen.getTarifGruppen().get(m.getTarifGruppe());	
		vzaeVerteilt += m.setVollzeitAequivalent(t.getVollzeitAequivalent());
		}
	if (fc.isProjectStage(ProjectStage.Development)) {log.log(Level.INFO, logPrefix + "F�r den Monat " + Datumsformate.MONATLANG_JAHR.format(selectedMonth) + " wurden insgesamt " + NumberFormat.getCurrencyInstance().format(vzaeVerteilt) + " Vollzeit�quivalente auf " + hannit.getMitarbeiterNachPNR().size() + " Mitarbeiter verteilt.");}	
		
	/*
	 * Im Log wird nun zu Pr�fzwecken ausgegeben, wie hoch das Vollzeit�quivalent Insgesamt betr�gt:	
	 */
	vzaeTotal = 0;	
		for (Mitarbeiter m : hannit.getMitarbeiterNachPNR().values())
		{
			for (String a : m.getAzvMonat().keySet())
			{
			vzaeTotal += m.getAzvMonat().get(a).getBruttoAufwand();	
			}
		}
		if (NumberFormat.getCurrencyInstance().format(vzaeTotal).equals(NumberFormat.getCurrencyInstance().format(vzaeVerteilt)))
		{
		log.log(Level.INFO, logPrefix + "F�r den Monat " + Datumsformate.MONATLANG_JAHR.format(selectedMonth) + " wurden insgesamt " + NumberFormat.getCurrencyInstance().format(vzaeTotal) + " Vollzeit�quivalente entsprechend der Arbeitszeitanteile verteilt.");	
		}
		else
		{
		log.log(Level.SEVERE, logPrefix + "F�r den Monat " + Datumsformate.MONATLANG_JAHR.format(selectedMonth) + " wurden insgesamt " + NumberFormat.getCurrencyInstance().format(vzaeTotal) + " Vollzeit�quivalente entsprechend der Arbeitszeitanteile verteilt.");
		}	
		
		/*
		 * Nun steht das Vollzeit�quivalent f�r jeden Mitarbeiter fest.
		 * Die Mitarbeiter werden erneut durchlaufen und es werden die Monatssummen f�r alle
		 * gemeldeten Kostenstellen / Kostentr�ger gebildet	
		 */
		mSumme = new MonatsSummen();
		mSumme.setGesamtSummen(hannit.getMitarbeiterNachPNR());
		mSumme.setBerichtsMonat(selectedMonth);
		
		PersonalDurchschnittsKosten pdk = new PersonalDurchschnittsKosten(selectedMonth);
		pdk.setMitarbeiter(hannit.getMitarbeiterNachPNR());
		
		GemeinKosten gk = new GemeinKosten(selectedMonth);
		gk.setMitarbeiter(hannit.getMitarbeiterNachPNR());
				
		/*
		 * Nachdem alle Kostenstellen / Kostentr�ger verteilt sind, wird die Gesamtsumme gebildet und im Log ausgegeben.
		 * Diese MUSS gleich dem Gesamtbruttoaufwand sein !
		 */
		double monatssummenTotal = mSumme.getKstktrMonatssumme();
		
		if (NumberFormat.getCurrencyInstance().format(monatssummenTotal).equals(NumberFormat.getCurrencyInstance().format(vzaeVerteilt)))
		{
		mSumme.setChecked(true);
		mSumme.setSummeOK(true);
		pdk.setChecked(true);
		pdk.setDatenOK(true);
		gk.setChecked(true);
		gk.setDatenOK(true);
		if (fc.isProjectStage(ProjectStage.Development)) {log.log(Level.INFO, logPrefix + "F�r den Monat " + Datumsformate.MONATLANG_JAHR.format(selectedMonth) + " wurden insgesamt " + NumberFormat.getCurrencyInstance().format(monatssummenTotal) + " auf " + mSumme.getGesamtKosten().size() + " Kostenstellen / Kostentr�ger verteilt.");}
		detail = "F�r den Monat " + Datumsformate.MONATLANG_JAHR.format(selectedMonth) + " wurden insgesamt " + NumberFormat.getCurrencyInstance().format(vzaeVerteilt) + " auf " + mSumme.getGesamtKosten().size() + " Kostenstellen / Kostentr�ger verteilt. Das entspricht der Summe der Personalaufwendungen i.H.v. " + NumberFormat.getCurrencyInstance().format(monatssummenTotal); 
		msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Monatssummen erfolgreich gepr�ft.", detail);
		fc.addMessage(null, msg);
		}
		else
		{
		mSumme.setChecked(true);
		mSumme.setSummeOK(false);
		pdk.setChecked(true);
		pdk.setDatenOK(false);
		gk.setChecked(true);
		gk.setDatenOK(false);
		detail = "F�r den Monat " + Datumsformate.MONATLANG_JAHR.format(selectedMonth) + " wurden insgesamt " + NumberFormat.getCurrencyInstance().format(vzaeVerteilt) + " auf " + mSumme.getGesamtKosten().size() + " Kostenstellen / Kostentr�ger verteilt. Die Summe der Personalaufwendungen betr�gt aber " + NumberFormat.getCurrencyInstance().format(monatssummenTotal); 
		log.log(Level.SEVERE, logPrefix + detail);		
		msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Monatssummen sind fehlerhaft !", detail);
		fc.addMessage(null, msg);
		}	
		
	gesamt = new ListDataModel<Kostenrechnungsobjekt>(new ArrayList<Kostenrechnungsobjekt>(mSumme.getGesamtKosten().values()));
	gesamtKTR = new ListDataModel<Kostenrechnungsobjekt>(new ArrayList<Kostenrechnungsobjekt>(mSumme.getGesamtKostentraeger().values()));
	gesamtKST = new ListDataModel<Kostenrechnungsobjekt>(new ArrayList<Kostenrechnungsobjekt>(mSumme.getGesamtKostenstellen().values()));
	tarifgruppenListe = new ListDataModel<Tarifgruppe>(new ArrayList<Tarifgruppe>(tarifgruppen.getTarifGruppen().values()));

	setTree();
	}	
	
	public MSSQLDataService getDataService() {return dataService;}
	public void setDataService(MSSQLDataService dataService) {this.dataService = dataService;}
	public IndexSelectOneController getIndexSelectOneController() {return indexSelectOneController;}
	public void setIndexSelectOneController(IndexSelectOneController indexSelectOneController) {this.indexSelectOneController = indexSelectOneController;}

	public ListDataModel<Kostenrechnungsobjekt> getMonatsGesamtSummen() 
	{
	return gesamt;		
	}
	
	public ListDataModel<Kostenrechnungsobjekt> getGesamtSummenRowsKST() 
	{
	return gesamtKST;	
	}
	
	public ListDataModel<Kostenrechnungsobjekt> getGesamtSummenRowsKTR() 
	{
	return gesamtKTR;	
	}	
	
	public MonatsSummen getMonatsSummen() {return mSumme;}

	public ListDataModel<Tarifgruppe> getTarifgruppenRows() 
	{
	return tarifgruppenListe;	
	}

	public String getLabelVZAE() 
	{
	return "Vollzeit�quivalent f�r Auswertungsmonat " + Datumsformate.DF_MONATJAHR.format(DateUtility.asLocalDate(tarifgruppen.getBerichtsMonat()));
	}
	
	public Tarifgruppen getTarifgruppen() 
	{
	return tarifgruppen;
	}

}
