/**
 * 
 */
package de.hannit.fsch.klr.model.team;

import java.sql.Date;

/**
 * @author fsch
 * @since 25.03.2014
 */
public class TeamMitgliedschaft
{
private int personalNummer = -1;
private int teamNummer = -1;
private Date sqlStartdatum = null;
private Date sqlEnddatum = null;

	/**
	 * Ein Mitarbeiter hat mindestens eine (die aktuelle) oder 
	 * mehrere Teammitgliedschaften, z.b. wenn das Team gewechselt wurde, 
	 * oder die passive Phase der Altersteilzeit begonnen hat
	 */
	public TeamMitgliedschaft()
	{
		// TODO Auto-generated constructor stub
	}

	public int getPersonalNummer()
	{
		return personalNummer;
	}

	public void setPersonalNummer(int personalNummer)
	{
		this.personalNummer = personalNummer;
	}

	public int getTeamNummer()
	{
		return teamNummer;
	}

	public void setTeamNummer(int teamNummer)
	{
		this.teamNummer = teamNummer;
	}

	public Date getSqlStartdatum()
	{
		return sqlStartdatum;
	}

	public void setSqlStartdatum(Date sqlStartdatum)
	{
		this.sqlStartdatum = sqlStartdatum;
	}

	public Date getSqlEnddatum()
	{
		return sqlEnddatum;
	}

	public void setSqlEnddatum(Date sqlEnddatum)
	{
		this.sqlEnddatum = sqlEnddatum;
	}

	
}
