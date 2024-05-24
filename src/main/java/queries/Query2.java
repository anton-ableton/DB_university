package queries;

import entities.Category;
import entities.Department;
import entities.Teacher;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

//        Получить список и общее число преподавателей указанных кафедр либо указанного
//        факультета полностью либо указанных категорий (ассистенты, доценты, профессора и т.д.)
//        по половому признаку, году рождения, возрасту, признаку наличия и количеству детей,
//        размеру заработной платы, являющихся аспирантами, защитивших кандидатские,
//        докторские диссертации в указанный период.

public class Query2 extends JFrame {

  private EntityManagerFactory entityManagerFactory;
  private EntityManager entityManager;

  private JTable teacherTable;
  private DefaultTableModel teacherTableModel;

  public Query2() {
    initializeDatabase();
    initComponents();
    loadTeacherData();
  }

  private void initializeDatabase() {
    entityManagerFactory = Persistence.createEntityManagerFactory("FacultyPU");
    entityManager = entityManagerFactory.createEntityManager();
  }

  private void initComponents() {
    setTitle("Преподаватели по характеристикам");
    setSize(800, 400);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    JPanel panel = new JPanel(new BorderLayout());
    getContentPane().add(panel);

    // Table
    teacherTableModel = new DefaultTableModel();
    teacherTable = new JTable(teacherTableModel);
    JScrollPane scrollPane = new JScrollPane(teacherTable);
    panel.add(scrollPane, BorderLayout.CENTER);

    // Buttons
    JPanel buttonPanel = new JPanel(new FlowLayout());
    panel.add(buttonPanel, BorderLayout.SOUTH);

    JButton addButton = new JButton("Select");
    addButton.addActionListener(e -> selectTeacher());
    buttonPanel.add(addButton);

    JButton clearButton = new JButton("Clear");
    clearButton.addActionListener(e -> loadTeacherData());
    buttonPanel.add(clearButton);

  }

  private void loadTeacherData() {
    // Очищаем таблицу перед загрузкой новых данных
    teacherTableModel.setRowCount(0);

    // Загружаем данные из базы данных
    java.util.List<Teacher> teachers = entityManager.createQuery("SELECT t FROM Teacher t", Teacher.class).getResultList();
    teacherTableModel.setColumnIdentifiers(new String[]{"ID", "Name", "Lastname", "Category", "Department", "Birth Year", "Gender", "Age", "Have Kids", "Salary Amount", "Is Graduate", "Has Defended Thesis", "Dissertation Year"});
    for (Teacher teacher : teachers) {
      teacherTableModel.addRow(new Object[]{teacher.getTeacherId(), teacher.getTeacherName(), teacher.getTeacherLastname(), teacher.getCategory().getCategoryName(), teacher.getDepartment().getDepartmentName(), teacher.getTeacherBirthYear(), teacher.getTeacherGender(), teacher.getTeacherAge(), teacher.isTeacherHaveKids(), teacher.getTeacherSalaryAmount(), teacher.isTeacherIsGraduate(), teacher.isTeacherHasDefendedThesis(), teacher.getTeacherDissertationYear()});
    }
  }

  private void selectTeacher() {
    java.util.List<Category> allCategories = entityManager.createQuery("SELECT c FROM Category c", Category.class).getResultList();
    java.util.List<Department> allDepartments = entityManager.createQuery("SELECT d FROM Department d", Department.class).getResultList();
    if (allCategories.isEmpty() || allDepartments.isEmpty()) {
      JOptionPane.showMessageDialog(this, "There are no categories or departments available. Please create a category and department first.");
      return;
    }

    // Создаем выпадающие списки для выбора категории и департамента
    JComboBox<String> categoryComboBox = new JComboBox<>();
    JComboBox<String> departmentComboBox = new JComboBox<>();

    // Заполняем выпадающие списки категорий и департаментов
    for (Category category : allCategories) {
      categoryComboBox.addItem(category.getCategoryName());
    }
    for (Department department : allDepartments) {
      departmentComboBox.addItem(department.getDepartmentName());
    }

    JPanel panel = new JPanel(new GridLayout(13, 2));
    panel.add(new JLabel("Name:"));
    JTextField nameField = new JTextField();
    panel.add(nameField);
    panel.add(new JLabel("Last Name:"));
    JTextField lastNameField = new JTextField();
    panel.add(lastNameField);
    panel.add(new JLabel("Category:"));
    panel.add(categoryComboBox);
    panel.add(new JLabel("Department:"));
    panel.add(departmentComboBox);
    panel.add(new JLabel("Birth Year:"));
    JTextField birthYearField = new JTextField();
    panel.add(birthYearField);
    panel.add(new JLabel("Gender:"));
    JComboBox<String> genderComboBox = new JComboBox<>(new String[]{"Male", "Female"});
    panel.add(genderComboBox);
    panel.add(new JLabel("Have Kids:"));
    JCheckBox haveKidsCheckBox = new JCheckBox();
    panel.add(haveKidsCheckBox);
    panel.add(new JLabel("Salary Amount:"));
    JTextField salaryAmountField = new JTextField();
    panel.add(salaryAmountField);
    panel.add(new JLabel("Is Graduate:"));
    JCheckBox isGraduateCheckBox = new JCheckBox();
    panel.add(isGraduateCheckBox);
    panel.add(new JLabel("Has Defended Thesis:"));
    JCheckBox hasDefendedThesisCheckBox = new JCheckBox();
    panel.add(hasDefendedThesisCheckBox);
    panel.add(new JLabel("Dissertation Year:"));
    JTextField dissertationYearField = new JTextField();
    panel.add(dissertationYearField);

    int result = JOptionPane.showConfirmDialog(null, panel, "Add Teacher", JOptionPane.OK_CANCEL_OPTION);
    if (result == JOptionPane.OK_OPTION) {
      // Получаем значения из текстовых полей, флажков и выпадающего списка
      String teacherName = nameField.getText().trim();
      String teacherLastName = lastNameField.getText().trim();
      String birthYearStr = birthYearField.getText().trim();
      String salaryAmountStr = salaryAmountField.getText().trim();
      String dissertationYearStr = dissertationYearField.getText().trim();
      boolean haveKids = haveKidsCheckBox.isSelected();
      boolean isGraduate = isGraduateCheckBox.isSelected();
      boolean hasDefendedThesis = hasDefendedThesisCheckBox.isSelected();

      // Проверяем, пустые ли текстовые поля, и формируем соответствующий запрос
      String query = "SELECT t FROM Teacher t WHERE 1 = 1";
      if (!teacherName.isEmpty()) {
        query += " AND t.teacherName = '" + teacherName + "'";
      }
      if (!teacherLastName.isEmpty()) {
        query += " AND t.teacherLastname = '" + teacherLastName + "'";
      }
      if (categoryComboBox.getSelectedIndex() != -1) {
        query += " AND t.category.categoryName = '" + categoryComboBox.getSelectedItem() + "'";
      }
      if (departmentComboBox.getSelectedIndex() != -1) {
        query += " AND t.department.departmentName = '" + departmentComboBox.getSelectedItem() + "'";
      }
      if (!birthYearStr.isEmpty()) {
        query += " AND t.teacherBirthYear = " + Integer.parseInt(birthYearStr);
      }
      if (!salaryAmountStr.isEmpty()) {
        query += " AND t.teacherSalaryAmount = " + Integer.parseInt(salaryAmountStr);
      }
      if (isGraduate) {
        query += " AND t.teacherIsGraduate = true";
      }
      if (hasDefendedThesis) {
        query += " AND t.teacherHasDefendedThesis = true";
      }
      if (!dissertationYearStr.isEmpty()) {
        query += " AND t.teacherDissertationYear = " + Integer.parseInt(dissertationYearStr);
      }

      // Выполняем запрос и получаем список преподавателей
      List<Teacher> teachers = entityManager.createQuery(query, Teacher.class).getResultList();

      // Очищаем таблицу перед обновлением данных
      teacherTableModel.setRowCount(0);

      // Заполняем таблицу данными о преподавателях
      for (Teacher teacher : teachers) {
        teacherTableModel.addRow(new Object[]{
                teacher.getTeacherId(),
                teacher.getTeacherName(),
                teacher.getTeacherLastname(),
                teacher.getCategory().getCategoryName(), // Показываем название категории
                teacher.getDepartment().getDepartmentName(), // Показываем название департамента
                teacher.getTeacherBirthYear(),
                teacher.getTeacherAge(),
                teacher.getTeacherGender(),
                teacher.isTeacherHaveKids(),
                teacher.getTeacherSalaryAmount(),
                teacher.isTeacherIsGraduate(),
                teacher.isTeacherHasDefendedThesis(),
                teacher.getTeacherDissertationYear()
        });
      }
    }
  }


  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new Query2().setVisible(true));
  }

}