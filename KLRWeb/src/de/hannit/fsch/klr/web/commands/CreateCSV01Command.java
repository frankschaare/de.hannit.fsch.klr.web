package de.hannit.fsch.klr.web.commands;

import de.hannit.fsch.common.MonatsSummen;
import de.hannit.fsch.klr.model.ICommand;

public class CreateCSV01Command implements ICommand
{
private MonatsSummen monatsSummen = null;
private final String TTT_ENABLED = "Erstellt die Buchungss�tze f�r die Datei 01_Entlastung 0400 auf andere KST";
private final String TTT_DISABLED = "Nicht verf�gbar, da die Monatssumme der Kostenstellen / Kostentr�ger nicht der Monatssumme der Vollzeit�quivalente entspricht.";
private String toolTipText = TTT_DISABLED;

	public CreateCSV01Command() 
	{
	
	}

	@Override
	public void monatsSummenChanged(MonatsSummen incoming) 
	{
	setMonatsSummen(incoming);	
	}

	@Override
	public boolean getDisabled() 
	{
	boolean disabled = true;
	toolTipText = TTT_DISABLED;
		
		if (monatsSummen != null && monatsSummen.isChecked() && monatsSummen.getSummeOK())
		{
		disabled = false;
		toolTipText = TTT_ENABLED;
		}
	return disabled;
	}

	public MonatsSummen getMonatsSummen() {return monatsSummen;}
	public void setMonatsSummen(MonatsSummen toSet) {this.monatsSummen = toSet;}
	public String getToolTipText() {return toolTipText;}
	
	

}
