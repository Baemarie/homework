package view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import dao.EquipeDAO;
import model.Equipe;
import java.util.List;
import java.util.Optional;

/**
 * EquipePanel - Gestion des équipes
 */
public class EquipePanel extends ScrollPane {

    private EquipeDAO equipeDAO;
    private TableView<Equipe> tableView;
    private ObservableList<Equipe> equipeList;

    public EquipePanel(EquipeDAO equipeDAO) {
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

        Button addBtn = new Button("+ Ajouter une équipe");
        addBtn.getStyleClass().add("button-add");
        addBtn.setOnAction(e -> openAddEquipeDialog());

        Button editBtn = new Button("✏️ Modifier");
        editBtn.getStyleClass().add("button-secondary");
        editBtn.setOnAction(e -> openEditEquipeDialog());

        Button deleteBtn = new Button("🗑️ Supprimer");
        deleteBtn.getStyleClass().add("button-danger");
        deleteBtn.setOnAction(e -> deleteEquipe());

        Button refreshBtn = new Button("🔄 Rafraîchir");
        refreshBtn.getStyleClass().add("button-secondary");
        refreshBtn.setOnAction(e -> refreshData());

        toolBar.getChildren().addAll(addBtn, editBtn, deleteBtn, refreshBtn);
        return toolBar;
    }

    private TableView<Equipe> createTableView() {
        tableView = new TableView<>();
        tableView.getStyleClass().add("table-view");
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Equipe, String> nomColumn = new TableColumn<>("Nom");
        nomColumn.setCellValueFactory(cellData -> javafx.beans.property.SimpleStringProperty.asObject(cellData.getValue().getNom()));
        nomColumn.setPrefWidth(150);

        TableColumn<Equipe, String> villeColumn = new TableColumn<>("Ville");
        villeColumn.setCellValueFactory(cellData -> javafx.beans.property.SimpleStringProperty.asObject(cellData.getValue().getVille()));
        villeColumn.setPrefWidth(150);

        TableColumn<Equipe, String> entraineurColumn = new TableColumn<>("Entraîneur");
        entraineurColumn.setCellValueFactory(cellData -> javafx.beans.property.SimpleStringProperty.asObject(cellData.getValue().getEntraineur()));
        entraineurColumn.setPrefWidth(150);

        TableColumn<Equipe, String> dateColumn = new TableColumn<>("Date de création");
        dateColumn.setCellValueFactory(cellData -> javafx.beans.property.SimpleStringProperty.asObject(
                cellData.getValue().getDateCreation() != null ? cellData.getValue().getDateCreation().toString() : "-"));
        dateColumn.setPrefWidth(120);

        tableView.getColumns().addAll(nomColumn, villeColumn, entraineurColumn, dateColumn);
        tableView.setRowFactory(tv -> {
            TableRow<Equipe> row = new TableRow<Equipe>() {
                @Override
                protected void updateItem(Equipe item, boolean empty) {
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
            List<Equipe> equipes = equipeDAO.getTous();
            equipeList = FXCollections.observableArrayList(equipes);
            tableView.setItems(equipeList);
        } catch (Exception e) {
            showError("Erreur lors du chargement des équipes");
            e.printStackTrace();
        }
    }

    private void openAddEquipeDialog() {
        EquipeFormDialog dialog = new EquipeFormDialog(null);
        Optional<Equipe> result = dialog.showAndWait();

        result.ifPresent(equipe -> {
            if (equipeDAO.ajouter(equipe)) {
                showSuccess("Équipe ajoutée avec succès");
                refreshData();
            } else {
                showError("Erreur lors de l'ajout de l'équipe");
            }
        });
    }

    private void openEditEquipeDialog() {
        Equipe selectedEquipe = tableView.getSelectionModel().getSelectedItem();
        if (selectedEquipe == null) {
            showWarning("Veuillez sélectionner une équipe");
            return;
        }

        EquipeFormDialog dialog = new EquipeFormDialog(selectedEquipe);
        Optional<Equipe> result = dialog.showAndWait();

        result.ifPresent(equipe -> {
            if (equipeDAO.modifier(equipe)) {
                showSuccess("Équipe modifiée avec succès");
                refreshData();
            } else {
                showError("Erreur lors de la modification");
            }
        });
    }

    private void deleteEquipe() {
        Equipe selectedEquipe = tableView.getSelectionModel().getSelectedItem();
        if (selectedEquipe == null) {
            showWarning("Veuillez sélectionner une équipe");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmation");
        confirmDialog.setHeaderText("Supprimer l'équipe?");
        confirmDialog.setContentText("Êtes-vous sûr de vouloir supprimer " + selectedEquipe.getNom() + "?");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (equipeDAO.supprimer(selectedEquipe.getId())) {
                showSuccess("Équipe supprimée");
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
