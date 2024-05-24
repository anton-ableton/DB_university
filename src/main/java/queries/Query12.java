package queries;

import entities.Category;
import entities.Department;
import entities.Thesis;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

// Получить список руководителей дипломных работ с указанной кафедры, либо факультета
// полностью и раздельно по некоторым категориям преподавателей.

public class Query12 extends JFrame {
  private EntityManagerFactory entityManagerFactory;
  private EntityManager entityManager;

  private JTable thesisTable;
  private DefaultTableModel thesisTableModel;

  public Query12() {
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
    List<Thesis> theses = entityManager.createQuery("SELECT t FROM Thesis t", Thesis.class).getResultList();
    thesisTableModel.setColumnIdentifiers(new String[]{"ID", "Teacher", "Title", "Department", "Faculty"});
    for (Thesis thesis : theses) {
      String teacherName = thesis.getTeacher() != null ? thesis.getTeacher().getTeacherName() + " " + thesis.getTeacher().getTeacherLastname() : "N/A";
      String department = thesis.getTeacher() != null ? thesis.getTeacher().getDepartment().getDepartmentName() : "N/A";
      String faculty = thesis.getTeacher() != null ? thesis.getTeacher().getDepartment().getFaculty().getFacultyName() : "N/A";
      thesisTableModel.addRow(new Object[]{thesis.getThesisId(), teacherName, thesis.getTitle(), department, faculty});
    }
  }

  private void selectThesis() {
    List<Category> allCategories = entityManager.createQuery("SELECT c FROM Category c", Category.class).getResultList();
    List<Department> allDepartments = entityManager.createQuery("SELECT d FROM Department d", Department.class).getResultList();
    List<String> faculties = entityManager.createQuery("SELECT DISTINCT d.faculty.facultyName FROM Department d", String.class).getResultList();

    if (allCategories.isEmpty() || allDepartments.isEmpty() || faculties.isEmpty()) {
      JOptionPane.showMessageDialog(this, "There are no categories, departments, or faculties available. Please create them first.");
      return;
    }

    // Создаем выпадающие списки для выбора категории, департамента и факультета
    JComboBox<String> categoryComboBox = new JComboBox<>();
    JComboBox<String> departmentComboBox = new JComboBox<>();
    JComboBox<String> facultyComboBox = new JComboBox<>();

    // Заполняем выпадающие списки категорий и департаментов
    for (Category category : allCategories) {
      categoryComboBox.addItem(category.getCategoryName());
    }
    for (Department department : allDepartments) {
      departmentComboBox.addItem(department.getDepartmentName());
    }
    for (String faculty : faculties) {
      facultyComboBox.addItem(faculty);
    }

    JPanel panel = new JPanel(new GridLayout(5, 2));
    panel.add(new JLabel("Department / Faculty:"));
    JCheckBox switchBox = new JCheckBox();
    panel.add(switchBox);
    panel.add(new JLabel("Department:"));
    panel.add(departmentComboBox);
    panel.add(new JLabel("Faculty:"));
    panel.add(facultyComboBox);
    panel.add(new JLabel("Need Category:"));
    JCheckBox needCategoryCheckBox = new JCheckBox();
    panel.add(needCategoryCheckBox);
    panel.add(new JLabel("Category:"));
    panel.add(categoryComboBox);

    int result = JOptionPane.showConfirmDialog(null, panel, "Select Department and Teacher", JOptionPane.OK_CANCEL_OPTION);
    if (result == JOptionPane.OK_OPTION) {
      boolean searchByDepartment = switchBox.isSelected();
      boolean needCategory = needCategoryCheckBox.isSelected();
      String query;
      List<Thesis> theses;

      // Формируем запрос в зависимости от значения флажков
      if (searchByDepartment) {
        if (needCategory) {
          query = "SELECT t FROM Thesis t WHERE t.teacher.department.departmentName = :department AND t.teacher.category.categoryName = :category";
        } else {
          query = "SELECT t FROM Thesis t WHERE t.teacher.department.departmentName = :department";
        }
      } else {
        if (needCategory) {
          query = "SELECT t FROM Thesis t WHERE t.teacher.department.faculty.facultyName = :faculty AND t.teacher.category.categoryName = :category";
        } else {
          query = "SELECT t FROM Thesis t WHERE t.teacher.department.faculty.facultyName = :faculty";
        }
      }

      // Выполняем запрос и получаем список тем дипломных работ
      try {
        entityManager.getTransaction().begin();
        javax.persistence.TypedQuery<Thesis> typedQuery = entityManager.createQuery(query, Thesis.class);

        if (searchByDepartment) {
          typedQuery.setParameter("department", departmentComboBox.getSelectedItem());
        } else {
          typedQuery.setParameter("faculty", facultyComboBox.getSelectedItem());
        }

        if (needCategory) {
          typedQuery.setParameter("category", categoryComboBox.getSelectedItem());
        }

        theses = typedQuery.getResultList();
        entityManager.getTransaction().commit();
      } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error occurred: " + e.getMessage());
        return;
      }

      // Очищаем таблицу перед обновлением данных
      thesisTableModel.setRowCount(0);

      // Заполняем таблицу данными о темах дипломных работ
      for (Thesis thesis : theses) {
        String teacherName = thesis.getTeacher() != null ? thesis.getTeacher().getTeacherName() + " " + thesis.getTeacher().getTeacherLastname() : "N/A";
        String department = thesis.getTeacher() != null ? thesis.getTeacher().getDepartment().getDepartmentName() : "N/A";
        String faculty = thesis.getTeacher() != null ? thesis.getTeacher().getDepartment().getFaculty().getFacultyName() : "N/A";
        thesisTableModel.addRow(new Object[]{thesis.getThesisId(), teacherName, thesis.getTitle(), department, faculty});
      }
    }
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new Query12().setVisible(true));
  }
}
