// init-db.js - Importskript für TeamBlack
// Datenbank: ai_use_case_evaluator

const dbName = 'ai_use_case_evaluator';
const targetDb = db.getSiblingDB(dbName);

print(`Starte Datenbank-Setup für '${dbName}'...`);

// 1. Alte Collections löschen (Idempotenz für Repositories)
targetDb.kriterien.drop();
targetDb.usecases.drop();

// 2. Collection 'kriterien' mit Validierung erstellen
targetDb.createCollection('kriterien', {
  validator: {
    $jsonSchema: {
      bsonType: 'object',
      required: ['schluessel', 'name', 'maxPunkte', 'stufen'],
      properties: {
        schluessel: {
          bsonType: 'string',
          pattern: '^[a-z][a-z0-9_]*$'
        },
        name: {
          bsonType: 'string',
          pattern: '\\S'
        },
        maxPunkte: {
          enum: [20]
        },
        stufen: {
          bsonType: 'array',
          minItems: 1,
          uniqueItems: true,
          items: {
            bsonType: 'object',
            required: ['stufe', 'punkte'],
            properties: {
              stufe: {
                bsonType: 'string',
                pattern: '\\S'
              },
              punkte: {
                enum: [0, 5, 10, 15, 20]
              }
            }
          }
        }
      }
    }
  },
  validationLevel: 'strict',
  validationAction: 'error'
});

// 3. Collection 'usecases' mit Validierung erstellen
targetDb.createCollection('usecases', {
  validator: {
    $jsonSchema: {
      bsonType: 'object',
      required: [
        'titel',
        'beschreibung',
        'kategorie',
        'branche',
        'bewertung',
        'nutzwert',
        'empfehlung',
        'erstelltAm',
        'geaendertAm'
      ],
      properties: {
        titel: { bsonType: 'string', pattern: '\\S' },
        beschreibung: { bsonType: 'string', pattern: '\\S' },
        kategorie: { bsonType: 'string', pattern: '\\S' },
        branche: { bsonType: 'string', pattern: '\\S' },
        bewertung: {
          bsonType: 'object',
          required: [
            'datenmenge',
            'datenstruktur',
            'automatisierungspotenzial',
            'risiko',
            'fehlertoleranz'
          ],
          properties: {
            datenmenge: {
              bsonType: 'object',
              required: ['stufe', 'punkte'],
              properties: {
                stufe: { enum: ['Sehr hoch', 'Hoch', 'Mittel', 'Niedrig', 'Sehr niedrig'] },
                punkte: { enum: [0, 5, 10, 15, 20] }
              }
            },
            datenstruktur: {
              bsonType: 'object',
              required: ['stufe', 'punkte'],
              properties: {
                stufe: { enum: ['Unstrukturiert', 'Teilstrukturiert', 'Strukturiert', 'Stark regelbasiert'] },
                punkte: { enum: [0, 5, 10, 15, 20] }
              }
            },
            automatisierungspotenzial: {
              bsonType: 'object',
              required: ['stufe', 'punkte'],
              properties: {
                stufe: { enum: ['Sehr hoch', 'Hoch', 'Mittel', 'Niedrig', 'Kein Potenzial'] },
                punkte: { enum: [0, 5, 10, 15, 20] }
              }
            },
            risiko: {
              bsonType: 'object',
              required: ['stufe', 'punkte'],
              properties: {
                stufe: { enum: ['Sehr gering', 'Gering', 'Mittel', 'Hoch', 'Sehr hoch'] },
                punkte: { enum: [0, 5, 10, 15, 20] }
              }
            },
            fehlertoleranz: {
              bsonType: 'object',
              required: ['stufe', 'punkte'],
              properties: {
                stufe: { enum: ['Sehr hoch', 'Hoch', 'Mittel', 'Gering', 'Keine'] },
                punkte: { enum: [0, 5, 10, 15, 20] }
              }
            }
          }
        },
        nutzwert: {
          bsonType: 'number',
          minimum: 0,
          maximum: 100,
          multipleOf: 1
        },
        empfehlung: {
          enum: [
            'KI sinnvoll',
            'KI als Assistenz sinnvoll',
            'Klassische Lösung empfohlen',
            'Manuelle Bearbeitung empfohlen'
          ]
        },
        erstelltAm: { bsonType: 'date' },
        geaendertAm: { bsonType: 'date' }
      }
    }
  },
  validationLevel: 'strict',
  validationAction: 'error'
});

// 4. Initialdaten einfügen: kriterien
targetDb.kriterien.insertMany([
  {
    _id: ObjectId('6aa01823946ab7fdda635f96'),
    schluessel: 'datenmenge',
    name: 'Datenmenge',
    maxPunkte: 20,
    stufen: [
      { stufe: 'Sehr hoch', punkte: 20 },
      { stufe: 'Hoch', punkte: 15 },
      { stufe: 'Mittel', punkte: 10 },
      { stufe: 'Niedrig', punkte: 5 },
      { stufe: 'Sehr niedrig', punkte: 0 }
    ]
  },
  {
    _id: ObjectId('6aa01881946ab7fdda635f98'),
    schluessel: 'datenstruktur',
    name: 'Datenstruktur',
    maxPunkte: 20,
    stufen: [
      { stufe: 'Unstrukturiert', punkte: 20 },
      { stufe: 'Teilstrukturiert', punkte: 15 },
      { stufe: 'Strukturiert', punkte: 5 },
      { stufe: 'Stark regelbasiert', punkte: 0 }
    ]
  },
  {
    _id: ObjectId('6aa018ca946ab7fdda635f9a'),
    schluessel: 'automatisierungspotenzial',
    name: 'Automatisierungspotenzial',
    maxPunkte: 20,
    stufen: [
      { stufe: 'Sehr hoch', punkte: 20 },
      { stufe: 'Hoch', punkte: 15 },
      { stufe: 'Mittel', punkte: 10 },
      { stufe: 'Niedrig', punkte: 5 },
      { stufe: 'Kein Potenzial', punkte: 0 }
    ]
  },
  {
    _id: ObjectId('6aa01916946ab7fdda635f9c'),
    schluessel: 'risiko',
    name: 'Risiko',
    maxPunkte: 20,
    stufen: [
      { stufe: 'Sehr gering', punkte: 20 },
      { stufe: 'Gering', punkte: 15 },
      { stufe: 'Mittel', punkte: 10 },
      { stufe: 'Hoch', punkte: 5 },
      { stufe: 'Sehr hoch', punkte: 0 }
    ]
  },
  {
    _id: ObjectId('6aa0195b946ab7fdda635f9e'),
    schluessel: 'fehlertoleranz',
    name: 'Fehlertoleranz',
    maxPunkte: 20,
    stufen: [
      { stufe: 'Sehr hoch', punkte: 20 },
      { stufe: 'Hoch', punkte: 15 },
      { stufe: 'Mittel', punkte: 10 },
      { stufe: 'Gering', punkte: 5 },
      { stufe: 'Keine', punkte: 0 }
    ]
  }
]);

// 5. Initialdaten einfügen: usecases
targetDb.usecases.insertMany([
  {
    _id: ObjectId('6aa019e3946ab7fdda635fa1'),
    titel: 'Automatische Ticketklassifikation',
    beschreibung: 'KI analysiert eingehende Support-Tickets und ordnet sie automatisch einer passenden Kategorie zu.',
    kategorie: 'Textklassifikation',
    branche: 'IT-Support',
    bewertung: {
      datenmenge: { stufe: 'Sehr hoch', punkte: 20 },
      datenstruktur: { stufe: 'Unstrukturiert', punkte: 20 },
      automatisierungspotenzial: { stufe: 'Sehr hoch', punkte: 20 },
      risiko: { stufe: 'Gering', punkte: 15 },
      fehlertoleranz: { stufe: 'Hoch', punkte: 15 }
    },
    nutzwert: 90,
    empfehlung: 'KI sinnvoll',
    erstelltAm: new Date(1788877651468),
    geaendertAm: new Date(1788877651469)
  }
]);

// 6. Indizes anlegen: kriterien
targetDb.kriterien.createIndex(
  { schluessel: 1 },
  { name: 'uq_kriterien_schluessel', unique: true }
);

// 7. Indizes anlegen: usecases
targetDb.usecases.createIndex(
  { titel: 1 },
  { name: 'uq_usecases_titel', unique: true }
);

targetDb.usecases.createIndex(
  { nutzwert: -1 },
  { name: 'idx_usecases_nutzwert' }
);

targetDb.usecases.createIndex(
  { kategorie: 1, empfehlung: 1 },
  { name: 'idx_usecases_kategorie_empfehlung' }
);

targetDb.usecases.createIndex(
  { beschreibung: 'text' },
  {
    name: 'idx_usecases_beschreibung_text',
    default_language: 'german'
  }
);

print('Import erfolgreich abgeschlossen!');