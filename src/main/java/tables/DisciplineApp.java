package tables;

import entities.Discipline;

import javax.persistence.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DisciplineApp extends JFrame {

  private EntityManagerFactory entityManagerFactory;
  private EntityManager entityManager;

  private JTable disciplineTable;
  private DefaultTableModel disciplineTableModel;

  public DisciplineApp() {
    initializeDatabase();
    initComponents();
    loadDisciplineData();
  }

  private void initializeDatabase() {
    entityManagerFactory = Persistence.createEntityManagerFactory("FacultyPU");
    entityManager = entityManagerFactory.createEntityManager();
  }

  private void initComponents() {
    setTitle("Discipline Management");
    setSize(600, 400);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    JPanel panel = new JPanel(new BorderLayout());
    getContentPane().add(panel);

    // Table
    disciplineTableModel = new DefaultTableModel();
    disciplineTable = new JTable(disciplineTableModel);
    JScrollPane scrollPane = new JScrollPane(disciplineTable);
    panel.add(scrollPane, BorderLayout.CENTER);

    // Buttons
    JPanel buttonPanel = new JPanel(new FlowLayout());
    panel.add(buttonPanel, BorderLayout.SOUTH);

    JButton addButton = new JButton("Add");
    addButton.addActionListener(e -> addDiscipline());
    buttonPanel.add(addButton);

    JButton editButton = new JButton("Edit");
    editButton.addActionListener(e -> editDiscipline());
    buttonPanel.add(editButton);

    JButton deleteButton = new JButton("Delete");
    deleteButton.addActionListener(e -> deleteDiscipline());
    buttonPanel.add(deleteButton);
  }

  private void loadDisciplineData() {
    // Очищаем таблицу перед загрузкой новых данных
    disciplineTableModel.setRowCount(0);

    // Загружаем данные из базы данных
    List<Discipline> disciplines = entityManager.createQuery("SELECT d FROM Discipline d", Discipline.class).getResultList();
    disciplineTableModel.setColumnIdentifiers(new String[]{"ID", "Name", "Year", "Semester", "Control Form"});
    for (Discipline discipline : disciplines) {
      disciplineTableModel.addRow(new Object[]{
              discipline.getDisciplineId(),
              discipline.getDisciplineName(),
              discipline.getDisciplineYear(),
              discipline.getDisciplineSemester(),
              discipline.getDisciplineControlForm()
      });
    }
  }

  private void addDiscipline() {
    int disciplineId = JsonUtils.getField("discipline_id");

    // Создаем панель для ввода данных
    JPanel inputPanel = new JPanel(new GridLayout(4, 2));
    JTextField nameField = new JTextField();
    JTextField yearField = new JTextField();
    JTextField semesterField = new JTextField();
    JComboBox<String> controlFormComboBox = new JComboBox<>(new String[]{"exam", "setoff"});

    inputPanel.add(new JLabel("Discipline Name:"));
    inputPanel.add(nameField);
    inputPanel.add(new JLabel("Year:"));
    inputPanel.add(yearField);
    inputPanel.add(new JLabel("Semester:"));
    inputPanel.add(semesterField);
    inputPanel.add(new JLabel("Control Form:"));
    inputPanel.add(controlFormComboBox);

    int result = JOptionPane.showConfirmDialog(this, inputPanel, "Enter Discipline Details", JOptionPane.OK_CANCEL_OPTION);
    if (result == JOptionPane.OK_OPTION) {
      String disciplineName = nameField.getText();
      String disciplineYearText = yearField.getText();
      String disciplineSemesterText = semesterField.getText();
      String controlForm = (String) controlFormComboBox.getSelectedItem();
      if (disciplineName.isEmpty() || disciplineYearText.isEmpty() || disciplineSemesterText.isEmpty() || controlForm == null) {
        JOptionPane.showMessageDialog(this, "All fields are required.");
        return;
      }
      try {
        int disciplineYear = Integer.parseInt(disciplineYearText);
        int disciplineSemester = Integer.parseInt(disciplineSemesterText);
        JsonUtils.incrementField("discipline_id");
        entityManager.getTransaction().begin();

        Discipline newDiscipline = new Discipline();
        newDiscipline.setDisciplineId(disciplineId);
        newDiscipline.setDisciplineName(disciplineName);
        newDiscipline.setDisciplineYear(disciplineYear);
        newDiscipline.setDisciplineSemester(disciplineSemester);
        newDiscipline.setDisciplineControlForm(controlForm);

        entityManager.persist(newDiscipline);

        entityManager.getTransaction().commit();

        loadDisciplineData();
      } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Please enter valid numbers for year and semester.");
      }
    }
  }

  private void editDiscipline() {
    int selectedRow = disciplineTable.getSelectedRow();
    if (selectedRow != -1) {
      int disciplineId = (int) disciplineTableModel.getValueAt(selectedRow, 0);

      Discipline discipline = entityManager.find(Discipline.class, disciplineId);

      // Создаем панель для редактирования данных
      JPanel inputPanel = new JPanel(new GridLayout(4, 2));
      JTextField nameField = new JTextField(discipline.getDisciplineName());
      JTextField yearField = new JTextField(String.valueOf(discipline.getDisciplineYear()));
      JTextField semesterField = new JTextField(String.valueOf(discipline.getDisciplineSemester()));
      JComboBox<String> controlFormComboBox = new JComboBox<>(new String[]{"exam", "setoff"});
      controlFormComboBox.setSelectedItem(discipline.getDisciplineControlForm());

      inputPanel.add(new JLabel("Discipline Name:"));
      inputPanel.add(nameField);
      inputPanel.add(new JLabel("Year:"));
      inputPanel.add(yearField);
      inputPanel.add(new JLabel("Semester:"));
      inputPanel.add(semesterField);
      inputPanel.add(new JLabel("Control Form:"));
      inputPanel.add(controlFormComboBox);

      int result = JOptionPane.showConfirmDialog(this, inputPanel, "Edit Discipline Details", JOptionPane.OK_CANCEL_OPTION);
      if (result == JOptionPane.OK_OPTION) {
        String newName = nameField.getText();
        String newYearText = yearField.getText();
        String newSemesterText = semesterField.getText();
        String newControlForm = (String) controlFormComboBox.getSelectedItem();
        if (newName.isEmpty() || newYearText.isEmpty() || newSemesterText.isEmpty() || newControlForm == null) {
          JOptionPane.showMessageDialog(this, "All fields are required.");
          return;
        }
        try {
          int newYear = Integer.parseInt(newYearText);
          int newSemester = Integer.parseInt(newSemesterText);
          entityManager.getTransaction().begin();
          discipline.setDisciplineName(newName);
          discipline.setDisciplineYear(newYear);
          discipline.setDisciplineSemester(newSemester);
          discipline.setDisciplineControlForm(newControlForm);
          entityManager.getTransaction().commit();
          loadDisciplineData();
        } catch (NumberFormatException e) {
          JOptionPane.showMessageDialog(this, "Please enter valid numbers for year and semester.");
        }
      }
    } else {
      JOptionPane.showMessageDialog(this, "Please select a discipline to edit.");
    }
  }

  private void deleteDiscipline() {
    int selectedRow = disciplineTable.getSelectedRow();
    if (selectedRow != -1) {
      int disciplineId = (int) disciplineTableModel.getValueAt(selectedRow, 0);
      int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this discipline?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
      if (option == JOptionPane.YES_OPTION) {

        String[] relatedEntities = {"Exam"};
        String[] relatedFields = {"discipline.disciplineId"};

        for (int i = 0; i < relatedEntities.length; i++) {
          if (hasAssociatedEntities(relatedEntities[i], relatedFields[i], disciplineId)) {
            JOptionPane.showMessageDialog(this, String.format("Cannot delete discipline. There are %s associated with this discipline.", relatedEntities[i].toLowerCase() + "s"));
            return;
          }
        }

        entityManager.getTransaction().begin();
        Discipline discipline = entityManager.find(Discipline.class, disciplineId);
        entityManager.remove(discipline);
        entityManager.getTransaction().commit();
        loadDisciplineData();
      }
    } else {
      JOptionPane.showMessageDialog(this, "Please select a discipline to delete.");
    }
  }

  private boolean hasAssociatedEntities(String entityName, String fieldName, int id) {
    String query = String.format("SELECT COUNT(e) FROM %s e WHERE e.%s = :disciplineId", entityName, fieldName);
    Long count = entityManager.createQuery(query, Long.class)
            .setParameter("disciplineId", id)
            .getSingleResult();
    return count > 0;
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new DisciplineApp().setVisible(true));
  }
}
