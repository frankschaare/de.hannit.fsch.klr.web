/**
 * 
 */
package de.hannit.fsch.klr.model.mitarbeiter;

import java.text.NumberFormat;
import java.util.Date;
import java.util.TreeMap;

import de.hannit.fsch.klr.model.azv.Arbeitszeitanteil;
import de.hannit.fsch.klr.model.team.Team;

/**
 * Bei den Personaldurchschnittskosten werden die Vollzeitäquivalente
 * unterteilt nach den Teams und dort wiederum unterteilt nach Beamten / Angestellten dargestellt.
 * 
 * Davon abgezogen werden alle Mitarbeiter, deren Tätigkeit auf Vorkostenstellen gebucht wird.
 * Dies sind:
 * - die Geschäftsführung
 * - alle Mitarbeiter von Team 1
 * - alle passiven Altersteilzeitler
 * 
 * Diese Werte werden in Navision bei der Verteilung der Umlage 002 und 003 benötigt.
 * 
 * @author fsch
 * @since 11.02.2014
 *
 */
public class PersonalDurchschnittsKosten
{
public static final String COLUMN1_OE = "Organisationseinheit";
public static final String COLUMN2_ANGESTELLTE = "Angestellte";
public static final String COLUMN3_ANGESTELLTE_VZAE = "VZÄ";
public static final String COLUMN4_BEAMTE = "Beamte";
public static final String COLUMN5_BEAMTE_VZAE = "VZÄ";
public static final String COLUMN6_SUMME_BRUTTO = "Gesamtkosten";
public static final String COLUMN7_SUMME_VZAE = "Gesamt VZÄ";
public static final String COLUMN8_ABZUG_VORKOSSTENSTELLEN = "./. Vorkostenstellen";
public static final String COLUMN9_VZAE_ENDKOSTENSTELLEN = "VZÄ Endkostenstellen";

private TreeMap<Integer, Mitarbeiter> mitarbeiter;
private TreeMap<Integer, Team> teams = new TreeMap<Integer, Team>();	
private Date berichtsMonat = null;
private boolean checked = false;
private boolean datenOK = false;

	/**
	 * 
	 */
	public PersonalDurchschnittsKosten(Date selectedMonth)
	{
	this.berichtsMonat = selectedMonth;	
	}

	public TreeMap<Integer, Mitarbeiter> getMitarbeiter()
	{
	return mitarbeiter;
	}
	
	public TreeMap<Integer, Team> getTeams()
	{
	return teams;
	}

	/**
	 * Gesamtsumme aller Bruttoaufwendungen 
	 */
	public double getSummeBruttoGesamt()
	{
	double sumGesamt = 0;
	
		for (Team t : teams.values())
		{
			if (t.getAngestellte().size() > 0)
			{
				for (Mitarbeiter m : t.getAngestellte().values())
				{
				sumGesamt += m.getBrutto();	
				}			
			}
			if (t.getBeamte().size() > 0)
			{
				for (Mitarbeiter m : t.getBeamte().values())
				{
				sumGesamt += m.getBrutto();	
				}			
			}			
		}
	return sumGesamt;	
	}
	
	/**
	 * Gesamtsumme aller Vollzeiäquivalente 
	 */
	public double getSummeVZAEGesamt()
	{
	double sumGesamt = 0;
	
		for (Team t : teams.values())
		{
			if (t.getAngestellte().size() > 0)
			{
				for (Mitarbeiter m : t.getAngestellte().values())
				{
				sumGesamt += m.getStellenAnteil();	
				}			
			}
			if (t.getBeamte().size() > 0)
			{
				for (Mitarbeiter m : t.getBeamte().values())
				{
				sumGesamt += m.getStellenAnteil();	
				}			
			}			
		}
	return sumGesamt;	
	}
	
	/**
	 * Gesamt Anzahl Mitarbeiter 
	 */
	public int getAnzahlMitarbeiter()
	{
	int anzGesamt = 0;
	
		for (Team t : teams.values())
		{
		anzGesamt += t.getAngestellte().size();
		anzGesamt += t.getBeamte().size();
		}
	return anzGesamt;	
	}	
	
	/**
	 * Summe der Bruttoaufwendungen für alle Angestellten des Teams
	 */
	public double getSummeBruttoAngestellte(int teamNR)
	{
	double sumBruttoAngestellte = 0;
	Team t = teams.get(teamNR);
	
		if (t.getAngestellte().size() > 0)
		{
			for (Mitarbeiter m : t.getAngestellte().values())
			{
			sumBruttoAngestellte += m.getBrutto();	
			}			
		}
	
	return sumBruttoAngestellte;	
	}

	/**
	 * Summe der Stellen auf Vorkostenstellen
	 * @param teamNR
	 * @return
	 */
	public double getSummeVorkostenstellen(int teamNR)
	{
	double sumVorkostenstellen = 0;
	Team t = teams.get(teamNR);
	
		for (Mitarbeiter m : t.getMitarbeitAufVorkostenstellen().values())
		{
		sumVorkostenstellen -= m.getStellenAnteil();	
		}
	
	return sumVorkostenstellen;	
	}
	
	/**
	 * Summe der Vollzeitäquivalente für alle Angestellten des Teams
	 * @param teamNR
	 * @return
	 */
	public double getSummeVZAEAngestellte(int teamNR)
	{
	double sumVZAEAngestellte = 0;
	Team t = teams.get(teamNR);
	
		for (Mitarbeiter m : t.getAngestellte().values())
		{
		sumVZAEAngestellte += m.getStellenAnteil();	
		}
	
	return sumVZAEAngestellte;	
	}	
	
	/**
	 * Summe der Bruttoaufwendungen für alle Beamten des Teams
	 */
	public double getSummeBruttoBeamte(int teamNR)
	{
	double sumBruttoBeamte = 0;
	Team t = teams.get(teamNR);
	
		for (Mitarbeiter m : t.getBeamte().values())
		{
			sumBruttoBeamte += m.getBrutto();	
		}
	
	return sumBruttoBeamte;	
	}
	
	/**
	 * Summe der Vollzeitäquivalente für alle Beamten des Teams
	 * @param teamNR
	 * @return
	 */
	public double getSummeVZAEBeamte(int teamNR)
	{
	double sumVZAEBeamte = 0;
	Team t = teams.get(teamNR);
	
		for (Mitarbeiter m : t.getBeamte().values())
		{
		sumVZAEBeamte += m.getStellenAnteil();	
		}
	
	return sumVZAEBeamte;	
	}		
	
	public TreeMap<Integer, SummenZeile> getSummentabelle()
	{
	TreeMap<Integer, SummenZeile> summenTabelle = new TreeMap<Integer, SummenZeile>();
	int teamNR = -1;
	int anzahlSummenZeilen = 0;
	SummenZeile sz = null;
	
	double sumBruttoAngestellte = 0;
	double sumBruttoAngestellteGesamt = 0;
	double sumVZAEAngestellte = 0;
	double sumVZAEAngestellteGesamt = 0;
	double sumBruttoBeamte = 0;
	double sumBruttoBeamteGesamt = 0;
	double sumVZAEBeamte = 0;
	double sumVZAEBeamteGesamt = 0;
	double abzugVorkostenStelle = 0;
	double sumEndkostenstelle = 0;
	double sumEndkostenstellenGesamt = 0;
	
		for (Team t : teams.values())
		{
		sz = new SummenZeile();	
		// Spalte 1: Organisationseinheit
		teamNR = t.getTeamNummer();	
		sz.setColumn0(t.getOE());
		
		// Spalte 2: Summe Brutto Angestellte
		sumBruttoAngestellte = getSummeBruttoAngestellte(teamNR);
		sz.setColumn1(NumberFormat.getCurrencyInstance().format(sumBruttoAngestellte));
		sumBruttoAngestellteGesamt += sumBruttoAngestellte;
		
		// Spalte 3: Summe VZÄ Angestellte		
		sumVZAEAngestellte = getSummeVZAEAngestellte(teamNR);
		sz.setColumn2(String.valueOf(sumVZAEAngestellte));
		sumVZAEAngestellteGesamt += sumVZAEAngestellte;
		
		// Spalte 4: Summe Brutto Beamte		
		sumBruttoBeamte = getSummeBruttoBeamte(teamNR);
		sz.setColumn3(NumberFormat.getCurrencyInstance().format(sumBruttoBeamte));
		sumBruttoBeamteGesamt += sumBruttoBeamte;
		
		// Spalte 5: Summe VZÄ Beamte
		sumVZAEBeamte = getSummeVZAEBeamte(teamNR);
		sz.setColumn4(String.valueOf(sumVZAEBeamte));
		sumVZAEBeamteGesamt += sumVZAEBeamte;
		
		// Spalte 6: Gesamtsumme Brutto
		sz.setColumn5(NumberFormat.getCurrencyInstance().format((sumBruttoAngestellte + sumBruttoBeamte)));
		// Spalte 7: Gesamtsumme VZÄ		
		sz.setColumn6(String.valueOf((sumVZAEAngestellte + sumVZAEBeamte)));
		// Spalte 8: Abzug Vorkostenstellen
		sz.setColumn7(String.valueOf(getSummeVorkostenstellen(teamNR)));
		// Spalte 9: Summe Endkostenstellen
		sumEndkostenstelle = (sumVZAEAngestellte + sumVZAEBeamte) + getSummeVorkostenstellen(teamNR);
		sumEndkostenstellenGesamt += sumEndkostenstelle;
		sz.setColumn8(String.valueOf(sumEndkostenstelle));		
		
		summenTabelle.put(teamNR, sz);
		anzahlSummenZeilen += 1;
		}
	sz = new SummenZeile();
	sz.setLeerZeile();
	summenTabelle.put(anzahlSummenZeilen, sz);
	anzahlSummenZeilen += 1;
	
	// Gesamtsummenzeile	
	sz = new SummenZeile();
	sz.setColumn0("Summen:");
	sz.setColumn1(NumberFormat.getCurrencyInstance().format(sumBruttoAngestellteGesamt));
	sz.setColumn2(String.valueOf(sumVZAEAngestellteGesamt));
	sz.setColumn3(NumberFormat.getCurrencyInstance().format(sumBruttoBeamteGesamt));
	sz.setColumn4(String.valueOf(sumVZAEBeamteGesamt));
	sz.setColumn5(NumberFormat.getCurrencyInstance().format((sumBruttoAngestellteGesamt + sumBruttoBeamteGesamt)));
	sz.setColumn6(String.valueOf((sumVZAEAngestellteGesamt + sumVZAEBeamteGesamt)));
	sz.setColumn7("");
	sz.setColumn8(String.valueOf(sumEndkostenstellenGesamt));
	
	summenTabelle.put(anzahlSummenZeilen, sz);
	
	return summenTabelle;
	}
	
	public void setMitarbeiter(TreeMap<Integer, Mitarbeiter> mitarbeiter)
	{
	this.mitarbeiter = mitarbeiter;
			
	TreeMap<String, Arbeitszeitanteil> azvs = null;
	/*
	 * Wiewiel Prozentanteil hat der Mitarbeiter pro Team aufgewendet ?
	 */
	TreeMap<Integer, Integer> prozentanteileTeams = null;
	int teamNR = -1;
	int prozentanteil;
		/*
		 * Die Liste der gesamten Mitarbeiter wird durchlaufen.
		 * Die Mitarbeiter werden nach Teams gegliedert.
		 * 
		 * Gleichzeitig wird eine Liste mit allen Teams gebildet, für die AZV-Anteile gemeldet wurden
		 */
		for (Mitarbeiter m : mitarbeiter.values())
		{
		teamNR = m.getTeamNR();
			// Altersteilzeitler werden in ihr letztes aktives Team einsortiert:	
			switch (teamNR)
			{
			case 0: addTeamMitglied(teamNR, m); break;
			case 1: addTeamMitglied(teamNR, m); break;
			case 2: addTeamMitglied(teamNR, m); break;
			case 3: addTeamMitglied(teamNR, m); break;
			case 4: addTeamMitglied(teamNR, m); break;
			case 5: addTeamMitglied(teamNR, m); break;
			// Sonderfall Vorstand, wird auf Team 0 umgesetzt:
			case 9: 
			teamNR = 0;
			addTeamMitglied(teamNR, m); 
			break;
				
			default:
			teamNR = m.getLetzteTeamNR();	
			addTeamMitglied(teamNR, m);
			break;
			}	
		
		prozentanteileTeams = new TreeMap<Integer, Integer>();	
		azvs = m.getAzvMonat();	
			for (Arbeitszeitanteil azv : azvs.values())
			{
				if (prozentanteileTeams.containsKey(teamNR))
				{
				prozentanteil = prozentanteileTeams.get(teamNR);	
				prozentanteileTeams.put(teamNR, (prozentanteil + azv.getProzentanteil()));	
				}
				else
				{
				prozentanteileTeams.put(teamNR, azv.getProzentanteil());
				}			
			}
			
		}	
	}
	
	private void addTeamMitglied(int teamNR, Mitarbeiter m)
	{
		// Teamliste prüfen
		if (!teams.containsKey(teamNR))
		{
		teams.put(teamNR, new Team(teamNR));	
		}	
	teams.get(teamNR).addMitarbeiter(m);	
	}

	public Date getBerichtsMonat()
	{
	return berichtsMonat;
	}

public boolean isChecked(){return checked;}
public void setChecked(boolean checked){this.checked = checked;}	

public boolean isDatenOK(){return datenOK;}
public void setDatenOK(boolean ok){this.datenOK = ok;}	

}
