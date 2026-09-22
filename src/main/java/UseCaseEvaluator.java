import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class UseCaseEvaluator {

    private final Map<String, Map<String, Integer>> punktetabelle;

    public UseCaseEvaluator() {
        this.punktetabelle = standardTabelle();
    }

    public UseCaseEvaluator(Map<String, Map<String, Integer>> punktetabelle) {
        this.punktetabelle = punktetabelle;
    }

    public UseCase bewerte(UseCase useCase) {
        int summe = 0;
        useCase.getPunkte().clear();

        for (Map.Entry<String, String> eintrag : useCase.getStufen().entrySet()) {
            int punkte = punkteFuer(eintrag.getKey(), eintrag.getValue());
            useCase.getPunkte().put(eintrag.getKey(), punkte);
            summe = summe + punkte;
        }

        useCase.setNutzwert(summe);
        useCase.setEmpfehlung(empfehlungFuer(summe));
        return useCase;
    }

    public int punkteFuer(String kriterium, String stufe) {
        Map<String, Integer> stufenPunkte = punktetabelle.get(normiere(kriterium));
        if (stufenPunkte == null || stufe == null) {
            return 0;
        }
        for (Map.Entry<String, Integer> eintrag : stufenPunkte.entrySet()) {
            if (eintrag.getKey().equalsIgnoreCase(stufe.trim())) {
                return eintrag.getValue();
            }
        }
        return 0;
    }

    public String empfehlungFuer(int nutzwert) {
        if (nutzwert >= 80) {
            return "KI sinnvoll";
        }
        if (nutzwert >= 60) {
            return "KI als Assistenz sinnvoll";
        }
        if (nutzwert >= 40) {
            return "Klassische Lösung empfohlen";
        }
        return "Manuelle Bearbeitung empfohlen";
    }

    public List<String> kriterien() {
        return new ArrayList<>(punktetabelle.keySet());
    }

    public List<String> stufenFuer(String kriterium) {
        Map<String, Integer> stufenPunkte = punktetabelle.get(normiere(kriterium));
        if (stufenPunkte == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(stufenPunkte.keySet());
    }

    public int maximalPunkte() {
        int max = 0;
        for (Map<String, Integer> stufenPunkte : punktetabelle.values()) {
            int hoechste = 0;
            for (int punkte : stufenPunkte.values()) {
                if (punkte > hoechste) {
                    hoechste = punkte;
                }
            }
            max = max + hoechste;
        }
        return max;
    }

    public static Map<String, Map<String, Integer>> standardTabelle() {
        Map<String, Map<String, Integer>> tabelle = new LinkedHashMap<>();

        Map<String, Integer> datenmenge = new LinkedHashMap<>();
        datenmenge.put("Sehr hoch", 20);
        datenmenge.put("Hoch", 15);
        datenmenge.put("Mittel", 10);
        datenmenge.put("Niedrig", 5);
        datenmenge.put("Sehr niedrig", 0);
        tabelle.put("datenmenge", datenmenge);

        Map<String, Integer> datenstruktur = new LinkedHashMap<>();
        datenstruktur.put("Unstrukturiert", 20);
        datenstruktur.put("Teilstrukturiert", 15);
        datenstruktur.put("Strukturiert", 5);
        datenstruktur.put("Stark regelbasiert", 0);
        tabelle.put("datenstruktur", datenstruktur);

        Map<String, Integer> automatisierung = new LinkedHashMap<>();
        automatisierung.put("Sehr hoch", 20);
        automatisierung.put("Hoch", 15);
        automatisierung.put("Mittel", 10);
        automatisierung.put("Niedrig", 5);
        automatisierung.put("Kein Potenzial", 0);
        tabelle.put("automatisierungspotenzial", automatisierung);

        Map<String, Integer> risiko = new LinkedHashMap<>();
        risiko.put("Sehr gering", 20);
        risiko.put("Gering", 15);
        risiko.put("Mittel", 10);
        risiko.put("Hoch", 5);
        risiko.put("Sehr hoch", 0);
        tabelle.put("risiko", risiko);

        Map<String, Integer> fehlertoleranz = new LinkedHashMap<>();
        fehlertoleranz.put("Sehr hoch", 20);
        fehlertoleranz.put("Hoch", 15);
        fehlertoleranz.put("Mittel", 10);
        fehlertoleranz.put("Gering", 5);
        fehlertoleranz.put("Keine", 0);
        tabelle.put("fehlertoleranz", fehlertoleranz);

        return tabelle;
    }

    private static String normiere(String wert) {
        if (wert == null) {
            return "";
        }
        return wert.trim().toLowerCase(Locale.ROOT);
    }
}