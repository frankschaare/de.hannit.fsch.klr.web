/**
 * 
 */
package de.hannit.fsch.klr.model;

import de.hannit.fsch.common.MonatsSummen;

/**
 * @author fsch
 * Intercace f�r alle Command Klassen
 *
 */
public interface ICommand 
{
public void monatsSummenChanged(MonatsSummen incoming);
public MonatsSummen getMonatsSummen();
public void setMonatsSummen(MonatsSummen toSet);
public boolean getDisabled();
public String getToolTipText();
}
