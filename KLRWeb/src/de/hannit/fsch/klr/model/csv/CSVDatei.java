/**
 * 
 */
package de.hannit.fsch.klr.model.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.primefaces.model.UploadedFile;

/**
 * @author fsch
 * @since 14.06.2013
 *
 */
public class CSVDatei extends File 
{
/**
 * 
 */
private static final long serialVersionUID = 7390814932872973058L;
public static final String DEFAULT_DELIMITER = ";";
public static final String PATH_PRÄFIX = "C:\\temp\\";
public static final String DATEI_NICHTVORHANDEN = "Keine DateiInfo verfügbar.";


private List<String> lines;
/**
 * Felder aus gelesenen Dateien
 */
private ArrayList<String[]> fields = new ArrayList<String[]>();	
/**
 * Felder für zu schreibende Dateien
 */
private ArrayList<String> content = null;
/**
 * Felder für zu schreibende Dateien und evtl. bereits vorhandenen Dateien
 */
private ArrayList<String> completeContent = null;

protected String dateiInfo = "";
protected String dateiName = null;
protected Path dateiPfad = null;
protected boolean dateiVorhanden = false;


protected boolean hasHeader = true;
protected boolean errors = false;
protected boolean checked = false;
/**
 * Gibt an, ob die Daten erfolgreich gespeichert wurden.
 * Wenn der InsertHandler die Daten gespeichert hat, setzt er saved auf true
 * und versendet die Datei erneut über den Broker.
 * Der NavPart kann dann den Tree neu laden
 */
protected boolean saved = false;
protected String delimiter = ";";
protected int lineCount = -1;


	/**
	 * @param arg0
	 */
	public CSVDatei(String strPath) 
	{
	super(strPath);
	}

	/**
	 * @param arg0
	 */
	public CSVDatei(URI arg0) {super(arg0);}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public CSVDatei(String strPath, String strName) 
	{
	super(strPath, strName);
	this.dateiName = strName;
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public CSVDatei(File arg0, String arg1) {super(arg0, arg1);}
	
	public ArrayList<String> getCompleteContent() {return completeContent;}
	public ArrayList<String> getContent() {return content;}
	
	/*
	 * Fügt neue Zeilen zum Inhalt hinzu.
	 * Es ist möglich, das bereits Zeilen vorhanden sind. 
	 * In diesem Fall werden die vorhandenen Zeilen ergänzt. 
	 */
	public void setContent(ArrayList<String> incoming) 
	{
		if (completeContent != null)
		{
		completeContent.addAll(incoming);
		this.content = incoming;
		}
		else
		{
		this.content = incoming;
		}
	}

	/*
	 * Zieldatei vorhanden ?
	 */
	public boolean existsZielDatei()
	{
	return Files.exists(this.toPath(), new LinkOption[]{LinkOption.NOFOLLOW_LINKS});	
	}
	
	/*
	 * Prüft, ob das Zielverzeichnis existiert.
	 * Falls nicht, wird versucht, es zu erstellen
	 */
	public void createZielverzeichnis()
	{
	Path zielOrdner = this.toPath().getParent();

		if (Files.exists(zielOrdner, LinkOption.NOFOLLOW_LINKS))
		{
		//getLog().info("Verzeichnis " + zielOrdner.toString() + " wurde nicht angelegt, da es bereits existiert", this.getClass().getName() + ".createZielverzeichnis()");	
		}
		else
		{
			try
			{
			Files.createDirectory(zielOrdner);
			//log.confirm("Verzeichnis " + zielOrdner.toString() + " wurde erfolgreich angelegt.", this.getClass().getName() + ".createZielverzeichnis()");
			}
			catch (IOException e)
			{
			//log.error("Verzeichnis " + zielOrdner.toString() + " konnte nicht erstellt werden. !", this.getClass().getName() + ".createZielverzeichnis()", e);	
			e.printStackTrace();
			}	
		}
	}
	
	public void createCSVDatei(String dateiPfad, String dateiName)
	{
	Path testPath = FileSystems.getDefault().getPath(dateiPfad, dateiName);
		if (Files.exists(testPath, new LinkOption[]{LinkOption.NOFOLLOW_LINKS}))
		{
		//log.error("CSV-Datei " + testPath.toString() + " existiert bereits ! Bitte prüfen und ggf. manuell löschen.", this.getClass().getName() + ".createCSVDatei(String dateiPfad, String dateiName)", null);	
		}
		else
		{
			try
			{
			Files.createFile(testPath);
			Files.write(testPath, lines, Charset.forName("ISO-8859-15"), StandardOpenOption.WRITE);
			//log.confirm("Datei " + testPath.toString() + " wurde erfolgreich angelegt.", this.getClass().getName() + ".createCSVDatei(String dateiPfad, String dateiName)");
			}
			catch (IOException e)
			{
			//log.error("Datei " + testPath.toString() + " konnte nicht erstellt werden. !", this.getClass().getName() + ".createCSVDatei(String dateiPfad, String dateiName)", e);	
			e.printStackTrace();
			}	
		}
	}	

	public void append() 
	{
		if (!Files.exists(this.toPath(), new LinkOption[]{LinkOption.NOFOLLOW_LINKS}))
		{
		//log.error("CSV-Datei " + this.getPath() + " wurde nicht gefunden !", this.getClass().getName() + ".write()", null);	
		}
		else
		{
			try
			{
			Files.write(this.toPath(), getContent(), Charset.forName("ISO-8859-15"), StandardOpenOption.APPEND);
			//log.confirm("Datei " + this.getPath() + " wurde erfolgreich ergänzt.", this.getClass().getName() + ".write()");
			}
			catch (IOException e)
			{
			//log.error("Fehler beim Ergänzen der Datei " + this.toPath(), this.getClass().getName() + ".write()", e);	
			e.printStackTrace();
			}	
		}		
	}	
	
	public boolean write() 
	{
	return false;
	
	}
	
	public void read(UploadedFile file) 
	{
	lineCount = 1;

		try 
		{
		BufferedReader buffer = new BufferedReader(new InputStreamReader(file.getInputstream()));
		lines = buffer.lines().collect(Collectors.toCollection(ArrayList::new));			
		for (String line : lines)
			{
			switch (lineCount) 
			{
			// Erste Zeile wird nur verarbeitet wenn keine Kopfzeile vorhanden ist
			case 1:
				if (!hasHeader) 
				{
				fields.add(line.split(delimiter));	
				}
			break;

			default:
			fields.add(line.split(delimiter));
			break;
			}	
			lineCount++;
			}	
		} 
		catch (IOException e) 
		{
		e.printStackTrace();	
		}
	}
	
	public void read() 
	{
	
	}
	
	public boolean isChecked(){return checked;}
	public void setChecked(boolean checked){this.checked = checked;}
	
	public boolean hasErrors(){return errors;}
	public void setErrors(boolean errors){this.errors = errors;}

	public boolean isSaved(){return saved;}
	public void setSaved(boolean saved){this.saved = saved;}

	public void resetLineCount(){this.lineCount = 1;}
	
	public List<String> getLines() 
	{
	return lines;
	}

	public ArrayList<String[]> getFields() 
	{
		return fields;
	}

	public boolean isHeader() {
		return hasHeader;
	}

	public void hasHeader(boolean toSet) 
	{
	this.hasHeader = toSet;
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public int getLineCount() 
	{
		if (hasHeader) 
		{
		return (lineCount - 2);	
		} 
		else 
		{
		return (lineCount - 1);
		}
	}

	public void setLineCount(int lineCount) {this.lineCount = lineCount;}

	public String getDateiInfo() {return dateiInfo;}
	public void setDateiInfo(String dateiInfo) {this.dateiInfo = dateiInfo;}
	public boolean isDateiVorhanden() {return dateiVorhanden;}
	public void setDateiVorhanden(boolean dateiVorhanden) {this.dateiVorhanden = dateiVorhanden;}
	
	
}
