package tables;

import entities.Student;
import entities.Group;

import javax.persistence.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Objects;

public class StudentApp extends JFrame {

  private EntityManagerFactory entityManagerFactory;
  private EntityManager entityManager;

  private JTable studentTable;
  private DefaultTableModel studentTableModel;

  public StudentApp() {
    initializeDatabase();
    initComponents();
    loadStudentData();
  }

  private void initializeDatabase() {
    entityManagerFactory = Persistence.createEntityManagerFactory("FacultyPU");
    entityManager = entityManagerFactory.createEntityManager();
  }

  private void initComponents() {
    setTitle("Student Management");
    setSize(800, 400);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    JPanel panel = new JPanel(new BorderLayout());
    getContentPane().add(panel);

    // Table
    studentTableModel = new DefaultTableModel();
    studentTable = new JTable(studentTableModel);

    JScrollPane scrollPane = new JScrollPane(studentTable);
    panel.add(scrollPane, BorderLayout.CENTER);

    // Buttons
    JPanel buttonPanel = new JPanel(new FlowLayout());
    panel.add(buttonPanel, BorderLayout.SOUTH);

    JButton addButton = new JButton("Add");
    addButton.addActionListener(e -> addStudent());
    buttonPanel.add(addButton);

    JButton editButton = new JButton("Edit");
    editButton.addActionListener(e -> editStudent());
    buttonPanel.add(editButton);

    JButton deleteButton = new JButton("Delete");
    deleteButton.addActionListener(e -> deleteStudent());
    buttonPanel.add(deleteButton);
  }

  private void loadStudentData() {
    // Очищаем таблицу перед загрузкой новых данных
    studentTableModel.setRowCount(0);

    // Загружаем данные из базы данных
    List<Student> students = entityManager.createQuery("SELECT s FROM Student s", Student.class).getResultList();
    studentTableModel.setColumnIdentifiers(new String[]{"ID", "Lastname", "Name", "Group", "Course", "Birth Year", "Gender", "Age", "Have Kids", "Have Scholarship", "Scholarship Amount"});
    for (Student student : students) {
      studentTableModel.addRow(new Object[]{student.getStudentId(), student.getStudentName(), student.getStudentLastname(), student.getGroup().getGroupNum(), student.getStudentCourse(), student.getStudentBirthYear(), student.getStudentGender(), student.getStudentAge(), student.isStudentHaveKids(), student.isStudentHaveScolarship(), student.getStudentScolarshipAmount()});
    }
  }

  private void addStudent() {
    int studentId = JsonUtils.getField("student_id");
    List<Group> allGroups = entityManager.createQuery("SELECT g FROM Group g", Group.class).getResultList();
    if (allGroups.isEmpty()) {
      JOptionPane.showMessageDialog(this, "There are no groups available. Please create a group first.");
      return;
    }

    // Создаем выпадающий список для выбора группы
    JComboBox<Integer> groupComboBox = new JComboBox<>();

    // Заполняем выпадающий список факультетов
    List<Group> groups = entityManager.createQuery("SELECT f FROM Group f", Group.class).getResultList();
    for (Group group : groups) {
      groupComboBox.addItem(group.getGroupNum());
    }


    JPanel panel = new JPanel(new GridLayout(11, 2));
    panel.add(new JLabel("Last Name:"));
    JTextField nameField = new JTextField();
    panel.add(nameField);
    panel.add(new JLabel("Name:"));
    JTextField lastNameField = new JTextField();
    panel.add(lastNameField);
    panel.add(new JLabel("Group:"));
    panel.add(groupComboBox); // Добавляем выпадающий список вместо текстового поля для группы
    panel.add(new JLabel("Course:"));
    JTextField courseField = new JTextField();
    panel.add(courseField);
    panel.add(new JLabel("Birth Year:"));
    JTextField birthYearField = new JTextField();
    panel.add(birthYearField);
    panel.add(new JLabel("Gender:"));
    JComboBox<String> genderComboBox = new JComboBox<>(new String[]{"Male", "Female"});
    panel.add(genderComboBox);
    panel.add(new JLabel("Have Kids:"));
    JCheckBox haveKidsCheckBox = new JCheckBox();
    panel.add(haveKidsCheckBox);
    panel.add(new JLabel("Have Scholarship:"));
    JCheckBox haveScholarshipCheckBox = new JCheckBox();
    panel.add(haveScholarshipCheckBox);
    panel.add(new JLabel("Scholarship Amount:"));
    JTextField scholarshipAmountField = new JTextField();
    panel.add(scholarshipAmountField);

    int result = JOptionPane.showConfirmDialog(null, panel, "Add Student", JOptionPane.OK_CANCEL_OPTION);
    if (result == JOptionPane.OK_OPTION) {
      String studentName = nameField.getText().trim();
      String studentLastName = lastNameField.getText().trim();
      String courseStr = courseField.getText().trim();
      String birthYearStr = birthYearField.getText().trim();
      String scholarshipAmountStr = scholarshipAmountField.getText().trim();
      if (studentName.isEmpty() || studentLastName.isEmpty() || courseStr.isEmpty() || birthYearStr.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please fill in all required fields.");
        return;
      }

      // Парсим значения из текстовых полей
      int course, birthYear, scholarshipAmount;
      try {
        course = Integer.parseInt(courseStr);
        birthYear = Integer.parseInt(birthYearStr);
        scholarshipAmount = Integer.parseInt(scholarshipAmountStr);
      } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Invalid input for numeric fields.");
        return;
      }

      // Получаем выбранную группу из выпадающего списка
      Integer groupNum = (Integer) groupComboBox.getSelectedItem();

      JsonUtils.incrementField("student_id");
      // Создаем нового студента и сохраняем его в базе данных
      entityManager.getTransaction().begin();
      Student newStudent = new Student();
      newStudent.setStudentId(studentId);
      newStudent.setStudentName(studentName);
      newStudent.setStudentLastname(studentLastName);
      Group selectedGroup = groups.stream().filter(f -> f.getGroupNum() == (groupNum)).findFirst().orElse(null);
      newStudent.setGroup(selectedGroup);
      newStudent.setStudentCourse(course);
      newStudent.setStudentBirthYear(birthYear);
      newStudent.setStudentAge(2024 - birthYear);
      newStudent.setStudentGender(genderComboBox.getSelectedItem().toString());
      newStudent.setStudentHaveKids(haveKidsCheckBox.isSelected());
      newStudent.setStudentHaveScolarship(haveScholarshipCheckBox.isSelected());
      newStudent.setStudentScolarshipAmount(scholarshipAmount);
      entityManager.persist(newStudent);
      entityManager.getTransaction().commit();

      // Обновляем данные в таблице
      loadStudentData();
    }
  }


  private void editStudent() {
    int selectedRow = studentTable.getSelectedRow();
    if (selectedRow != -1) {
      int studentId = (int) studentTableModel.getValueAt(selectedRow, 0);
      Student student = entityManager.find(Student.class, studentId);
      if (student != null) {

        JComboBox<Integer> groupComboBox = new JComboBox<>();
        List<Group> groups = entityManager.createQuery("SELECT f FROM Group f", Group.class).getResultList();
        for (Group group : groups) {
          groupComboBox.addItem(group.getGroupNum());
        }
        groupComboBox.setSelectedItem(student.getGroup());

        // Создаем панель с текстовыми полями и компонентами выбора
        JPanel panel = new JPanel(new GridLayout(11, 2));
        panel.add(new JLabel("Name:"));
        JTextField nameField = new JTextField(student.getStudentName());
        panel.add(nameField);
        panel.add(new JLabel("Last Name:"));
        JTextField lastNameField = new JTextField(student.getStudentLastname());
        panel.add(lastNameField);
        panel.add(new JLabel("Group:"));
        panel.add(groupComboBox);
        panel.add(new JLabel("Course:"));
        JTextField courseField = new JTextField(String.valueOf(student.getStudentCourse()));
        panel.add(courseField);
        panel.add(new JLabel("Birth Year:"));
        JTextField birthYearField = new JTextField(String.valueOf(student.getStudentBirthYear()));
        panel.add(birthYearField);
        panel.add(new JLabel("Gender:"));
        JComboBox<String> genderComboBox = new JComboBox<>(new String[]{"Male", "Female"});
        genderComboBox.setSelectedItem(student.getStudentGender());
        panel.add(genderComboBox);
        panel.add(new JLabel("Have Kids:"));
        JCheckBox haveKidsCheckBox = new JCheckBox();
        haveKidsCheckBox.setSelected(student.isStudentHaveKids());
        panel.add(haveKidsCheckBox);
        panel.add(new JLabel("Have Scholarship:"));
        JCheckBox haveScholarshipCheckBox = new JCheckBox();
        haveScholarshipCheckBox.setSelected(student.isStudentHaveScolarship());
        panel.add(haveScholarshipCheckBox);
        panel.add(new JLabel("Scholarship Amount:"));
        JTextField scholarshipAmountField = new JTextField(String.valueOf(student.getStudentScolarshipAmount()));
        panel.add(scholarshipAmountField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Edit Student", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
          // Проверяем, что все обязательные поля заполнены
          String studentName = nameField.getText().trim();
          String studentLastName = lastNameField.getText().trim();
          String courseStr = courseField.getText().trim();
          String birthYearStr = birthYearField.getText().trim();
          String scholarshipAmountStr = scholarshipAmountField.getText().trim();
          if (studentName.isEmpty() || studentLastName.isEmpty() || courseStr.isEmpty() || birthYearStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.");
            return;
          }

          // Парсим значения из текстовых полей
          int course, birthYear, scholarshipAmount;
          try {
            course = Integer.parseInt(courseStr);
            birthYear = Integer.parseInt(birthYearStr);
            scholarshipAmount = Integer.parseInt(scholarshipAmountStr);
          } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input for numeric fields.");
            return;
          }

          // Получаем выбранную группу из выпадающего списка
          Integer groupNum = (Integer) groupComboBox.getSelectedItem();

          // Обновляем данные студента и сохраняем их в базе данных
          entityManager.getTransaction().begin();
          student.setStudentName(studentName);
          student.setStudentLastname(studentLastName);
          Group selectedGroup = groups.stream().filter(f -> f.getGroupNum() == (groupNum)).findFirst().orElse(null);
          student.setGroup(selectedGroup);
          student.setStudentCourse(course);
          student.setStudentBirthYear(birthYear);
          student.setStudentGender(Objects.requireNonNull(genderComboBox.getSelectedItem()).toString());
          student.setStudentAge(2024 - birthYear);
          student.setStudentHaveKids(haveKidsCheckBox.isSelected());
          student.setStudentHaveScolarship(haveScholarshipCheckBox.isSelected());
          student.setStudentScolarshipAmount(scholarshipAmount);
          entityManager.getTransaction().commit();

          // Обновляем данные в таблице
          loadStudentData();
        }
      } else {
        JOptionPane.showMessageDialog(this, "Selected student not found.");
      }
    } else {
      JOptionPane.showMessageDialog(this, "Please select a student to edit.");
    }
  }

  private void deleteStudent() {
    int selectedRow = studentTable.getSelectedRow();
    if (selectedRow != -1) {
      int studentId = (int) studentTableModel.getValueAt(selectedRow, 0);
      int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this student?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
      if (option == JOptionPane.YES_OPTION) {

        String[] relatedEntities = {"Thesis", "Exam"};
        String[] relatedFields = {"student.studentId", "student.studentId"};

        for (int i = 0; i < relatedEntities.length; i++) {
          if (hasAssociatedEntities(relatedEntities[i], relatedFields[i], studentId)) {
            JOptionPane.showMessageDialog(this, String.format("Cannot delete student. There are %s associated with this student.", relatedEntities[i].toLowerCase() + "s"));
            return;
          }
        }

        entityManager.getTransaction().begin();
        Student student = entityManager.find(Student.class, studentId);
        entityManager.remove(student);
        entityManager.getTransaction().commit();
        loadStudentData();
      }
    } else {
      JOptionPane.showMessageDialog(this, "Please select a student to delete.");
    }
  }

  private boolean hasAssociatedEntities(String entityName, String fieldName, int id) {
    String query = String.format("SELECT COUNT(e) FROM %s e WHERE e.%s = :studentId", entityName, fieldName);
    Long count = entityManager.createQuery(query, Long.class)
            .setParameter("studentId", id)
            .getSingleResult();
    return count > 0;
  }


  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new StudentApp().setVisible(true));
  }

}
