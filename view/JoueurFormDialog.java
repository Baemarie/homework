package view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import model.Joueur;
import model.Equipe;
import dao.EquipeDAO;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * JoueurFormDialog - Dialog pour ajouter/modifier un joueur
 */
public class JoueurFormDialog extends Dialog<Joueur> {

    private TextField nomField;
    private TextField prenomField;
    private DatePicker dateNaissancePicker;
    private TextField nationaliteField;
    private ComboBox<String> posteCombo;
    private Spinner<Integer> numeroSpinner;
    private ComboBox<Equipe> equipeCombo;
    private Joueur originalJoueur;
    private EquipeDAO equipeDAO;

    public JoueurFormDialog(Joueur joueur, EquipeDAO equipeDAO) {
        this.originalJoueur = joueur;
        this.equipeDAO = equipeDAO;

        setTitle(joueur == null ? "Ajouter un joueur" : "Modifier le joueur");
        setHeaderText(null);

        DialogPane dialogPane = new DialogPane();
        dialogPane.getStyleClass().add("dialog-pane");

        VBox content = new VBox();
        content.setPadding(new Insets(20));
        content.setSpacing(15);

        // Form grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(0));

        // Nom
        Label nomLabel = new Label("Nom *");
        nomLabel.setStyle("-fx-text-fill: #FFD700; -fx-font-weight: bold;");
        nomField = new TextField();
        nomField.getStyleClass().add("text-field");
        nomField.setPromptText("Ex: Mbappé");
        if (joueur != null) {
            nomField.setText(joueur.getNom());
        }
        grid.add(nomLabel, 0, 0);
        grid.add(nomField, 1, 0);

        // Prénom
        Label prenomLabel = new Label("Prénom *");
        prenomLabel.setStyle("-fx-text-fill: #FFD700; -fx-font-weight: bold;");
        prenomField = new TextField();
        prenomField.getStyleClass().add("text-field");
        prenomField.setPromptText("Ex: Kylian");
        if (joueur != null) {
            prenomField.setText(joueur.getPrenom());
        }
        grid.add(prenomLabel, 0, 1);
        grid.add(prenomField, 1, 1);

        // Date de naissance
        Label dateLabel = new Label("Date de naissance");
        dateLabel.setStyle("-fx-text-fill: #FFD700; -fx-font-weight: bold;");
        dateNaissancePicker = new DatePicker();
        dateNaissancePicker.getStyleClass().add("date-picker");
        if (joueur != null && joueur.getDateNaissance() != null) {
            dateNaissancePicker.setValue(
                    joueur.getDateNaissance().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            );
        } else {
            dateNaissancePicker.setValue(LocalDate.now().minusYears(20));
        }
        grid.add(dateLabel, 0, 2);
        grid.add(dateNaissancePicker, 1, 2);

        // Nationalité
        Label nationaliteLabel = new Label("Nationalité *");
        nationaliteLabel.setStyle("-fx-text-fill: #FFD700; -fx-font-weight: bold;");
        nationaliteField = new TextField();
        nationaliteField.getStyleClass().add("text-field");
        nationaliteField.setPromptText("Ex: France");
        if (joueur != null) {
            nationaliteField.setText(joueur.getNationalite());
        }
        grid.add(nationaliteLabel, 0, 3);
        grid.add(nationaliteField, 1, 3);

        // Poste
        Label posteLabel = new Label("Poste *");
        posteLabel.setStyle("-fx-text-fill: #FFD700; -fx-font-weight: bold;");
        posteCombo = new ComboBox<>();
        posteCombo.getStyleClass().add("combo-box");
        ObservableList<String> postes = FXCollections.observableArrayList(
                "Gardien", "Défenseur", "Milieu", "Attaquant"
        );
        posteCombo.setItems(postes);
        if (joueur != null) {
            posteCombo.setValue(joueur.getPoste());
        }
        grid.add(posteLabel, 0, 4);
        grid.add(posteCombo, 1, 4);

        // Numéro de maillot
        Label numeroLabel = new Label("Numéro de maillot *");
        numeroLabel.setStyle("-fx-text-fill: #FFD700; -fx-font-weight: bold;");
        numeroSpinner = new Spinner<>(1, 99, joueur != null ? joueur.getNumeroMaillot() : 1);
        numeroSpinner.getStyleClass().add("spinner");
        grid.add(numeroLabel, 0, 5);
        grid.add(numeroSpinner, 1, 5);

        // Équipe
        Label equipeLabel = new Label("Équipe *");
        equipeLabel.setStyle("-fx-text-fill: #FFD700; -fx-font-weight: bold;");
        equipeCombo = new ComboBox<>();
        equipeCombo.getStyleClass().add("combo-box");
        loadEquipes();
        if (joueur != null) {
            for (Equipe e : equipeCombo.getItems()) {
                if (e.getId() == joueur.getEquipeId()) {
                    equipeCombo.setValue(e);
                    break;
                }
            }
        }
        grid.add(equipeLabel, 0, 6);
        grid.add(equipeCombo, 1, 6);

        content.getChildren().add(grid);

        // Validation note
        Label noteLabel = new Label("* Champs obligatoires");
        noteLabel.getStyleClass().add("label-warning");
        content.getChildren().add(noteLabel);

        dialogPane.setContent(content);

        // Buttons
        ButtonType okButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialogPane.getButtonTypes().addAll(okButtonType, cancelButtonType);

        // Style buttons
        Button okButton = (Button) dialogPane.lookupButton(okButtonType);
        okButton.getStyleClass().add("button-primary");
        Button cancelButton = (Button) dialogPane.lookupButton(cancelButtonType);
        cancelButton.getStyleClass().add("button-secondary");

        setDialogPane(dialogPane);

        setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                if (validateForm()) {
                    return createJoueurFromForm();
                }
            }
            return null;
        });
    }

    private void loadEquipes() {
        try {
            List<Equipe> equipes = equipeDAO.getTous();
            ObservableList<Equipe> equipeList = FXCollections.observableArrayList(equipes);
            equipeCombo.setItems(equipeList);
            equipeCombo.setConverter(new javafx.util.StringConverter<Equipe>() {
                @Override
                public String toString(Equipe object) {
                    return object != null ? object.getNom() : "";
                }

                @Override
                public Equipe fromString(String string) {
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean validateForm() {
        if (nomField.getText().trim().isEmpty()) {
            showAlert("Erreur", "Le nom est obligatoire");
            return false;
        }
        if (prenomField.getText().trim().isEmpty()) {
            showAlert("Erreur", "Le prénom est obligatoire");
            return false;
        }
        if (nationaliteField.getText().trim().isEmpty()) {
            showAlert("Erreur", "La nationalité est obligatoire");
            return false;
        }
        if (posteCombo.getValue() == null) {
            showAlert("Erreur", "Le poste est obligatoire");
            return false;
        }
        if (equipeCombo.getValue() == null) {
            showAlert("Erreur", "L'équipe est obligatoire");
            return false;
        }
        return true;
    }

    private Joueur createJoueurFromForm() {
        Joueur joueur = originalJoueur != null ? originalJoueur : new Joueur();

        joueur.setNom(nomField.getText().trim());
        joueur.setPrenom(prenomField.getText().trim());
        joueur.setNationalite(nationaliteField.getText().trim());
        joueur.setPoste(posteCombo.getValue());
        joueur.setNumeroMaillot(numeroSpinner.getValue());
        joueur.setEquipeId(equipeCombo.getValue().getId());

        Date date = java.sql.Date.valueOf(dateNaissancePicker.getValue());
        joueur.setDateNaissance(date);

        return joueur;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
