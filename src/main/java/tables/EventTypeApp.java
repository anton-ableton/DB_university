package tables;

import entities.EventType;

import javax.persistence.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EventTypeApp extends JFrame {

  private EntityManagerFactory entityManagerFactory;
  private EntityManager entityManager;

  private JTable eventTypeTable;
  private DefaultTableModel eventTypeTableModel;

  public EventTypeApp() {
    initializeDatabase();
    initComponents();
    loadEventTypeData();
  }

  private void initializeDatabase() {
    entityManagerFactory = Persistence.createEntityManagerFactory("FacultyPU");
    entityManager = entityManagerFactory.createEntityManager();
  }

  private void initComponents() {
    setTitle("Event Type Management");
    setSize(400, 300);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    JPanel panel = new JPanel(new BorderLayout());
    getContentPane().add(panel);

    // Table
    eventTypeTableModel = new DefaultTableModel();
    eventTypeTable = new JTable(eventTypeTableModel);
    JScrollPane scrollPane = new JScrollPane(eventTypeTable);
    panel.add(scrollPane, BorderLayout.CENTER);

    // Buttons
    JPanel buttonPanel = new JPanel(new FlowLayout());
    panel.add(buttonPanel, BorderLayout.SOUTH);

    JButton addButton = new JButton("Add");
    addButton.addActionListener(e -> addEventType());
    buttonPanel.add(addButton);

    JButton editButton = new JButton("Edit");
    editButton.addActionListener(e -> editEventType());
    buttonPanel.add(editButton);

    JButton deleteButton = new JButton("Delete");
    deleteButton.addActionListener(e -> deleteEventType());
    buttonPanel.add(deleteButton);
  }

  private void loadEventTypeData() {
    // Очищаем таблицу перед загрузкой новых данных
    eventTypeTableModel.setRowCount(0);

    // Загружаем данные из базы данных
    List<EventType> eventTypes = entityManager.createQuery("SELECT et FROM EventType et", EventType.class).getResultList();
    eventTypeTableModel.setColumnIdentifiers(new String[]{"ID", "Name", "Number of Hours"});
    for (EventType eventType : eventTypes) {
      eventTypeTableModel.addRow(new Object[]{eventType.getEventTypeId(), eventType.getEventTypeName(), eventType.getEventNumOfHours()});
    }
  }

  private void addEventType() {
    int eventTypeId = JsonUtils.getField("event_type_id");

    // Создаем панель для ввода данных
    JPanel inputPanel = new JPanel(new GridLayout(3, 2));
    JTextField nameField = new JTextField();
    JTextField hoursField = new JTextField();

    inputPanel.add(new JLabel("Event Type Name:"));
    inputPanel.add(nameField);
    inputPanel.add(new JLabel("Number of Hours:"));
    inputPanel.add(hoursField);

    int result = JOptionPane.showConfirmDialog(this, inputPanel, "Enter Event Type Details", JOptionPane.OK_CANCEL_OPTION);
    if (result == JOptionPane.OK_OPTION) {
      String eventTypeName = nameField.getText();
      String eventNumOfHoursText = hoursField.getText();
      if (eventTypeName.isEmpty() || eventNumOfHoursText.isEmpty()) {
        JOptionPane.showMessageDialog(this, "All fields are required.");
        return;
      }
      try {
        int eventNumOfHours = Integer.parseInt(eventNumOfHoursText);
        JsonUtils.incrementField("event_type_id");
        entityManager.getTransaction().begin();

        EventType newEventType = new EventType();
        newEventType.setEventTypeId(eventTypeId);
        newEventType.setEventTypeName(eventTypeName);
        newEventType.setEventNumOfHours(eventNumOfHours);

        entityManager.persist(newEventType);

        entityManager.getTransaction().commit();

        loadEventTypeData();
      } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Please enter a valid number of hours.");
      }
    }
  }

  private void editEventType() {
    int selectedRow = eventTypeTable.getSelectedRow();
    if (selectedRow != -1) {
      int eventTypeId = (int) eventTypeTableModel.getValueAt(selectedRow, 0);

      EventType eventType = entityManager.find(EventType.class, eventTypeId);

      // Создаем панель для редактирования данных
      JPanel inputPanel = new JPanel(new GridLayout(3, 2));
      JTextField nameField = new JTextField(eventType.getEventTypeName());
      JTextField hoursField = new JTextField(String.valueOf(eventType.getEventNumOfHours()));

      inputPanel.add(new JLabel("Event Type Name:"));
      inputPanel.add(nameField);
      inputPanel.add(new JLabel("Number of Hours:"));
      inputPanel.add(hoursField);

      int result = JOptionPane.showConfirmDialog(this, inputPanel, "Edit Event Type Details", JOptionPane.OK_CANCEL_OPTION);
      if (result == JOptionPane.OK_OPTION) {
        String newName = nameField.getText();
        String newNumOfHoursText = hoursField.getText();
        if (newName.isEmpty() || newNumOfHoursText.isEmpty()) {
          JOptionPane.showMessageDialog(this, "All fields are required.");
          return;
        }
        try {
          int newNumOfHours = Integer.parseInt(newNumOfHoursText);
          entityManager.getTransaction().begin();
          eventType.setEventTypeName(newName);
          eventType.setEventNumOfHours(newNumOfHours);
          entityManager.getTransaction().commit();
          loadEventTypeData();
        } catch (NumberFormatException e) {
          JOptionPane.showMessageDialog(this, "Please enter a valid number of hours.");
        }
      }
    } else {
      JOptionPane.showMessageDialog(this, "Please select an event type to edit.");
    }
  }

  private void deleteEventType() {
    int selectedRow = eventTypeTable.getSelectedRow();
    if (selectedRow != -1) {
      int eventTypeId = (int) eventTypeTableModel.getValueAt(selectedRow, 0);
      int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this event type?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
      if (option == JOptionPane.YES_OPTION) {
        entityManager.getTransaction().begin();
        EventType eventType = entityManager.find(EventType.class, eventTypeId);
        entityManager.remove(eventType);
        entityManager.getTransaction().commit();
        loadEventTypeData();
      }
    } else {
      JOptionPane.showMessageDialog(this, "Please select an event type to delete.");
    }
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new EventTypeApp().setVisible(true));
  }
}
