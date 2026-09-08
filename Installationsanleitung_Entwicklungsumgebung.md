# Installationsanleitung Entwicklungsumgebung

**AI Use Case Evaluator – Windows 11**
Modul IN258 – Document Oriented Databases and Exchange Formats

Diese Anleitung beschreibt die Installation der lokalen Entwicklungsumgebung für unser Gruppenprojekt. Alle Schritte wurden auf Windows 11 durchgeführt und getestet. Die Installation dauert etwa 20 Minuten.

---

## 1. Was installiert wird

Die Umgebung besteht aus fünf Komponenten. Java und Maven sind auf den HFTM-Geräten in der Regel bereits vorhanden.

| Komponente | Zweck | Getestete Version |
|---|---|---|
| Java (JDK) | Programmiersprache des Projekts | Temurin 25.0.1 |
| Apache Maven | Verwaltet Bibliotheken und den Build | 3.9.9 |
| MongoDB Server | Die Datenbank, läuft als Windows-Dienst | 8.3.7 |
| MongoDB Compass | Grafische Oberfläche zum Anschauen der Daten | 1.50.0 |
| MongoDB Shell (mongosh) | Befehlszeile für schnelle Tests | 2.9.2 |

> **Hinweis zum Begriff Server:** MongoDB heisst so, weil es dieselbe Software ist, die auch in Rechenzentren läuft. Bei uns läuft sie einfach als Programm im Hintergrund auf dem eigenen Laptop. Es wird kein zweiter Rechner und keine Internetverbindung benötigt.

---

## 2. Vorbereitung

### 2.1 Bestehende Installation prüfen

PowerShell öffnen und nacheinander eingeben:

```powershell
java -version
```

```powershell
mvn -version
```

Erscheinen Versionsnummern, ist alles vorhanden. Erscheint "wurde nicht als Name eines Cmdlet erkannt", fehlt die Komponente und muss nachinstalliert werden.

### 2.2 Terminal als Administrator öffnen

Für die Installation werden Adminrechte benötigt, sonst bricht der Vorgang ab.

- Rechtsklick auf das Windows-Startsymbol
- Terminal (Administrator) auswählen
- Die Rückfrage der Benutzerkontensteuerung mit Ja bestätigen

### 2.3 winget prüfen

winget ist der Paketmanager von Windows und bei Windows 11 vorinstalliert.

```powershell
winget --version
```

---

## 3. Installation

Die drei Pakete werden einzeln installiert. So ist bei einem Fehler sofort klar, welches Paket betroffen ist. Kommt eine Lizenzabfrage, mit `J` bestätigen.

### 3.1 Datenbank

```powershell
winget install MongoDB.Server
```

Download rund 858 MB. Der Dienst wird dabei automatisch eingerichtet und startet künftig mit Windows.

### 3.2 Grafische Oberfläche

```powershell
winget install MongoDB.Compass.Full
```

### 3.3 Shell

```powershell
winget install MongoDB.Shell
```

> **Wichtig:** Nach der Installation das Terminal schliessen und ein neues öffnen. Sonst ist der Suchpfad noch nicht aktualisiert und der Befehl `mongosh` wird nicht gefunden.

---

## 4. Installation überprüfen

### 4.1 Läuft der Dienst?

```powershell
Get-Service MongoDB
```

In der Spalte Status muss **Running** stehen. Steht dort Stopped, hilft folgender Befehl im Administrator-Terminal:

```powershell
Start-Service MongoDB
```

### 4.2 Verbindung testen

```powershell
mongosh
```

Erwartete Ausgabe: einige Zeilen mit der Serverversion, danach der Prompt `test>`.

Die gelbe Warnung "Access control is not enabled" ist normal und kann ignoriert werden. Sie bedeutet nur, dass keine Benutzeranmeldung eingerichtet ist, was bei einer lokalen Entwicklungsdatenbank üblich ist.

### 4.3 Testdokument speichern und wieder löschen

Diese Befehle direkt im mongosh-Prompt eingeben:

```javascript
use ai_usecase_evaluator
```

```javascript
db.usecases.insertOne({ titel: "Testfall", nutzwert: 90 })
```

```javascript
db.usecases.find()
```

```javascript
db.usecases.deleteMany({})
```

Erwartete Rückmeldungen: `acknowledged: true` mit einer `insertedId`, danach das gespeicherte Dokument, zuletzt `deletedCount: 1`. Die Shell wird mit `exit` verlassen.

> Achtung auf die Gross- und Kleinschreibung: MongoDB unterscheidet zwischen `insertOne` und `insertone`.

### 4.4 Compass verbinden

MongoDB Compass über das Startmenü öffnen. Im Verbindungsfeld steht bereits die richtige Adresse, es genügt ein Klick auf Connect.

```
mongodb://localhost:27017
```

---

## 5. Verbindungsdaten für das Projekt

| Einstellung | Wert |
|---|---|
| Verbindungszeichenfolge | `mongodb://localhost:27017` |
| Port | `27017` |
| Datenbank | `ai_usecase_evaluator` |
| Collection Use Cases | `usecases` |
| Collection Kriterien | `kriterien` |
| Benutzer / Passwort | nicht erforderlich (lokal) |

Damit alle mit denselben Namen arbeiten, verwenden wir genau diese Schreibweise, alles klein und ohne Umlaute.

---

## 6. Bekannte Stolpersteine

| Problem | Ursache und Lösung |
|---|---|
| `mongosh` wird nicht gefunden | Terminal wurde nach der Installation nicht neu geöffnet. Fenster schliessen und neu starten. |
| Installation bricht ab | Terminal ohne Adminrechte gestartet. Als Administrator neu öffnen. |
| Dienst steht auf Stopped | Mit `Start-Service MongoDB` im Administrator-Terminal starten. |
| Verbindung wird abgelehnt | Dienst prüfen mit `Get-Service MongoDB`. Läuft er nicht, kann sich nichts verbinden. |
| Fehler beim Kompilieren im OneDrive-Ordner | OneDrive sperrt erzeugte Dateien während der Synchronisation. Projekt ausserhalb von OneDrive ablegen, zum Beispiel unter `C:\Projekte`. |

---

## 7. Nächste Schritte

- Maven-Projekt anlegen und die MongoDB-Java-Bibliothek einbinden
- Verbindung aus Java heraus aufbauen und testen
- Datenmodell und Collections gemäss Anforderungskatalog umsetzen
- CRUD-Funktionen, Bewertungslogik und Aggregationen implementieren

---

*Erstellt von Murat, Backend Developer. Bei Problemen meldet euch, dann gehen wir es gemeinsam durch.*
