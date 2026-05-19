package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import model.Equipe;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * EquipeFormDialog - Dialog pour ajouter/modifier une équipe
 */
public class EquipeFormDialog extends Dialog<Equipe> {

    private TextField nomField;
    private TextField villeField;
    private TextField entraineurField;
    private DatePicker dateCreationPicker;
    private Equipe originalEquipe;

    public EquipeFormDialog(Equipe equipe) {
        this.originalEquipe = equipe;

        setTitle(equipe == null ? "Ajouter une équipe" : "Modifier l'équipe");
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
        Label nomLabel = new Label("Nom de l'équipe *");
        nomLabel.setStyle("-fx-text-fill: #FFD700; -fx-font-weight: bold;");
        nomField = new TextField();
        nomField.getStyleClass().add("text-field");
        nomField.setPromptText("Ex: AS Monaco");
        if (equipe != null) {
            nomField.setText(equipe.getNom());
        }
        grid.add(nomLabel, 0, 0);
        grid.add(nomField, 1, 0);
        GridPane.setColumnIndex(nomField, 1);

        // Ville
        Label villeLabel = new Label("Ville *");
        villeLabel.setStyle("-fx-text-fill: #FFD700; -fx-font-weight: bold;");
        villeField = new TextField();
        villeField.getStyleClass().add("text-field");
        villeField.setPromptText("Ex: Monaco");
        if (equipe != null) {
            villeField.setText(equipe.getVille());
        }
        grid.add(villeLabel, 0, 1);
        grid.add(villeField, 1, 1);

        // Entraîneur
        Label entraineurLabel = new Label("Entraîneur *");
        entraineurLabel.setStyle("-fx-text-fill: #FFD700; -fx-font-weight: bold;");
        entraineurField = new TextField();
        entraineurField.getStyleClass().add("text-field");
        entraineurField.setPromptText("Ex: Carlo Ancelotti");
        if (equipe != null) {
            entraineurField.setText(equipe.getEntraineur());
        }
        grid.add(entraineurLabel, 0, 2);
        grid.add(entraineurField, 1, 2);

        // Date de création
        Label dateLabel = new Label("Date de création");
        dateLabel.setStyle("-fx-text-fill: #FFD700; -fx-font-weight: bold;");
        dateCreationPicker = new DatePicker();
        dateCreationPicker.getStyleClass().add("date-picker");
        if (equipe != null && equipe.getDateCreation() != null) {
            dateCreationPicker.setValue(
                    equipe.getDateCreation().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            );
        } else {
            dateCreationPicker.setValue(LocalDate.now());
        }
        grid.add(dateLabel, 0, 3);
        grid.add(dateCreationPicker, 1, 3);

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
                    return createEquipeFromForm();
                }
            }
            return null;
        });
    }

    private boolean validateForm() {
        if (nomField.getText().trim().isEmpty()) {
            showAlert("Erreur", "Le nom de l'équipe est obligatoire");
            return false;
        }
        if (villeField.getText().trim().isEmpty()) {
            showAlert("Erreur", "La ville est obligatoire");
            return false;
        }
        if (entraineurField.getText().trim().isEmpty()) {
            showAlert("Erreur", "L'entraîneur est obligatoire");
            return false;
        }
        return true;
    }

    private Equipe createEquipeFromForm() {
        Equipe equipe = originalEquipe != null ? originalEquipe : new Equipe();

        equipe.setNom(nomField.getText().trim());
        equipe.setVille(villeField.getText().trim());
        equipe.setEntraineur(entraineurField.getText().trim());

        Date date = java.sql.Date.valueOf(dateCreationPicker.getValue());
        equipe.setDateCreation(date);

        return equipe;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
