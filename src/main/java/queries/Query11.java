package queries;

import entities.Department;
import entities.Teacher;
import entities.Thesis;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

// Получить список студентов и тем дипломных работ, выполняемых ими на указанной
// кафедре либо у указанного преподавателя.

public class Query11 extends JFrame {
  private EntityManagerFactory entityManagerFactory;
  private EntityManager entityManager;

  private JTable thesisTable;
  private DefaultTableModel thesisTableModel;

  public Query11() {
    initializeDatabase();
    initComponents();
    loadThesisData();
  }

  private void initializeDatabase() {
    entityManagerFactory = Persistence.createEntityManagerFactory("FacultyPU");
    entityManager = entityManagerFactory.createEntityManager();
  }

  private void initComponents() {
    setTitle("Thesis Management");
    setSize(800, 400);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    JPanel panel = new JPanel(new BorderLayout());
    getContentPane().add(panel);

    // Table
    thesisTableModel = new DefaultTableModel();
    thesisTable = new JTable(thesisTableModel);
    JScrollPane scrollPane = new JScrollPane(thesisTable);
    panel.add(scrollPane, BorderLayout.CENTER);

    // Buttons
    JPanel buttonPanel = new JPanel(new FlowLayout());
    panel.add(buttonPanel, BorderLayout.SOUTH);

    JButton addButton = new JButton("Select");
    addButton.addActionListener(e -> selectThesis());
    buttonPanel.add(addButton);

    JButton clearButton = new JButton("Clear");
    clearButton.addActionListener(e -> loadThesisData());
    buttonPanel.add(clearButton);
  }

  private void loadThesisData() {
    // Очищаем таблицу перед загрузкой новых данных
    thesisTableModel.setRowCount(0);

    // Загружаем данные из базы данных
    java.util.List<Thesis> theses = entityManager.createQuery("SELECT t FROM Thesis t", Thesis.class).getResultList();
    thesisTableModel.setColumnIdentifiers(new String[]{"ID", "Title", "Student", "Teacher", "Department"});
    for (Thesis thesis : theses) {
      String studentName = thesis.getStudent() != null ? thesis.getStudent().getStudentName() + " " + thesis.getStudent().getStudentLastname() : "N/A";
      String teacherName = thesis.getTeacher() != null ? thesis.getTeacher().getTeacherName() + " " + thesis.getTeacher().getTeacherLastname() : "N/A";
      String department = thesis.getTeacher() != null ? thesis.getTeacher().getDepartment().getDepartmentName() : "N/A";
      thesisTableModel.addRow(new Object[]{thesis.getThesisId(), thesis.getTitle(), studentName, teacherName, department});
    }
  }

  private void selectThesis() {
    java.util.List<Thesis> theses;
    List<Teacher> teachers;

    // Загружаем данные о преподавателях и кафедрах
    try {
      entityManager.getTransaction().begin();
      entityManager.getTransaction().commit();
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Error occurred: " + e.getMessage());
      return;
    }

    // Создаем панель для выбора кафедры и преподавателя
    JComboBox<Department> departmentComboBox = new JComboBox<>();
    JComboBox<Teacher> teacherComboBox = new JComboBox<>();

    // Загружаем данные о кафедрах и преподавателях
    try {
      entityManager.getTransaction().begin();
      List<Department> departments = entityManager.createQuery("SELECT d FROM Department d", Department.class).getResultList();
      List<Teacher> teachersList = entityManager.createQuery("SELECT t FROM Teacher t", Teacher.class).getResultList();
      entityManager.getTransaction().commit();

      for (Department department : departments) {
        departmentComboBox.addItem(department);
      }
      for (Teacher teacher : teachersList) {
        teacherComboBox.addItem(teacher);
      }
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Error occurred: " + e.getMessage());
      return;
    }

    JPanel panel = new JPanel(new GridLayout(4, 2));
    panel.add(new JLabel("Department / Teacher:"));
    JCheckBox switchBox = new JCheckBox();
    panel.add(switchBox);
    panel.add(new JLabel("Department:"));
    panel.add(departmentComboBox);
    panel.add(new JLabel("Teacher:"));
    panel.add(teacherComboBox);

    int result = JOptionPane.showConfirmDialog(null, panel, "Select Department and Teacher", JOptionPane.OK_CANCEL_OPTION);
    if (result == JOptionPane.OK_OPTION) {
      // Получаем значение флажка switchBox
      boolean searchByDepartment = switchBox.isSelected();

      // Формируем запрос в зависимости от значения флажка
      String query;
      if (searchByDepartment) {
        query = "SELECT t FROM Thesis t WHERE t.teacher.department = :department";
      } else {
        query = "SELECT t FROM Thesis t WHERE t.teacher = :teacher";
      }

      // Выполняем запрос и получаем список тем дипломных работ
      try {
        entityManager.getTransaction().begin();
        theses = entityManager.createQuery(query, Thesis.class)
                .setParameter(searchByDepartment ? "department" : "teacher",
                        searchByDepartment ? departmentComboBox.getSelectedItem() : teacherComboBox.getSelectedItem())
                .getResultList();
        entityManager.getTransaction().commit();
      } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error occurred: " + e.getMessage());
        return;
      }

      // Очищаем таблицу перед обновлением данных
      thesisTableModel.setRowCount(0);

      // Заполняем таблицу данными о темах дипломных работ
      for (Thesis thesis : theses) {
        String studentName = thesis.getStudent() != null ? thesis.getStudent().getStudentName() + " " + thesis.getStudent().getStudentLastname() : "N/A";
        String teacherName = thesis.getTeacher() != null ? thesis.getTeacher().getTeacherName() + " " + thesis.getTeacher().getTeacherLastname() : "N/A";
        String department = thesis.getTeacher() != null ? thesis.getTeacher().getDepartment().getDepartmentName() : "N/A";
        thesisTableModel.addRow(new Object[]{thesis.getThesisId(), thesis.getTitle(), studentName, teacherName, department});
      }
    }
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new Query11().setVisible(true));
  }
}
