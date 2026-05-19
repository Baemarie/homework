package view;

import dao.ClassementDAO;
import dao.EquipeDAO;
import dao.MatchDAO;
import model.Equipe;
import model.Match;
import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

// ===================== Dialog Planifier Match =====================
class MatchFormDialog extends JDialog {

    private MatchDAO matchDAO;
    private EquipeDAO equipeDAO;
    private List<Equipe> equipes;
    private JComboBox<String> cmbDomicile, cmbExterieur, cmbPhase;
    private JTextField txtDate, txtJournee;

    private final String[] PHASES = {"Phase de groupes", "Quart de finale", "Demi-finale", "Finale"};

    public MatchFormDialog(Frame parent, MatchDAO mDao, EquipeDAO eDao) {
        super(parent, "Planifier un Match", true);
        this.matchDAO = mDao;
        this.equipeDAO = eDao;
        this.equipes = eDao.getTous();
        initUI();
    }

    private void initUI() {
        setSize(420, 300);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        cmbDomicile  = new JComboBox<>();
        cmbExterieur = new JComboBox<>();
        equipes.forEach(e -> { cmbDomicile.addItem(e.getNom()); cmbExterieur.addItem(e.getNom()); });
        if (equipes.size() > 1) cmbExterieur.setSelectedIndex(1);

        txtDate    = new JTextField("2026-06-01 15:00", 18);
        txtJournee = new JTextField("1", 18);
        cmbPhase   = new JComboBox<>(PHASES);

        String[] labels = {"Équipe domicile *", "Équipe extérieur *", "Date (AAAA-MM-JJ HH:mm)", "Journée", "Phase"};
        Component[] fields = {cmbDomicile, cmbExterieur, txtDate, txtJournee, cmbPhase};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; form.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1; form.add(fields[i], gbc);
        }

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton btnSave   = creerBouton("💾 Planifier",  new Color(34, 139, 34));
        JButton btnCancel = creerBouton("❌ Annuler",    new Color(178, 34, 34));
        btnSave.addActionListener(e -> sauvegarder());
        btnCancel.addActionListener(e -> dispose());
        btnPanel.add(btnSave); btnPanel.add(btnCancel);

        add(form, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void sauvegarder() {
        if (cmbDomicile.getSelectedIndex() == cmbExterieur.getSelectedIndex()) {
            JOptionPane.showMessageDialog(this, "Les deux équipes doivent être différentes.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int domId = equipes.get(cmbDomicile.getSelectedIndex()).getId();
        int extId = equipes.get(cmbExterieur.getSelectedIndex()).getId();
        int journee;
        try { journee = Integer.parseInt(txtJournee.getText().trim()); }
        catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Journée invalide.", "Erreur", JOptionPane.ERROR_MESSAGE); return;
        }
        java.util.Date date = null;
        try { date = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(txtDate.getText().trim()); }
        catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, "Format date invalide (AAAA-MM-JJ HH:mm).", "Erreur", JOptionPane.ERROR_MESSAGE); return;
        }
        Match m = new Match(domId, extId, date, journee, (String) cmbPhase.getSelectedItem());
        matchDAO.ajouter(m);
        dispose();
    }

    private JButton creerBouton(String t, Color c) {
        JButton b = new JButton(t);
        b.setBackground(c); b.setForeground(Color.WHITE);
        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setFocusPainted(false); b.setBorderPainted(false);
        return b;
    }
}

// ===================== Dialog Saisir Résultat =====================
class ResultatDialog extends JDialog {

    private MatchDAO matchDAO;
    private ClassementDAO classementDAO;
    private Match match;
    private JSpinner spnDomicile, spnExterieur;

    public ResultatDialog(Frame parent, Match match, MatchDAO mDao, ClassementDAO cDao) {
        super(parent, "Saisir le Résultat", true);
        this.match = match;
        this.matchDAO = mDao;
        this.classementDAO = cDao;
        initUI();
    }

    private void initUI() {
        setSize(380, 220);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));

        JPanel info = new JPanel(new FlowLayout(FlowLayout.CENTER));
        info.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        JLabel lblMatch = new JLabel(match.getEquipeDomicileNom() + "  vs  " + match.getEquipeExterieurNom());
        lblMatch.setFont(new Font("Arial", Font.BOLD, 15));
        info.add(lblMatch);

        JPanel scorePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        spnDomicile  = new JSpinner(new SpinnerNumberModel(0, 0, 20, 1));
        spnExterieur = new JSpinner(new SpinnerNumberModel(0, 0, 20, 1));
        spnDomicile.setPreferredSize(new Dimension(60, 35));
        spnExterieur.setPreferredSize(new Dimension(60, 35));
        ((JSpinner.DefaultEditor) spnDomicile.getEditor()).getTextField().setFont(new Font("Arial", Font.BOLD, 18));
        ((JSpinner.DefaultEditor) spnExterieur.getEditor()).getTextField().setFont(new Font("Arial", Font.BOLD, 18));

        scorePanel.add(new JLabel(match.getEquipeDomicileNom()));
        scorePanel.add(spnDomicile);
        scorePanel.add(new JLabel(" - "));
        scorePanel.add(spnExterieur);
        scorePanel.add(new JLabel(match.getEquipeExterieurNom()));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton btnSave   = new JButton("💾 Enregistrer");
        JButton btnCancel = new JButton("❌ Annuler");

        btnSave.setBackground(new Color(34, 139, 34));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Arial", Font.BOLD, 12));
        btnSave.setFocusPainted(false);

        btnCancel.setBackground(new Color(178, 34, 34));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFont(new Font("Arial", Font.BOLD, 12));
        btnCancel.setFocusPainted(false);

        btnSave.addActionListener(e -> {
            int dom = (int) spnDomicile.getValue();
            int ext = (int) spnExterieur.getValue();
            matchDAO.saisirResultat(match.getId(), dom, ext);
            match.setButsDomicile(dom);
            match.setButsExterieur(ext);
            classementDAO.mettreAJourApresMatch(match);
            dispose();
        });
        btnCancel.addActionListener(e -> dispose());

        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);

        add(info, BorderLayout.NORTH);
        add(scorePanel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
    }
}
