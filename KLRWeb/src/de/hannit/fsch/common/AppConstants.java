/**
 * 
 */
package de.hannit.fsch.common;

import java.text.DecimalFormat;

/**
 * @author fsch
 *
 */
public class AppConstants 
{
public static final String ORGANISATION = "HANNIT";
public static final String LOGGER = "LOGGER";
public static final String LOG_STACK = "LOGSTACK";
public static final String CONTEXT_TARIFGRUPPEN = "cTARIFGRUPPEN";
public static final String CONTEXT_MONATSSUMMEN = "cMonatssummen";
public static final String CONTEXT_PERSONALDURCHSCHNITTSKOSTEN = "cPDK";
public static final String CONTEXT_CSV01 = "cCSV01";
public static final String CONTEXT_GEMEINKOSTEN = "cGK";
public static final String CONTEXT_ERGEBNIS = "cERGEBNIS";
public static final String CONTEXT_SELECTED_MITARBEITER = "selectedMitarbeiter";
public static final DecimalFormat KOMMAZAHL = new DecimalFormat("0.00");

public static final String ENDKOSTENSTELLE_TEAM1 = "1110";
public static final String ENDKOSTENSTELLE_TEAM2 = "2010";
public static final String ENDKOSTENSTELLE_TEAM3 = "3010";
public static final String ENDKOSTENSTELLE_TEAM4 = "4010";
public static final String KOSTENSTELLE_AUSBILDUNG = "1060";
public static final String KOSTENSTELLE_AUSBILDUNG_BESCHREIBUNG = "Ausbildung";
public static final String KOSTENSTELLE_SERVICEDESK = "5015";
public static final String KOSTENSTELLE_SERVICEDESK_BESCHREIBUNG = "ServiceDesk";
public static final String AZV_ADDROW = "addRow";

public static final String TEAM1 = "Team 1";
public static final String TEAM2 = "Team 2";
public static final String TEAM3 = "Team 3";
public static final String TEAM4 = "Team 4";
public static final String TEAM5 = "Team 5";
public static final int INTEGER_TEAM_AUSZUBILDENDE = 1;

public static interface ActiveSelections
{
public static final String AUSWERTUNGSMONAT = "AUSWERTUNGSZEITRAUM/MONAT";
public static final String MONATSBERICHT = "MONATSBERICHT";
public static final String PART_GEMEINKOSTEN = "de.hannit.fsch.rcp.klr.partdescriptor.csv.gemeinkosten";
}

public static interface PartIDs
{
public static final String NAVPART = "de.hannit.fsch.rcp.klr.parts.NavPart";	
public static final String CSV01 = "de.hannit.fsch.rcp.klr.partdescriptor.csv.csv01";
public static final String AZVPART = "de.hannit.fsch.rcp.klr.partdescriptor.azv";
public static final String AZVWEBSERVICEPART = "de.hannit.fsch.rcp.klr.partdescriptor.azv.webservice";
}
}
