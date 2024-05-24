package queries;

import entities.Department;
import entities.Faculty;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;


//        Получить перечень кафедр, проводящих занятия в указанной группе либо на указанном
//        курсе указанного факультета в указанном семестре, либо за указанный период.

public class Query4 extends JFrame {
  private EntityManagerFactory entityManagerFactory;
  private EntityManager entityManager;

  private JTable departmentTable;
  private DefaultTableModel departmentTableModel;

  public Query4() {
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

    JButton addButton = new JButton("Select");
    addButton.addActionListener(e -> selectDepartment());
    buttonPanel.add(addButton);

    JButton clearButton = new JButton("Clear");
    clearButton.addActionListener(e -> loadDepartmentData());
    buttonPanel.add(clearButton);
  }

  private void loadDepartmentData() {
    // Очищаем таблицу перед загрузкой новых данных
    departmentTableModel.setRowCount(0);

    // Загружаем данные из базы данных
    java.util.List<Department> departments = entityManager.createQuery("SELECT d FROM Department d", Department.class).getResultList();
    departmentTableModel.setColumnIdentifiers(new String[]{"ID", "Name", "Faculty"});
    for (Department department : departments) {
      departmentTableModel.addRow(new Object[]{
              department.getDepartmentId(),
              department.getDepartmentName(),
              department.getFaculty().getFacultyName()
      });
    }
  }


  private void selectDepartment() {
    int selectedRow = departmentTable.getSelectedRow();
    if (selectedRow != -1) {
      int departmentId = (int) departmentTableModel.getValueAt(selectedRow, 0);
      Department department = entityManager.find(Department.class, departmentId);

      JTextField departmentNameField = new JTextField(department.getDepartmentName());
      JComboBox<String> facultyComboBox = new JComboBox<>();
      facultyComboBox.setSelectedItem(department.getFaculty());

      JPanel panel = new JPanel(new GridLayout(0, 1));
      panel.add(new JLabel("Department Name:"));
      panel.add(departmentNameField);
      panel.add(new JLabel("Group:"));
//      panel.add(groupComboBox);
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

  private List<Faculty> getFaculties() {
    return entityManager.createQuery("SELECT f FROM Faculty f", Faculty.class).getResultList();
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new Query4().setVisible(true));
  }

}
