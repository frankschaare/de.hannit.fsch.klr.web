package de.hannit.fsch.util;

import java.time.format.DateTimeFormatter;

public class StringUtility 
{
public static DateTimeFormatter DF_MONAT = DateTimeFormatter.ofPattern("MMMM");
public static DateTimeFormatter DF_JAHR = DateTimeFormatter.ofPattern("yyyy");	
public static DateTimeFormatter DF_MONATJAHR = DateTimeFormatter.ofPattern("MMMM yyyy");	
	
	private StringUtility() {}
	
	/*
	 * Löscht die letzten beiden Zeichen eines Strings.
	 * Wird häufig benötigt, um aus Listen generierte Strings aufzuhübschen
	 */
	public static String removeLast2Char(String str) 
	{
    return str.substring(0,str.length()-2);
    }
	 
}
