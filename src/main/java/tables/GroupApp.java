package tables;

import entities.Faculty;
import entities.Group;

import javax.persistence.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GroupApp extends JFrame {

  private EntityManagerFactory entityManagerFactory;
  private EntityManager entityManager;

  private JTable groupTable;
  private DefaultTableModel groupTableModel;

  public GroupApp() {
    initializeDatabase();
    initComponents();
    loadGroupData();
  }

  private void initializeDatabase() {
    entityManagerFactory = Persistence.createEntityManagerFactory("FacultyPU");
    entityManager = entityManagerFactory.createEntityManager();
  }

  private void initComponents() {
    setTitle("Group Management");
    setSize(500, 400);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    JPanel panel = new JPanel(new BorderLayout());
    getContentPane().add(panel);

    // Table
    groupTableModel = new DefaultTableModel();
    groupTable = new JTable(groupTableModel);
    JScrollPane scrollPane = new JScrollPane(groupTable);
    panel.add(scrollPane, BorderLayout.CENTER);

    // Buttons
    JPanel buttonPanel = new JPanel(new FlowLayout());
    panel.add(buttonPanel, BorderLayout.SOUTH);

    JButton addButton = new JButton("Add");
    addButton.addActionListener(e -> addGroup());
    buttonPanel.add(addButton);

    JButton editButton = new JButton("Edit");
    editButton.addActionListener(e -> editGroup());
    buttonPanel.add(editButton);

    JButton deleteButton = new JButton("Delete");
    deleteButton.addActionListener(e -> deleteGroup());
    buttonPanel.add(deleteButton);
  }

  private void loadGroupData() {
    // Очищаем таблицу перед загрузкой новых данных
    groupTableModel.setRowCount(0);

    // Загружаем данные из базы данных
    List<Group> groups = entityManager.createQuery("SELECT g FROM Group g", Group.class).getResultList();
    groupTableModel.setColumnIdentifiers(new String[]{"ID", "Number", "Faculty"});
    for (Group group : groups) {
      groupTableModel.addRow(new Object[]{group.getGroupId(), group.getGroupNum(), group.getFaculty().getFacultyName()});
    }
  }

  private void addGroup() {
    int groupId = JsonUtils.getField("group_id");

    // Создаем панель для ввода данных
    JPanel inputPanel = new JPanel(new GridLayout(3, 2));
    JTextField groupNumField = new JTextField();
    JComboBox<String> facultyComboBox = new JComboBox<>();

    // Заполняем выпадающий список факультетов
    List<Faculty> faculties = entityManager.createQuery("SELECT f FROM Faculty f", Faculty.class).getResultList();
    for (Faculty faculty : faculties) {
      facultyComboBox.addItem(faculty.getFacultyName());
    }

    inputPanel.add(new JLabel("Group Number:"));
    inputPanel.add(groupNumField);
    inputPanel.add(new JLabel("Faculty:"));
    inputPanel.add(facultyComboBox);

    int result = JOptionPane.showConfirmDialog(this, inputPanel, "Enter Group Details", JOptionPane.OK_CANCEL_OPTION);
    if (result == JOptionPane.OK_OPTION) {
      String groupNumText = groupNumField.getText();
      String facultyName = (String) facultyComboBox.getSelectedItem();
      if (groupNumText.isEmpty() || facultyName == null) {
        JOptionPane.showMessageDialog(this, "All fields are required.");
        return;
      }
      try {
        int groupNum = Integer.parseInt(groupNumText);
        JsonUtils.incrementField("group_id");
        entityManager.getTransaction().begin();

        Group newGroup = new Group();
        newGroup.setGroupId(groupId);
        newGroup.setGroupNum(groupNum);

        // Назначаем выбранный факультет
        Faculty selectedFaculty = faculties.stream().filter(f -> f.getFacultyName().equals(facultyName)).findFirst().orElse(null);
        newGroup.setFaculty(selectedFaculty);

        entityManager.persist(newGroup);

        entityManager.getTransaction().commit();

        loadGroupData();
      } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Please enter a valid number for group number.");
      }
    }
  }

  private void editGroup() {
    int selectedRow = groupTable.getSelectedRow();
    if (selectedRow != -1) {
      int groupId = (int) groupTableModel.getValueAt(selectedRow, 0);

      Group group = entityManager.find(Group.class, groupId);

      // Создаем панель для редактирования данных
      JPanel inputPanel = new JPanel(new GridLayout(3, 2));
      JTextField groupNumField = new JTextField(String.valueOf(group.getGroupNum()));
      JComboBox<String> facultyComboBox = new JComboBox<>();

      // Заполняем выпадающий список факультетов
      List<Faculty> faculties = entityManager.createQuery("SELECT f FROM Faculty f", Faculty.class).getResultList();
      for (Faculty faculty : faculties) {
        facultyComboBox.addItem(faculty.getFacultyName());
      }
      facultyComboBox.setSelectedItem(group.getFaculty().getFacultyName());

      inputPanel.add(new JLabel("Group Number:"));
      inputPanel.add(groupNumField);
      inputPanel.add(new JLabel("Faculty:"));
      inputPanel.add(facultyComboBox);

      int result = JOptionPane.showConfirmDialog(this, inputPanel, "Edit Group Details", JOptionPane.OK_CANCEL_OPTION);
      if (result == JOptionPane.OK_OPTION) {
        String newGroupNumText = groupNumField.getText();
        String newFacultyName = (String) facultyComboBox.getSelectedItem();
        if (newGroupNumText.isEmpty() || newFacultyName == null) {
          JOptionPane.showMessageDialog(this, "All fields are required.");
          return;
        }
        try {
          int newGroupNum = Integer.parseInt(newGroupNumText);
          entityManager.getTransaction().begin();
          group.setGroupNum(newGroupNum);

          // Назначаем выбранный факультет
          Faculty selectedFaculty = faculties.stream().filter(f -> f.getFacultyName().equals(newFacultyName)).findFirst().orElse(null);
          group.setFaculty(selectedFaculty);

          entityManager.getTransaction().commit();
          loadGroupData();
        } catch (NumberFormatException e) {
          JOptionPane.showMessageDialog(this, "Please enter a valid number for group number.");
        }
      }
    } else {
      JOptionPane.showMessageDialog(this, "Please select a group to edit.");
    }
  }

  private void deleteGroup() {
    int selectedRow = groupTable.getSelectedRow();
    if (selectedRow != -1) {
      int groupId = (int) groupTableModel.getValueAt(selectedRow, 0);
      int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this group?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
      if (option == JOptionPane.YES_OPTION) {
        // Check if the group has associated students
        if (hasAssociatedEntities("Student", "group.groupId", groupId)) {
          JOptionPane.showMessageDialog(this, "Cannot delete group. There are students associated with this group.");
          return;
        }

        entityManager.getTransaction().begin();
        Group group = entityManager.find(Group.class, groupId);
        entityManager.remove(group);
        entityManager.getTransaction().commit();
        loadGroupData();
      }
    } else {
      JOptionPane.showMessageDialog(this, "Please select a group to delete.");
    }
  }


  private boolean hasAssociatedEntities(String entityName, String fieldName, int id) {
    String query = String.format("SELECT COUNT(e) FROM %s e WHERE e.%s = :groupId", entityName, fieldName);
    Long count = entityManager.createQuery(query, Long.class)
            .setParameter("groupId", id)
            .getSingleResult();
    return count > 0;
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new GroupApp().setVisible(true));
  }
}
