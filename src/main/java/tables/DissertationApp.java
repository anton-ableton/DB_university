package tables;

import entities.Dissertation;
import entities.Teacher;

import javax.persistence.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DissertationApp extends JFrame {

  private EntityManagerFactory entityManagerFactory;
  private EntityManager entityManager;

  private JTable dissertationTable;
  private DefaultTableModel dissertationTableModel;

  public DissertationApp() {
    initializeDatabase();
    initComponents();
    loadDissertationData();
  }

  private void initializeDatabase() {
    entityManagerFactory = Persistence.createEntityManagerFactory("FacultyPU");
    entityManager = entityManagerFactory.createEntityManager();
  }

  private void initComponents() {
    setTitle("Dissertation Management");
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

    JButton addButton = new JButton("Add");
    addButton.addActionListener(e -> addDissertation());
    buttonPanel.add(addButton);

    JButton editButton = new JButton("Edit");
    editButton.addActionListener(e -> editDissertation());
    buttonPanel.add(editButton);

    JButton deleteButton = new JButton("Delete");
    deleteButton.addActionListener(e -> deleteDissertation());
    buttonPanel.add(deleteButton);
  }

  private void loadDissertationData() {
    // Очищаем таблицу перед загрузкой новых данных
    dissertationTableModel.setRowCount(0);

    // Загружаем данные из базы данных
    List<Dissertation> dissertations = entityManager.createQuery("SELECT d FROM Dissertation d", Dissertation.class).getResultList();
    dissertationTableModel.setColumnIdentifiers(new String[]{"ID", "Title", "Teacher", "Type"});
    for (Dissertation dissertation : dissertations) {
      dissertationTableModel.addRow(new Object[]{dissertation.getDissertationId(), dissertation.getDissertationTitle(), dissertation.getTeacher().getTeacherName() + " " + dissertation.getTeacher().getTeacherLastname(), dissertation.getDissertationType()});
    }
  }

  private void addDissertation() {
    int dissertationId = JsonUtils.getField("dissertation_id");

    // Создаем панель для ввода данных
    JPanel inputPanel = new JPanel(new GridLayout(4, 2));
    JTextField dissertationTitleField = new JTextField();
    JComboBox<String> teacherComboBox = new JComboBox<>();
    JComboBox<String> dissertationTypeComboBox = new JComboBox<>(new String[]{"PhD", "Doctoral"});

    // Заполняем выпадающий список преподавателей
    List<Teacher> teachers = entityManager.createQuery("SELECT t FROM Teacher t", Teacher.class).getResultList();
    for (Teacher teacher : teachers) {
      teacherComboBox.addItem(teacher.getTeacherName() + " " + teacher.getTeacherLastname());
    }

    inputPanel.add(new JLabel("Dissertation Title:"));
    inputPanel.add(dissertationTitleField);
    inputPanel.add(new JLabel("Teacher:"));
    inputPanel.add(teacherComboBox);
    inputPanel.add(new JLabel("Dissertation Type:"));
    inputPanel.add(dissertationTypeComboBox);

    int result = JOptionPane.showConfirmDialog(this, inputPanel, "Enter Dissertation Details", JOptionPane.OK_CANCEL_OPTION);
    if (result == JOptionPane.OK_OPTION) {
      String dissertationTitle = dissertationTitleField.getText();
      String teacherName = (String) teacherComboBox.getSelectedItem();
      String dissertationType = (String) dissertationTypeComboBox.getSelectedItem();

      if (dissertationTitle.isEmpty() || teacherName == null || dissertationType == null) {
        JOptionPane.showMessageDialog(this, "All fields are required.");
        return;
      }

      try {
        JsonUtils.incrementField("dissertation_id");
        entityManager.getTransaction().begin();

        Dissertation newDissertation = new Dissertation();
        newDissertation.setDissertationId(dissertationId);
        newDissertation.setDissertationTitle(dissertationTitle);
        newDissertation.setDissertationType(dissertationType);

        // Назначаем выбранного преподавателя
        Teacher selectedTeacher = teachers.stream().filter(t ->
                (t.getTeacherName() + " " + t.getTeacherLastname()).equals(teacherName)).findFirst().orElse(null);
        newDissertation.setTeacher(selectedTeacher);

        entityManager.persist(newDissertation);

        entityManager.getTransaction().commit();

        loadDissertationData();
      } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error occurred: " + e.getMessage());
      }
    }
  }


  private void editDissertation() {
    int selectedRow = dissertationTable.getSelectedRow();
    if (selectedRow != -1) {
      int dissertationId = (int) dissertationTableModel.getValueAt(selectedRow, 0);

      Dissertation dissertation = entityManager.find(Dissertation.class, dissertationId);

      // Создаем панель для редактирования данных
      JPanel inputPanel = new JPanel(new GridLayout(4, 2));
      JTextField dissertationTitleField = new JTextField(dissertation.getDissertationTitle());
      JComboBox<String> teacherComboBox = new JComboBox<>();
      JComboBox<String> dissertationTypeComboBox = new JComboBox<>(new String[]{"PhD", "Doctoral"});

      // Заполняем выпадающий список преподавателей
      List<Teacher> teachers = entityManager.createQuery("SELECT t FROM Teacher t", Teacher.class).getResultList();
      for (Teacher teacher : teachers) {
        teacherComboBox.addItem(teacher.getTeacherName() + " " + teacher.getTeacherLastname());
      }
      teacherComboBox.setSelectedItem(dissertation.getTeacher().getTeacherName() + " " + dissertation.getTeacher().getTeacherLastname());
      dissertationTypeComboBox.setSelectedItem(dissertation.getDissertationType());

      inputPanel.add(new JLabel("Dissertation Title:"));
      inputPanel.add(dissertationTitleField);
      inputPanel.add(new JLabel("Teacher:"));
      inputPanel.add(teacherComboBox);
      inputPanel.add(new JLabel("Dissertation Type:"));
      inputPanel.add(dissertationTypeComboBox);

      int result = JOptionPane.showConfirmDialog(this, inputPanel, "Edit Dissertation Details", JOptionPane.OK_CANCEL_OPTION);
      if (result == JOptionPane.OK_OPTION) {
        String newDissertationTitle = dissertationTitleField.getText();
        String newTeacherName = (String) teacherComboBox.getSelectedItem();
        String newDissertationType = (String) dissertationTypeComboBox.getSelectedItem();

        if (newDissertationTitle.isEmpty() || newTeacherName == null || newDissertationType == null) {
          JOptionPane.showMessageDialog(this, "All fields are required.");
          return;
        }

        try {
          entityManager.getTransaction().begin();
          dissertation.setDissertationTitle(newDissertationTitle);
          dissertation.setDissertationType(newDissertationType);

          // Назначаем выбранного преподавателя
          Teacher selectedTeacher = teachers.stream().filter(t ->
                  (t.getTeacherName() + " " + t.getTeacherLastname()).equals(newTeacherName)).findFirst().orElse(null);
          dissertation.setTeacher(selectedTeacher);

          entityManager.getTransaction().commit();
          loadDissertationData();
        } catch (Exception e) {
          JOptionPane.showMessageDialog(this, "Error occurred: " + e.getMessage());
        }
      }
    } else {
      JOptionPane.showMessageDialog(this, "Please select a dissertation to edit.");
    }
  }

  private void deleteDissertation() {
    int selectedRow = dissertationTable.getSelectedRow();
    if (selectedRow != -1) {
      int dissertationId = (int) dissertationTableModel.getValueAt(selectedRow, 0);
      int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this dissertation?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
      if (option == JOptionPane.YES_OPTION) {
        entityManager.getTransaction().begin();
        Dissertation dissertation = entityManager.find(Dissertation.class, dissertationId);
        entityManager.remove(dissertation);
        entityManager.getTransaction().commit();
        loadDissertationData();
      }
    } else {
      JOptionPane.showMessageDialog(this, "Please select a dissertation to delete.");
    }
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new DissertationApp().setVisible(true));
  }
}
