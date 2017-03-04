package de.hannit.fsch.util;

import java.time.format.DateTimeFormatter;

public class StringUtility 
{
public static DateTimeFormatter DF_MONAT = DateTimeFormatter.ofPattern("MMMM");
public static DateTimeFormatter DF_JAHR = DateTimeFormatter.ofPattern("yyyy");	
public static DateTimeFormatter DF_MONATJAHR = DateTimeFormatter.ofPattern("MMMM yyyy");	
	
	private StringUtility() {}
	
	/*
	 * L�scht die letzten beiden Zeichen eines Strings.
	 * Wird h�ufig ben�tigt, um aus Listen generierte Strings aufzuh�bschen
	 */
	public static String removeLast2Char(String str) 
	{
    return str.substring(0,str.length()-2);
    }
	 
}
