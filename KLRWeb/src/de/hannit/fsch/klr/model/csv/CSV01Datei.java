/**
 * 
 */
package de.hannit.fsch.klr.model.csv;

import java.io.File;
import java.net.URI;

/**
 * @author fsch
 *
 */
public class CSV01Datei extends CSVDatei
{
private static final long serialVersionUID = -1532024416411533051L;

public static final String ZELLE2_NEHME = "0";
public static final String ZELLE2_GEBE = "1";
public static final String ZELLE4_NEHME = "1100100";
public static final String ZELLE4_GEBE = "1110100";
public static final String ZELLE5_PRÄFIX = "UML-";
public static final String ZELLE6_PRÄFIX = "AZV ";
public static final String ENTLASTUNGSKONTO = "0400";
//public static final String PATH_PRÄFIX = "\\\\RegionHannover.de\\daten\\hannit\\Rechnungswesen AöR\\KLR\\Arbeitszeitverteilung\\Reports\\";
public static final String PATH_SUFFIX = "\\CSV\\";
public static final String DATEINAME_PRÄFIX = "01_CSV_Entlastung 0400 auf andere KST";
public static final String DATEINAME_SUFFIX = ".csv";	

	/**
	 * @param arg0
	 */
	public CSV01Datei(String strPath)
	{
	super(strPath, (CSV01Datei.DATEINAME_PRÄFIX + CSV01Datei.DATEINAME_SUFFIX));
	}

	/**
	 * @param arg0
	 */
	public CSV01Datei(URI arg0)
	{
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public CSV01Datei(String arg0, String arg1)
	{
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public CSV01Datei(File arg0, String arg1)
	{
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

}
