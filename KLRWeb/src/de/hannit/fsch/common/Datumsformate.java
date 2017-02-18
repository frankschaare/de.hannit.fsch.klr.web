package de.hannit.fsch.common;

import java.text.SimpleDateFormat;

public class Datumsformate
{
public static final SimpleDateFormat MILLISEKUNDEN = new SimpleDateFormat("HH:mm:ss:SS");
public static final SimpleDateFormat MONATLANG_JAHR = new SimpleDateFormat("MMMM yyyy");
public static final	SimpleDateFormat MONATLANG_PUNKT_JAHR = new SimpleDateFormat("MMMM.yyyy");
public static final	SimpleDateFormat MONATLANG = new SimpleDateFormat("MMMM");
public static final	SimpleDateFormat JAHR = new SimpleDateFormat("yyyy");
public static final SimpleDateFormat STANDARDFORMAT = new SimpleDateFormat( "dd.MM.yy" );
public static final SimpleDateFormat STANDARDFORMAT_JAHR_VIERSTELLIG = new SimpleDateFormat( "dd.MM.yyyy" );
public static final SimpleDateFormat STANDARDFORMAT_SQLSERVER = new SimpleDateFormat( "yyyy-MM-dd" );
}
