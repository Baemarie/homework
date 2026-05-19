package view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Match;
import model.Equipe;
import dao.EquipeDAO;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * MatchFormDialog - Dialog pour ajouter/modifier un match
 */
public class MatchFormDialog extends Dialog<Match> {

    private ComboBox<Equipe> domicileCombo;
    private ComboBox<Equipe> exterieurCombo;
    private DatePicker dateMatchPicker;
    private Spinner<Integer> journeeSpinner;
    private TextField phaseField;
    private Spinner<Integer> butsDomicileSpinner;
    private Spinner<Integer> butsExterieurSpinner;
    private ComboBox<String> statutCombo;
    private Match originalMatch;
    private EquipeDAO equipeDAO;

    public MatchFormDialog(Match match, EquipeDAO equipeDAO) {
        this.originalMatch = match;
        this.equipeDAO = equipeDAO;

        setTitle(match == null ? "Ajouter un match" : "Modifier le match");
        setHeaderText(null);

        DialogPane dialogPane = new DialogPane();
        dialogPane.getStyleClass().add("dialog-pane");
        dialogPane.setPrefWidth(600);

        VBox content = new VBox();
        content.setPadding(new Insets(20));
        content.setSpacing(15);

        // Form grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(0));

        // Journée
        Label journeeLabel = new Label("Journée *");
        journeeLabel.setStyle("-fx-text-fill: #FFD700; -fx-font-weight: bold;");
        journeeSpinner = new Spinner<>(1, 38, match != null ? match.getJournee() : 1);
        journeeSpinner.getStyleClass().add("spinner");
        grid.add(journeeLabel, 0, 0);
        grid.add(journeeSpinner, 1, 0);

        // Phase
        Label phaseLabel = new Label("Phase *");
        phaseLabel.setStyle("-fx-text-fill: #FFD700; -fx-font-weight: bold;");
        phaseField = new TextField();
        phaseField.getStyleClass().add("text-field");
        phaseField.setPromptText("Ex: Groupe A");
        if (match != null) {
            phaseField.setText(match.getPhase());
        }
        grid.add(phaseLabel, 0, 1);
        grid.add(phaseField, 1, 1);

        // Date
        Label dateLabel = new Label("Date du match *");
        dateLabel.setStyle("-fx-text-fill: #FFD700; -fx-font-weight: bold;");
        dateMatchPicker = new DatePicker();
        dateMatchPicker.getStyleClass().add("date-picker");
        if (match != null && match.getDateMatch() != null) {
            dateMatchPicker.setValue(
                    match.getDateMatch().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            );
        } else {
            dateMatchPicker.setValue(LocalDate.now());
        }
        grid.add(dateLabel, 0, 2);
        grid.add(dateMatchPicker, 1, 2);

        // Équipe domicile
        Label domicileLabel = new Label("Équipe Domicile *");
        domicileLabel.setStyle("-fx-text-fill: #FFD700; -fx-font-weight: bold;");
        domicileCombo = new ComboBox<>();
        domicileCombo.getStyleClass().add("combo-box");
        loadEquipes();
        if (match != null) {
            for (Equipe e : domicileCombo.getItems()) {
                if (e.getId() == match.getEquipeDomicileId()) {
                    domicileCombo.setValue(e);
                    break;
                }
            }
        }
        grid.add(domicileLabel, 0, 3);
        grid.add(domicileCombo, 1, 3);

        // Buts domicile
        Label butsDomLabel = new Label("Buts Domicile");
        butsDomLabel.setStyle("-fx-text-fill: #FFD700; -fx-font-weight: bold;");
        butsDomicileSpinner = new Spinner<>(0, 20, match != null ? match.getButsDomicile() : 0);
        butsDomicileSpinner.getStyleClass().add("spinner");
        butsDomicileSpinner.setPrefWidth(100);
        grid.add(butsDomLabel, 0, 4);
        grid.add(butsDomicileSpinner, 1, 4);

        // Équipe extérieur
        Label exterieurLabel = new Label("Équipe Extérieur *");
        exterieurLabel.setStyle("-fx-text-fill: #FFD700; -fx-font-weight: bold;");
        exterieurCombo = new ComboBox<>();
        exterieurCombo.getStyleClass().add("combo-box");
        exterieurCombo.setItems(domicileCombo.getItems());
        if (match != null) {
            for (Equipe e : exterieurCombo.getItems()) {
                if (e.getId() == match.getEquipeExterieurId()) {
                    exterieurCombo.setValue(e);
                    break;
                }
            }
        }
        grid.add(exterieurLabel, 0, 5);
        grid.add(exterieurCombo, 1, 5);

        // Buts extérieur
        Label butsExtLabel = new Label("Buts Extérieur");
        butsExtLabel.setStyle("-fx-text-fill: #FFD700; -fx-font-weight: bold;");
        butsExterieurSpinner = new Spinner<>(0, 20, match != null ? match.getButsExterieur() : 0);
        butsExterieurSpinner.getStyleClass().add("spinner");
        butsExterieurSpinner.setPrefWidth(100);
        grid.add(butsExtLabel, 0, 6);
        grid.add(butsExterieurSpinner, 1, 6);

        // Statut
        Label statutLabel = new Label("Statut *");
        statutLabel.setStyle("-fx-text-fill: #FFD700; -fx-font-weight: bold;");
        statutCombo = new ComboBox<>();
        statutCombo.getStyleClass().add("combo-box");
        ObservableList<String> statuts = FXCollections.observableArrayList("Planifié", "En cours", "Terminé");
        statutCombo.setItems(statuts);
        if (match != null) {
            statutCombo.setValue(match.getStatut());
        } else {
            statutCombo.setValue("Planifié");
        }
        grid.add(statutLabel, 0, 7);
        grid.add(statutCombo, 1, 7);

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
                    return createMatchFromForm();
                }
            }
            return null;
        });
    }

    private void loadEquipes() {
        try {
            List<Equipe> equipes = equipeDAO.getTous();
            ObservableList<Equipe> equipeList = FXCollections.observableArrayList(equipes);
            domicileCombo.setItems(equipeList);
            domicileCombo.setConverter(new javafx.util.StringConverter<Equipe>() {
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
        if (phaseField.getText().trim().isEmpty()) {
            showAlert("Erreur", "La phase est obligatoire");
            return false;
        }
        if (domicileCombo.getValue() == null) {
            showAlert("Erreur", "L'équipe domicile est obligatoire");
            return false;
        }
        if (exterieurCombo.getValue() == null) {
            showAlert("Erreur", "L'équipe extérieur est obligatoire");
            return false;
        }
        if (domicileCombo.getValue().getId() == exterieurCombo.getValue().getId()) {
            showAlert("Erreur", "Les deux équipes doivent être différentes");
            return false;
        }
        if (statutCombo.getValue() == null) {
            showAlert("Erreur", "Le statut est obligatoire");
            return false;
        }
        return true;
    }

    private Match createMatchFromForm() {
        Match match = originalMatch != null ? originalMatch : new Match();

        match.setJournee(journeeSpinner.getValue());
        match.setPhase(phaseField.getText().trim());
        match.setEquipeDomicileId(domicileCombo.getValue().getId());
        match.setEquipeExterieurId(exterieurCombo.getValue().getId());
        match.setButsDomicile(butsDomicileSpinner.getValue());
        match.setButsExterieur(butsExterieurSpinner.getValue());
        match.setStatut(statutCombo.getValue());

        Date date = java.sql.Date.valueOf(dateMatchPicker.getValue());
        match.setDateMatch(date);

        return match;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
