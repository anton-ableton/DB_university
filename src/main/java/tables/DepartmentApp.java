package tables;

import entities.Department;
import entities.Faculty;

import javax.persistence.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DepartmentApp extends JFrame {

  private EntityManagerFactory entityManagerFactory;
  private EntityManager entityManager;

  private JTable departmentTable;
  private DefaultTableModel departmentTableModel;

  public DepartmentApp() {
    initializeDatabase();
    initComponents();
    loadDepartmentData();
  }

  private void initializeDatabase() {
    entityManagerFactory = Persistence.createEntityManagerFactory("FacultyPU");
    entityManager = entityManagerFactory.createEntityManager();
  }

  private void initComponents() {
    setTitle("Department Management");
    setSize(500, 400);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    JPanel panel = new JPanel(new BorderLayout());
    getContentPane().add(panel);

    // Table
    departmentTableModel = new DefaultTableModel();
    departmentTable = new JTable(departmentTableModel);
    JScrollPane scrollPane = new JScrollPane(departmentTable);
    panel.add(scrollPane, BorderLayout.CENTER);

    // Buttons
    JPanel buttonPanel = new JPanel(new FlowLayout());
    panel.add(buttonPanel, BorderLayout.SOUTH);

    JButton addButton = new JButton("Add");
    addButton.addActionListener(e -> addDepartment());
    buttonPanel.add(addButton);

    JButton editButton = new JButton("Edit");
    editButton.addActionListener(e -> editDepartment());
    buttonPanel.add(editButton);

    JButton deleteButton = new JButton("Delete");
    deleteButton.addActionListener(e -> deleteDepartment());
    buttonPanel.add(deleteButton);
  }

  private void loadDepartmentData() {
    // Очищаем таблицу перед загрузкой новых данных
    departmentTableModel.setRowCount(0);

    // Загружаем данные из базы данных
    List<Department> departments = entityManager.createQuery("SELECT d FROM Department d", Department.class).getResultList();
    departmentTableModel.setColumnIdentifiers(new String[]{"ID", "Name", "Faculty"});
    for (Department department : departments) {
      departmentTableModel.addRow(new Object[]{
              department.getDepartmentId(),
              department.getDepartmentName(),
              department.getFaculty().getFacultyName()
      });
    }
  }

  private void addDepartment() {
    JTextField departmentNameField = new JTextField();
//    JComboBox<Faculty> facultyComboBox = new JComboBox<>(getFaculties().toArray(new Faculty[0]));
    JComboBox<String> facultyComboBox = new JComboBox<>();
    List<Faculty> faculties = entityManager.createQuery("SELECT f FROM Faculty f", Faculty.class).getResultList();
    for (Faculty faculty : faculties) {
      facultyComboBox.addItem(faculty.getFacultyName());
    }

    JPanel panel = new JPanel(new GridLayout(0, 1));
    panel.add(new JLabel("Department Name:"));
    panel.add(departmentNameField);
    panel.add(new JLabel("Faculty:"));
    panel.add(facultyComboBox);

    int result = JOptionPane.showConfirmDialog(null, panel, "Add Department", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    if (result == JOptionPane.OK_OPTION) {
      String departmentName = departmentNameField.getText();
      String facultyName = (String) facultyComboBox.getSelectedItem();

      if (departmentName != null && !departmentName.isEmpty() && facultyName != null) {
        int departmentId = JsonUtils.getField("department_id");
        JsonUtils.incrementField("department_id");
        entityManager.getTransaction().begin();

        Department newDepartment = new Department();
        newDepartment.setDepartmentId(departmentId);
        newDepartment.setDepartmentName(departmentName);
        Faculty selectedFaculty = faculties.stream().filter(f -> f.getFacultyName().equals(facultyName)).findFirst().orElse(null);
        newDepartment.setFaculty(selectedFaculty);

        entityManager.persist(newDepartment);
        entityManager.getTransaction().commit();

        loadDepartmentData();
      } else {
        JOptionPane.showMessageDialog(this, "All fields are required.");
      }
    }
  }

  private void editDepartment() {
    int selectedRow = departmentTable.getSelectedRow();
    if (selectedRow != -1) {
      int departmentId = (int) departmentTableModel.getValueAt(selectedRow, 0);
      Department department = entityManager.find(Department.class, departmentId);

      JTextField departmentNameField = new JTextField(department.getDepartmentName());
      JComboBox<Faculty> facultyComboBox = new JComboBox<>(getFaculties().toArray(new Faculty[0]));
      facultyComboBox.setSelectedItem(department.getFaculty());

      JPanel panel = new JPanel(new GridLayout(0, 1));
      panel.add(new JLabel("Department Name:"));
      panel.add(departmentNameField);
      panel.add(new JLabel("Faculty:"));
      panel.add(facultyComboBox);

      int result = JOptionPane.showConfirmDialog(null, panel, "Edit Department", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
      if (result == JOptionPane.OK_OPTION) {
        String newDepartmentName = departmentNameField.getText();
        Faculty selectedFaculty = (Faculty) facultyComboBox.getSelectedItem();

        if (newDepartmentName != null && !newDepartmentName.isEmpty() && selectedFaculty != null) {
          entityManager.getTransaction().begin();
          department.setDepartmentName(newDepartmentName);
          department.setFaculty(selectedFaculty);
          entityManager.getTransaction().commit();
          loadDepartmentData();
        } else {
          JOptionPane.showMessageDialog(this, "All fields are required.");
        }
      }
    } else {
      JOptionPane.showMessageDialog(this, "Please select a department to edit.");
    }
  }

  private void deleteDepartment() {
    int selectedRow = departmentTable.getSelectedRow();
    if (selectedRow != -1) {
      int departmentId = (int) departmentTableModel.getValueAt(selectedRow, 0);
      int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this department?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
      if (option == JOptionPane.YES_OPTION) {
        // Check if the group has associated students
        if (hasAssociatedEntities("Teacher", "department.departmentId", departmentId)) {
          JOptionPane.showMessageDialog(this, "Cannot delete department. There are teachers associated with this department.");
          return;
        }

        entityManager.getTransaction().begin();
        Department department = entityManager.find(Department.class, departmentId);
        entityManager.remove(department);
        entityManager.getTransaction().commit();
        loadDepartmentData();
      }
    } else {
      JOptionPane.showMessageDialog(this, "Please select a department to delete.");
    }
  }

  private boolean hasAssociatedEntities(String entityName, String fieldName, int id) {
    String query = String.format("SELECT COUNT(e) FROM %s e WHERE e.%s = :departmentId", entityName, fieldName);
    Long count = entityManager.createQuery(query, Long.class)
            .setParameter("departmentId", id)
            .getSingleResult();
    return count > 0;
  }

  private List<Faculty> getFaculties() {
    return entityManager.createQuery("SELECT f FROM Faculty f", Faculty.class).getResultList();
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new DepartmentApp().setVisible(true));
  }
}
