package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import dao.*;
import model.*;
import java.util.List;

/**
 * MainFrame - Cadre principal de l'application JavaFX
 * Gère la navigation et l'affichage des différentes sections
 */
public class MainFrame {

    private Stage primaryStage;
    private BorderPane root;
    private StackPane contentArea;
    private VBox sidebar;
    private Label currentSectionTitle;
    private Button activeNavButton;

    // DAO instances
    private EquipeDAO equipeDAO;
    private JoueurDAO joueurDAO;
    private MatchDAO matchDAO;
    private ClassementDAO classementDAO;

    // Panel instances
    private DashboardPanel dashboardPanel;
    private EquipePanel equipePanel;
    private JoueurPanel joueurPanel;
    private MatchPanel matchPanel;
    private ClassementPanel classementPanel;
    private StatistiquesPanel statistiquesPanel;

    public MainFrame() {
        this.equipeDAO = new EquipeDAO();
        this.joueurDAO = new JoueurDAO();
        this.matchDAO = new MatchDAO();
        this.classementDAO = new ClassementDAO();
    }

    public void show(Stage stage) {
        this.primaryStage = stage;
        primaryStage.setTitle("🏆 Gestion Championnat de Football");
        primaryStage.setWidth(1400);
        primaryStage.setHeight(800);

        root = new BorderPane();
        root.getStyleClass().add("main-pane");

        // Créer la barre latérale
        createSidebar();
        root.setLeft(sidebar);

        // Créer la zone de contenu
        contentArea = new StackPane();
        contentArea.getStyleClass().add("content-pane");
        root.setCenter(contentArea);

        // Créer la barre supérieure
        createTopBar();
        root.setTop(createTopBar());

        // Initialiser les panneaux
        initializePanels();

        // Afficher le dashboard par défaut
        showDashboard();

        // Créer la scène
        Scene scene = new Scene(root);
        try {
            String cssResource = getClass().getResource("stylesheet.css").toExternalForm();
            scene.getStylesheets().add(cssResource);
        } catch (Exception e) {
            System.err.println("Could not load stylesheet: " + e.getMessage());
        }

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void createSidebar() {
        sidebar = new VBox();
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(220);
        sidebar.setSpacing(0);

        // Titre de la barre latérale
        Label sidebarTitle = new Label("⚽ CHAMPIONNAT");
        sidebarTitle.getStyleClass().add("sidebar-title");

        sidebar.getChildren().add(sidebarTitle);

        // Boutons de navigation
        Button dashboardBtn = createNavButton("🏠 Dashboard", () -> showDashboard());
        Button equipeBtn = createNavButton("👥 Équipes", () -> showEquipes());
        Button joueurBtn = createNavButton("👨 Joueurs", () -> showJoueurs());
        Button matchBtn = createNavButton("⚽ Matchs", () -> showMatchs());
        Button classementBtn = createNavButton("🏆 Classement", () -> showClassement());
        Button statistiquesBtn = createNavButton("📊 Statistiques", () -> showStatistiques());

        sidebar.getChildren().addAll(dashboardBtn, equipeBtn, joueurBtn, matchBtn, classementBtn, statistiquesBtn);

        // Ajouter un espaceur pour pousser les boutons vers le haut
        Pane spacer = new Pane();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        sidebar.getChildren().add(spacer);

        // Bouton de quitter
        Button quitBtn = createNavButton("🚪 Quitter", () -> primaryStage.close());
        sidebar.getChildren().add(quitBtn);
    }

    private Button createNavButton(String text, Runnable action) {
        Button btn = new Button(text);
        btn.getStyleClass().add("nav-button");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setTextAlignment(javafx.scene.text.TextAlignment.LEFT);
        btn.setWrapText(false);
        btn.setPadding(new Insets(12, 15, 12, 15));
        btn.setOnAction(e -> action.run());
        return btn;
    }

    private HBox createTopBar() {
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(15, 20, 15, 20));
        topBar.setSpacing(20);
        topBar.setStyle("-fx-background-color: #0d1a0d; -fx-border-color: #1a2e1a; -fx-border-width: 0 0 2 0;");
        topBar.setAlignment(Pos.CENTER_LEFT);

        currentSectionTitle = new Label("Dashboard");
        currentSectionTitle.getStyleClass().add("label-title");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Informations rapides
        Label infoLabel = new Label("Bienvenue dans le Gestion du Championnat");
        infoLabel.setStyle("-fx-text-fill: #90a090; -fx-font-size: 11px;");

        topBar.getChildren().addAll(currentSectionTitle, spacer, infoLabel);
        return topBar;
    }

    private void initializePanels() {
        dashboardPanel = new DashboardPanel(equipeDAO, joueurDAO, matchDAO, classementDAO);
        equipePanel = new EquipePanel(equipeDAO);
        joueurPanel = new JoueurPanel(joueurDAO, equipeDAO);
        matchPanel = new MatchPanel(matchDAO, equipeDAO);
        classementPanel = new ClassementPanel(classementDAO, equipeDAO);
        statistiquesPanel = new StatistiquesPanel(matchDAO, equipeDAO, classementDAO);
    }

    private void showPanel(Pane panel, String title) {
        currentSectionTitle.setText(title);
        contentArea.getChildren().clear();
        
        // Animation de transition
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), panel);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        
        contentArea.getChildren().add(panel);
        fadeIn.play();
    }

    private void showDashboard() {
        updateActiveButton("🏠 Dashboard");
        showPanel(dashboardPanel, "🏠 Dashboard");
    }

    private void showEquipes() {
        updateActiveButton("👥 Équipes");
        equipePanel.refreshData();
        showPanel(equipePanel, "👥 Équipes");
    }

    private void showJoueurs() {
        updateActiveButton("👨 Joueurs");
        joueurPanel.refreshData();
        showPanel(joueurPanel, "👨 Joueurs");
    }

    private void showMatchs() {
        updateActiveButton("⚽ Matchs");
        matchPanel.refreshData();
        showPanel(matchPanel, "⚽ Matchs");
    }

    private void showClassement() {
        updateActiveButton("🏆 Classement");
        classementPanel.refreshData();
        showPanel(classementPanel, "🏆 Classement");
    }

    private void showStatistiques() {
        updateActiveButton("📊 Statistiques");
        statistiquesPanel.refreshData();
        showPanel(statistiquesPanel, "📊 Statistiques");
    }

    private void updateActiveButton(String buttonText) {
        // Remove active style from all buttons
        for (javafx.scene.Node node : sidebar.getChildren()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                    if (btn.getText().equals(buttonText)) {
                        btn.getStyleClass().add("active");
                    } else {
                        btn.getStyleClass().remove("active");
                }
            }
        }
    }
}
