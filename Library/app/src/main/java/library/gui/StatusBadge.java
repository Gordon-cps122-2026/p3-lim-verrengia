package library.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JPanel;

/** Rounded status badge (Available / Unavailable). */
class StatusBadge extends JPanel {

  private final String label;
  private final boolean available;

  StatusBadge(String label, boolean available) {
    this.label = label;
    this.available = available;
    setOpaque(false);
  }

  @Override
  public Dimension getPreferredSize() {
    FontMetrics fm = getFontMetrics(LibraryUiTheme.uiFont(Font.BOLD, 12));
    return new Dimension(fm.stringWidth(label) + 18, 24);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    Color fill = available ? LibraryUiTheme.BADGE_AVAILABLE : LibraryUiTheme.BADGE_UNAVAILABLE;
    int w = getWidth();
    int h = getHeight();
    g2.setColor(fill);
    g2.fill(new RoundRectangle2D.Float(0, 0, w - 1, h - 1, h / 2, h / 2));
    g2.setColor(Color.WHITE);
    g2.setFont(LibraryUiTheme.uiFont(Font.BOLD, 12));
    FontMetrics fm = g2.getFontMetrics();
    int tx = (w - fm.stringWidth(label)) / 2;
    int ty = (h + fm.getAscent() - fm.getDescent()) / 2;
    g2.drawString(label, tx, ty);
    g2.dispose();
  }
}
