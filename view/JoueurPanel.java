package view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import dao.JoueurDAO;
import dao.EquipeDAO;
import model.Joueur;
import model.Equipe;
import java.util.List;
import java.util.Optional;

/**
 * JoueurPanel - Gestion des joueurs
 */
public class JoueurPanel extends ScrollPane {

    private JoueurDAO joueurDAO;
    private EquipeDAO equipeDAO;
    private TableView<Joueur> tableView;
    private ObservableList<Joueur> joueurList;

    public JoueurPanel(JoueurDAO joueurDAO, EquipeDAO equipeDAO) {
        this.joueurDAO = joueurDAO;
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

        Button addBtn = new Button("+ Ajouter un joueur");
        addBtn.getStyleClass().add("button-add");
        addBtn.setOnAction(e -> openAddJoueurDialog());

        Button editBtn = new Button("✏️ Modifier");
        editBtn.getStyleClass().add("button-secondary");
        editBtn.setOnAction(e -> openEditJoueurDialog());

        Button deleteBtn = new Button("🗑️ Supprimer");
        deleteBtn.getStyleClass().add("button-danger");
        deleteBtn.setOnAction(e -> deleteJoueur());

        Button refreshBtn = new Button("🔄 Rafraîchir");
        refreshBtn.getStyleClass().add("button-secondary");
        refreshBtn.setOnAction(e -> refreshData());

        toolBar.getChildren().addAll(addBtn, editBtn, deleteBtn, refreshBtn);
        return toolBar;
    }

    private TableView<Joueur> createTableView() {
        tableView = new TableView<>();
        tableView.getStyleClass().add("table-view");
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Joueur, String> nomColumn = new TableColumn<>("Nom");
        nomColumn.setCellValueFactory(cellData -> javafx.beans.property.SimpleStringProperty.asObject(cellData.getValue().getNom()));
        nomColumn.setPrefWidth(120);

        TableColumn<Joueur, String> prenomColumn = new TableColumn<>("Prénom");
        prenomColumn.setCellValueFactory(cellData -> javafx.beans.property.SimpleStringProperty.asObject(cellData.getValue().getPrenom()));
        prenomColumn.setPrefWidth(120);

        TableColumn<Joueur, String> posteColumn = new TableColumn<>("Poste");
        posteColumn.setCellValueFactory(cellData -> javafx.beans.property.SimpleStringProperty.asObject(cellData.getValue().getPoste()));
        posteColumn.setPrefWidth(100);

        TableColumn<Joueur, Integer> numeroColumn = new TableColumn<>("Numéro");
        numeroColumn.setCellValueFactory(cellData -> javafx.beans.property.SimpleObjectProperty.asObject(cellData.getValue().getNumeroMaillot()));
        numeroColumn.setPrefWidth(80);

        TableColumn<Joueur, String> equipeColumn = new TableColumn<>("Équipe");
        equipeColumn.setCellValueFactory(cellData -> javafx.beans.property.SimpleStringProperty.asObject(
                cellData.getValue().getEquipeNom() != null ? cellData.getValue().getEquipeNom() : "-"));
        equipeColumn.setPrefWidth(150);

        TableColumn<Joueur, String> nationaliteColumn = new TableColumn<>("Nationalité");
        nationaliteColumn.setCellValueFactory(cellData -> javafx.beans.property.SimpleStringProperty.asObject(cellData.getValue().getNationalite()));
        nationaliteColumn.setPrefWidth(120);

        tableView.getColumns().addAll(nomColumn, prenomColumn, posteColumn, numeroColumn, equipeColumn, nationaliteColumn);

        tableView.setRowFactory(tv -> {
            TableRow<Joueur> row = new TableRow<Joueur>() {
                @Override
                protected void updateItem(Joueur item, boolean empty) {
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
            List<Joueur> joueurs = joueurDAO.getTous();
            
            // Charger les noms des équipes
            for (Joueur joueur : joueurs) {
                Equipe equipe = equipeDAO.getParId(joueur.getEquipeId());
                if (equipe != null) {
                    joueur.setEquipeNom(equipe.getNom());
                }
            }

            joueurList = FXCollections.observableArrayList(joueurs);
            tableView.setItems(joueurList);
        } catch (Exception e) {
            showError("Erreur lors du chargement des joueurs");
            e.printStackTrace();
        }
    }

    private void openAddJoueurDialog() {
        JoueurFormDialog dialog = new JoueurFormDialog(null, equipeDAO);
        Optional<Joueur> result = dialog.showAndWait();

        result.ifPresent(joueur -> {
            if (joueurDAO.ajouter(joueur)) {
                showSuccess("Joueur ajouté avec succès");
                refreshData();
            } else {
                showError("Erreur lors de l'ajout du joueur");
            }
        });
    }

    private void openEditJoueurDialog() {
        Joueur selectedJoueur = tableView.getSelectionModel().getSelectedItem();
        if (selectedJoueur == null) {
            showWarning("Veuillez sélectionner un joueur");
            return;
        }

        JoueurFormDialog dialog = new JoueurFormDialog(selectedJoueur, equipeDAO);
        Optional<Joueur> result = dialog.showAndWait();

        result.ifPresent(joueur -> {
            if (joueurDAO.modifier(joueur)) {
                showSuccess("Joueur modifié avec succès");
                refreshData();
            } else {
                showError("Erreur lors de la modification");
            }
        });
    }

    private void deleteJoueur() {
        Joueur selectedJoueur = tableView.getSelectionModel().getSelectedItem();
        if (selectedJoueur == null) {
            showWarning("Veuillez sélectionner un joueur");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmation");
        confirmDialog.setHeaderText("Supprimer le joueur?");
        confirmDialog.setContentText("Êtes-vous sûr de vouloir supprimer " + selectedJoueur.getPrenom() + " " + selectedJoueur.getNom() + "?");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (joueurDAO.supprimer(selectedJoueur.getId())) {
                showSuccess("Joueur supprimé");
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
