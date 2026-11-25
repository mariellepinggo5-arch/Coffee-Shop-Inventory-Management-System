package com.inventory.view;

import com.inventory.util.UITheme;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * SplashScreen – the first JFrame shown when BrewTrack launches.
 * Displays branding, a progress bar, and status messages,
 * then hands off to LoginFrame automatically.
 */
public class SplashScreen extends JFrame {

    private JProgressBar progressBar;
    private JLabel       statusLabel;

    // Steps shown during loading  {display text,  sleep ms after}
    private static final Object[][] STEPS = {
        { "Warming up the espresso machine...",   400 },
        { "Loading database configuration...",    400 },
        { "Connecting to MySQL Workbench...",      500 },
        { "Fetching coffee inventory data...",     400 },
        { "Preparing the barista dashboard...",    400 },
        { "Almost ready – pulling your shot ☕",  350 },
        { "Welcome to BrewTrack!",                 300 },
    };

    public SplashScreen() {
        setUndecorated(true);          // no title bar / borders
        setSize(520, 340);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // Rounded window shape (Java 7+)
        try {
            setShape(new RoundRectangle2D.Double(0, 0, 520, 340, 24, 24));
        } catch (Exception ignored) {}

        setContentPane(buildContent());
        setVisible(true);

        // Run loading animation on background thread
        new Thread(this::runLoadingSequence, "splash-loader").start();
    }

    // ══════════════════════════════════════════════════════════════
    // UI LAYOUT
    // ══════════════════════════════════════════════════════════════
    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                // Deep espresso gradient background
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                    0, 0,          new Color(28, 18, 10),   // top  – midnight roast
                    0, getHeight(), new Color(60, 38, 20)   // bottom – espresso
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
            }
        };
        root.setOpaque(false);
        root.setBorder(BorderFactory.createEmptyBorder(44, 50, 32, 50));

        // ── TOP: logo area ──
        JPanel logoPanel = new JPanel();
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setOpaque(false);

        // Coffee cup icon (drawn via label emoji – crisp on all JDKs)
        JLabel iconLabel = new JLabel("☕", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 56));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel brewLabel = new JLabel("BrewTrack", SwingConstants.CENTER);
        brewLabel.setFont(new Font("Segoe UI", Font.BOLD, 42));
        brewLabel.setForeground(Color.WHITE);
        brewLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tagLabel = new JLabel("Coffee Shop Inventory Management System", SwingConstants.CENTER);
        tagLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tagLabel.setForeground(new Color(200, 175, 148));
        tagLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel isufstLabel = new JLabel("ISUFST  ·  OOP Final Project  ·  2026", SwingConstants.CENTER);
        isufstLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        isufstLabel.setForeground(new Color(140, 115, 90));
        isufstLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        logoPanel.add(iconLabel);
        logoPanel.add(Box.createVerticalStrut(6));
        logoPanel.add(brewLabel);
        logoPanel.add(Box.createVerticalStrut(6));
        logoPanel.add(tagLabel);
        logoPanel.add(Box.createVerticalStrut(4));
        logoPanel.add(isufstLabel);

        root.add(logoPanel, BorderLayout.CENTER);

        // ── BOTTOM: progress area ──
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setOpaque(false);

        // Status text
        statusLabel = new JLabel("Starting up...", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        statusLabel.setForeground(new Color(180, 155, 128));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Progress bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(false);
        progressBar.setOpaque(false);
        progressBar.setBackground(new Color(60, 40, 22));
        progressBar.setForeground(new Color(180, 83, 9));   // caramel amber
        progressBar.setPreferredSize(new Dimension(0, 6));
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 6));
        progressBar.setBorderPainted(false);
        // Custom rounded progress bar UI
        progressBar.setUI(new javax.swing.plaf.basic.BasicProgressBarUI() {
            @Override
            protected void paintDeterminate(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = c.getWidth(), h = c.getHeight();
                // Track
                g2.setColor(new Color(55, 36, 20));
                g2.fillRoundRect(0, 0, w, h, h, h);
                // Fill
                int fill = (int) (w * progressBar.getPercentComplete());
                if (fill > 0) {
                    GradientPaint gp = new GradientPaint(
                        0, 0, new Color(217, 119, 6),
                        fill, 0, new Color(180, 83, 9)
                    );
                    g2.setPaint(gp);
                    g2.fillRoundRect(0, 0, fill, h, h, h);
                }
            }
            @Override
            protected void paintIndeterminate(Graphics g, JComponent c) {
                paintDeterminate(g, c);
            }
        });

        // Version tag at the very bottom
        JLabel versionLabel = new JLabel("v1.0.0  –  Built with Java Swing & MySQL", SwingConstants.CENTER);
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        versionLabel.setForeground(new Color(100, 80, 60));
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        bottomPanel.add(statusLabel);
        bottomPanel.add(Box.createVerticalStrut(10));
        bottomPanel.add(progressBar);
        bottomPanel.add(Box.createVerticalStrut(10));
        bottomPanel.add(versionLabel);

        root.add(bottomPanel, BorderLayout.SOUTH);
        return root;
    }

    // ══════════════════════════════════════════════════════════════
    // LOADING SEQUENCE
    // ══════════════════════════════════════════════════════════════
    private void runLoadingSequence() {
        int totalMs = 0;
        for (Object[] s : STEPS) totalMs += (int) s[1];

        int elapsed = 0;
        for (Object[] step : STEPS) {
            final String msg = (String) step[0];
            final int    wait = (int) step[1];
            elapsed += wait;
            final int progress = (int) (elapsed * 100.0 / totalMs);

            SwingUtilities.invokeLater(() -> {
                statusLabel.setText(msg);
                progressBar.setValue(progress);
            });

            try { Thread.sleep(wait); } catch (InterruptedException ignored) {}
        }

        // All done — close splash, open login
        SwingUtilities.invokeLater(() -> {
            dispose();
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
        });
    }
}
