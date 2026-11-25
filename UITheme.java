package com.inventory.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Taskbar;
import java.awt.Window;
import java.awt.geom.Arc2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;


public class UITheme {

    public static final Color PRIMARY = new Color(92, 58, 33);         // Rich Espresso #5C3A21
    public static final Color PRIMARY_DARK = new Color(66, 40, 21);    // Dark Roast #422815
    public static final Color SECONDARY = new Color(120, 89, 71);      // Mocha Brown #785947
    public static final Color ACCENT = new Color(180, 83, 9);          // Warm Caramel Amber #B45309
    public static final Color SUCCESS = new Color(21, 128, 61);        // Fresh Herb Green #15803D
    public static final Color WARNING = new Color(217, 119, 6);        // Honey / Amber #D97706
    public static final Color DANGER = new Color(220, 38, 38);         // Warning Crimson #DC2626
    public static final Color BACKGROUND = new Color(251, 249, 246);   // Warm Cream #FBF9F6
    public static final Color CARD_BG = Color.WHITE;
    public static final Color TEXT_MAIN = new Color(28, 19, 14);       // Deep Espresso #1C130E
    public static final Color TEXT_MUTED = new Color(133, 107, 91);    // Soft Roast Slate #856B5B
    public static final Color BORDER_COLOR = new Color(230, 222, 214); // Warm Sand Border
    public static final Color SIDEBAR_BG = new Color(35, 26, 21);      // Midnight Roast #231A15
    public static final Color SIDEBAR_TEXT = new Color(245, 240, 235);
    public static final Color SIDEBAR_HOVER = new Color(59, 44, 36);

    
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_SUBHEADER = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);

    public static final Border BORDER_INPUT = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
        BorderFactory.createEmptyBorder(6, 10, 6, 10)
    );

    public static final Border BORDER_CARD = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
        BorderFactory.createEmptyBorder(15, 15, 15, 15)
    );

    private static Image appIcon = null;

        public static synchronized Image getAppIcon() {
        if (appIcon != null) {
            return appIcon;
        }

        int size = 64;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        GradientPaint bgPaint = new GradientPaint(0, 0, new Color(59, 31, 14), size, size, new Color(28, 16, 9));
        g2.setPaint(bgPaint);
        g2.fill(new RoundRectangle2D.Double(2, 2, size - 4, size - 4, 16, 16));

        g2.setColor(new Color(217, 119, 6));
        g2.setStroke(new BasicStroke(2.0f));
        g2.draw(new RoundRectangle2D.Double(2, 2, size - 4, size - 4, 16, 16));

        g2.setColor(new Color(245, 240, 235));
        g2.fillRoundRect(12, 47, 40, 5, 3, 3);

        GradientPaint cupPaint = new GradientPaint(16, 26, new Color(255, 255, 255), 44, 46, new Color(230, 220, 210));
        g2.setPaint(cupPaint);
        g2.fillRoundRect(16, 26, 28, 20, 6, 6);

        g2.setColor(new Color(245, 240, 235));
        g2.setStroke(new BasicStroke(3.0f));
        g2.draw(new Arc2D.Double(38, 28, 12, 14, 270, 180, Arc2D.OPEN));

        g2.setColor(new Color(92, 58, 33));
        g2.fillOval(18, 28, 24, 7);

        g2.setColor(new Color(217, 119, 6));
        g2.fillOval(26, 30, 8, 4);

        g2.setColor(new Color(217, 119, 6, 200));
        g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawArc(22, 13, 6, 11, 45, 180);
        g2.drawArc(22, 18, 6, 8, 225, 180);
        g2.drawArc(32, 11, 6, 12, 45, 180);
        g2.drawArc(32, 17, 6, 8, 225, 180);

        g2.dispose();
        appIcon = img;
        return appIcon;
    }
    public static void applyAppIcon(Window window) {
        Image icon = getAppIcon();
        if (window != null) {
            window.setIconImage(icon);
        }
        try {
            if (Taskbar.isTaskbarSupported()) {
                Taskbar taskbar = Taskbar.getTaskbar();
                if (taskbar.isSupported(Taskbar.Feature.ICON_IMAGE)) {
                    taskbar.setIconImage(icon);
                }
            }
        } catch (Throwable ignored) {}
    }

    public static JButton createPrimaryButton(String text) {
        return createButton(text, PRIMARY, Color.WHITE);
    }

    public static JButton createSuccessButton(String text) {
        return createButton(text, SUCCESS, Color.WHITE);
    }

    public static JButton createDangerButton(String text) {
        return createButton(text, DANGER, Color.WHITE);
    }

    public static JButton createSecondaryButton(String text) {
        return createButton(text, SECONDARY, Color.WHITE);
    }

    public static JButton createAccentButton(String text) {
        return createButton(text, ACCENT, Color.WHITE);
    }

    public static JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static void styleTable(JTable table) {
        table.setFont(FONT_REGULAR);
        table.setRowHeight(32);
        table.setGridColor(BORDER_COLOR);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(243, 235, 226));
        table.setSelectionForeground(TEXT_MAIN);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BOLD);
        header.setBackground(new Color(245, 240, 235));
        header.setForeground(TEXT_MAIN);
        header.setPreferredSize(new Dimension(0, 36));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_COLOR));

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean isSel, boolean hasFocus, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, val, isSel, hasFocus, r, c);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return comp;
            }
        };
        table.setDefaultRenderer(Object.class, leftRenderer);
    }
}
