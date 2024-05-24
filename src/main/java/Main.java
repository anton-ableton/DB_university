import javax.swing.*;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;

public class Main {


  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel(new FlatMacDarkLaf());
    } catch (Exception e) {
      e.printStackTrace();
    }

    SwingUtilities.invokeLater(() -> {
      StartFrame startFrame = new StartFrame();
      startFrame.setVisible(true);
    });

  }

}
