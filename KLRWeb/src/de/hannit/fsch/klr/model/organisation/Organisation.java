/**
 * 
 */
package de.hannit.fsch.klr.model.organisation;

import java.util.Date;
import java.util.TreeMap;

import de.hannit.fsch.klr.model.kostenrechnung.KostenStelle;
import de.hannit.fsch.klr.model.kostenrechnung.KostenTraeger;
import de.hannit.fsch.klr.model.mitarbeiter.Mitarbeiter;
import de.hannit.fsch.klr.model.mitarbeiter.Vorstand;
import de.hannit.fsch.klr.model.team.Team;


/**
 * @author fsch
 *
 */
public class Organisation implements IOrganisation
{
private final String name = "Hannoversche Informationstechnologien";
private TreeMap<Date, Monatsbericht> monatsBerichte = new TreeMap<Date, Monatsbericht>(); 

private TreeMap<Integer, Mitarbeiter> mitarbeiterPNR = null;
private TreeMap<String, Mitarbeiter> mitarbeiterNachname = null;
private TreeMap<Integer, Team> teams = null;
private TreeMap<Integer, KostenStelle> kostenstellen = null;
private TreeMap<Integer, KostenTraeger> kostentraeger = null;

private Vorstand vorstand = null;

	/**
	 * 
	 */
	public Organisation()
	{
	vorstand = new Vorstand();	
	}
	
	public TreeMap<Date, Monatsbericht> getMonatsBerichte()	{return monatsBerichte;}
	public void setMonatsBerichte(TreeMap<Date, Monatsbericht> monatsBerichte) {this.monatsBerichte = monatsBerichte;}

	/* (non-Javadoc)
	 * @see de.hannit.fsch.common.organisation.IOrganisation#getName()
	 */
	@Override
	public String getName() {return this.name;}

	@Override
	public void setMitarbeiter(TreeMap<Integer, Mitarbeiter> incoming)
	{
	this.mitarbeiterPNR = incoming;	
	mitarbeiterNachname = new TreeMap<String, Mitarbeiter>();	
	teams = new TreeMap<Integer, Team>();
	int teamNR = -1;
	
		for (Mitarbeiter mPNR : mitarbeiterPNR.values())
		{
		mitarbeiterNachname.put(mPNR.getNachname() + ", " + mPNR.getVorname() , mPNR);
		
			if (mPNR.getStatus() == Mitarbeiter.STATUS_ALTERSTEILZEIT_ANGESTELLTE || mPNR.getStatus() == Mitarbeiter.STATUS_ALTERSTEILZEIT_BEAMTE)
			{
			teamNR = 7;	
			}
			else
			{
			teamNR = mPNR.getTeamNR();
			}
			if (! teams.containsKey(teamNR))
			{
			Team team = new Team(teamNR);
			teams.put(teamNR, team);
			}
		teams.get(teamNR).addMitarbeiter(mPNR);
		}
	}

	@Override
	public TreeMap<String, Mitarbeiter> getMitarbeiterNachName(){return this.mitarbeiterNachname;}
	@Override
	public TreeMap<Integer, Mitarbeiter> getMitarbeiterNachPNR() {return this.mitarbeiterPNR;}

	@Override
	public TreeMap<Integer, Team> getTeams()
	{
	return teams;
	}

	@Override
	public String[] getComboValuesKostenstellen()
	{
	String[] result;
		if (this.kostenstellen != null)
		{
		result = new String[this.kostenstellen.size()];	
			for (int i = 0; i < this.kostenstellen.size(); i++)
			{
			result[i] = kostenstellen.get(i).getBezeichnung() + ": " + kostenstellen.get(i).getBeschreibung();
			}
		}
		else
		{
		result = new String[]{"error"};	
		}
	return result;
	}

	@Override
	public void setKostenstellen(TreeMap<Integer, KostenStelle> kostenstellen)
	{
	this.kostenstellen = kostenstellen;	
	}

	@Override
	public void setKostentraeger(TreeMap<Integer, KostenTraeger> kostentraeger)
	{
	this.kostentraeger = kostentraeger;	
	}

	@Override
	public TreeMap<Integer, KostenTraeger> getKostentraeger()
	{
	return this.kostentraeger;
	}

	@Override
	public TreeMap<Integer, KostenStelle> getKostenStellen()
	{
	return this.kostenstellen;
	}

	@Override
	public Vorstand getVorstand()
	{
	return vorstand;
	}

}
