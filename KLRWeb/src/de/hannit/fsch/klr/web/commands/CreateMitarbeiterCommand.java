package de.hannit.fsch.klr.web.commands;

import de.hannit.fsch.common.MonatsSummen;
import de.hannit.fsch.klr.model.ICommand;
import de.hannit.fsch.klr.model.mitarbeiter.Mitarbeiter;

public class CreateMitarbeiterCommand implements ICommand 
{
private final String TTT_ENABLED = "Erstellt oder ändert ";
private String toolTipText = TTT_ENABLED;	
private Mitarbeiter mitarbeiter = new Mitarbeiter();	

	public CreateMitarbeiterCommand() 
	{
	toolTipText = TTT_ENABLED;	
	}

	@Override
	public void mitarbeiterChanged(Mitarbeiter incoming) 
	{
	mitarbeiter = incoming;
	}

	@Override
	public void setMitarbeiter(Mitarbeiter toSet) {this.mitarbeiter = toSet;}

	@Override
	public Mitarbeiter getMitarbeiter() {return mitarbeiter;}

	@Override
	public void monatsSummenChanged(MonatsSummen incoming) {}

	@Override
	public MonatsSummen getMonatsSummen() {return null;}

	@Override
	public void setMonatsSummen(MonatsSummen toSet) {}

	@Override
	public boolean getDisabled() 
	{
	return false;
	}

	@Override
	public String getToolTipText() 
	{
	String suffix = mitarbeiter != null ? "Mitarbeiterdaten für Personalnummer " + mitarbeiter.getPersonalNR() : "Mitarbeiterdaten";	
	return toolTipText + suffix;
	}

}
