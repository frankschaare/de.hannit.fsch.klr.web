/**
 * 
 */
package de.hannit.fsch.klr.model;

import de.hannit.fsch.common.MonatsSummen;
import de.hannit.fsch.klr.model.mitarbeiter.Mitarbeiter;

/**
 * @author fsch
 * Intercace für alle Command Klassen
 *
 */
public interface ICommand 
{
public void mitarbeiterChanged(Mitarbeiter incoming);	
public void setMitarbeiter(Mitarbeiter toSet);
public Mitarbeiter getMitarbeiter(); 
public void monatsSummenChanged(MonatsSummen incoming);
public MonatsSummen getMonatsSummen();
public void setMonatsSummen(MonatsSummen toSet);
public boolean getDisabled();
public String getToolTipText();
}
