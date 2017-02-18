/**
 * 
 */
package de.hannit.fsch.klr.model.team;

import java.util.TreeMap;

import de.hannit.fsch.klr.model.mitarbeiter.Mitarbeiter;

/**
 * @author fsch
 *
 */
public class Team
{
private int teamNummer = -1;
private String teamBezeichnung = null;
private TreeMap<String, Mitarbeiter> teamMitglieder = new TreeMap<String, Mitarbeiter>();
private TreeMap<Integer, Mitarbeiter> angestellte = new TreeMap<Integer, Mitarbeiter>();
private TreeMap<Integer, Mitarbeiter> beamte = new TreeMap<Integer, Mitarbeiter>();
private TreeMap<Integer, Mitarbeiter> mitarbeitAufVorkostenstellen = new TreeMap<Integer, Mitarbeiter>();
private TreeMap<Integer, Mitarbeiter> mitarbeitAltersteilzeit = new TreeMap<Integer, Mitarbeiter>();

	/**
	 * 
	 */
	public Team(int nummer)
	{
	setTeamNummer(nummer);	
	}

	public void addMitarbeiter(Mitarbeiter m)
	{
	teamMitglieder.put(m.getNachname(), m);
	
		switch (m.getStatus())
		{
		case Mitarbeiter.STATUS_BEAMTER:
		beamte.put(m.getPersonalNR(), m);	
		break;
		
		case Mitarbeiter.STATUS_ALTERSTEILZEIT_BEAMTE:
		beamte.put(m.getPersonalNR(), m);
		mitarbeitAufVorkostenstellen.put(m.getPersonalNR(), m);
		break;

		case Mitarbeiter.STATUS_ALTERSTEILZEIT_ANGESTELLTE:
		angestellte.put(m.getPersonalNR(), m);
		mitarbeitAufVorkostenstellen.put(m.getPersonalNR(), m);
		break;

		
		default:
		angestellte.put(m.getPersonalNR(), m);	
		break;
		}
		/*
		 * Für die Teamnummern 0 und 1 werden alle Mitarbeiter auf Vorkostenstellen gebucht.
		 * Sie werden daher zusätzlich in der Liste mitarbeitAufVorkostenstellen gesichert
		 * und später bei der Berechnung herausgenommen.
		 */
		switch (this.teamNummer)
		{
		case 0:
		mitarbeitAufVorkostenstellen.put(m.getPersonalNR(), m);	
		break;

		case 1:
		mitarbeitAufVorkostenstellen.put(m.getPersonalNR(), m);	
		break;

		default:
		break;
		}
	}
	
	public TreeMap<String, Mitarbeiter> getTeamMitglieder(){return teamMitglieder;}
	public TreeMap<Integer, Mitarbeiter> getMitarbeitAufVorkostenstellen(){return mitarbeitAufVorkostenstellen;}

	public String getOE()
	{
	String strOE = null;
		switch (getTeamNummer())
		{
		case 0:
		strOE = "Geschäftsführung";	
		break;

		default:
		strOE = "81.01.0" + String.valueOf(getTeamNummer());	
		break;
		}
	return strOE;
	}
	
	public int getTeamNummer(){	return teamNummer;}
	
	public String getTeamBezeichnung(){return this.teamBezeichnung;}
	public void setTeamNummer(int teamNummer)
	{
	this.teamNummer = teamNummer;
	
		switch (teamNummer)
		{
		case 0:
		this.teamBezeichnung = "Vorstand";	
		break;
		case 9:
		this.teamBezeichnung = "Vorstand";	
		break;

		default:
		this.teamBezeichnung = "Team " + String.valueOf(teamNummer);	
		break;
		}
	}

	public TreeMap<Integer, Mitarbeiter> getAngestellte()
	{
		return angestellte;
	}

	public void setAngestellte(TreeMap<Integer, Mitarbeiter> angestellte)
	{
		this.angestellte = angestellte;
	}

	public TreeMap<Integer, Mitarbeiter> getBeamte()
	{
		return beamte;
	}

	public void setBeamte(TreeMap<Integer, Mitarbeiter> beamte)
	{
		this.beamte = beamte;
	}

	
}
