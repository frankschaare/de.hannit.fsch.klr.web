package de.hannit.fsch.klr.model;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Datumsformate
{
private static String datumsFormat = "dd.MM.yyyy";
public static DateTimeFormatter df = DateTimeFormatter.ofPattern(datumsFormat);
public static DateTimeFormatter dfDatumUhrzeit = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss").withLocale(Locale.GERMAN);
public static DateTimeFormatter DF_MONAT = DateTimeFormatter.ofPattern("MMMM");
public static DateTimeFormatter DF_JAHR = DateTimeFormatter.ofPattern("yyyy");
public static DateTimeFormatter DF_MONATJAHR = DateTimeFormatter.ofPattern("MMMM yyyy");
public static DateTimeFormatter DF_JAHRMONAT = DateTimeFormatter.ofPattern("yyyyMM");
public static DateTimeFormatter DF_JAHR_KURZ = DateTimeFormatter.ofPattern("yy");
public static DateTimeFormatter dfDatumUhrzeitMax = DateTimeFormatter.ofPattern("EEEE', 'dd. MMMM yyyy HH:mm").withLocale(Locale.GERMAN);
public static DateTimeFormatter dfUhrzeit = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss").withLocale(Locale.GERMAN);
public static DateTimeFormatter dfStunde = DateTimeFormatter.ofPattern("HH").withLocale(Locale.GERMAN);
public static DateTimeFormatter dfStundeMinute = DateTimeFormatter.ofPattern("HH:ss").withLocale(Locale.GERMAN);
	
// Formate für alte Java Date API	
public static final SimpleDateFormat MONATLANG_JAHR = new SimpleDateFormat("MMMM yyyy");
public static final	SimpleDateFormat MONATLANG_PUNKT_JAHR = new SimpleDateFormat("MMMM.yyyy");
public static final	SimpleDateFormat MONATLANG = new SimpleDateFormat("MMMM");
public static final	SimpleDateFormat JAHR = new SimpleDateFormat("yyyy");
public static final SimpleDateFormat STANDARDFORMAT = new SimpleDateFormat( "dd.MM.yy" );
public static final SimpleDateFormat STANDARDFORMAT_JAHR_VIERSTELLIG = new SimpleDateFormat( "dd.MM.yyyy" );
public static final SimpleDateFormat STANDARDFORMAT_SQLSERVER = new SimpleDateFormat( "yyyy-MM-dd" );
}
