import tables.*;
import queries.*;

import javax.swing.*;
import java.awt.*;

public class StartFrame extends JFrame {

  public StartFrame() {
    setTitle("NSU DATA");
    setSize(1200, 500);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    JPanel contentPane = new JPanel();
    contentPane.setLayout(new BorderLayout());

    JLabel imageLabel = new JLabel(new ImageIcon("src/main/resources/nsu_image.jpg"));
    contentPane.add(imageLabel, BorderLayout.NORTH);

    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new GridLayout(2, 6, 10, 10));

    JButton categoriesButton = new JButton("Categories");
    JButton departmentsButton = new JButton("Departments");
    JButton disciplinesButton = new JButton("Disciplines");
    JButton dissertationsButton = new JButton("Dissertations");
    JButton eventTypesButton = new JButton("Event Types");
    JButton examsButton = new JButton("Exams");
    JButton facultiesButton = new JButton("Faculties");
    JButton groupsButton = new JButton("Groups");
    JButton studentsButton = new JButton("Students");
    JButton teachersButton = new JButton("Teachers");
    JButton thesesButton = new JButton("Theses");
    JButton TDButton = new JButton("Teachers Disciplines");

    Dimension buttonSize = new Dimension(100, 40);  // Измените размер кнопок
    JButton[] buttons = {
            categoriesButton, departmentsButton, disciplinesButton, dissertationsButton,
            eventTypesButton, examsButton, facultiesButton, groupsButton,
            studentsButton, teachersButton, thesesButton, TDButton
    };
    for (JButton button : buttons) {
      button.setPreferredSize(buttonSize);
      buttonPanel.add(button);
    }

    categoriesButton.addActionListener(e -> new CategoryApp().setVisible(true));
    departmentsButton.addActionListener(e -> new DepartmentApp().setVisible(true));
    disciplinesButton.addActionListener(e ->  new DisciplineApp().setVisible(true));
    dissertationsButton.addActionListener(e -> new DissertationApp().setVisible(true));
    eventTypesButton.addActionListener(e -> new EventTypeApp().setVisible(true));
    examsButton.addActionListener(e -> new ExamApp().setVisible(true));
    facultiesButton.addActionListener(e -> new FacultyApp().setVisible(true));
    groupsButton.addActionListener(e ->  new GroupApp().setVisible(true));
    studentsButton.addActionListener(e -> new StudentApp().setVisible(true));
    teachersButton.addActionListener(e ->  new TeacherApp().setVisible(true));
    thesesButton.addActionListener(e ->  new ThesisApp().setVisible(true));
    TDButton.addActionListener(e -> new DisciplineAndEventTypeApp().setVisible(true));

    JPanel themesPanel = new JPanel();
    themesPanel.setLayout(new BoxLayout(themesPanel, BoxLayout.Y_AXIS));
    JLabel themesLabel = new JLabel("<html><span style='font-family: Arial; font-size: 18px; " +
            "color: #555555; font-weight: bold;" +
            "'>_____________________________________________Queries_____________________________________________</span></html>");
    themesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    themesPanel.add(Box.createRigidArea(new Dimension(0, 20)));
    themesPanel.add(themesLabel);
    themesPanel.add(Box.createRigidArea(new Dimension(0, 20)));

    JPanel buttonPanel2 = new JPanel();
    buttonPanel2.setLayout(new GridLayout(2, 7, 10, 10));
    JButton query1Button = new JButton("Query 1");
    JButton query2Button = new JButton("Query 2");
    JButton query3Button = new JButton("Query 3");
    JButton query4Button = new JButton("-Query 4");
    JButton query5Button = new JButton("-Query 5");
    JButton query6Button = new JButton("-Query 6");
    JButton query7Button = new JButton("-Query 7");
    JButton query8Button = new JButton("-Query 8");
    JButton query9Button = new JButton("-Query 9");
    JButton query10Button = new JButton("-Query 10");
    JButton query11Button = new JButton("Query 11");
    JButton query12Button = new JButton("Query 12");
    JButton query13Button = new JButton("-Query 13");
    JButton exitButton = new JButton("Exit");

    JButton[] queryButtons = {
            query1Button, query2Button, query3Button, query4Button, query5Button,
            query6Button, query7Button, query8Button, query9Button, query10Button,
            query11Button, query12Button, query13Button, exitButton
    };
    for (JButton button : queryButtons) {
      button.setPreferredSize(new Dimension(80, 40));  // Измените размер кнопок
      buttonPanel2.add(button);
    }

    query1Button.addActionListener(e -> new Query1().setVisible(true));
    query2Button.addActionListener(e -> new Query2().setVisible(true));
    query3Button.addActionListener(e -> new Query3().setVisible(true));
    query4Button.addActionListener(e -> new Query4().setVisible(true));
    query5Button.addActionListener(e ->  new Query5().setVisible(true));
    query6Button.addActionListener(e ->  new Query6().setVisible(true));
    query7Button.addActionListener(e ->  new Query7().setVisible(true));
    query8Button.addActionListener(e ->  new Query8().setVisible(true));
    query9Button.addActionListener(e ->  new Query9().setVisible(true));
    query10Button.addActionListener(e ->  new Query10().setVisible(true));
    query11Button.addActionListener(e ->  new Query11().setVisible(true));
    query12Button.addActionListener(e ->  new Query12().setVisible(true));
    query13Button.addActionListener(e ->  new Query13().setVisible(true));
    exitButton.addActionListener(e ->  System.exit(0) );

    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BorderLayout());
    mainPanel.add(buttonPanel, BorderLayout.NORTH);
    mainPanel.add(themesPanel, BorderLayout.CENTER);  // Добавьте панель тем
    mainPanel.add(buttonPanel2, BorderLayout.SOUTH);  // Переместите панель кнопок запросов вниз

    contentPane.add(mainPanel, BorderLayout.CENTER);

    add(contentPane);

    setResizable(false);
    centerFrameOnScreen();
    setVisible(true);
  }

  private void centerFrameOnScreen() {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int screenWidth = screenSize.width;
    int screenHeight = screenSize.height;
    int frameWidth = getWidth();
    int frameHeight = getHeight();
    int x = (screenWidth - frameWidth) / 2;
    int y = (screenHeight - frameHeight) / 2;
    setLocation(x, y);
  }

  public static void main(String[] args) {
    new StartFrame();
  }
}
