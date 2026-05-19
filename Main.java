import view.MainFrame;
import javax.swing.*;

/**
 * Point d'entrée de l'application Gestion Championnat de Football
 * 
 * Prérequis :
 *  - WAMP Server démarré (MySQL sur port 3306)
 *  - Base de données créée via le script championnat_db.sql
 *  - Fichier mysql-connector-java.jar ajouté aux librairies du projet NetBeans
 */
public class Main {
    public static void main(String[] args) {
        // Appliquer le look and feel du système (Windows natif)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Ignore, look and feel par défaut si échec
        }

        // Lancer l'interface graphique sur l'EDT (Event Dispatch Thread)
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
