package tables;

import entities.ControlForm;

import javax.persistence.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ControlFormApp extends JFrame {

  private EntityManagerFactory entityManagerFactory;
  private EntityManager entityManager;

  private JTable controlFormTable;
  private DefaultTableModel controlFormTableModel;

  public ControlFormApp() {
    initializeDatabase();
    initComponents();
    loadControlFormData();
  }

  private void initializeDatabase() {
    entityManagerFactory = Persistence.createEntityManagerFactory("FacultyPU");
    entityManager = entityManagerFactory.createEntityManager();
  }

  private void initComponents() {
    setTitle("Control Form Management");
    setSize(400, 300);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    JPanel panel = new JPanel(new BorderLayout());
    getContentPane().add(panel);

    // Table
    controlFormTableModel = new DefaultTableModel();
    controlFormTable = new JTable(controlFormTableModel);
    JScrollPane scrollPane = new JScrollPane(controlFormTable);
    panel.add(scrollPane, BorderLayout.CENTER);

    // Buttons
    JPanel buttonPanel = new JPanel(new FlowLayout());
    panel.add(buttonPanel, BorderLayout.SOUTH);

    JButton addButton = new JButton("Add");
    addButton.addActionListener(e -> addControlForm());
    buttonPanel.add(addButton);

    JButton editButton = new JButton("Edit");
    editButton.addActionListener(e -> editControlForm());
    buttonPanel.add(editButton);

    JButton deleteButton = new JButton("Delete");
    deleteButton.addActionListener(e -> deleteControlForm());
    buttonPanel.add(deleteButton);
  }

  private void loadControlFormData() {
    // Очищаем таблицу перед загрузкой новых данных
    controlFormTableModel.setRowCount(0);

    // Загружаем данные из базы данных
    List<ControlForm> controlForms = entityManager.createQuery("SELECT cf FROM ControlForm cf", ControlForm.class).getResultList();
    controlFormTableModel.setColumnIdentifiers(new String[]{"ID", "Name"});
    for (ControlForm controlForm : controlForms) {
      controlFormTableModel.addRow(new Object[]{controlForm.getControlFormId(), controlForm.getControlFormName()});
    }
  }

  private void addControlForm() {
    int controlFormId = JsonUtils.getField("control_form_id");
    String controlFormName = JOptionPane.showInputDialog(this, "Enter control form name:");
    if (controlFormName != null && !controlFormName.isEmpty()) {
      JsonUtils.incrementField("control_form_id");
      entityManager.getTransaction().begin();

      ControlForm newControlForm = new ControlForm();
      newControlForm.setControlFormId(controlFormId);
      newControlForm.setControlFormName(controlFormName);

      entityManager.persist(newControlForm);

      entityManager.getTransaction().commit();

      loadControlFormData();
    }
  }

  private void editControlForm() {
    int selectedRow = controlFormTable.getSelectedRow();
    if (selectedRow != -1) {
      int controlFormId = (int) controlFormTableModel.getValueAt(selectedRow, 0);
      String newName = JOptionPane.showInputDialog(this, "Enter new name:");
      if (newName != null && !newName.isEmpty()) {
        entityManager.getTransaction().begin();
        ControlForm controlForm = entityManager.find(ControlForm.class, controlFormId);
        controlForm.setControlFormName(newName);
        entityManager.getTransaction().commit();
        loadControlFormData();
      }
    } else {
      JOptionPane.showMessageDialog(this, "Please select a control form to edit.");
    }
  }

  private void deleteControlForm() {
    int selectedRow = controlFormTable.getSelectedRow();
    if (selectedRow != -1) {
      int controlFormId = (int) controlFormTableModel.getValueAt(selectedRow, 0);
      int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this control form?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
      if (option == JOptionPane.YES_OPTION) {
        entityManager.getTransaction().begin();
        ControlForm controlForm = entityManager.find(ControlForm.class, controlFormId);
        entityManager.remove(controlForm);
        entityManager.getTransaction().commit();
        loadControlFormData();
      }
    } else {
      JOptionPane.showMessageDialog(this, "Please select a control form to delete.");
    }
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new ControlFormApp().setVisible(true));
  }
}
