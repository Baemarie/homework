package view;

import dao.EquipeDAO;
import model.Equipe;
import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class EquipeFormDialog extends JDialog {

    private EquipeDAO equipeDAO;
    private Equipe equipe;
    private JTextField txtNom, txtVille, txtEntraineur, txtDate;
    private boolean modeModification;

    public EquipeFormDialog(Frame parent, Equipe equipe, EquipeDAO dao) {
        super(parent, equipe == null ? "Ajouter une Équipe" : "Modifier l'Équipe", true);
        this.equipeDAO = dao;
        this.equipe = equipe;
        this.modeModification = (equipe != null);
        initUI();
        if (modeModification) remplirChamps();
    }

    private void initUI() {
        setSize(400, 280);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        txtNom        = new JTextField(20);
        txtVille      = new JTextField(20);
        txtEntraineur = new JTextField(20);
        txtDate       = new JTextField(20);
        txtDate.setToolTipText("Format : AAAA-MM-JJ");

        String[] labels = {"Nom *", "Ville", "Entraîneur", "Date création (AAAA-MM-JJ)"};
        JTextField[] fields = {txtNom, txtVille, txtEntraineur, txtDate};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            formPanel.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1;
            formPanel.add(fields[i], gbc);
        }

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton btnSave   = new JButton(modeModification ? "✏️ Modifier" : "💾 Enregistrer");
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

        add(formPanel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void remplirChamps() {
        txtNom.setText(equipe.getNom());
        txtVille.setText(equipe.getVille());
        txtEntraineur.setText(equipe.getEntraineur());
        if (equipe.getDateCreation() != null) {
            txtDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(equipe.getDateCreation()));
        }
    }

    private void sauvegarder() {
        String nom = txtNom.getText().trim();
        if (nom.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le nom de l'équipe est obligatoire.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        java.util.Date date = null;
        if (!txtDate.getText().trim().isEmpty()) {
            try {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(txtDate.getText().trim());
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(this, "Format de date invalide. Utilisez AAAA-MM-JJ.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if (modeModification) {
            equipe.setNom(nom);
            equipe.setVille(txtVille.getText().trim());
            equipe.setEntraineur(txtEntraineur.getText().trim());
            equipe.setDateCreation(date);
            equipeDAO.modifier(equipe);
        } else {
            Equipe nouv = new Equipe(nom, txtVille.getText().trim(), txtEntraineur.getText().trim(), date);
            equipeDAO.ajouter(nouv);
        }
        dispose();
    }
}
