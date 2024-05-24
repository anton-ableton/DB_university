package queries;

import entities.Group;
import entities.Student;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

// Получить перечень и общее число студентов указанных групп либо указанного курса
// (курсов) факультета полностью, по половому признаку, году рождения, возрасту, признаку
// наличия детей, по признаку получения и размеру стипендии.

public class Query1 extends JFrame {

  private EntityManagerFactory entityManagerFactory;
  private EntityManager entityManager;

  private JTable studentTable;
  private DefaultTableModel studentTableModel;

  public Query1() {
    initializeDatabase();
    initComponents();
    loadStudentData();
  }

  private void initializeDatabase() {
    entityManagerFactory = Persistence.createEntityManagerFactory("FacultyPU");
    entityManager = entityManagerFactory.createEntityManager();
  }

  private void initComponents() {
    setTitle("Студенты по характеристикам");
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

    JButton addButton = new JButton("Select");
    addButton.addActionListener(e -> selectStudent());
    buttonPanel.add(addButton);

    JButton clearButton = new JButton("Clear");
    clearButton.addActionListener(e -> loadStudentData());
    buttonPanel.add(clearButton);

  }

  private void loadStudentData() {
    // Очищаем таблицу перед загрузкой новых данных
    studentTableModel.setRowCount(0);

    // Загружаем данные из базы данных
    java.util.List<Student> students = entityManager.createQuery("SELECT s FROM Student s", Student.class).getResultList();
    studentTableModel.setColumnIdentifiers(new String[]{"ID", "Lastname", "Name", "Group", "Course", "Birth Year", "Gender", "Age", "Have Kids", "Have Scholarship", "Scholarship Amount"});
    for (Student student : students) {
      studentTableModel.addRow(new Object[]{student.getStudentId(), student.getStudentName(), student.getStudentLastname(), student.getGroup().getGroupNum(), student.getStudentCourse(), student.getStudentBirthYear(), student.getStudentGender(), student.getStudentAge(), student.isStudentHaveKids(), student.isStudentHaveScolarship(), student.getStudentScolarshipAmount()});
    }
  }

  private void selectStudent() {
    java.util.List<Group> allGroups = entityManager.createQuery("SELECT g FROM Group g", Group.class).getResultList();
    if (allGroups.isEmpty()) {
      JOptionPane.showMessageDialog(this, "There are no groups available. Please create a group first.");
      return;
    }

    // Создаем выпадающий список для выбора группы
    JComboBox<Integer> groupComboBox = new JComboBox<>();

    // Заполняем выпадающий список факультетов
    java.util.List<Group> groups = entityManager.createQuery("SELECT f FROM Group f", Group.class).getResultList();
    for (Group group : groups) {
      groupComboBox.addItem(group.getGroupNum());
    }


    JPanel panel = new JPanel(new GridLayout(12, 2));
    panel.add(new JLabel("Last Name:"));
    JTextField nameField = new JTextField();
    panel.add(nameField);
    panel.add(new JLabel("Name:"));
    JTextField lastNameField = new JTextField();
    panel.add(lastNameField);
    panel.add(new JLabel("Select Group:"));
    JCheckBox selectGroup = new JCheckBox();
    panel.add(selectGroup);
    panel.add(new JLabel("Group:"));
    panel.add(groupComboBox); // Добавляем выпадающий список вместо текстового поля для группы
    panel.add(new JLabel("Course:"));
    JTextField courseField = new JTextField();
    panel.add(courseField);
    panel.add(new JLabel("Birth Year:"));
    JTextField birthYearField = new JTextField();
    panel.add(birthYearField);
    panel.add(new JLabel("Select Gender:"));
    JCheckBox selectGender = new JCheckBox();
    panel.add(selectGender);
    panel.add(new JLabel("Gender:"));
    JComboBox<String> genderComboBox = new JComboBox<>(new String[]{"Male", "Female"});
    panel.add(genderComboBox);
    panel.add(new JLabel("Have Kids:"));
    JCheckBox haveKidsCheckBox = new JCheckBox();
    panel.add(haveKidsCheckBox);
    panel.add(new JLabel("Need Scholarship:"));
    JCheckBox needScholarshipCheckBox = new JCheckBox();
    panel.add(needScholarshipCheckBox);
    panel.add(new JLabel("Have Scholarship:"));
    JCheckBox haveScholarshipCheckBox = new JCheckBox();
    panel.add(haveScholarshipCheckBox);
    panel.add(new JLabel("Scholarship Amount:"));
    JTextField scholarshipAmountField = new JTextField();
    panel.add(scholarshipAmountField);

    int result = JOptionPane.showConfirmDialog(null, panel, "Add Student", JOptionPane.OK_CANCEL_OPTION);
    if (result == JOptionPane.OK_OPTION) {
      // Получаем значения из текстовых полей, флажков и выпадающего списка
      String studentName = nameField.getText().trim();
      String studentLastName = lastNameField.getText().trim();
      Integer groupNum = (Integer) groupComboBox.getSelectedItem();
      String courseStr = courseField.getText().trim();
      String birthYearStr = birthYearField.getText().trim();
      String gender = (String) genderComboBox.getSelectedItem();
      boolean haveKids = haveKidsCheckBox.isSelected();
      boolean haveScholarship = haveScholarshipCheckBox.isSelected();
      boolean selectGroupp = selectGroup.isSelected();
      boolean selectGenderr = selectGender.isSelected();
      boolean needScholarship = needScholarshipCheckBox.isSelected();
      String scholarshipAmountStr = scholarshipAmountField.getText().trim();

      // Строим базовый SQL-запрос
      String query = "SELECT s FROM Student s WHERE 1 = 1";

      // Добавляем условия фильтрации на основе значений полей формы

      if (!studentName.isEmpty()) {
        query += " AND s.studentName = '" + studentName + "'";
      }
      if (!studentLastName.isEmpty()) {
        query += " AND s.studentLastname = '" + studentLastName + "'";
      }
      if (groupNum != null && selectGroupp) {
        query += " AND s.group.groupNum = " + groupNum;
      }
      if (!courseStr.isEmpty()) {
        int course = Integer.parseInt(courseStr);
        query += " AND s.studentCourse = " + course;
      }
      if (!birthYearStr.isEmpty()) {
        int birthYear = Integer.parseInt(birthYearStr);
        query += " AND s.studentBirthYear = " + birthYear;
      }
      if (!gender.isEmpty() && selectGenderr) {
        query += " AND s.studentGender = '" + gender + "'";
      }
      query += " AND s.studentHaveKids = " + haveKids;
      if (needScholarship) {
        query += " AND s.studentHaveScolarship = " + haveScholarship;
      }
      if (!scholarshipAmountStr.isEmpty() && needScholarship) {
        int scholarshipAmount = Integer.parseInt(scholarshipAmountStr);
        query += " AND s.studentScolarshipAmount = " + scholarshipAmount;
      }

      // Выполняем запрос и получаем список студентов
      List<Student> students = entityManager.createQuery(query, Student.class).getResultList();

      // Очищаем таблицу перед обновлением данных
      studentTableModel.setRowCount(0);

      // Заполняем таблицу данными о студентах
      for (Student student : students) {
        studentTableModel.addRow(new Object[]{
                student.getStudentId(),
                student.getStudentName(),
                student.getStudentLastname(),
                student.getGroup().getGroupNum(), // Показываем номер группы
                student.getStudentCourse(),
                student.getStudentBirthYear(),
                student.getStudentAge(),
                student.getStudentGender(),
                student.isStudentHaveKids(),
                student.isStudentHaveScolarship(),
                student.getStudentScolarshipAmount()
        });
      }
    }
  }


  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new Query1().setVisible(true));
  }

}