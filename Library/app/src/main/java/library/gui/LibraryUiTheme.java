package library.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;

/** Visual constants for the library UI mockup. */
final class LibraryUiTheme {

  static final Color BG_WHITE = Color.WHITE;
  static final Color TAB_BAR_BG = new Color(0xF0F0F0);
  static final Color SEARCH_BORDER = new Color(0xDDDDDD);
  static final Color TABLE_HEADER_BG = new Color(0xF7F7F7);
  static final int SEARCH_FIELD_ARC = 20;
  static final Color ROW_DIVIDER = new Color(0xE5E5E5);
  static final Color ROW_HOVER = new Color(0xF5F5F5);
  static final Color PRIMARY_FILL = new Color(0x1A1A1A);
  static final Color SECONDARY_FILL = new Color(0xF0F0F0);
  static final Color TEXT_MUTED = new Color(0x999999);
  static final Color TEXT_SUB = new Color(0x888888);
  static final Color BADGE_AVAILABLE = new Color(0x2E7D32);
  static final Color BADGE_UNAVAILABLE = new Color(0xF57C00);
  static final Color SUCCESS = new Color(0x2E7D32);
  static final Color ERROR = new Color(0xC62828);
  static final Color EXPAND_BG = new Color(0xE8E8E8);
  /** Borrower row expand panel (mockup). */
  static final Color BORROWER_EXPAND_PANEL_BG = new Color(0xEBEBEB);
  static final Color EXPAND_ROW = new Color(0xEEEEEE);
  static final Color BORDER_LIGHT = new Color(0xE0E0E0);

  static final int CORNER_ARC = 12;
  static final int TABLE_ROW_HEIGHT = 56;

  private LibraryUiTheme() {}

  static Font uiFont(int style, int size) {
    Font f = new Font("Segoe UI", style, size);
    if ("Dialog".equals(f.getFamily())) {
      f = new Font(Font.SANS_SERIF, style, size);
    }
    return f;
  }

  static JTextField styledTextField() {
    JTextField f = new JTextField();
    f.setBorder(
        BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1, true),
            new EmptyBorder(10, 12, 10, 12)));
    f.setBackground(BG_WHITE);
    f.setFont(uiFont(Font.PLAIN, 14));
    return f;
  }

  /** Search-style field: thin light gray rounded outline (mockup). */
  static JTextField styledSearchField() {
    JTextField f = new JTextField();
    f.setBorder(
        BorderFactory.createCompoundBorder(
            new RoundedOutlineBorder(SEARCH_BORDER, 1, SEARCH_FIELD_ARC),
            new EmptyBorder(10, 12, 10, 12)));
    f.setBackground(BG_WHITE);
    f.setFont(uiFont(Font.PLAIN, 14));
    return f;
  }

  private static final class RoundedOutlineBorder extends AbstractBorder {
    private final Color color;
    private final int thickness;
    private final int arc;

    RoundedOutlineBorder(Color color, int thickness, int arc) {
      this.color = color;
      this.thickness = thickness;
      this.arc = arc;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
      Graphics2D g2 = (Graphics2D) g.create();
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2.setColor(color);
      g2.setStroke(new BasicStroke(thickness));
      float t = thickness / 2f;
      g2.draw(
          new RoundRectangle2D.Float(
              x + t, y + t, width - thickness, height - thickness, arc, arc));
      g2.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
      return new Insets(thickness, thickness, thickness, thickness);
    }

    @Override
    public boolean isBorderOpaque() {
      return false;
    }
  }
}
