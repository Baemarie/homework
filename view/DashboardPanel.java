package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import dao.EquipeDAO;
import dao.JoueurDAO;
import dao.MatchDAO;
import dao.ClassementDAO;
import model.Match;
import java.util.List;

/**
 * DashboardPanel - Écran d'accueil avec statistiques principales
 */
public class DashboardPanel extends ScrollPane {

    private EquipeDAO equipeDAO;
    private JoueurDAO joueurDAO;
    private MatchDAO matchDAO;
    private ClassementDAO classementDAO;

    public DashboardPanel(EquipeDAO equipeDAO, JoueurDAO joueurDAO, MatchDAO matchDAO, ClassementDAO classementDAO) {
        this.equipeDAO = equipeDAO;
        this.joueurDAO = joueurDAO;
        this.matchDAO = matchDAO;
        this.classementDAO = classementDAO;

        getStyleClass().add("content-pane");
        setFitToWidth(true);
        setPadding(new Insets(0));

        VBox mainContent = new VBox();
        mainContent.getStyleClass().add("content-container");
        mainContent.setSpacing(20);

        // Titre
        Label titleLabel = new Label("Tableau de Bord");
        titleLabel.getStyleClass().add("label-title");

        // Grille de statistiques
        HBox statsGrid = createStatsGrid();
        statsGrid.setPadding(new Insets(0));

        // Résumé rapide
        VBox summaryBox = createSummaryBox();

        mainContent.getChildren().addAll(titleLabel, statsGrid, summaryBox);

        setContent(mainContent);
    }

    private HBox createStatsGrid() {
        HBox grid = new HBox();
        grid.setSpacing(20);
        grid.setPrefHeight(200);

        int totalEquipes = equipeDAO.getTous().size();
        int totalJoueurs = joueurDAO.getTous().size();
        int matchsJoues = countPlayedMatches();
        int topScorerGoals = getTopScorerGoals();

        VBox card1 = createStatCard("👥 Équipes", String.valueOf(totalEquipes), "Total d'équipes");
        VBox card2 = createStatCard("👨 Joueurs", String.valueOf(totalJoueurs), "Total de joueurs");
        VBox card3 = createStatCard("⚽ Matchs", String.valueOf(matchsJoues), "Matchs joués");
        VBox card4 = createStatCard("🎯 Top Scorer", String.valueOf(topScorerGoals), "Buts marqués");

        HBox.setHgrow(card1, Priority.ALWAYS);
        HBox.setHgrow(card2, Priority.ALWAYS);
        HBox.setHgrow(card3, Priority.ALWAYS);
        HBox.setHgrow(card4, Priority.ALWAYS);

        grid.getChildren().addAll(card1, card2, card3, card4);
        return grid;
    }

    private VBox createStatCard(String icon, String value, String label) {
        VBox card = new VBox();
        card.getStyleClass().add("stat-card");
        card.setAlignment(Pos.CENTER);
        card.setSpacing(10);

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 36px;");

        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("stat-value");

        Label labelText = new Label(label);
        labelText.getStyleClass().add("stat-label");

        card.getChildren().addAll(iconLabel, valueLabel, labelText);
        return card;
    }

    private VBox createSummaryBox() {
        VBox summaryBox = new VBox();
        summaryBox.getStyleClass().add("card");
        summaryBox.setSpacing(15);

        Label titleLabel = new Label("Informations Rapides");
        titleLabel.getStyleClass().add("card-title");

        VBox content = new VBox();
        content.setSpacing(10);

        try {
            List<Match> allMatches = matchDAO.getTous();
            int totalMatches = allMatches.size();
            int playedMatches = (int) allMatches.stream().filter(m -> "Terminé".equals(m.getStatut())).count();
            int plannedMatches = (int) allMatches.stream().filter(m -> "Planifié".equals(m.getStatut())).count();

            Label matchInfo = new Label("📊 Total Matchs: " + totalMatches + " | Terminés: " + playedMatches + " | Planifiés: " + plannedMatches);
            matchInfo.setStyle("-fx-text-fill: #e0e0e0;");

            Label equipesInfo = new Label("👥 Nombre d'équipes: " + equipeDAO.getTous().size());
            equipesInfo.setStyle("-fx-text-fill: #e0e0e0;");

            Label joueursInfo = new Label("👨 Nombre de joueurs: " + joueurDAO.getTous().size());
            joueursInfo.setStyle("-fx-text-fill: #e0e0e0;");

            content.getChildren().addAll(matchInfo, equipesInfo, joueursInfo);
        } catch (Exception e) {
            Label errorLabel = new Label("⚠️ Erreur lors du chargement des données");
            errorLabel.getStyleClass().add("error");
            content.getChildren().add(errorLabel);
        }

        summaryBox.getChildren().addAll(titleLabel, content);
        return summaryBox;
    }

    private int countPlayedMatches() {
        try {
            List<Match> matches = matchDAO.getTous();
            return (int) matches.stream().filter(m -> "Terminé".equals(m.getStatut())).count();
        } catch (Exception e) {
            return 0;
        }
    }

    private int getTopScorerGoals() {
        try {
            List<Match> matches = matchDAO.getTous();
            return matches.stream()
                    .filter(m -> "Terminé".equals(m.getStatut()))
                    .mapToInt(m -> Math.max(m.getButsDomicile(), m.getButsExterieur()))
                    .max()
                    .orElse(0);
        } catch (Exception e) {
            return 0;
        }
    }
}
