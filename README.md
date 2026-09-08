# AI Use Case Evaluator

## Modul

IN258 Document Oriented Databases and Exchange Formats

## Projektziel

Ziel dieses Projekts ist die Entwicklung einer Anwendung zur Bewertung von KI-Anwendungsfällen.

Die Anwendung speichert verschiedene Use Cases in einer dokumentenorientierten Datenbank (MongoDB), bewertet deren KI-Eignung anhand definierter Kriterien und ermöglicht den Vergleich unterschiedlicher Lösungsansätze.

Leitfrage:

> Unter welchen Bedingungen erzeugt der Einsatz von KI einen grösseren Nutzen als eine klassische, regelbasierte oder manuelle Lösung?

---

# Technologien

- Java 21
- Maven
- MongoDB
- MongoDB Compass
- Git / GitHub
- Visual Studio Code

---

# Projektstruktur

```text
AI-UseCase-Evaluator
│
├── src
│   └── main
│       └── java
│           ├── Main.java
│           │
│           ├── model
│           │   └── UseCase.java
│           │
│           ├── service
│           │   └── MongoService.java
│           │
│           └── evaluation
│               └── UseCaseEvaluator.java
│
├── screenshots
│
├── README.md
│
└── pom.xml
```

## Team aufteilung:

### Team App: (main)
- Murat (Lead)
- Koray
- Dawit
- Zlata

### Team Web: (optional)
- Mostafa (Lead)
- Dawit
- Zlata

## Stand der dinge:

### Person 1 – Data Architect (Dawit)
#### Verantwortlichkeiten
- ⬜ Datenmodell entwerfen
- ⬜ MongoDB Collections definieren
- ⬜ Dokumentstruktur festlegen
- ⬜ Validierungen definieren
- ⬜ Indizes erstellen
  
#### Artefakte
- ⬜ UseCase-Datenmodell
- ⬜ MongoDB Schema
- ⬜ Indexierungsstrategie
  
### Person 2 – Backend Developer (du)
#### Verantwortlichkeiten
- ✅ Verbindung zu MongoDB
- ⬜ CRUD-Funktionen
- ⬜ Services und Datenzugriff
- ⬜ Daten speichern und abrufen
  
#### Artefakte
- ⬜ MongoService.java
- ⬜ CRUD-Funktionen
- ✅ Datenbankanbindung
  
### Person 3 – Data Analyst (Koray)
- ⬜ alle Punkte offen
  
### Person 4 – AI & Risk Analyst (Zlata)
#### Verantwortlichkeiten
- ✅ Kriterienkatalog definieren
- ✅ Nutzwertmodell entwickeln
- 🟡 Risiken bewerten (Risiko ist als Kriterium drin, eine eigene Risikobetrachtung fehlt)
- 🟡 Testfälle definieren (zwei Beispielbewertungen vorhanden, kein Testfallkatalog)
- ⬜ Reflexion erstellen
  
#### Artefakte
- ✅ Bewertungslogik (fachlich definiert, technisch noch nicht implementiert)
- 🟡 Testfälle
- ⬜ Reflexion
- ⬜ Präsentation

