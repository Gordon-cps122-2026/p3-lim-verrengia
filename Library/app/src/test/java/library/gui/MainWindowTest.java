package library.gui;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Component;
import java.lang.reflect.Field;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import library.model.LibraryDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MainWindowTest {
  private MainWindow main;
  private LibraryDatabase db;

  @BeforeEach
  public void setUp() {
    // Use test mode to avoid reading/writing files
    db = LibraryDatabase.getInstance(true);
    main = new MainWindow(db);
  }

  @Test
  public void testMainInitialization() throws Exception {
    // Use reflection to access the private frame field
    Field frameField = MainWindow.class.getDeclaredField("frame");
    frameField.setAccessible(true);
    JFrame frame = (JFrame) frameField.get(main);

    // Check that frame is properly configured
    assertNotNull(frame);
    assertNotNull(frame.getTitle());
    assertFalse(frame.getTitle().isEmpty(), "Frame title should not be empty");
    assertEquals(JFrame.EXIT_ON_CLOSE, frame.getDefaultCloseOperation());

    // Check that frame contains a tabbed pane
    Component[] components = frame.getContentPane().getComponents();
    boolean hasTabbedPane = false;
    for (Component comp : components) {
      if (comp instanceof JTabbedPane) {
        hasTabbedPane = true;
        JTabbedPane tabbedPane = (JTabbedPane) comp;

        // Check that the tabbed pane has two tabs
        assertEquals(2, tabbedPane.getTabCount());
        assertEquals("Books", tabbedPane.getTitleAt(0));
        assertEquals("Borrowers", tabbedPane.getTitleAt(1));
        break;
      }
    }
    assertTrue(hasTabbedPane, "Main should have a JTabbedPane");
  }

  @Test
  public void testDisplay() throws Exception {
    // Use reflection to access the private frame field
    Field frameField = MainWindow.class.getDeclaredField("frame");
    frameField.setAccessible(true);
    JFrame frame = (JFrame) frameField.get(main);

    // Initially frame should not be visible
    assertFalse(frame.isVisible());

    // Call display method
    main.display();

    // Now frame should be visible
    assertTrue(frame.isVisible());

    // Clean up - hide the frame to avoid UI remnants after test
    frame.setVisible(false);
  }
}
