import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;

public class MongoService implements AutoCloseable {

    private final MongoClient client;
    private final MongoCollection<Document> usecases;
    private final UseCaseEvaluator evaluator = new UseCaseEvaluator();

    public MongoService() {
        String verbindung = umgebung("MONGO_URI", "mongodb://localhost:27017");
        String datenbankName = umgebung("MONGO_DB", "ai_usecase_evaluator");

        this.client = MongoClients.create(verbindung);
        MongoDatabase datenbank = client.getDatabase(datenbankName);
        this.usecases = datenbank.getCollection("usecases");
    }

    private static String umgebung(String schluessel, String standard) {
        String wert = System.getenv(schluessel);
        if (wert == null || wert.isBlank()) {
            return standard;
        }
        return wert;
    }

    public UseCaseEvaluator getEvaluator() {
        return evaluator;
    }

    // ---------- FA-01 Erfassen ----------

    public UseCase speichern(UseCase useCase) {
        evaluator.bewerte(useCase);
        Date jetzt = new Date();
        useCase.setErstelltAm(jetzt);
        useCase.setGeaendertAm(jetzt);
        Document dokument = useCase.toDocument();
        usecases.insertOne(dokument);
        useCase.setId(dokument.getObjectId("_id"));
        return useCase;
    }

    // ---------- FA-02 Anzeigen ----------

    public List<UseCase> alle() {
        List<UseCase> liste = new ArrayList<>();
        for (Document dokument : usecases.find().sort(Sorts.descending("nutzwert"))) {
            liste.add(UseCase.fromDocument(dokument));
        }
        return liste;
    }

    public List<UseCase> findeNachEmpfehlung(String empfehlung) {
        List<UseCase> liste = new ArrayList<>();
        for (Document dokument : usecases.find(Filters.eq("empfehlung", empfehlung))
                .sort(Sorts.descending("nutzwert"))) {
            liste.add(UseCase.fromDocument(dokument));
        }
        return liste;
    }

    public List<UseCase> sucheImTitel(String begriff) {
        List<UseCase> liste = new ArrayList<>();
        for (Document dokument : usecases.find(Filters.regex("titel", begriff, "i"))
                .sort(Sorts.descending("nutzwert"))) {
            liste.add(UseCase.fromDocument(dokument));
        }
        return liste;
    }

    // ---------- FA-03 Bearbeiten ----------

    public boolean aktualisieren(UseCase useCase) {
        if (useCase.getId() == null) {
            return false;
        }
        evaluator.bewerte(useCase);
        Date jetzt = new Date();
        if (useCase.getErstelltAm() == null) {
            useCase.setErstelltAm(jetzt);
        }
        useCase.setGeaendertAm(jetzt);
        Document neu = useCase.toDocument();
        Document setBefehl = new Document("$set", neu);
        return usecases.updateOne(Filters.eq("_id", useCase.getId()), setBefehl)
                .getModifiedCount() > 0;
    }

    // ---------- FA-04 Loeschen ----------

    public boolean loeschen(ObjectId id) {
        return usecases.deleteOne(Filters.eq("_id", id)).getDeletedCount() > 0;
    }

    public long alleLoeschen() {
        return usecases.deleteMany(new Document()).getDeletedCount();
    }

    // ---------- Kennzahlen ----------

    public long anzahl() {
        return usecases.countDocuments();
    }

    public MongoCollection<Document> getUseCases() {
        return usecases;
    }

    @Override
    public void close() {
        client.close();
    }
}