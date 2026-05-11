package library.gui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.Timer;

/** Single-line feedback that auto-clears after a delay. */
class InlineFeedback extends JLabel {

  private final Timer clearTimer;

  InlineFeedback() {
    setFont(LibraryUiTheme.uiFont(Font.PLAIN, 14));
    setOpaque(false);
    clearTimer =
        new Timer(
            4000,
            e -> {
              setText("");
              setForeground(Color.BLACK);
            });
    clearTimer.setRepeats(false);
  }

  void showMessage(String text, Color color) {
    clearTimer.stop();
    setForeground(color);
    setText(text);
    clearTimer.restart();
  }

  void clearNow() {
    clearTimer.stop();
    setText("");
  }
}
