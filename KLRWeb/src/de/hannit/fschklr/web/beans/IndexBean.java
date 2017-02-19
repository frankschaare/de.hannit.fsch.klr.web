package de.hannit.fschklr.web.beans;

import java.io.Serializable;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.ProjectStage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import de.hannit.fsch.common.MonatsSummen;
import de.hannit.fsch.klr.dataservice.mssql.MSSQLDataService;
import de.hannit.fsch.klr.model.Datumsformate;
import de.hannit.fsch.klr.model.azv.Arbeitszeitanteil;
import de.hannit.fsch.klr.model.mitarbeiter.GemeinKosten;
import de.hannit.fsch.klr.model.mitarbeiter.Mitarbeiter;
import de.hannit.fsch.klr.model.mitarbeiter.PersonalDurchschnittsKosten;
import de.hannit.fsch.klr.model.mitarbeiter.Tarifgruppe;
import de.hannit.fsch.klr.model.mitarbeiter.Tarifgruppen;
import de.hannit.fsch.klr.model.organisation.Organisation;
import de.hannit.fsch.util.DateUtility;

@ManagedBean
@SessionScoped
public class IndexBean implements Serializable
{
private static final long serialVersionUID = 4726044687673797206L;
@ManagedProperty (value = "#{dataService}")
private MSSQLDataService dataService;	
private final static Logger log = Logger.getLogger(IndexBean.class.getSimpleName());	
private String logPrefix = null;	
private FacesContext fc = null;
private Organisation hannit = null;
private Tarifgruppen tarifgruppen = null;
private MonatsSummen mSumme = null;
private TreeNode root;

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
	hannit = new Organisation();
	// TODO TESTDUMMY ENTFERNEN !!!
	LocalDate testDummy = LocalDate.of(2015, 2, 1);
	loadData(DateUtility.asDate(testDummy));
	setTree();
	}
	
	@SuppressWarnings("unused")
	private void setTree() 
	{
    root = new DefaultTreeNode("Root", null);
    	
    	TreeNode mNode = null;
    	TreeNode azvNode = null;
		for (Mitarbeiter m : hannit.getMitarbeiterNachPNR().values())
		{
		mNode  = new DefaultTreeNode(m, root);
			for (Arbeitszeitanteil	azv : m.getAzvMonat().values()) 
			{
			azvNode = new DefaultTreeNode(azv, mNode);	
			}
			
		}
	}
	
    public TreeNode getRoot() {return root;}	

	/*
	 * Lädt Daten aus der DB zur Weiterverwendung im CSVDetailspart.
	 * Wird initial einmal und bei jeder Änderung der MonatsCombo aufgerufen.
	 */
	public void loadData(Date selectedMonth)
	{
	logPrefix = this.getClass().getName() + ".loadData(): ";
	
	if (fc.isProjectStage(ProjectStage.Development)) {log.log(Level.INFO, logPrefix + "Fordere Mitarbeiterliste und AZV-Daten für den Monat " + Datumsformate.MONATLANG_JAHR.format(selectedMonth) + " vom DataService an.");}	
	
	hannit.setMitarbeiter(dataService.getAZVMonat(selectedMonth));
	if (fc.isProjectStage(ProjectStage.Development)) {log.log(Level.INFO, logPrefix + "Mitarbeiterliste enthält " + hannit.getMitarbeiterNachPNR().size() + " Mitarbeiter");}	

	/*
	 * Prüfung, ob alle AZV-Anteile des Mitarbeiters zusammengezählt 100% ergeben 
	 */
		for (Mitarbeiter m : hannit.getMitarbeiterNachPNR().values())
		{
			if (m.getAzvProzentSumme() != 100)
			{
			log.log(Level.SEVERE, logPrefix + "AZV-Meldungen für Mitarbeiter: " + m.getNachname() + ", " + m.getVorname() + " sind ungleich 100% !");				
			}
		}

	if (fc.isProjectStage(ProjectStage.Development)) {log.log(Level.INFO, logPrefix + "Fordere Tarifgruppen für den Monat " + Datumsformate.MONATLANG_JAHR.format(selectedMonth) + " vom DataService an.");}	
	tarifgruppen = dataService.getTarifgruppen(selectedMonth);
	tarifgruppen.setAnzahlMitarbeiter(hannit.getMitarbeiterNachPNR().size());
	tarifgruppen.setBerichtsMonat(selectedMonth);
	if (fc.isProjectStage(ProjectStage.Development)) {log.log(Level.INFO, logPrefix + "Es wurden "+ tarifgruppen.getTarifGruppen().size() + " Tarifgruppen geladen.");}	
	
	// TODO Das geht so nicht mehr: Speichert die Tarifgruppen zur initialen Verwendung im Applikationscontext ab:
	//	if (application != null)
	//	{
	//	context = application.getContext();
	//	context.set(AppConstants.CONTEXT_TARIFGRUPPEN, tarifgruppen);
	//	log.info("Tarifgruppen für den Monat " + Datumsformate.MONATLANG_JAHR.format(selectedMonth) + ", wurden im Applikationskontext gespeichert.", plugin);
	//	}
	
	// log.info("Eventbroker versendet Tarifgruppen für den Monat " + Datumsformate.MONATLANG_JAHR.format(selectedMonth) + ", Topic: Topics.TARIFGRUPPEN", plugin);
	// broker.send(Topics.TARIFGRUPPEN, tarifgruppen);
	
	/*
	 * Nachdem die Tarifgruppen geladen wurden, wird für jeden Mitarbeiter
	 * das passende Vollzeitäquivalent gespeichert:
	 */
	vzaeVerteilt = 0;
		Tarifgruppe t = null;
		for (Mitarbeiter m : hannit.getMitarbeiterNachPNR().values())
		{
		t = tarifgruppen.getTarifGruppen().get(m.getTarifGruppe());	
		vzaeVerteilt += m.setVollzeitAequivalent(t.getVollzeitAequivalent());
		}
	if (fc.isProjectStage(ProjectStage.Development)) {log.log(Level.INFO, logPrefix + "Für den Monat " + Datumsformate.MONATLANG_JAHR.format(selectedMonth) + " wurden insgesamt " + NumberFormat.getCurrencyInstance().format(vzaeVerteilt) + " Vollzeitäquivalente auf " + hannit.getMitarbeiterNachPNR().size() + " Mitarbeiter verteilt.");}	
		
	/*
	 * Im Log wird nun zu Prüfzwecken ausgegeben, wie hoch das Vollzeitäquivalent Insgesamt beträgt:	
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
		log.log(Level.INFO, logPrefix + "Für den Monat " + Datumsformate.MONATLANG_JAHR.format(selectedMonth) + " wurden insgesamt " + NumberFormat.getCurrencyInstance().format(vzaeTotal) + " Vollzeitäquivalente entsprechend der Arbeitszeitanteile verteilt.");	
		}
		else
		{
		log.log(Level.SEVERE, logPrefix + "Für den Monat " + Datumsformate.MONATLANG_JAHR.format(selectedMonth) + " wurden insgesamt " + NumberFormat.getCurrencyInstance().format(vzaeTotal) + " Vollzeitäquivalente entsprechend der Arbeitszeitanteile verteilt.");
		}	
		
		/*
		 * Nun steht das Vollzeitäquivalent für jeden Mitarbeiter fest.
		 * Die Mitarbeiter werden erneut durchlaufen und es werden die Monatssummen für alle
		 * gemeldeten Kostenstellen / Kostenträger gebildet	
		 */
		mSumme = new MonatsSummen();
		mSumme.setGesamtSummen(hannit.getMitarbeiterNachPNR());
		mSumme.setBerichtsMonat(selectedMonth);
		
		PersonalDurchschnittsKosten pdk = new PersonalDurchschnittsKosten(selectedMonth);
		pdk.setMitarbeiter(hannit.getMitarbeiterNachPNR());
		
		GemeinKosten gk = new GemeinKosten(selectedMonth);
		gk.setMitarbeiter(hannit.getMitarbeiterNachPNR());
				
		/*
		 * Nachdem alle Kostenstellen / Kostenträger verteilt sind, wird die Gesamtsumme gebildet und im Log ausgegeben.
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
		if (fc.isProjectStage(ProjectStage.Development)) {log.log(Level.INFO, logPrefix + "Für den Monat " + Datumsformate.MONATLANG_JAHR.format(selectedMonth) + " wurden insgesamt " + NumberFormat.getCurrencyInstance().format(monatssummenTotal) + " auf " + mSumme.getGesamtKosten().size() + " Kostenstellen / Kostenträger verteilt.");}	
		}
		else
		{
		mSumme.setChecked(true);
		mSumme.setSummeOK(false);
		pdk.setChecked(true);
		pdk.setDatenOK(false);
		gk.setChecked(true);
		gk.setDatenOK(false);
		log.log(Level.SEVERE, logPrefix + "Für den Monat " + Datumsformate.MONATLANG_JAHR.format(selectedMonth) + " wurden insgesamt " + NumberFormat.getCurrencyInstance().format(monatssummenTotal) + " auf " + mSumme.getGesamtKosten().size() + " Kostenstellen / Kostenträger verteilt.");		
		}	
		
	/*
	 * Nach Abschluss aller Prüfungen werden die Monatssummen versendet:	
	 */
	// TODO: Broker Action geht nicht mehr	
	// log.info("Eventbroker versendet Monatssummen für den Monat " + Datumsformate.MONATLANG_JAHR.format(selectedMonth) + ", Topic: Topics.MONATSSUMMEN", plugin);
	// broker.send(Topics.MONATSSUMMEN, mSumme);
		// Speichert die Monatssummen zur initialen Verwendung im CSVDetailsPart im Applikationscontext ab:
	//	if (application != null)
	//	{
	//	context = application.getContext();
	//	context.set(AppConstants.CONTEXT_MONATSSUMMEN, mSumme);
	//	log.info("Tarifgruppen für den Monat " + Datumsformate.MONATLANG_JAHR.format(selectedMonth) + ", wurden im Applikationskontext gespeichert.", plugin);
	//	}
	// broker.send(Topics.PERSONALDURCHSCHNITTSKOSTEN, pdk);
	// broker.send(Topics.GEMEINKOSTERKOSTEN, gk);
	
	// tvPNR.setInput(hannit.getMitarbeiterNachPNR());
	// tvNachname.setInput(hannit.getMitarbeiterNachName());
	}	
	
	public MSSQLDataService getDataService() {return dataService;}
	public void setDataService(MSSQLDataService dataService) {this.dataService = dataService;}

	public String getTest() 
	{
	return "Ole !";	
	}

}
