import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {

    private static final Scanner EINGABE = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println();
        System.out.println("AI Use Case Evaluator");
        System.out.println("=====================");

        try (MongoService service = new MongoService()) {
            System.out.println("Verbindung steht. Gespeicherte Use Cases: " + service.anzahl());

            boolean laeuft = true;
            while (laeuft) {
                zeigeMenue();
                String wahl = zeileLesen("Auswahl: ");

                try {
                    switch (wahl) {
                        case "1" -> erfassen(service);
                        case "2" -> rangliste(service);
                        case "3" -> details(service);
                        case "4" -> bearbeiten(service);
                        case "5" -> loeschen(service);
                        case "6" -> vergleichen(service);
                        case "7" -> filtern(service);
                        case "0" -> laeuft = false;
                        default -> System.out.println("Unbekannte Auswahl.");
                    }
                } catch (Exception fehler) {
                    System.out.println();
                    System.out.println("Aktion fehlgeschlagen: " + fehler.getMessage());
                }
            }
        } catch (Exception fehler) {
            System.out.println();
            System.out.println("Fehler: " + fehler.getMessage());
            System.out.println("Verbindung pruefen: lokal mit Get-Service MongoDB, bei Atlas Netzwerk und Zugangsdaten.");
        }

        System.out.println("Programm beendet.");
    }

    private static void zeigeMenue() {
        System.out.println();
        System.out.println("--------------------------------------");
        System.out.println(" 1  Use Case erfassen");
        System.out.println(" 2  Alle anzeigen (Rangliste)");
        System.out.println(" 3  Details anzeigen");
        System.out.println(" 4  Bewertung bearbeiten");
        System.out.println(" 5  Use Case loeschen");
        System.out.println(" 6  Zwei Use Cases vergleichen");
        System.out.println(" 7  Nach Empfehlung filtern");
        System.out.println(" 0  Beenden");
        System.out.println("--------------------------------------");
    }

    private static void erfassen(MongoService service) {
        System.out.println();
        System.out.println("Neuen Use Case erfassen");

        String titel = zeileLesen("Titel: ");
        if (titel.isBlank()) {
            System.out.println("Abgebrochen, Titel ist ein Pflichtfeld.");
            return;
        }

        UseCase useCase = new UseCase(
                titel,
                pflichtfeldLesen("Beschreibung: "),
                pflichtfeldLesen("Kategorie: "),
                pflichtfeldLesen("Branche: "));

        UseCaseEvaluator evaluator = service.getEvaluator();
        for (String kriterium : evaluator.kriterien()) {
            useCase.setStufe(kriterium, stufeAbfragen(evaluator, kriterium));
        }

        service.speichern(useCase);

        System.out.println();
        System.out.println("Gespeichert. Nutzwert: " + useCase.getNutzwert()
                + " von " + evaluator.maximalPunkte()
                + "  ->  " + useCase.getEmpfehlung());
    }

    private static String stufeAbfragen(UseCaseEvaluator evaluator, String kriterium) {
        List<String> stufen = evaluator.stufenFuer(kriterium);

        System.out.println();
        System.out.println(kriterium + ":");
        for (int i = 0; i < stufen.size(); i++) {
            String stufe = stufen.get(i);
            System.out.println("  " + (i + 1) + ") " + stufe
                    + "  [" + evaluator.punkteFuer(kriterium, stufe) + " Punkte]");
        }

        while (true) {
            int wahl = zahlLesen("Auswahl 1-" + stufen.size() + ": ");
            if (wahl >= 1 && wahl <= stufen.size()) {
                return stufen.get(wahl - 1);
            }
            System.out.println("Bitte eine Zahl zwischen 1 und " + stufen.size() + " eingeben.");
        }
    }

    private static void rangliste(MongoService service) {
        zeigeListe(service.alle());
    }

    private static void zeigeListe(List<UseCase> liste) {
        System.out.println();
        if (liste.isEmpty()) {
            System.out.println("Noch keine Use Cases erfasst.");
            return;
        }

        System.out.printf("%-4s %-38s %-20s %3s  %s%n", "Nr", "Titel", "Kategorie", "Pkt", "Empfehlung");
        System.out.println("-".repeat(100));
        for (int i = 0; i < liste.size(); i++) {
            System.out.printf("%-4d %s%n", i + 1, liste.get(i).alsZeile());
        }
        System.out.println("-".repeat(100));
        System.out.println(liste.size() + " Use Cases");
    }

    private static void details(MongoService service) {
        UseCase useCase = auswaehlen(service, "Nummer fuer Details: ");
        if (useCase != null) {
            zeigeDetails(useCase);
        }
    }

    private static void zeigeDetails(UseCase useCase) {
        System.out.println();
        System.out.println("Titel:        " + useCase.getTitel());
        System.out.println("Beschreibung: " + useCase.getBeschreibung());
        System.out.println("Kategorie:    " + useCase.getKategorie());
        System.out.println("Branche:      " + useCase.getBranche());
        System.out.println();
        System.out.println("Bewertung:");
        for (Map.Entry<String, String> eintrag : useCase.getStufen().entrySet()) {
            String kriterium = eintrag.getKey();
            System.out.printf("  %-28s %-20s %3d Punkte%n",
                    kriterium, eintrag.getValue(), useCase.getPunkte().getOrDefault(kriterium, 0));
        }
        System.out.println();
        System.out.println("Nutzwert:   " + useCase.getNutzwert());
        System.out.println("Empfehlung: " + useCase.getEmpfehlung());
    }

    private static void bearbeiten(MongoService service) {
        UseCase useCase = auswaehlen(service, "Nummer zum Bearbeiten: ");
        if (useCase == null) {
            return;
        }

        UseCaseEvaluator evaluator = service.getEvaluator();
        List<String> kriterien = evaluator.kriterien();

        System.out.println();
        System.out.println("Welches Kriterium soll geaendert werden?");
        for (int i = 0; i < kriterien.size(); i++) {
            String kriterium = kriterien.get(i);
            System.out.println("  " + (i + 1) + ") " + kriterium
                    + "  (aktuell: " + useCase.getStufe(kriterium) + ")");
        }

        int wahl = zahlLesen("Auswahl 1-" + kriterien.size() + ": ");
        if (wahl < 1 || wahl > kriterien.size()) {
            System.out.println("Abgebrochen.");
            return;
        }

        String kriterium = kriterien.get(wahl - 1);
        useCase.setStufe(kriterium, stufeAbfragen(evaluator, kriterium));

        if (service.aktualisieren(useCase)) {
            System.out.println();
            System.out.println("Aktualisiert. Neuer Nutzwert: " + useCase.getNutzwert()
                    + "  ->  " + useCase.getEmpfehlung());
        } else {
            System.out.println("Nichts geaendert.");
        }
    }

    private static void loeschen(MongoService service) {
        UseCase useCase = auswaehlen(service, "Nummer zum Loeschen: ");
        if (useCase == null) {
            return;
        }

        String antwort = zeileLesen("\"" + useCase.getTitel() + "\" wirklich loeschen? (j/n): ");
        if (!antwort.equalsIgnoreCase("j")) {
            System.out.println("Abgebrochen.");
            return;
        }

        if (service.loeschen(useCase.getId())) {
            System.out.println("Geloescht.");
        } else {
            System.out.println("Konnte nicht geloescht werden.");
        }
    }

    private static void vergleichen(MongoService service) {
        List<UseCase> liste = service.alle();
        if (liste.size() < 2) {
            System.out.println();
            System.out.println("Fuer einen Vergleich braucht es mindestens zwei Use Cases.");
            return;
        }

        zeigeListe(liste);
        int ersteNummer = zahlLesen("Nummer des ersten: ");
        int zweiteNummer = zahlLesen("Nummer des zweiten: ");

        if (!gueltig(ersteNummer, liste) || !gueltig(zweiteNummer, liste)) {
            System.out.println("Ungueltige Auswahl.");
            return;
        }

        UseCase links = liste.get(ersteNummer - 1);
        UseCase rechts = liste.get(zweiteNummer - 1);

        System.out.println();
        System.out.printf("%-28s %-22s %-22s%n", "Kriterium", kurz(links.getTitel()), kurz(rechts.getTitel()));
        System.out.println("-".repeat(74));

        for (String kriterium : service.getEvaluator().kriterien()) {
            System.out.printf("%-28s %-22s %-22s%n",
                    kriterium, zelle(links, kriterium), zelle(rechts, kriterium));
        }

        System.out.println("-".repeat(74));
        System.out.printf("%-28s %-22d %-22d%n", "Nutzwert", links.getNutzwert(), rechts.getNutzwert());
        System.out.printf("%-28s %-22s %-22s%n", "Empfehlung",
                kurz(links.getEmpfehlung()), kurz(rechts.getEmpfehlung()));

        System.out.println();
        if (links.getNutzwert() > rechts.getNutzwert()) {
            System.out.println("Fuer KI besser geeignet: " + links.getTitel());
        } else if (rechts.getNutzwert() > links.getNutzwert()) {
            System.out.println("Fuer KI besser geeignet: " + rechts.getTitel());
        } else {
            System.out.println("Beide erreichen denselben Nutzwert.");
        }
    }

    private static String zelle(UseCase useCase, String kriterium) {
        String stufe = useCase.getStufe(kriterium);
        if (stufe == null) {
            return "-";
        }
        return kurz(stufe) + " (" + useCase.getPunkte().getOrDefault(kriterium, 0) + ")";
    }

    private static void filtern(MongoService service) {
        String[] kategorien = {
                "KI sinnvoll",
                "KI als Assistenz sinnvoll",
                "Klassische Lösung empfohlen",
                "Manuelle Bearbeitung empfohlen"
        };

        System.out.println();
        for (int i = 0; i < kategorien.length; i++) {
            System.out.println("  " + (i + 1) + ") " + kategorien[i]);
        }

        int wahl = zahlLesen("Auswahl 1-" + kategorien.length + ": ");
        if (wahl < 1 || wahl > kategorien.length) {
            System.out.println("Ungueltige Auswahl.");
            return;
        }

        zeigeListe(service.findeNachEmpfehlung(kategorien[wahl - 1]));
    }

    private static UseCase auswaehlen(MongoService service, String frage) {
        List<UseCase> liste = service.alle();
        if (liste.isEmpty()) {
            System.out.println();
            System.out.println("Noch keine Use Cases erfasst.");
            return null;
        }

        zeigeListe(liste);
        int nummer = zahlLesen(frage);
        if (!gueltig(nummer, liste)) {
            System.out.println("Ungueltige Nummer.");
            return null;
        }
        return liste.get(nummer - 1);
    }

    private static boolean gueltig(int nummer, List<UseCase> liste) {
        return nummer >= 1 && nummer <= liste.size();
    }

    private static String kurz(String text) {
        if (text == null) {
            return "-";
        }
        return (text.length() <= 20) ? text : text.substring(0, 19) + ".";
    }

    private static String zeileLesen(String frage) {
        System.out.print(frage);
        return EINGABE.nextLine().trim();
    }

    private static String pflichtfeldLesen(String frage) {
        while (true) {
            String eingabe = zeileLesen(frage);
            if (!eingabe.isBlank()) {
                return eingabe;
            }
            System.out.println("Pflichtfeld, bitte einen Wert eingeben.");
        }
    }

    private static int zahlLesen(String frage) {
        System.out.print(frage);
        String eingabe = EINGABE.nextLine().trim();
        try {
            return Integer.parseInt(eingabe);
        } catch (NumberFormatException fehler) {
            return -1;
        }
    }
}