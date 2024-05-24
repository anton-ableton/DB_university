package tables;

import entities.Thesis;
import entities.Student;
import entities.Teacher;

import javax.persistence.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ThesisApp extends JFrame {

  private EntityManagerFactory entityManagerFactory;
  private EntityManager entityManager;

  private JTable thesisTable;
  private DefaultTableModel thesisTableModel;

  public ThesisApp() {
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

    JButton addButton = new JButton("Add");
    addButton.addActionListener(e -> addThesis());
    buttonPanel.add(addButton);

    JButton editButton = new JButton("Edit");
    editButton.addActionListener(e -> editThesis());
    buttonPanel.add(editButton);

    JButton deleteButton = new JButton("Delete");
    deleteButton.addActionListener(e -> deleteThesis());
    buttonPanel.add(deleteButton);
  }

  private void loadThesisData() {
    // Очищаем таблицу перед загрузкой новых данных
    thesisTableModel.setRowCount(0);

    // Загружаем данные из базы данных
    List<Thesis> theses = entityManager.createQuery("SELECT t FROM Thesis t", Thesis.class).getResultList();
    thesisTableModel.setColumnIdentifiers(new String[]{"ID", "Title", "Student", "Teacher"});
    for (Thesis thesis : theses) {
      String studentName = thesis.getStudent() != null ? thesis.getStudent().getStudentName() + " " + thesis.getStudent().getStudentLastname() : "N/A";
      String teacherName = thesis.getTeacher() != null ? thesis.getTeacher().getTeacherName() + " " + thesis.getTeacher().getTeacherLastname() : "N/A";
      thesisTableModel.addRow(new Object[]{thesis.getThesisId(), thesis.getTitle(), studentName, teacherName});
    }
  }

  private void addThesis() {
    int thesisId = JsonUtils.getField("thesis_id");

    JComboBox<Student> studentComboBox = new JComboBox<>();
    JComboBox<Teacher> teacherComboBox = new JComboBox<>();

    List<Student> students = entityManager.createQuery("SELECT s FROM Student s", Student.class).getResultList();
    for (Student student : students) {
      studentComboBox.addItem(student);
    }

    List<Teacher> teachers = entityManager.createQuery("SELECT t FROM Teacher t", Teacher.class).getResultList();
    for (Teacher teacher : teachers) {
      teacherComboBox.addItem(teacher);
    }

    JPanel panel = new JPanel(new GridLayout(4, 2));
    panel.add(new JLabel("Title:"));
    JTextField titleField = new JTextField();
    panel.add(titleField);
    panel.add(new JLabel("Student:"));
    panel.add(studentComboBox);
    panel.add(new JLabel("Teacher:"));
    panel.add(teacherComboBox);

    int result = JOptionPane.showConfirmDialog(null, panel, "Add Thesis", JOptionPane.OK_CANCEL_OPTION);
    if (result == JOptionPane.OK_OPTION) {
      String title = titleField.getText().trim();
      if (title.isEmpty() || studentComboBox.getSelectedItem() == null || teacherComboBox.getSelectedItem() == null) {
        JOptionPane.showMessageDialog(this, "Please fill in all required fields.");
        return;
      }

      try {
        JsonUtils.incrementField("thesis_id");
        entityManager.getTransaction().begin();

        Thesis newThesis = new Thesis();
        newThesis.setThesisId(thesisId);
        newThesis.setTitle(title);

        Teacher selectedTeacher = (Teacher) teacherComboBox.getSelectedItem();
        Student selectedStudent = (Student) studentComboBox.getSelectedItem();
        newThesis.setTeacher(selectedTeacher);
        newThesis.setStudent(selectedStudent);

        entityManager.persist(newThesis);

        entityManager.getTransaction().commit();

        loadThesisData();
      } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error occurred: " + e.getMessage());
      }
    }
  }

  private void editThesis() {
    int selectedRow = thesisTable.getSelectedRow();
    if (selectedRow != -1) {
      int thesisId = (int) thesisTableModel.getValueAt(selectedRow, 0);
      Thesis thesis = entityManager.find(Thesis.class, thesisId);
      if (thesis != null) {
        JComboBox<Student> studentComboBox = new JComboBox<>();
        JComboBox<Teacher> teacherComboBox = new JComboBox<>();

        List<Student> students = entityManager.createQuery("SELECT s FROM Student s", Student.class).getResultList();
        for (Student student : students) {
          studentComboBox.addItem(student);
        }
        studentComboBox.setSelectedItem(thesis.getStudent());

        List<Teacher> teachers = entityManager.createQuery("SELECT t FROM Teacher t", Teacher.class).getResultList();
        for (Teacher teacher : teachers) {
          teacherComboBox.addItem(teacher);
        }
        teacherComboBox.setSelectedItem(thesis.getTeacher());

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("Title:"));
        JTextField titleField = new JTextField(thesis.getTitle());
        panel.add(titleField);
        panel.add(new JLabel("Student:"));
        panel.add(studentComboBox);
        panel.add(new JLabel("Teacher:"));
        panel.add(teacherComboBox);

        int result = JOptionPane.showConfirmDialog(null, panel, "Edit Thesis", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
          String title = titleField.getText().trim();
          if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.");
            return;
          }

          Student selectedStudent = (Student) studentComboBox.getSelectedItem();
          Teacher selectedTeacher = (Teacher) teacherComboBox.getSelectedItem();

          entityManager.getTransaction().begin();
          thesis.setTitle(title);
          thesis.setStudent(selectedStudent);
          thesis.setTeacher(selectedTeacher);
          entityManager.getTransaction().commit();

          loadThesisData();
        }
      } else {
        JOptionPane.showMessageDialog(this, "Selected thesis not found.");
      }
    } else {
      JOptionPane.showMessageDialog(this, "Please select a thesis to edit.");
    }
  }

  private void deleteThesis() {
    int selectedRow = thesisTable.getSelectedRow();
    if (selectedRow != -1) {
      int thesisId = (int) thesisTableModel.getValueAt(selectedRow, 0);
      int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this thesis?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
      if (option == JOptionPane.YES_OPTION) {
        entityManager.getTransaction().begin();
        Thesis thesis = entityManager.find(Thesis.class, thesisId);
        entityManager.remove(thesis);
        entityManager.getTransaction().commit();
        loadThesisData();
      }
    } else {
      JOptionPane.showMessageDialog(this, "Please select a thesis to delete.");
    }
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new ThesisApp().setVisible(true));
  }
}
