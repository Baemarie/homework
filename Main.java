import view.MainFrame;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Point d'entrée de l'application Gestion Championnat de Football
 * (JavaFX)
 * 
 * Prérequis :
 *  - WAMP Server démarré (MySQL sur port 3306)
 *  - Base de données créée via le script championnat_db.sql
 *  - Fichier mysql-connector-java.jar ajouté aux librairies du projet
 *  - VM options: --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            MainFrame mainFrame = new MainFrame();
            mainFrame.show(primaryStage);
        } catch (Exception e) {
            System.err.println("Erreur lors du lancement de l'application");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
