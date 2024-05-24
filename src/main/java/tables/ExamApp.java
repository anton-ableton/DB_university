package tables;

import entities.Discipline;
import entities.Student;
import entities.Teacher;
import entities.Exam;


import javax.persistence.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ExamApp extends JFrame {

  private EntityManagerFactory entityManagerFactory;
  private EntityManager entityManager;

  private JTable examTable;
  private DefaultTableModel examTableModel;

  public ExamApp() {
    initializeDatabase();
    initComponents();
    loadExamData();
  }

  private void initializeDatabase() {
    entityManagerFactory = Persistence.createEntityManagerFactory("FacultyPU");
    entityManager = entityManagerFactory.createEntityManager();
  }

  private void initComponents() {
    setTitle("Exam Management");
    setSize(800, 400);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    JPanel panel = new JPanel(new BorderLayout());
    getContentPane().add(panel);

    // Table
    examTableModel = new DefaultTableModel();
    examTable = new JTable(examTableModel);
    JScrollPane scrollPane = new JScrollPane(examTable);
    panel.add(scrollPane, BorderLayout.CENTER);

    // Buttons
    JPanel buttonPanel = new JPanel(new FlowLayout());
    panel.add(buttonPanel, BorderLayout.SOUTH);

    JButton addButton = new JButton("Add");
    addButton.addActionListener(e -> addExam());
    buttonPanel.add(addButton);

    JButton editButton = new JButton("Edit");
    editButton.addActionListener(e -> editExam());
    buttonPanel.add(editButton);

    JButton deleteButton = new JButton("Delete");
    deleteButton.addActionListener(e -> deleteExam());
    buttonPanel.add(deleteButton);
  }

  private void loadExamData() {
    examTableModel.setRowCount(0);

    List<Exam> exams = entityManager.createQuery("SELECT e FROM Exam e", Exam.class).getResultList();
    examTableModel.setColumnIdentifiers(new String[]{"ID", "Discipline", "Student", "Teacher", "Grade"});
    for (Exam exam : exams) {
      examTableModel.addRow(new Object[]{exam.getExamId(), exam.getDiscipline().getDisciplineName(),
              exam.getStudent().getStudentName() + " " + exam.getStudent().getStudentLastname(),
              exam.getTeacher().getTeacherName() + " " + exam.getTeacher().getTeacherLastname(),
              exam.getExamGrade()});
    }
  }

  private void addExam() {
    int examId = JsonUtils.getField("exam_id");
    JComboBox<Student> studentComboBox = new JComboBox<>();
    JComboBox<Teacher> teacherComboBox = new JComboBox<>();
    JComboBox<Discipline> disciplineComboBox = new JComboBox<>();

    List<Student> students = entityManager.createQuery("SELECT s FROM Student s", Student.class).getResultList();
    for (Student student : students) {
      studentComboBox.addItem(student);
    }

    List<Teacher> teachers = entityManager.createQuery("SELECT t FROM Teacher t", Teacher.class).getResultList();
    for (Teacher teacher : teachers) {
      teacherComboBox.addItem(teacher);
    }

    List<Discipline> disciplines = entityManager.createQuery("SELECT d FROM Discipline d", Discipline.class).getResultList();
    for (Discipline discipline : disciplines) {
      disciplineComboBox.addItem(discipline);
    }

    JPanel panel = new JPanel(new GridLayout(5, 2));
    panel.add(new JLabel("Grade:"));
    JTextField gradeField = new JTextField();
    panel.add(gradeField);
    panel.add(new JLabel("Student:"));
    panel.add(studentComboBox);
    panel.add(new JLabel("Teacher:"));
    panel.add(teacherComboBox);
    panel.add(new JLabel("Discipline:"));
    panel.add(disciplineComboBox);

    int result = JOptionPane.showConfirmDialog(null, panel, "Add Exam", JOptionPane.OK_CANCEL_OPTION);
    if (result == JOptionPane.OK_OPTION) {
      String grade = gradeField.getText().trim();
      if (grade.isEmpty() || studentComboBox.getSelectedItem() == null || teacherComboBox.getSelectedItem() == null || disciplineComboBox.getSelectedItem() == null) {
        JOptionPane.showMessageDialog(this, "Please fill in all required fields.");
        return;
      }

      try {
        JsonUtils.incrementField("exam_id");
        entityManager.getTransaction().begin();

        Exam newExam = new Exam();
        newExam.setExamId(examId);
        newExam.setExamGrade(grade);
        newExam.setStudent((Student) studentComboBox.getSelectedItem());
        newExam.setTeacher((Teacher) teacherComboBox.getSelectedItem());
        newExam.setDiscipline((Discipline) disciplineComboBox.getSelectedItem());

        entityManager.persist(newExam);

        entityManager.getTransaction().commit();

        loadExamData();
      } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error occurred: " + e.getMessage());
      }
    }
  }

  private void editExam() {
    int selectedRow = examTable.getSelectedRow();
    if (selectedRow != -1) {
      int examId = (int) examTableModel.getValueAt(selectedRow, 0);
      Exam exam = entityManager.find(Exam.class, examId);
      if (exam != null) {
        JComboBox<Student> studentComboBox = new JComboBox<>();
        JComboBox<Teacher> teacherComboBox = new JComboBox<>();
        JComboBox<Discipline> disciplineComboBox = new JComboBox<>();

        List<Student> students = entityManager.createQuery("SELECT s FROM Student s", Student.class).getResultList();
        for (Student student : students) {
          studentComboBox.addItem(student);
        }
        studentComboBox.setSelectedItem(exam.getStudent());

        List<Teacher> teachers = entityManager.createQuery("SELECT t FROM Teacher t", Teacher.class).getResultList();
        for (Teacher teacher : teachers) {
          teacherComboBox.addItem(teacher);
        }
        teacherComboBox.setSelectedItem(exam.getTeacher());

        List<Discipline> disciplines = entityManager.createQuery("SELECT d FROM Discipline d", Discipline.class).getResultList();
        for (Discipline discipline : disciplines) {
          disciplineComboBox.addItem(discipline);
        }
        disciplineComboBox.setSelectedItem(exam.getDiscipline());

        JPanel panel = new JPanel(new GridLayout(5, 2));
        panel.add(new JLabel("Grade:"));
        JTextField gradeField = new JTextField(exam.getExamGrade());
        panel.add(gradeField);
        panel.add(new JLabel("Student:"));
        panel.add(studentComboBox);
        panel.add(new JLabel("Teacher:"));
        panel.add(teacherComboBox);
        panel.add(new JLabel("Discipline:"));
        panel.add(disciplineComboBox);

        int result = JOptionPane.showConfirmDialog(null, panel, "Edit Exam", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
          String grade = gradeField.getText().trim();
          if (grade.isEmpty() || studentComboBox.getSelectedItem() == null || teacherComboBox.getSelectedItem() == null || disciplineComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.");
            return;
          }

          try {
            entityManager.getTransaction().begin();

            exam.setExamGrade(grade);
            exam.setStudent((Student) studentComboBox.getSelectedItem());
            exam.setTeacher((Teacher) teacherComboBox.getSelectedItem());
            exam.setDiscipline((Discipline) disciplineComboBox.getSelectedItem());

            entityManager.getTransaction().commit();

            loadExamData();
          } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error occurred: " + e.getMessage());
          }
        }
      } else {
        JOptionPane.showMessageDialog(this, "Selected exam not found.");
      }
    } else {
      JOptionPane.showMessageDialog(this, "Please select an exam to edit.");
    }
  }


  private void deleteExam() {
    int selectedRow = examTable.getSelectedRow();
    if (selectedRow != -1) {
      int examId = (int) examTableModel.getValueAt(selectedRow, 0);
      int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this exam?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
      if (option == JOptionPane.YES_OPTION) {
        entityManager.getTransaction().begin();
        Exam exam = entityManager.find(Exam.class, examId);
        entityManager.remove(exam);
        entityManager.getTransaction().commit();
        loadExamData();
      }
    } else {
      JOptionPane.showMessageDialog(this, "Please select an exam to delete.");
    }
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new ExamApp().setVisible(true));
  }
}
