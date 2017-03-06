package de.hannit.fsch.klr.web.commands;

import de.hannit.fsch.common.MonatsSummen;
import de.hannit.fsch.klr.model.ICommand;
import de.hannit.fsch.klr.model.mitarbeiter.Mitarbeiter;

public class CreateCSV02Command implements ICommand
{
private MonatsSummen monatsSummen = null;
private final String TTT_ENABLED = "Erstellt die Buchungssätze für die Datei 01_Entlastung 0400 auf andere KST";
private final String TTT_DISABLED = "Nicht verfügbar, da die Monatssumme der Kostenstellen / Kostenträger nicht der Monatssumme der Vollzeitäquivalente entspricht.";
private String toolTipText = TTT_DISABLED;
private boolean disabled = true;

	public CreateCSV02Command() 
	{
	
	}

	@Override
	public void monatsSummenChanged(MonatsSummen incoming) 
	{
	setMonatsSummen(incoming);	
	}

	public void setDisabled(boolean toSet) 
	{
	this.disabled = toSet;
	}

	@Override
	public boolean getDisabled() 
	{	
		if (monatsSummen != null && monatsSummen.isChecked() && monatsSummen.getSummeOK() && monatsSummen.getMonatsNummer().equalsIgnoreCase("01"))
		{
		disabled = false;
		toolTipText = TTT_ENABLED;
		}
	return disabled;
	}

	public MonatsSummen getMonatsSummen() {return monatsSummen;}
	public void setMonatsSummen(MonatsSummen toSet) 
	{
	this.monatsSummen = toSet;
		if (monatsSummen != null && monatsSummen.isChecked() && monatsSummen.getSummeOK())
		{
		disabled = false;
		toolTipText = TTT_ENABLED;
		}
	}
	public String getToolTipText() {return toolTipText;}

	@Override
	public void mitarbeiterChanged(Mitarbeiter incoming) {}

	@Override
	public void setMitarbeiter(Mitarbeiter toSet) {}

	@Override
	public Mitarbeiter getMitarbeiter() {return null;}

}
