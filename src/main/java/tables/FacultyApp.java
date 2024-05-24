package tables;

import entities.Faculty;

import javax.persistence.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FacultyApp extends JFrame {

  private EntityManagerFactory entityManagerFactory;
  private EntityManager entityManager;

  private JTable facultyTable;
  private DefaultTableModel facultyTableModel;

  public FacultyApp() {
    initializeDatabase();
    initComponents();
    loadFacultyData();
  }

  private void initializeDatabase() {
    entityManagerFactory = Persistence.createEntityManagerFactory("FacultyPU");
    entityManager = entityManagerFactory.createEntityManager();
  }

  private void initComponents() {
    setTitle("Faculty Management");
    setSize(400, 300);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    JPanel panel = new JPanel(new BorderLayout());
    getContentPane().add(panel);

    // Table
    facultyTableModel = new DefaultTableModel();
    facultyTable = new JTable(facultyTableModel);
    JScrollPane scrollPane = new JScrollPane(facultyTable);
    panel.add(scrollPane, BorderLayout.CENTER);

    // Buttons
    JPanel buttonPanel = new JPanel(new FlowLayout());
    panel.add(buttonPanel, BorderLayout.SOUTH);

    JButton addButton = new JButton("Add");
    addButton.addActionListener(e -> addFaculty());
    buttonPanel.add(addButton);

    JButton editButton = new JButton("Edit");
    editButton.addActionListener(e -> editFaculty());
    buttonPanel.add(editButton);

    JButton deleteButton = new JButton("Delete");
    deleteButton.addActionListener(e -> deleteFaculty());
    buttonPanel.add(deleteButton);
  }

  private void loadFacultyData() {
    // Очищаем таблицу перед загрузкой новых данных
    facultyTableModel.setRowCount(0);

    // Загружаем данные из базы данных
    List<Faculty> faculties = entityManager.createQuery("SELECT f FROM Faculty f", Faculty.class).getResultList();
    facultyTableModel.setColumnIdentifiers(new String[]{"ID", "Name"});
    for (Faculty faculty : faculties) {
      facultyTableModel.addRow(new Object[]{faculty.getFacultyId(), faculty.getFacultyName()});
    }
  }

  private void addFaculty() {
    int facultyId = JsonUtils.getField("faculty_id");
    String facultyName = JOptionPane.showInputDialog(this, "Enter faculty name:");
    if (facultyName != null && !facultyName.isEmpty()) {
      entityManager.getTransaction().begin();

      Faculty newFaculty = new Faculty();
      newFaculty.setFacultyId(facultyId);
      newFaculty.setFacultyName(facultyName);

      entityManager.persist(newFaculty);

      entityManager.getTransaction().commit();

      loadFacultyData();
    }
  }

  private void editFaculty() {
    int selectedRow = facultyTable.getSelectedRow();
    if (selectedRow != -1) {
      int facultyId = (int) facultyTableModel.getValueAt(selectedRow, 0);
      String newName = JOptionPane.showInputDialog(this, "Enter new name:");
      if (newName != null && !newName.isEmpty()) {
        entityManager.getTransaction().begin();
        Faculty faculty = entityManager.find(Faculty.class, facultyId);
        faculty.setFacultyName(newName);
        entityManager.getTransaction().commit();
        loadFacultyData();
      }
    } else {
      JOptionPane.showMessageDialog(this, "Please select a faculty to edit.");
    }
  }

  private void deleteFaculty() {
    int selectedRow = facultyTable.getSelectedRow();
    if (selectedRow != -1) {
      int facultyId = (int) facultyTableModel.getValueAt(selectedRow, 0);
      int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this faculty?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
      if (option == JOptionPane.YES_OPTION) {
        // Обобщенная проверка связанных таблиц
        String[] relatedEntities = {"Group", "Department"};
        String[] relatedFields = {"faculty.facultyId", "faculty.facultyId"};

        for (int i = 0; i < relatedEntities.length; i++) {
          if (hasAssociatedEntities(relatedEntities[i], relatedFields[i], facultyId)) {
            JOptionPane.showMessageDialog(this, String.format("Cannot delete faculty. There are %s associated with this faculty.", relatedEntities[i].toLowerCase() + "s"));
            return;
          }
        }

        entityManager.getTransaction().begin();
        Faculty faculty = entityManager.find(Faculty.class, facultyId);
        entityManager.remove(faculty);
        entityManager.getTransaction().commit();
        loadFacultyData();
      }
    } else {
      JOptionPane.showMessageDialog(this, "Please select a faculty to delete.");
    }
  }

  private boolean hasAssociatedEntities(String entityName, String fieldName, int facultyId) {
    String query = String.format("SELECT COUNT(e) FROM %s e WHERE e.%s = :facultyId", entityName, fieldName);
    Long count = entityManager.createQuery(query, Long.class)
            .setParameter("facultyId", facultyId)
            .getSingleResult();
    return count > 0;
  }


  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new FacultyApp().setVisible(true));
  }
}
