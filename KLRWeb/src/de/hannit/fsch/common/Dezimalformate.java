package de.hannit.fsch.common;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class Dezimalformate
{
public static final DecimalFormat DEFAULT = new DecimalFormat("0.00");
public static final DecimalFormat KOMMAZAHL = new DecimalFormat("#0.00"); 
public static final DecimalFormat DFBRUTTO = (DecimalFormat)NumberFormat.getNumberInstance(Locale.GERMAN);
}
