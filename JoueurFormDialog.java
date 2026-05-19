package view;

import dao.EquipeDAO;
import dao.JoueurDAO;
import model.Equipe;
import model.Joueur;
import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class JoueurFormDialog extends JDialog {

    private JoueurDAO joueurDAO;
    private EquipeDAO equipeDAO;
    private Joueur joueur;
    private boolean modeModif;

    private JTextField txtNom, txtPrenom, txtNationalite, txtNumero, txtDate;
    private JComboBox<String> cmbPoste, cmbEquipe;
    private List<Equipe> equipes;

    private final String[] POSTES = {"Gardien", "Défenseur", "Milieu", "Attaquant"};

    public JoueurFormDialog(Frame parent, Joueur joueur, JoueurDAO jDao, EquipeDAO eDao) {
        super(parent, joueur == null ? "Ajouter un Joueur" : "Modifier le Joueur", true);
        this.joueur = joueur;
        this.joueurDAO = jDao;
        this.equipeDAO = eDao;
        this.modeModif = (joueur != null);
        this.equipes = eDao.getTous();
        initUI();
        if (modeModif) remplir();
    }

    private void initUI() {
        setSize(420, 360);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        txtPrenom      = new JTextField(18);
        txtNom         = new JTextField(18);
        txtDate        = new JTextField(18); txtDate.setToolTipText("AAAA-MM-JJ");
        txtNationalite = new JTextField(18);
        txtNumero      = new JTextField(18);
        cmbPoste       = new JComboBox<>(POSTES);
        cmbEquipe      = new JComboBox<>();
        equipes.forEach(e -> cmbEquipe.addItem(e.getNom()));

        String[] labels = {"Prénom *", "Nom *", "Date naissance", "Nationalité", "N° Maillot *", "Poste *", "Équipe *"};
        Component[] fields = {txtPrenom, txtNom, txtDate, txtNationalite, txtNumero, cmbPoste, cmbEquipe};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            form.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1;
            form.add(fields[i], gbc);
        }

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton btnSave   = new JButton(modeModif ? "✏️ Modifier" : "💾 Enregistrer");
        JButton btnCancel = new JButton("❌ Annuler");

        btnSave.setBackground(new Color(34, 139, 34));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Arial", Font.BOLD, 12));
        btnSave.setFocusPainted(false);

        btnCancel.setBackground(new Color(178, 34, 34));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFont(new Font("Arial", Font.BOLD, 12));
        btnCancel.setFocusPainted(false);

        btnSave.addActionListener(e -> sauvegarder());
        btnCancel.addActionListener(e -> dispose());

        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);

        add(form, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void remplir() {
        txtPrenom.setText(joueur.getPrenom());
        txtNom.setText(joueur.getNom());
        txtNationalite.setText(joueur.getNationalite());
        txtNumero.setText(String.valueOf(joueur.getNumeroMaillot()));
        cmbPoste.setSelectedItem(joueur.getPoste());
        if (joueur.getDateNaissance() != null)
            txtDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(joueur.getDateNaissance()));
        for (int i = 0; i < equipes.size(); i++) {
            if (equipes.get(i).getId() == joueur.getEquipeId()) {
                cmbEquipe.setSelectedIndex(i);
                break;
            }
        }
    }

    private void sauvegarder() {
        String prenom = txtPrenom.getText().trim();
        String nom = txtNom.getText().trim();
        if (prenom.isEmpty() || nom.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Prénom et Nom sont obligatoires.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int numero;
        try { numero = Integer.parseInt(txtNumero.getText().trim()); }
        catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Numéro de maillot invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        java.util.Date date = null;
        if (!txtDate.getText().trim().isEmpty()) {
            try { date = new SimpleDateFormat("yyyy-MM-dd").parse(txtDate.getText().trim()); }
            catch (ParseException ex) {
                JOptionPane.showMessageDialog(this, "Format date invalide (AAAA-MM-JJ).", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        int equipeId = equipes.get(cmbEquipe.getSelectedIndex()).getId();
        String poste = (String) cmbPoste.getSelectedItem();

        if (modeModif) {
            joueur.setPrenom(prenom); joueur.setNom(nom);
            joueur.setDateNaissance(date); joueur.setNationalite(txtNationalite.getText().trim());
            joueur.setPoste(poste); joueur.setNumeroMaillot(numero); joueur.setEquipeId(equipeId);
            joueurDAO.modifier(joueur);
        } else {
            Joueur j = new Joueur(nom, prenom, date, txtNationalite.getText().trim(), poste, numero, equipeId);
            joueurDAO.ajouter(j);
        }
        dispose();
    }
}
