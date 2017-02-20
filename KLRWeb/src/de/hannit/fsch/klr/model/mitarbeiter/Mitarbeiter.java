/**
 * 
 */
package de.hannit.fsch.klr.model.mitarbeiter;

import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

import de.hannit.fsch.klr.model.Constants;
import de.hannit.fsch.klr.model.azv.Arbeitszeitanteil;
import de.hannit.fsch.klr.model.team.TeamMitgliedschaft;

/**
 * @author fsch
 *
 */
public class Mitarbeiter extends Person 
{
public static final int STATUS_SONSTIGE = 0;
public static final int STATUS_ANGESTELLTER = 1;
public static final int STATUS_BEAMTER = 2;
public static final int STATUS_AUSZUBILDENDER = 6;
public static final int STATUS_ALTERSTEILZEIT_ANGESTELLTE = 7;
public static final int STATUS_ALTERSTEILZEIT_BEAMTE = 8;
public static final int STATUS_ELTERNZEIT = 5;

private int personalNR = 0;
private int teamNR = -1;
private int letzteTeamNR = -1;
private int status = -1;
private String benutzerName;
private Date abrechnungsMonat = null;
private double brutto;
private double vollzeitAequivalent = 0;
private String tarifGruppe = null;
private String tarifStufe = null;
private double stellenAnteil = 0;

private ArrayList<Arbeitszeitanteil> arbeitszeitAnteile = null;
private ArrayList<TeamMitgliedschaft> teamMitgliedschaften = null;
private TreeMap<String, Arbeitszeitanteil> azvMonat = null;
private boolean azvAktuell = true;

/*
 * Wird benutzt, um das passende Icon im Navigationsbaum anzuzeigen
 */
private String typ = Constants.TREETYPES.TREETYPE_DEFAULT;

/**
 * Der Gesamtprozentanteil der gespeicherten Arbeitszeitanteile.
 * Dieser MUSS = 100 sein, sonst ist etwas schief gelaufen.
 * Wird vom NavPart und vom NavTreeContentProvider gepr�ft.
 */
private int azvProzentSumme = 0;

	/**
	 * 
	 */
	public Mitarbeiter() 
	{
	azvMonat = new TreeMap<String, Arbeitszeitanteil>();
	}
	
	public ArrayList<Arbeitszeitanteil> getArbeitszeitAnteile(){return arbeitszeitAnteile;}
	public void setArbeitszeitAnteile(ArrayList<Arbeitszeitanteil> arbeitszeitAnteile){this.arbeitszeitAnteile = arbeitszeitAnteile;}
	public void setTeamMitgliedschaften(ArrayList<TeamMitgliedschaft> teamMitgliedschaften)	
	{
	this.teamMitgliedschaften = teamMitgliedschaften;
		switch (this.teamMitgliedschaften.size())
		{
		case 0:	
		break;

		// Standard: Teammitgliedschaften enth�lt genau einen Eintrag
		case 1:
		this.teamNR = teamMitgliedschaften.get(teamMitgliedschaften.size()-1).getTeamNummer();	
		break;
		
		/*
		 * Sonderfall f�r z.b. Altersteilzeitler. 
		 * Hier enth�lt Teammitgliedschaften mindestens zwei Eintr�ge.
		 * F�r die Personaldurchschnittskosten wird dann zus�tzlich das letzte Team gespeichert:
		 */
		default:
			for (TeamMitgliedschaft teamMitgliedschaft : teamMitgliedschaften)
			{
				if (teamMitgliedschaft.getSqlEnddatum() == null)
				{
				this.teamNR = teamMitgliedschaft.getTeamNummer();
				}
				else
				{
				this.letzteTeamNR = teamMitgliedschaft.getTeamNummer();	
				}
			}
		break;
		}
		
		switch (this.teamNR)
		{
		case 6:
		setStatus(Mitarbeiter.STATUS_AUSZUBILDENDER);	
		break;
		case 7:
		setStatus(Mitarbeiter.STATUS_ALTERSTEILZEIT_ANGESTELLTE);	
		break;
		case 8:
		setStatus(Mitarbeiter.STATUS_ALTERSTEILZEIT_BEAMTE);	
		break;

		default:
		break;
		}
	}
	public TreeMap<String, Arbeitszeitanteil> getAzvMonat() {return azvMonat;}
	public void setAzvMonat(TreeMap<String, Arbeitszeitanteil> azvMonat) {this.azvMonat = azvMonat;}

	public int getTeamNR() 
	{
		switch (teamMitgliedschaften.size())
		{
		case 0:
			if (azvMonat != null && ! azvMonat.isEmpty())
			{
			teamNR = azvMonat.get(azvMonat.firstKey()).getITeam();	
			}
		break;
		case 1:
		teamNR = teamMitgliedschaften.get(teamMitgliedschaften.size()-1).getTeamNummer();	
		break;

		default:
		break;
		}
	return teamNR;
	}
	public int getLetzteTeamNR() 
	{
	return letzteTeamNR;
	}
	
	public String getLabel() 
	{
	return getNachname() + ", " + getVorname();
	}
	
	public int getPersonalNR() {return personalNR;}
	public void setPersonalNR(int personalNR) {this.personalNR = personalNR;}
	public void setPersonalNRAsString(String personalNR){this.personalNR = Integer.parseInt(personalNR);}
	public String getPersonalNRAsString() {return String.valueOf(personalNR);}

	public int getStatus(){return status;}
	public void setStatus(int status)
	{
		switch (this.status)
		{
		case Mitarbeiter.STATUS_AUSZUBILDENDER:
		break;
		case Mitarbeiter.STATUS_ALTERSTEILZEIT_ANGESTELLTE:
		break;
		case Mitarbeiter.STATUS_ALTERSTEILZEIT_BEAMTE:
		break;
		default:
		this.status = status;
		break;
		}
	}

	public String getBenutzerName() {return benutzerName;}
	public void setBenutzerName(String benutzerName) {this.benutzerName = benutzerName;}

	public Date getAbrechnungsMonat(){return abrechnungsMonat;}
	public void setAbrechnungsMonat(Date abrechnungsMonat)	{this.abrechnungsMonat = abrechnungsMonat;}

	public double getBrutto(){return brutto;}
	public void setBrutto(double brutto){this.brutto = brutto;}

	public double getVollzeitAequivalent(){return vollzeitAequivalent;}
	
	/**
	 * Speichert das Vollzeit�quivalent f�r den Mitarbeiter
	 * Das Vollzeit�quivalent wird aus den Tarifgruppen geliefert, wo der Mittelwert f�r die jeweilige Tarifgruppe bekannt ist.
	 * 
	 * Da das Vollzeit�quivalent f�r DIESEN Mitarbeiter gilt, wird mit dem Stellenanteil mutipliziert
	 * 
	 * Nachdem das Vollzeit�quivalent gespeichert wurde, wird in der Methode #updateBruttoanteil der Bruttoanteil
	 * f�r die gemeldeten Arbeitszeitanteile des Mitarbeiters gespeichert
	 * @param vollzeitAequivalent
	 */
	public double setVollzeitAequivalent(double vollzeitAequivalent)
	{
	this.vollzeitAequivalent = vollzeitAequivalent;
	updateBruttoanteil();
	return (this.vollzeitAequivalent * this.stellenAnteil);
	}
	
	/**
	 * 
	 * @return Die Summe aller AZV-Anteile. MUSS = 100% sein, sonst wird ein Fehler ausgegeben.
	 */
	public int getAzvProzentSumme()
	{
	Arbeitszeitanteil azv = null;
	azvProzentSumme = 0;
	
		for (String str : getAzvMonat().keySet())
		{
		azv = getAzvMonat().get(str);
		azvProzentSumme += azv.getProzentanteil();
		}
	return azvProzentSumme;	
	}

	private void updateBruttoanteil()
	{
	double brutto = 0;	
		for (Arbeitszeitanteil a : azvMonat.values())
		{
		brutto = ((this.brutto /100) * a.getProzentanteil());	
		// brutto = ((this.vollzeitAequivalent * this.stellenAnteil) / 100) * a.getProzentanteil();
		a.setBruttoAufwand(brutto);
		}
	}

	public String getTarifGruppe(){return tarifGruppe;}
	
	/**
	 * Legt die Tarifgruppe fest, in der der Mitarbeiter bezahlt wurde.
	 * Anhand der Tarifgruppe wird festgestellt, in welchem Beschaftigungsverh�ltnis der Mitarbeiter
	 * im aktuellen Monat stand.
	 */
	public void setTarifGruppe(String tarifGruppe)
	{
	this.tarifGruppe = tarifGruppe;

		switch (tarifGruppe)
		{
		case "5":
		setStatus(STATUS_ANGESTELLTER);	
		break;
		
		case "6":
		setStatus(STATUS_ANGESTELLTER);	
		break;
		
		case "7":
		setStatus(STATUS_ANGESTELLTER);	
		break;
		
		case "8":
		setStatus(STATUS_ANGESTELLTER);	
		break;
		
		case "9":
		setStatus(STATUS_ANGESTELLTER);	
		break;
		
		case "10":
		setStatus(STATUS_ANGESTELLTER);	
		break;
		
		case "11":
		setStatus(STATUS_ANGESTELLTER);	
		break;
		
		case "12":
		setStatus(STATUS_ANGESTELLTER);	
		break;
		
		case "13":
		setStatus(STATUS_ANGESTELLTER);	
		break;
		
		case "14":
		setStatus(STATUS_ANGESTELLTER);	
		break;
		
		case "15":
		setStatus(STATUS_ANGESTELLTER);	
		break;
		
		case "A8":
		setStatus(STATUS_BEAMTER);	
		break;
		
		case "A9":
		setStatus(STATUS_BEAMTER);	
		break;	
		
		case "A9mD":
		setStatus(STATUS_BEAMTER);	
		break;
		
		case "A10":
		setStatus(STATUS_BEAMTER);	
		break;	
		
		case "A11":
		setStatus(STATUS_BEAMTER);	
		break;
		
		case "A12":
		setStatus(STATUS_BEAMTER);	
		break;	
		
		case "A13":
		setStatus(STATUS_BEAMTER);	
		break;	
		
		case "A14":
		setStatus(STATUS_BEAMTER);	
		break;	
		
		case "A15":
		setStatus(STATUS_BEAMTER);	
		break;	
		
		case "A16":
		setStatus(STATUS_BEAMTER);	
		break;	
		
		case "AZU":
		setStatus(STATUS_AUSZUBILDENDER);	
		break;		

		default:
		setStatus(STATUS_SONSTIGE);	
		break;
		}
	}

	public String getTarifStufe(){return tarifStufe;}
	public void setTarifStufe(String tarifStufe){this.tarifStufe = tarifStufe;}

	public double getStellenAnteil(){return stellenAnteil;}
	public void setStellenAnteil(double stellenAnteil){this.stellenAnteil = stellenAnteil;}

	public boolean isAzvAktuell(){return azvAktuell;}
	public void setAzvAktuell(boolean azvAktuell){this.azvAktuell = azvAktuell;}

	public void setTyp(String typ) {this.typ = typ;}
	public String getTyp() 
	{
		switch (getStatus()) 
		{
		case STATUS_ALTERSTEILZEIT_ANGESTELLTE:
		setTyp(Constants.TREETYPES.TREETYPE_ALTERSTEILZEIT);	
		break;
		case STATUS_ALTERSTEILZEIT_BEAMTE:
		setTyp(Constants.TREETYPES.TREETYPE_ALTERSTEILZEIT);	
		break;
		case STATUS_AUSZUBILDENDER:
		setTyp(Constants.TREETYPES.TREETYPE_AUSZUBILDENDER);	
		break;

		default:
		typ = Constants.TREETYPES.TREETYPE_DEFAULT;
		break;
		}
		
		if (! isAzvAktuell()) 
		{
		typ = Constants.TREETYPES.TREETYPE_AZV_NICHT_AKTUELL;
		}
		
		if (getAzvProzentSumme() != 100) 
		{
		typ = Constants.TREETYPES.TREETYPE_ERROR;	
		}
		
		if (azvMonat == null) 
		{
		typ = Constants.TREETYPES.TREETYPE_DISABLED;	
		}
		 
	return typ;
	}

	
	
	
}
