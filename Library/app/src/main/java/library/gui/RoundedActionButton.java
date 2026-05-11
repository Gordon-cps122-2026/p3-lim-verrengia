package library.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JPanel;

/** Clickable rounded rectangle button (primary or secondary style). */
class RoundedActionButton extends JPanel {

  private final String text;
  private final boolean primary;
  private final Runnable action;
  private boolean hover;

  RoundedActionButton(String text, boolean primary, Runnable action) {
    this.text = text;
    this.primary = primary;
    this.action = action;
    setOpaque(false);
    setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
    addMouseListener(
        new MouseAdapter() {
          @Override
          public void mouseEntered(MouseEvent e) {
            hover = true;
            repaint();
          }

          @Override
          public void mouseExited(MouseEvent e) {
            hover = false;
            repaint();
          }

          @Override
          public void mouseClicked(MouseEvent e) {
            if (action != null) {
              action.run();
            }
          }
        });
  }

  @Override
  public Dimension getPreferredSize() {
    FontMetrics fm = getFontMetrics(LibraryUiTheme.uiFont(Font.PLAIN, 14));
    int w = fm.stringWidth(text) + 32;
    int h = 40;
    return new Dimension(Math.max(w, 88), h);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    int w = getWidth();
    int h = getHeight();
    int arc = LibraryUiTheme.CORNER_ARC;
    Color fill =
        primary
            ? (hover ? LibraryUiTheme.PRIMARY_FILL.brighter() : LibraryUiTheme.PRIMARY_FILL)
            : (hover ? new Color(0xE4E4E4) : LibraryUiTheme.SECONDARY_FILL);
    g2.setColor(fill);
    g2.fill(new RoundRectangle2D.Float(0, 0, w - 1, h - 1, arc, arc));
    g2.setColor(new Color(0xDDDDDD));
    g2.draw(new RoundRectangle2D.Float(0, 0, w - 1, h - 1, arc, arc));
    g2.setColor(primary ? Color.WHITE : LibraryUiTheme.PRIMARY_FILL);
    g2.setFont(LibraryUiTheme.uiFont(Font.PLAIN, 14));
    FontMetrics fm = g2.getFontMetrics();
    int tx = (w - fm.stringWidth(text)) / 2;
    int ty = (h + fm.getAscent() - fm.getDescent()) / 2;
    g2.drawString(text, tx, ty);
    g2.dispose();
  }
}
