package tables;

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
import java.util.Objects;

public class TeacherApp extends JFrame {

  private EntityManagerFactory entityManagerFactory;
  private EntityManager entityManager;

  private JTable teacherTable;
  private DefaultTableModel teacherTableModel;

  public TeacherApp() {
    initializeDatabase();
    initComponents();
    loadTeacherData();
  }

  private void initializeDatabase() {
    entityManagerFactory = Persistence.createEntityManagerFactory("FacultyPU");
    entityManager = entityManagerFactory.createEntityManager();
  }

  private void initComponents() {
    setTitle("Teacher Management");
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

    JButton addButton = new JButton("Add");
    addButton.addActionListener(e -> addTeacher());
    buttonPanel.add(addButton);

    JButton editButton = new JButton("Edit");
    editButton.addActionListener(e -> editTeacher());
    buttonPanel.add(editButton);

    JButton deleteButton = new JButton("Delete");
    deleteButton.addActionListener(e -> deleteTeacher());
    buttonPanel.add(deleteButton);
  }

  private void loadTeacherData() {
    // Очищаем таблицу перед загрузкой новых данных
    teacherTableModel.setRowCount(0);

    // Загружаем данные из базы данных
    List<Teacher> teachers = entityManager.createQuery("SELECT t FROM Teacher t", Teacher.class).getResultList();
    teacherTableModel.setColumnIdentifiers(new String[]{"ID", "Name", "Lastname", "Category", "Department", "Birth Year", "Gender", "Age", "Have Kids", "Salary Amount", "Is Graduate", "Has Defended Thesis", "Dissertation Year"});
    for (Teacher teacher : teachers) {
      teacherTableModel.addRow(new Object[]{teacher.getTeacherId(), teacher.getTeacherName(), teacher.getTeacherLastname(), teacher.getCategory().getCategoryName(), teacher.getDepartment().getDepartmentName(), teacher.getTeacherBirthYear(), teacher.getTeacherGender(), teacher.getTeacherAge(), teacher.isTeacherHaveKids(), teacher.getTeacherSalaryAmount(), teacher.isTeacherIsGraduate(), teacher.isTeacherHasDefendedThesis(), teacher.getTeacherDissertationYear()});
    }
  }

  private void addTeacher() {
    int teacherId = JsonUtils.getField("teacher_id");
    List<Category> allCategories = entityManager.createQuery("SELECT c FROM Category c", Category.class).getResultList();
    List<Department> allDepartments = entityManager.createQuery("SELECT d FROM Department d", Department.class).getResultList();
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
      String teacherName = nameField.getText().trim();
      String teacherLastName = lastNameField.getText().trim();
      String birthYearStr = birthYearField.getText().trim();
      String salaryAmountStr = salaryAmountField.getText().trim();
      String dissertationYearStr = dissertationYearField.getText().trim();
      if (teacherName.isEmpty() || teacherLastName.isEmpty() || birthYearStr.isEmpty() || salaryAmountStr.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please fill in all required fields.");
        return;
      }

      // Парсим значения из текстовых полей
      int birthYear, salaryAmount, dissertationYear;
      try {
        birthYear = Integer.parseInt(birthYearStr);
        salaryAmount = Integer.parseInt(salaryAmountStr);
        dissertationYear = dissertationYearStr.isEmpty() ? 0 : Integer.parseInt(dissertationYearStr);
      } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Invalid input for numeric fields.");
        return;
      }

      JsonUtils.incrementField("teacher_id");
      entityManager.getTransaction().begin();
      Teacher newTeacher = new Teacher();
      newTeacher.setTeacherId(teacherId);
      newTeacher.setTeacherName(teacherName);
      newTeacher.setTeacherLastname(teacherLastName);
      newTeacher.setCategory(allCategories.get(categoryComboBox.getSelectedIndex()));
      newTeacher.setDepartment(allDepartments.get(departmentComboBox.getSelectedIndex()));
      newTeacher.setTeacherBirthYear(birthYear);
      newTeacher.setTeacherAge(2024 - birthYear);
      newTeacher.setTeacherGender(Objects.requireNonNull(genderComboBox.getSelectedItem()).toString());
      newTeacher.setTeacherHaveKids(haveKidsCheckBox.isSelected());
      newTeacher.setTeacherSalaryAmount(salaryAmount);
      newTeacher.setTeacherIsGraduate(isGraduateCheckBox.isSelected());
      newTeacher.setTeacherHasDefendedThesis(hasDefendedThesisCheckBox.isSelected());
      newTeacher.setTeacherDissertationYear(dissertationYear);
      entityManager.persist(newTeacher);
      entityManager.getTransaction().commit();
      // Обновляем данные в таблице
      loadTeacherData();
    }
  }

  private void editTeacher() {
    int selectedRow = teacherTable.getSelectedRow();
    if (selectedRow != -1) {
      int teacherId = (int) teacherTableModel.getValueAt(selectedRow, 0);
      Teacher teacher = entityManager.find(Teacher.class, teacherId);
      if (teacher != null) {
        List<Category> allCategories = entityManager.createQuery("SELECT c FROM Category c", Category.class).getResultList();
        List<Department> allDepartments = entityManager.createQuery("SELECT d FROM Department d", Department.class).getResultList();

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
        categoryComboBox.setSelectedItem(teacher.getCategory().getCategoryName());
        departmentComboBox.setSelectedItem(teacher.getDepartment().getDepartmentName());

        JPanel panel = new JPanel(new GridLayout(13, 2));
        panel.add(new JLabel("Name:"));
        JTextField nameField = new JTextField(teacher.getTeacherName());
        panel.add(nameField);
        panel.add(new JLabel("Last Name:"));
        JTextField lastNameField = new JTextField(teacher.getTeacherLastname());
        panel.add(lastNameField);
        panel.add(new JLabel("Category:"));
        panel.add(categoryComboBox);
        panel.add(new JLabel("Department:"));
        panel.add(departmentComboBox);
        panel.add(new JLabel("Birth Year:"));
        JTextField birthYearField = new JTextField(String.valueOf(teacher.getTeacherBirthYear()));
        panel.add(birthYearField);
        panel.add(new JLabel("Gender:"));
        JComboBox<String> genderComboBox = new JComboBox<>(new String[]{"Male", "Female"});
        genderComboBox.setSelectedItem(teacher.getTeacherGender());
        panel.add(genderComboBox);
        panel.add(new JLabel("Have Kids:"));
        JCheckBox haveKidsCheckBox = new JCheckBox();
        haveKidsCheckBox.setSelected(teacher.isTeacherHaveKids());
        panel.add(haveKidsCheckBox);
        panel.add(new JLabel("Salary Amount:"));
        JTextField salaryAmountField = new JTextField(String.valueOf(teacher.getTeacherSalaryAmount()));
        panel.add(salaryAmountField);
        panel.add(new JLabel("Is Graduate:"));
        JCheckBox isGraduateCheckBox = new JCheckBox();
        isGraduateCheckBox.setSelected(teacher.isTeacherIsGraduate());
        panel.add(isGraduateCheckBox);
        panel.add(new JLabel("Has Defended Thesis:"));
        JCheckBox hasDefendedThesisCheckBox = new JCheckBox();
        hasDefendedThesisCheckBox.setSelected(teacher.isTeacherHasDefendedThesis());
        panel.add(hasDefendedThesisCheckBox);
        panel.add(new JLabel("Dissertation Year:"));
        JTextField dissertationYearField = new JTextField(teacher.getTeacherDissertationYear() == null ? "" : String.valueOf(teacher.getTeacherDissertationYear()));
        panel.add(dissertationYearField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Edit Teacher", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
          // Проверяем, что все обязательные поля заполнены
          String teacherName = nameField.getText().trim();
          String teacherLastName = lastNameField.getText().trim();
          String birthYearStr = birthYearField.getText().trim();
          String salaryAmountStr = salaryAmountField.getText().trim();
          String dissertationYearStr = dissertationYearField.getText().trim();
          if (teacherName.isEmpty() || teacherLastName.isEmpty() || birthYearStr.isEmpty() || salaryAmountStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.");
            return;
          }

          // Парсим значения из текстовых полей
          int birthYear, salaryAmount, dissertationYear;
          try {
            birthYear = Integer.parseInt(birthYearStr);
            salaryAmount = Integer.parseInt(salaryAmountStr);
            dissertationYear = dissertationYearStr.isEmpty() ? 0 : Integer.parseInt(dissertationYearStr);
          } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input for numeric fields.");
            return;
          }

          entityManager.getTransaction().begin();
          teacher.setTeacherName(teacherName);
          teacher.setTeacherLastname(teacherLastName);
          teacher.setCategory(allCategories.get(categoryComboBox.getSelectedIndex()));
          teacher.setDepartment(allDepartments.get(departmentComboBox.getSelectedIndex()));
          teacher.setTeacherBirthYear(birthYear);
          teacher.setTeacherAge(2024 - birthYear);
          teacher.setTeacherGender(Objects.requireNonNull(genderComboBox.getSelectedItem()).toString());
          teacher.setTeacherHaveKids(haveKidsCheckBox.isSelected());
          teacher.setTeacherSalaryAmount(salaryAmount);
          teacher.setTeacherIsGraduate(isGraduateCheckBox.isSelected());
          teacher.setTeacherHasDefendedThesis(hasDefendedThesisCheckBox.isSelected());
          teacher.setTeacherDissertationYear(dissertationYear);
          entityManager.getTransaction().commit();

          // Обновляем данные в таблице
          loadTeacherData();
        }
      } else {
        JOptionPane.showMessageDialog(this, "Selected teacher not found.");
      }
    } else {
      JOptionPane.showMessageDialog(this, "Please select a teacher to edit.");
    }
  }

  private void deleteTeacher() {
    int selectedRow = teacherTable.getSelectedRow();
    if (selectedRow != -1) {
      int teacherId = (int) teacherTableModel.getValueAt(selectedRow, 0);
      int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this teacher?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
      if (option == JOptionPane.YES_OPTION) {

        String[] relatedEntities = {"Dissertation", "Thesis", "Exam"};
        String[] relatedFields = {"teacher.teacherId", "teacher.teacherId", "teacher.teacherId"};

        for (int i = 0; i < relatedEntities.length; i++) {
          if (hasAssociatedEntities(relatedEntities[i], relatedFields[i], teacherId)) {
            JOptionPane.showMessageDialog(this, String.format("Cannot delete teacher. There are %s associated with this teacher.", relatedEntities[i].toLowerCase() + "s"));
            return;
          }
        }

        entityManager.getTransaction().begin();
        Teacher group = entityManager.find(Teacher.class, teacherId);
        entityManager.remove(group);
        entityManager.getTransaction().commit();
        loadTeacherData();
      }
    } else {
      JOptionPane.showMessageDialog(this, "Please select a teacher to delete.");
    }
  }

  private boolean hasAssociatedEntities(String entityName, String fieldName, int id) {
    String query = String.format("SELECT COUNT(e) FROM %s e WHERE e.%s = :teacherId", entityName, fieldName);
    Long count = entityManager.createQuery(query, Long.class)
            .setParameter("teacherId", id)
            .getSingleResult();
    return count > 0;
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new TeacherApp().setVisible(true));
  }
}
