/**
 * 
 */
package de.hannit.fsch.klr.dataservice;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import de.hannit.fsch.klr.model.azv.AZVDaten;
import de.hannit.fsch.klr.model.azv.AZVDatensatz;
import de.hannit.fsch.klr.model.azv.Arbeitszeitanteil;
import de.hannit.fsch.klr.model.loga.LoGaDatei;
import de.hannit.fsch.klr.model.loga.LoGaDatensatz;
import de.hannit.fsch.klr.model.mitarbeiter.Mitarbeiter;
import de.hannit.fsch.klr.model.mitarbeiter.Tarifgruppen;
import de.hannit.fsch.klr.model.organisation.Organisation;
import de.hannit.fsch.klr.model.team.TeamMitgliedschaft;

/**
 * @author fsch
 * 
 * Abstakte Implementierung aller Datenbankfunktionen
 *
 */
public interface DataService 
{
public String getConnectionInfo();	
public ArrayList<Mitarbeiter> getMitarbeiter();
public TreeMap<Integer,Mitarbeiter> getMitarbeiterOhneAZV();
public boolean existsMitarbeiter(int personalNummer);
public SQLException updateMitarbeiter(Mitarbeiter toUpdate);
public SQLException deleteMitarbeiter(Mitarbeiter toDelete);

public ArrayList<Arbeitszeitanteil> getArbeitszeitanteile(int personalNummer);
public ArrayList<Arbeitszeitanteil> getArbeitszeitanteileMAXMonat(int personalNummer);
public ArrayList<Arbeitszeitanteil> getArbeitszeitanteile(int personalNummer, java.util.Date selectedMonth);
public String[] getPersonalnummern();
public Integer getPersonalnummer(String nachname);
public Integer getPersonalnummer(String nachname, String username);
public Integer getPersonalnummerbyUserName(String userName);
public TreeMap<Integer, Mitarbeiter> getAZVMonat(java.util.Date selectedMonth);
public TreeMap<Integer, String> getAZVMAXMonat();
public Organisation getOrganisation();
public String getTarifgruppeAushilfen(int personalNummer);
public Tarifgruppen getTarifgruppen(java.util.Date selectedMonth);
public SQLException setMitarbeiter(Mitarbeiter m);
public boolean existsPersonaldurchschnittskosten(java.util.Date selectedMonth);
public boolean existsKostenstelle(String kostenStelle);
public SQLException setKostenstelle(String kostenStelle, String kostenStellenBezeichnung);	
public boolean existsKostentraeger(String kostenTraeger);
public SQLException setKostentraeger(String kostenTraeger, String kostenTraegerBezeichnung);
public SQLException setPersonaldurchschnittskosten(int teamNR, java.util.Date datum, double bruttoAngestellte, double vzaeAngestellte, double bruttoBeamte, double vzaeBeamte, double abzugVorkostenstellen);
public SQLException deletePersonaldurchschnittskosten(java.util.Date datum);

public SQLException setErgebnis(String kostenArt, int teamNR, Date berichtsMonat, double ertrag, double materialAufwand, double afa, double sba, double personalkosten, double summeEinzelkosten, double deckungsbeitrag1, double verteilung1110, double verteilung2010, double verteilung2020, double verteilung3010, double verteilung4010, double verteilungGesamt, double ergebnis);
public boolean existsErgebnis(int teamNR, java.util.Date selectedMonth);
public SQLException deleteErgebnis(java.sql.Date berichtsMonat, int teamNR);

public SQLException setTeammitgliedschaften(AZVDaten azvDaten);
public SQLException setTeammitgliedschaft(int personalNummer, int teamNummer, Date startDatum);
public SQLException updateTeammitgliedschaft(int personalNummer, int teamNummerAlt, int teamNummerNeu, Date startDatum, Date endDatum);
public int getAktuellesTeam(int personalNummer);
public double getLetzterStellenanteil(int personalNummer);
public ArrayList<TeamMitgliedschaft> getTeamMitgliedschaften(int personalNummer);
public boolean existsTeammitgliedschaft(int personalNummer);

public boolean existsAZVDatensatz(int personalNummer, java.sql.Date berichtsMonat);
public SQLException saveAZVChanges(TreeMap<String, Arbeitszeitanteil> unsavedChanges);
public SQLException deleteAZVDaten(java.sql.Date datum);
public SQLException setAZVDaten(AZVDatensatz datenSatz);
public SQLException setAZVMonatsDaten(String kostenObjekt, String strDatum, double dSumme);
public SQLException setVZAEMonatsDaten(String strDatum, String strTarifgruppe, double dSummeTarifgruppe, double dSummeStellen, double dVZAE);

public SQLException setDatenimport(String name, String pfad, int anzahlDaten, Date berichtsMonat, String datenQuelle);

public void setMitarbeiter(ArrayList<String[]> fields);	
public boolean existsLoGaDatensatz(LoGaDatensatz toCheck);
public SQLException setLoGaDaten(LoGaDatensatz datenSatz);	
public SQLException updateLoGaDaten(LoGaDatei toUpdate);	
public SQLException setLoGaDaten(TreeMap<Integer, LoGaDatensatz> toInsert);	

public SQLException setCallcenterDaten(List<String> lines);
}
