package view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import dao.ClassementDAO;
import dao.EquipeDAO;
import model.Classement;
import model.Equipe;
import java.util.List;

/**
 * ClassementPanel - Affichage du classement avec les médailles
 */
public class ClassementPanel extends ScrollPane {

    private ClassementDAO classementDAO;
    private EquipeDAO equipeDAO;
    private TableView<Classement> tableView;
    private ObservableList<Classement> classementList;

    public ClassementPanel(ClassementDAO classementDAO, EquipeDAO equipeDAO) {
        this.classementDAO = classementDAO;
        this.equipeDAO = equipeDAO;

        getStyleClass().add("content-pane");
        setFitToWidth(true);

        VBox mainContent = new VBox();
        mainContent.getStyleClass().add("content-container");
        mainContent.setSpacing(20);

        // Titre
        Label titleLabel = new Label("🏆 Classement du Championnat");
        titleLabel.getStyleClass().add("label-title");
        mainContent.getChildren().add(titleLabel);

        // Affichage du podium (top 3)
        HBox podium = createPodium();
        mainContent.getChildren().add(podium);

        // Tableau complet
        tableView = createTableView();
        VBox.setVgrow(tableView, Priority.ALWAYS);

        VBox tableContainer = new VBox();
        tableContainer.setStyle("-fx-border-radius: 8; -fx-background-radius: 8;");
        VBox.setVgrow(tableView, Priority.ALWAYS);
        tableContainer.getChildren().add(tableView);

        mainContent.getChildren().add(tableContainer);

        // Bouton rafraîchir
        Button refreshBtn = new Button("🔄 Rafraîchir");
        refreshBtn.getStyleClass().add("button-secondary");
        refreshBtn.setOnAction(e -> refreshData());
        HBox refreshBox = new HBox(refreshBtn);
        refreshBox.setAlignment(Pos.CENTER_LEFT);
        mainContent.getChildren().add(refreshBox);

        setContent(mainContent);
        refreshData();
    }

    private HBox createPodium() {
        HBox podium = new HBox();
        podium.setSpacing(20);
        podium.setAlignment(Pos.CENTER);
        podium.setPadding(new Insets(20));
        podium.setStyle("-fx-background-color: #1a2e1a; -fx-border-radius: 8; -fx-background-radius: 8;");

        try {
            List<Classement> classements = classementDAO.getTous();
            
            // Trouver top 3
            Classement first = null, second = null, third = null;
            
            for (Classement c : classements) {
                if (first == null || c.getPoints() > first.getPoints()) {
                    third = second;
                    second = first;
                    first = c;
                } else if (second == null || c.getPoints() > second.getPoints()) {
                    third = second;
                    second = c;
                } else if (third == null || c.getPoints() > third.getPoints()) {
                    third = c;
                }
            }

            // Créer les cartes du podium
            VBox secondCard = createPodiumCard("2️⃣ ARGENT", second, "#C0C0C0");
            VBox firstCard = createPodiumCard("1️⃣ OR", first, "#FFD700");
            VBox thirdCard = createPodiumCard("3️⃣ BRONZE", third, "#CD7F32");

            firstCard.setPrefHeight(200);
            secondCard.setPrefHeight(150);
            thirdCard.setPrefHeight(140);

            podium.getChildren().addAll(secondCard, firstCard, thirdCard);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return podium;
    }

    private VBox createPodiumCard(String medal, Classement classement, String color) {
        VBox card = new VBox();
        card.setAlignment(Pos.TOP_CENTER);
        card.setSpacing(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: #2a2a2a; -fx-border-radius: 8; -fx-background-radius: 8; " +
                      "-fx-border-color: " + color + "; -fx-border-width: 2;");

        Label medalLabel = new Label(medal);
        medalLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        if (classement != null) {
            Label teamLabel = new Label(classement.getEquipeNom());
            teamLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #FFD700;");

            Label pointsLabel = new Label(classement.getPoints() + " pts");
            pointsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #e0e0e0;");

            card.getChildren().addAll(medalLabel, teamLabel, pointsLabel);
        } else {
            Label emptyLabel = new Label("—");
            emptyLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #90a090;");
            card.getChildren().addAll(medalLabel, emptyLabel);
        }

        return card;
    }

    private TableView<Classement> createTableView() {
        tableView = new TableView<>();
        tableView.getStyleClass().add("table-view");
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Classement, Integer> positionColumn = new TableColumn<>("#");
        positionColumn.setCellValueFactory(cellData -> javafx.beans.property.SimpleObjectProperty.asObject(
                tableView.getItems().indexOf(cellData.getValue()) + 1));
        positionColumn.setPrefWidth(40);

        TableColumn<Classement, String> equipeColumn = new TableColumn<>("Équipe");
        equipeColumn.setCellValueFactory(cellData -> javafx.beans.property.SimpleStringProperty.asObject(cellData.getValue().getEquipeNom()));
        equipeColumn.setPrefWidth(150);

        TableColumn<Classement, Integer> pointsColumn = new TableColumn<>("Points");
        pointsColumn.setCellValueFactory(cellData -> javafx.beans.property.SimpleObjectProperty.asObject(cellData.getValue().getPoints()));
        pointsColumn.setPrefWidth(80);

        TableColumn<Classement, Integer> matchsColumn = new TableColumn<>("M");
        matchsColumn.setCellValueFactory(cellData -> javafx.beans.property.SimpleObjectProperty.asObject(cellData.getValue().getMatchsJoues()));
        matchsColumn.setPrefWidth(60);

        TableColumn<Classement, Integer> victoiresColumn = new TableColumn<>("V");
        victoiresColumn.setCellValueFactory(cellData -> javafx.beans.property.SimpleObjectProperty.asObject(cellData.getValue().getVictoires()));
        victoiresColumn.setPrefWidth(60);

        TableColumn<Classement, Integer> nulsColumn = new TableColumn<>("N");
        nulsColumn.setCellValueFactory(cellData -> javafx.beans.property.SimpleObjectProperty.asObject(cellData.getValue().getNuls()));
        nulsColumn.setPrefWidth(60);

        TableColumn<Classement, Integer> defaitesColumn = new TableColumn<>("D");
        defaitesColumn.setCellValueFactory(cellData -> javafx.beans.property.SimpleObjectProperty.asObject(cellData.getValue().getDefaites()));
        defaitesColumn.setPrefWidth(60);

        TableColumn<Classement, String> butsColumn = new TableColumn<>("Buts");
        butsColumn.setCellValueFactory(cellData -> javafx.beans.property.SimpleStringProperty.asObject(
                cellData.getValue().getButsPour() + "-" + cellData.getValue().getButsContre()));
        butsColumn.setPrefWidth(80);

        TableColumn<Classement, Integer> diffColumn = new TableColumn<>("Diff");
        diffColumn.setCellValueFactory(cellData -> javafx.beans.property.SimpleObjectProperty.asObject(cellData.getValue().getDifferenceButs()));
        diffColumn.setPrefWidth(80);

        tableView.getColumns().addAll(positionColumn, equipeColumn, pointsColumn, matchsColumn, victoiresColumn, nulsColumn, defaitesColumn, butsColumn, diffColumn);

        tableView.setRowFactory(tv -> {
            TableRow<Classement> row = new TableRow<Classement>() {
                @Override
                protected void updateItem(Classement item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || getIndex() < 0) {
                        setStyle("");
                    } else {
                        int position = getIndex() + 1;
                        if (position == 1) {
                            setStyle("-fx-background-color: #FFD70030;");
                        } else if (position == 2) {
                            setStyle("-fx-background-color: #C0C0C030;");
                        } else if (position == 3) {
                            setStyle("-fx-background-color: #CD7F3230;");
                        } else if (getIndex() % 2 == 0) {
                            setStyle("-fx-background-color: #252525;");
                        } else {
                            setStyle("-fx-background-color: #2a2a2a;");
                        }
                    }
                }
            };
            return row;
        });

        return tableView;
    }

    public void refreshData() {
        try {
            List<Classement> classements = classementDAO.getTous();
            
            // Trier par points décroissants
            classements.sort((c1, c2) -> Integer.compare(c2.getPoints(), c1.getPoints()));

            classementList = FXCollections.observableArrayList(classements);
            tableView.setItems(classementList);
        } catch (Exception e) {
            showError("Erreur lors du chargement du classement");
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
