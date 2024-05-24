package queries;

import entities.Dissertation;
import entities.Teacher;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;


//        Получить перечень и общее число тем кандидатских и докторских диссертаций,
//        защитивших сотрудниками указанной кафедры либо указанного факультета.

public class Query3 extends JFrame {
  private EntityManagerFactory entityManagerFactory;
  private EntityManager entityManager;

  private JTable dissertationTable;
  private DefaultTableModel dissertationTableModel;

  public Query3() {
    initializeDatabase();
    initComponents();
    loadDissertationData();
  }

  private void initializeDatabase() {
    entityManagerFactory = Persistence.createEntityManagerFactory("FacultyPU");
    entityManager = entityManagerFactory.createEntityManager();
  }

  private void initComponents() {
    setTitle("Диссертации по кафедрам и факультетам");
    setSize(600, 400);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    JPanel panel = new JPanel(new BorderLayout());
    getContentPane().add(panel);

    // Table
    dissertationTableModel = new DefaultTableModel();
    dissertationTable = new JTable(dissertationTableModel);
    JScrollPane scrollPane = new JScrollPane(dissertationTable);
    panel.add(scrollPane, BorderLayout.CENTER);

    // Buttons
    JPanel buttonPanel = new JPanel(new FlowLayout());
    panel.add(buttonPanel, BorderLayout.SOUTH);

    JButton addButton = new JButton("Select");
    addButton.addActionListener(e -> selectDissertation());
    buttonPanel.add(addButton);

    JButton clearButton = new JButton("Clear");
    clearButton.addActionListener(e -> loadDissertationData());
    buttonPanel.add(clearButton);
  }

  private void loadDissertationData() {
    // Очищаем таблицу перед загрузкой новых данных
    dissertationTableModel.setRowCount(0);

    // Загружаем данные из базы данных
    java.util.List<Dissertation> dissertations = entityManager.createQuery("SELECT d FROM Dissertation d", Dissertation.class).getResultList();
    dissertationTableModel.setColumnIdentifiers(new String[]{"ID", "Title", "Teacher", "Type"});
    for (Dissertation dissertation : dissertations) {
      dissertationTableModel.addRow(new Object[]{dissertation.getDissertationId(), dissertation.getDissertationTitle(), dissertation.getTeacher().getTeacherName() + " " + dissertation.getTeacher().getTeacherLastname(), dissertation.getDissertationType()});
    }
  }

  private void selectDissertation() {
    java.util.List<Dissertation> dissertations;

    // Загружаем данные о преподавателях и диссертациях
    try {
      entityManager.getTransaction().begin();
      entityManager.getTransaction().commit();
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Error occurred: " + e.getMessage());
      return;
    }

    // Создаем панель для выбора кафедры и факультета
    JComboBox<String> departmentComboBox = new JComboBox<>();
    JComboBox<String> facultyComboBox = new JComboBox<>();

    // Загружаем данные о кафедрах и факультетах
    try {
      entityManager.getTransaction().begin();
      List<String> departments = entityManager.createQuery("SELECT DISTINCT d.departmentName FROM Department d", String.class).getResultList();
      List<String> faculties = entityManager.createQuery("SELECT DISTINCT d.facultyName FROM Faculty d", String.class).getResultList();
      entityManager.getTransaction().commit();

      for (String department : departments) {
        departmentComboBox.addItem(department);
      }
      for (String faculty : faculties) {
        facultyComboBox.addItem(faculty);
      }
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Error occurred: " + e.getMessage());
      return;
    }

    JPanel panel = new JPanel(new GridLayout(4, 2));
    panel.add(new JLabel("Department / Faculty:"));
    JCheckBox switchBox = new JCheckBox();
    panel.add(switchBox);
    panel.add(new JLabel("Department:"));
    panel.add(departmentComboBox);
    panel.add(new JLabel("Faculty:"));
    panel.add(facultyComboBox);

    int result = JOptionPane.showConfirmDialog(null, panel, "Select Department and Faculty", JOptionPane.OK_CANCEL_OPTION);
    if (result == JOptionPane.OK_OPTION) {
      // Получаем значение флажка switchBox
      boolean searchByDepartment = switchBox.isSelected();

      // Формируем запрос в зависимости от значения флажка
      String query;
      if (searchByDepartment) {
        query = "SELECT d FROM Dissertation d WHERE d.teacher.department.departmentName = :department";
      } else {
        query = "SELECT d FROM Dissertation d WHERE d.teacher.department.faculty.facultyName = :faculty";
      }

      // Выполняем запрос и получаем список диссертаций
      try {
        entityManager.getTransaction().begin();
        dissertations = entityManager.createQuery(query, Dissertation.class)
                .setParameter(searchByDepartment ? "department" : "faculty",
                        searchByDepartment ? departmentComboBox.getSelectedItem() : facultyComboBox.getSelectedItem())
                .getResultList();
        entityManager.getTransaction().commit();
      } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error occurred: " + e.getMessage());
        return;
      }

      // Очищаем таблицу перед обновлением данных
      dissertationTableModel.setRowCount(0);

      // Заполняем таблицу данными о диссертациях
      for (Dissertation dissertation : dissertations) {
        Teacher teacher = dissertation.getTeacher(); // Объявляем переменную teacher здесь
        dissertationTableModel.addRow(new Object[]{
                dissertation.getDissertationId(),
                dissertation.getDissertationTitle(),
                teacher.getTeacherName() + " " + teacher.getTeacherLastname(),
                dissertation.getDissertationType(),
                teacher.getDepartment().getDepartmentName() // Показываем название кафедры
        });
      }
    }
  }


  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new Query3().setVisible(true));
  }

}