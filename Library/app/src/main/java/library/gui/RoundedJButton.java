package library.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JButton;

/** JButton with rounded filled background (primary or secondary). */
class RoundedJButton extends JButton {

  private final boolean primary;

  RoundedJButton(String text, boolean primary) {
    super(text);
    this.primary = primary;
    setFocusPainted(false);
    setContentAreaFilled(false);
    setOpaque(false);
    setBorderPainted(false);
    setRolloverEnabled(true);
    setForeground(primary ? Color.WHITE : LibraryUiTheme.PRIMARY_FILL);
    setFont(LibraryUiTheme.uiFont(Font.PLAIN, 14));
  }

  @Override
  protected void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    int w = getWidth();
    int h = getHeight();
    int arc = LibraryUiTheme.CORNER_ARC;
    Color fill = primary ? LibraryUiTheme.PRIMARY_FILL : LibraryUiTheme.SECONDARY_FILL;
    if (getModel().isPressed()) {
      fill = fill.darker();
    } else if (getModel().isRollover()) {
      fill = primary ? LibraryUiTheme.PRIMARY_FILL.brighter() : new Color(0xE4E4E4);
    }
    g2.setColor(fill);
    g2.fill(new RoundRectangle2D.Float(0, 0, w - 1, h - 1, arc, arc));
    // Secondary: flat fill only (no outline). Primary: no chrome outline.
    g2.setColor(getForeground());
    FontMetrics fm = g2.getFontMetrics();
    String t = getText();
    int tx = (w - fm.stringWidth(t)) / 2;
    int ty = (h + fm.getAscent() - fm.getDescent()) / 2;
    g2.drawString(t, tx, ty);
    g2.dispose();
  }
}
