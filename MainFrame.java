package view;

import dao.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainFrame extends JFrame {

    private JTabbedPane tabbedPane;
    private EquipePanel equipePanel;
    private JoueurPanel joueurPanel;
    private MatchPanel matchPanel;
    private ClassementPanel classementPanel;
    private StatistiquesPanel statistiquesPanel;
    private JLabel statusLabel;

    public MainFrame() {
        initUI();
    }

    private void initUI() {
        setTitle("🏆 Gestion Championnat de Football");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);

        // Couleurs thème football
        Color couleurPrimaire  = new Color(0, 100, 0);    // vert foncé
        Color couleurSecondaire = new Color(255, 215, 0);  // or

        // Panneau titre
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(couleurPrimaire);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel titleLabel = new JLabel("⚽ CHAMPIONNAT DE FOOTBALL", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(couleurSecondaire);
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        // Barre de statut
        statusLabel = new JLabel("  Bienvenue dans la gestion du championnat");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(new Color(240, 240, 240));
        statusPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));
        statusPanel.add(statusLabel, BorderLayout.WEST);

        // Onglets principaux
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 13));

        equipePanel      = new EquipePanel(this);
        joueurPanel      = new JoueurPanel(this);
        matchPanel       = new MatchPanel(this);
        classementPanel  = new ClassementPanel(this);
        statistiquesPanel = new StatistiquesPanel(this);

        tabbedPane.addTab("🏟️ Équipes",      equipePanel);
        tabbedPane.addTab("👤 Joueurs",       joueurPanel);
        tabbedPane.addTab("⚽ Matchs",        matchPanel);
        tabbedPane.addTab("🏆 Classement",    classementPanel);
        tabbedPane.addTab("📊 Statistiques",  statistiquesPanel);

        // Rafraîchir les panels selon l'onglet actif
        tabbedPane.addChangeListener(e -> {
            int idx = tabbedPane.getSelectedIndex();
            switch (idx) {
                case 0: equipePanel.charger(); break;
                case 1: joueurPanel.charger(); break;
                case 2: matchPanel.charger(); break;
                case 3: classementPanel.charger(); break;
                case 4: statistiquesPanel.charger(); break;
            }
        });

        // Layout principal
        setLayout(new BorderLayout());
        add(titlePanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);

        // Chargement initial
        equipePanel.charger();
    }

    public void setStatus(String message) {
        statusLabel.setText("  " + message);
    }

    public void rafraichirTout() {
        equipePanel.charger();
        joueurPanel.charger();
        matchPanel.charger();
        classementPanel.charger();
        statistiquesPanel.charger();
    }
}
