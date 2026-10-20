import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bson.Document;
import org.bson.types.ObjectId;

public class UseCase {

    private ObjectId id;
    private String titel;
    private String beschreibung;
    private String kategorie;
    private String branche;

    private final Map<String, String> stufen = new LinkedHashMap<>();
    private final Map<String, Integer> punkte = new LinkedHashMap<>();

    private int nutzwert;
    private String empfehlung;
    private Date erstelltAm;
    private Date geaendertAm;

    public UseCase() {
    }

    public UseCase(String titel, String beschreibung, String kategorie, String branche) {
        this.titel = titel;
        this.beschreibung = beschreibung;
        this.kategorie = kategorie;
        this.branche = branche;
    }

    public void setStufe(String kriterium, String stufe) {
        stufen.put(kriterium, stufe);
    }

    public String getStufe(String kriterium) {
        return stufen.get(kriterium);
    }

    public Document toDocument() {
        Document bewertung = new Document();
        for (Map.Entry<String, String> eintrag : stufen.entrySet()) {
            String kriterium = eintrag.getKey();
            Document einzel = new Document();
            einzel.append("stufe", eintrag.getValue());
            einzel.append("punkte", punkte.getOrDefault(kriterium, 0));
            bewertung.append(kriterium, einzel);
        }

        Document dokument = new Document();
        dokument.append("titel", titel);
        dokument.append("beschreibung", beschreibung);
        dokument.append("kategorie", kategorie);
        dokument.append("branche", branche);
        dokument.append("bewertung", bewertung);
        dokument.append("nutzwert", nutzwert);
        dokument.append("empfehlung", empfehlung);
        dokument.append("erstelltAm", erstelltAm);
        dokument.append("geaendertAm", geaendertAm);
        return dokument;
    }

    public static UseCase fromDocument(Document dokument) {
        UseCase useCase = new UseCase();
        useCase.id = dokument.getObjectId("_id");
        useCase.titel = dokument.getString("titel");
        useCase.beschreibung = dokument.getString("beschreibung");
        useCase.kategorie = dokument.getString("kategorie");
        useCase.branche = dokument.getString("branche");
        useCase.empfehlung = dokument.getString("empfehlung");

        Integer wert = dokument.getInteger("nutzwert");
        useCase.nutzwert = (wert == null) ? 0 : wert;

        useCase.erstelltAm = dokument.getDate("erstelltAm");
        useCase.geaendertAm = dokument.getDate("geaendertAm");

        Document bewertung = dokument.get("bewertung", Document.class);
        if (bewertung != null) {
            for (String kriterium : bewertung.keySet()) {
                Document einzel = bewertung.get(kriterium, Document.class);
                if (einzel == null) {
                    continue;
                }
                useCase.stufen.put(kriterium, einzel.getString("stufe"));
                Integer p = einzel.getInteger("punkte");
                useCase.punkte.put(kriterium, (p == null) ? 0 : p);
            }
        }
        return useCase;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public String getKategorie() {
        return kategorie;
    }

    public void setKategorie(String kategorie) {
        this.kategorie = kategorie;
    }

    public String getBranche() {
        return branche;
    }

    public void setBranche(String branche) {
        this.branche = branche;
    }

    public Map<String, String> getStufen() {
        return stufen;
    }

    public Map<String, Integer> getPunkte() {
        return punkte;
    }

    public int getNutzwert() {
        return nutzwert;
    }

    public void setNutzwert(int nutzwert) {
        this.nutzwert = nutzwert;
    }

    public String getEmpfehlung() {
        return empfehlung;
    }

    public void setEmpfehlung(String empfehlung) {
        this.empfehlung = empfehlung;
    }

    public Date getErstelltAm() {
        return erstelltAm;
    }

    public void setErstelltAm(Date erstelltAm) {
        this.erstelltAm = erstelltAm;
    }

    public Date getGeaendertAm() {
        return geaendertAm;
    }

    public void setGeaendertAm(Date geaendertAm) {
        this.geaendertAm = geaendertAm;
    }

    public String alsZeile() {
        return String.format("%-38s %-20s %3d  %s",
                kuerzen(titel, 38), kuerzen(kategorie, 20), nutzwert, empfehlung);
    }

    private String kuerzen(String text, int laenge) {
        if (text == null) {
            return "";
        }
        return (text.length() <= laenge) ? text : text.substring(0, laenge - 1) + ".";
    }

    @Override
    public String toString() {
        return titel + " (" + kategorie + ", " + branche + ") " + nutzwert + " Punkte";
    }
}