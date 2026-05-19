package view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import dao.MatchDAO;
import dao.EquipeDAO;
import model.Match;
import model.Equipe;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

/**
 * MatchPanel - Gestion des matchs
 */
public class MatchPanel extends ScrollPane {

    private MatchDAO matchDAO;
    private EquipeDAO equipeDAO;
    private TableView<Match> tableView;
    private ObservableList<Match> matchList;

    public MatchPanel(MatchDAO matchDAO, EquipeDAO equipeDAO) {
        this.matchDAO = matchDAO;
        this.equipeDAO = equipeDAO;

        getStyleClass().add("content-pane");
        setFitToWidth(true);

        VBox mainContent = new VBox();
        mainContent.getStyleClass().add("content-container");
        mainContent.setSpacing(15);

        // Barre d'outils
        HBox toolBar = createToolBar();
        mainContent.getChildren().add(toolBar);

        // Tableau
        tableView = createTableView();
        VBox.setVgrow(tableView, Priority.ALWAYS);
        mainContent.getChildren().add(tableView);

        setContent(mainContent);
        refreshData();
    }

    private HBox createToolBar() {
        HBox toolBar = new HBox();
        toolBar.setSpacing(10);
        toolBar.setAlignment(Pos.CENTER_LEFT);

        Button addBtn = new Button("+ Ajouter un match");
        addBtn.getStyleClass().add("button-add");
        addBtn.setOnAction(e -> openAddMatchDialog());

        Button editBtn = new Button("✏️ Saisir résultat");
        editBtn.getStyleClass().add("button-secondary");
        editBtn.setOnAction(e -> openEditMatchDialog());

        Button deleteBtn = new Button("🗑️ Supprimer");
        deleteBtn.getStyleClass().add("button-danger");
        deleteBtn.setOnAction(e -> deleteMatch());

        Button refreshBtn = new Button("🔄 Rafraîchir");
        refreshBtn.getStyleClass().add("button-secondary");
        refreshBtn.setOnAction(e -> refreshData());

        toolBar.getChildren().addAll(addBtn, editBtn, deleteBtn, refreshBtn);
        return toolBar;
    }

    private TableView<Match> createTableView() {
        tableView = new TableView<>();
        tableView.getStyleClass().add("table-view");
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Match, String> journeeColumn = new TableColumn<>("Journée");
        journeeColumn.setCellValueFactory(cellData -> javafx.beans.property.SimpleStringProperty.asObject(
                String.valueOf(cellData.getValue().getJournee())));
        journeeColumn.setPrefWidth(80);

        TableColumn<Match, String> domicileColumn = new TableColumn<>("Domicile");
        domicileColumn.setCellValueFactory(cellData -> javafx.beans.property.SimpleStringProperty.asObject(
                cellData.getValue().getEquipeDomicileNom() != null ? cellData.getValue().getEquipeDomicileNom() : "-"));
        domicileColumn.setPrefWidth(150);

        TableColumn<Match, String> scoreColumn = new TableColumn<>("Score");
        scoreColumn.setCellValueFactory(cellData -> javafx.beans.property.SimpleStringProperty.asObject(
                cellData.getValue().getButsDomicile() + " - " + cellData.getValue().getButsExterieur()));
        scoreColumn.setPrefWidth(100);

        TableColumn<Match, String> exterieurColumn = new TableColumn<>("Extérieur");
        exterieurColumn.setCellValueFactory(cellData -> javafx.beans.property.SimpleStringProperty.asObject(
                cellData.getValue().getEquipeExterieurNom() != null ? cellData.getValue().getEquipeExterieurNom() : "-"));
        exterieurColumn.setPrefWidth(150);

        TableColumn<Match, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(cellData -> javafx.beans.property.SimpleStringProperty.asObject(
                cellData.getValue().getDateMatch() != null ? new SimpleDateFormat("dd/MM/yyyy").format(cellData.getValue().getDateMatch()) : "-"));
        dateColumn.setPrefWidth(120);

        TableColumn<Match, String> statutColumn = new TableColumn<>("Statut");
        statutColumn.setCellValueFactory(cellData -> javafx.beans.property.SimpleStringProperty.asObject(cellData.getValue().getStatut()));
        statutColumn.setPrefWidth(120);

        tableView.getColumns().addAll(journeeColumn, domicileColumn, scoreColumn, exterieurColumn, dateColumn, statutColumn);

        tableView.setRowFactory(tv -> {
            TableRow<Match> row = new TableRow<Match>() {
                @Override
                protected void updateItem(Match item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || getIndex() < 0) {
                        setStyle("");
                    } else {
                        if (getIndex() % 2 == 0) {
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
            List<Match> matches = matchDAO.getTous();
            
            // Charger les noms des équipes
            for (Match match : matches) {
                Equipe domicile = equipeDAO.getParId(match.getEquipeDomicileId());
                Equipe exterieur = equipeDAO.getParId(match.getEquipeExterieurId());
                
                if (domicile != null) {
                    match.setEquipeDomicileNom(domicile.getNom());
                }
                if (exterieur != null) {
                    match.setEquipeExterieurNom(exterieur.getNom());
                }
            }

            matchList = FXCollections.observableArrayList(matches);
            tableView.setItems(matchList);
        } catch (Exception e) {
            showError("Erreur lors du chargement des matchs");
            e.printStackTrace();
        }
    }

    private void openAddMatchDialog() {
        MatchFormDialog dialog = new MatchFormDialog(null, equipeDAO);
        Optional<Match> result = dialog.showAndWait();

        result.ifPresent(match -> {
            if (matchDAO.ajouter(match)) {
                showSuccess("Match ajouté avec succès");
                refreshData();
            } else {
                showError("Erreur lors de l'ajout du match");
            }
        });
    }

    private void openEditMatchDialog() {
        Match selectedMatch = tableView.getSelectionModel().getSelectedItem();
        if (selectedMatch == null) {
            showWarning("Veuillez sélectionner un match");
            return;
        }

        MatchFormDialog dialog = new MatchFormDialog(selectedMatch, equipeDAO);
        Optional<Match> result = dialog.showAndWait();

        result.ifPresent(match -> {
            if (matchDAO.modifier(match)) {
                showSuccess("Match modifié avec succès");
                refreshData();
            } else {
                showError("Erreur lors de la modification");
            }
        });
    }

    private void deleteMatch() {
        Match selectedMatch = tableView.getSelectionModel().getSelectedItem();
        if (selectedMatch == null) {
            showWarning("Veuillez sélectionner un match");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmation");
        confirmDialog.setHeaderText("Supprimer le match?");
        confirmDialog.setContentText("Êtes-vous sûr de vouloir supprimer ce match?");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (matchDAO.supprimer(selectedMatch.getId())) {
                showSuccess("Match supprimé");
                refreshData();
            } else {
                showError("Erreur lors de la suppression");
            }
        }
    }

    private void showSuccess(String message) {
        showAlert(Alert.AlertType.INFORMATION, "Succès", message);
    }

    private void showError(String message) {
        showAlert(Alert.AlertType.ERROR, "Erreur", message);
    }

    private void showWarning(String message) {
        showAlert(Alert.AlertType.WARNING, "Attention", message);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
