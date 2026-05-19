package view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import dao.MatchDAO;
import dao.EquipeDAO;
import dao.ClassementDAO;
import model.Match;
import model.Equipe;
import model.Classement;
import java.util.*;
import java.util.stream.Collectors;

/**
 * StatistiquesPanel - Statistiques avec graphiques JavaFX
 */
public class StatistiquesPanel extends ScrollPane {

    private MatchDAO matchDAO;
    private EquipeDAO equipeDAO;
    private ClassementDAO classementDAO;

    public StatistiquesPanel(MatchDAO matchDAO, EquipeDAO equipeDAO, ClassementDAO classementDAO) {
        this.matchDAO = matchDAO;
        this.equipeDAO = equipeDAO;
        this.classementDAO = classementDAO;

        getStyleClass().add("content-pane");
        setFitToWidth(true);

        VBox mainContent = new VBox();
        mainContent.getStyleClass().add("content-container");
        mainContent.setSpacing(20);

        // Titre
        Label titleLabel = new Label("📊 Statistiques");
        titleLabel.getStyleClass().add("label-title");
        mainContent.getChildren().add(titleLabel);

        // Graphiques
        HBox chartsRow = new HBox();
        chartsRow.setSpacing(20);

        try {
            // Graphique des buts par équipe
            BarChart<String, Number> goalsChart = createGoalsChart();
            VBox goalsBox = createChartContainer("Buts marqués par équipe", goalsChart);
            HBox.setHgrow(goalsBox, Priority.ALWAYS);

            // Graphique des résultats
            PieChart resultsChart = createResultsChart();
            VBox resultsBox = createChartContainer("Résultats des matchs", resultsChart);
            HBox.setHgrow(resultsBox, Priority.ALWAYS);

            chartsRow.getChildren().addAll(goalsBox, resultsBox);
            mainContent.getChildren().add(chartsRow);

            // Graphique des points par équipe
            BarChart<String, Number> pointsChart = createPointsChart();
            VBox pointsBox = createChartContainer("Points par équipe", pointsChart);
            VBox.setVgrow(pointsBox, Priority.ALWAYS);
            mainContent.getChildren().add(pointsBox);

        } catch (Exception e) {
            Label errorLabel = new Label("Erreur lors du chargement des statistiques");
            errorLabel.getStyleClass().add("error");
            mainContent.getChildren().add(errorLabel);
            e.printStackTrace();
        }

        setContent(mainContent);
    }

    private VBox createChartContainer(String title, javafx.scene.chart.Chart chart) {
        VBox box = new VBox();
        box.getStyleClass().add("card");
        box.setSpacing(10);
        box.setStyle("-fx-border-color: #3a3a3a; -fx-border-width: 1;");

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("card-title");

        chart.getStyleClass().add("chart");
        VBox.setVgrow(chart, Priority.ALWAYS);

        box.getChildren().addAll(titleLabel, chart);
        return box;
    }

    private BarChart<String, Number> createGoalsChart() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setStyle("-fx-text-fill: #FFD700; -fx-font-size: 11px;");
        
        NumberAxis yAxis = new NumberAxis();
        yAxis.setStyle("-fx-text-fill: #90a090;");
        yAxis.setLabel("Buts");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setStyle("-fx-background-color: #1a1a1a;");
        chart.setLegendVisible(false);

        XYChart.Series<String, Number> goalsSeries = new XYChart.Series<>();
        goalsSeries.setName("Buts marqués");

        try {
            List<Match> matches = matchDAO.getTous();
            Map<Integer, Integer> goalsPerTeam = new HashMap<>();

            for (Match match : matches) {
                if ("Terminé".equals(match.getStatut())) {
                    goalsPerTeam.put(match.getEquipeDomicileId(), 
                            goalsPerTeam.getOrDefault(match.getEquipeDomicileId(), 0) + match.getButsDomicile());
                    goalsPerTeam.put(match.getEquipeExterieurId(), 
                            goalsPerTeam.getOrDefault(match.getEquipeExterieurId(), 0) + match.getButsExterieur());
                }
            }

            // Ajouter les équipes avec leurs buts
            List<Equipe> equipes = equipeDAO.getTous();
            for (Equipe equipe : equipes) {
                int goals = goalsPerTeam.getOrDefault(equipe.getId(), 0);
                goalsSeries.getData().add(new XYChart.Data<>(equipe.getNom(), goals));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        chart.getData().add(goalsSeries);
        return chart;
    }

    private BarChart<String, Number> createPointsChart() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setStyle("-fx-text-fill: #FFD700; -fx-font-size: 11px;");
        
        NumberAxis yAxis = new NumberAxis();
        yAxis.setStyle("-fx-text-fill: #90a090;");
        yAxis.setLabel("Points");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setStyle("-fx-background-color: #1a1a1a;");
        chart.setLegendVisible(false);
        chart.setPrefHeight(400);

        XYChart.Series<String, Number> pointsSeries = new XYChart.Series<>();
        pointsSeries.setName("Points");

        try {
            List<Classement> classements = classementDAO.getTous();
            classements.sort((c1, c2) -> Integer.compare(c2.getPoints(), c1.getPoints()));

            for (Classement classement : classements) {
                pointsSeries.getData().add(new XYChart.Data<>(classement.getEquipeNom(), classement.getPoints()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        chart.getData().add(pointsSeries);
        return chart;
    }

    private PieChart createResultsChart() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        try {
            List<Match> matches = matchDAO.getTous();
            List<Match> playedMatches = matches.stream()
                    .filter(m -> "Terminé".equals(m.getStatut()))
                    .collect(Collectors.toList());

            int wins = 0;
            int draws = 0;
            int losses = 0;

            // Pour une équipe donnée, on compte ses résultats
            // Ici on compte simplement : domicile gagne = victoire, égalité = nul
            for (Match match : playedMatches) {
                if (match.getButsDomicile() > match.getButsExterieur()) {
                    wins++;
                    losses++;
                } else if (match.getButsDomicile() < match.getButsExterieur()) {
                    wins++;
                    losses++;
                } else {
                    draws += 2;
                }
            }

            // Si pas assez de matchs, ajouter des valeurs par défaut
            if (wins + draws + losses == 0) {
                wins = 1;
                draws = 1;
                losses = 1;
            }

            pieChartData.add(new PieChart.Data("Victoires", wins));
            pieChartData.add(new PieChart.Data("Nuls", draws));
            pieChartData.add(new PieChart.Data("Défaites", losses));

        } catch (Exception e) {
            pieChartData.add(new PieChart.Data("Données", 1));
            e.printStackTrace();
        }

        PieChart pieChart = new PieChart(pieChartData);
        pieChart.setStyle("-fx-background-color: #1a1a1a;");
        pieChart.setLegendVisible(true);
        pieChart.setPrefHeight(300);

        // Colorier les segments
        int index = 0;
        for (PieChart.Data data : pieChartData) {
            if (index == 0) {
                data.getNode().setStyle("-fx-pie-color: #4CAF50;");
            } else if (index == 1) {
                data.getNode().setStyle("-fx-pie-color: #FFD700;");
            } else {
                data.getNode().setStyle("-fx-pie-color: #FF6B6B;");
            }
            index++;
        }

        return pieChart;
    }

    public void refreshData() {
        // Les données sont rechargées à chaque fois que le panneau est affiché
    }
}
