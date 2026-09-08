import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class Main {
    public static void main(String[] args) {
        String verbindung = "mongodb://localhost:27017";

        try (MongoClient client = MongoClients.create(verbindung)) {
            MongoDatabase datenbank = client.getDatabase("ai_usecase_evaluator");

            System.out.println("Verbindung steht.");
            System.out.println("Datenbank: " + datenbank.getName());
            System.out.println("Anzahl Use Cases: " + datenbank.getCollection("usecases").countDocuments());
        }
    }
}