package tables;

import entities.*;

import javax.persistence.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DisciplineAndEventTypeApp extends JFrame {
  private EntityManagerFactory entityManagerFactory;
  private EntityManager entityManager;
  private JTable disciplineAndEventTypeTable;
  private DefaultTableModel disciplineAndEventTypeTableModel;

  public DisciplineAndEventTypeApp() {
    initializeDatabase();
    initComponents();
    loadDisciplineAndEventTypeData();
  }

  private void initializeDatabase() {
    entityManagerFactory = Persistence.createEntityManagerFactory("FacultyPU");
    entityManager = entityManagerFactory.createEntityManager();
  }

  private void initComponents() {
    setTitle("Discipline and Event Type Management");
    setSize(800, 400);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    JPanel panel = new JPanel(new BorderLayout());
    getContentPane().add(panel);

    // Table
    disciplineAndEventTypeTableModel = new DefaultTableModel();
    disciplineAndEventTypeTable = new JTable(disciplineAndEventTypeTableModel);
    JScrollPane scrollPane = new JScrollPane(disciplineAndEventTypeTable);
    panel.add(scrollPane, BorderLayout.CENTER);

    // Buttons
    JPanel buttonPanel = new JPanel(new FlowLayout());
    panel.add(buttonPanel, BorderLayout.SOUTH);

    JButton addButton = new JButton("Add");
    addButton.addActionListener(e -> addDisciplineAndEventType());
    buttonPanel.add(addButton);

    JButton editButton = new JButton("Edit");
    editButton.addActionListener(e -> editDisciplineAndEventType());
    buttonPanel.add(editButton);

    JButton deleteButton = new JButton("Delete");
    deleteButton.addActionListener(e -> deleteDisciplineAndEventType());
    buttonPanel.add(deleteButton);
  }

  private void loadDisciplineAndEventTypeData() {
    disciplineAndEventTypeTableModel.setRowCount(0);

    List<DisciplineAndEventType> disciplineAndEventTypes = entityManager.createQuery("SELECT d FROM DisciplineAndEventType d", DisciplineAndEventType.class).getResultList();
    disciplineAndEventTypeTableModel.setColumnIdentifiers(new String[]{"ID", "Discipline", "Event Type", "Teacher"});
    for (DisciplineAndEventType disciplineAndEventType : disciplineAndEventTypes) {
      disciplineAndEventTypeTableModel.addRow(new Object[]{disciplineAndEventType.getId(), disciplineAndEventType.getDiscipline().getDisciplineName(), disciplineAndEventType.getEventType().getEventTypeName(), disciplineAndEventType.getTeacher().getTeacherName() + " " + disciplineAndEventType.getTeacher().getTeacherLastname()});
    }
  }


  private void addDisciplineAndEventType() {
    JComboBox<Discipline> disciplineComboBox = new JComboBox<>();
    JComboBox<EventType> eventTypeComboBox = new JComboBox<>();
    JComboBox<Teacher> teacherComboBox = new JComboBox<>();

    List<Discipline> disciplines = entityManager.createQuery("SELECT d FROM Discipline d", Discipline.class).getResultList();
    List<EventType> eventTypes = entityManager.createQuery("SELECT e FROM EventType e", EventType.class).getResultList();
    List<Teacher> teachers = entityManager.createQuery("SELECT t FROM Teacher t", Teacher.class).getResultList();

    for (Discipline discipline : disciplines) {
      disciplineComboBox.addItem(discipline);
    }
    for (EventType eventType : eventTypes) {
      eventTypeComboBox.addItem(eventType);
    }
    for (Teacher teacher : teachers) {
      teacherComboBox.addItem(teacher);
    }

    JPanel panel = new JPanel(new GridLayout(4, 2));
    panel.add(new JLabel("Discipline:"));
    panel.add(disciplineComboBox);
    panel.add(new JLabel("Event Type:"));
    panel.add(eventTypeComboBox);
    panel.add(new JLabel("Teacher:"));
    panel.add(teacherComboBox);

    int result = JOptionPane.showConfirmDialog(null, panel, "Add Discipline and Event Type", JOptionPane.OK_CANCEL_OPTION);
    if (result == JOptionPane.OK_OPTION) {
      Discipline selectedDiscipline = (Discipline) disciplineComboBox.getSelectedItem();
      EventType selectedEventType = (EventType) eventTypeComboBox.getSelectedItem();
      Teacher selectedTeacher = (Teacher) teacherComboBox.getSelectedItem();

      DisciplineAndEventType disciplineAndEventType = new DisciplineAndEventType();
      disciplineAndEventType.setDiscipline(selectedDiscipline);
      disciplineAndEventType.setEventType(selectedEventType);
      disciplineAndEventType.setTeacher(selectedTeacher);

      entityManager.getTransaction().begin();
      entityManager.persist(disciplineAndEventType);
      entityManager.getTransaction().commit();

      loadDisciplineAndEventTypeData();
    }
  }

  private void editDisciplineAndEventType() {
    int selectedRow = disciplineAndEventTypeTable.getSelectedRow();
    if (selectedRow != -1) {
      DisciplineAndEventType disciplineAndEventType = entityManager.find(DisciplineAndEventType.class, (int) disciplineAndEventTypeTableModel.getValueAt(selectedRow, 0));
      if (disciplineAndEventType != null) {
        JComboBox<Discipline> disciplineComboBox = new JComboBox<>();
        JComboBox<EventType> eventTypeComboBox = new JComboBox<>();
        JComboBox<Teacher> teacherComboBox = new JComboBox<>();

        List<Discipline> disciplines = entityManager.createQuery("SELECT d FROM Discipline d", Discipline.class).getResultList();
        List<EventType> eventTypes = entityManager.createQuery("SELECT e FROM EventType e", EventType.class).getResultList();
        List<Teacher> teachers = entityManager.createQuery("SELECT t FROM Teacher t", Teacher.class).getResultList();

        for (Discipline discipline : disciplines) {
          disciplineComboBox.addItem(discipline);
        }
        for (EventType eventType : eventTypes) {
          eventTypeComboBox.addItem(eventType);
        }
        for (Teacher teacher : teachers) {
          teacherComboBox.addItem(teacher);
        }

        disciplineComboBox.setSelectedItem(disciplineAndEventType.getDiscipline());
        eventTypeComboBox.setSelectedItem(disciplineAndEventType.getEventType());
        teacherComboBox.setSelectedItem(disciplineAndEventType.getTeacher());

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("Discipline:"));
        panel.add(disciplineComboBox);
        panel.add(new JLabel("Event Type:"));
        panel.add(eventTypeComboBox);
        panel.add(new JLabel("Teacher:"));
        panel.add(teacherComboBox);

        int result = JOptionPane.showConfirmDialog(null, panel, "Edit Discipline and Event Type", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
          Discipline selectedDiscipline = (Discipline) disciplineComboBox.getSelectedItem();
          EventType selectedEventType = (EventType) eventTypeComboBox.getSelectedItem();
          Teacher selectedTeacher = (Teacher) teacherComboBox.getSelectedItem();

          entityManager.getTransaction().begin();
          disciplineAndEventType.setDiscipline(selectedDiscipline);
          disciplineAndEventType.setEventType(selectedEventType);
          disciplineAndEventType.setTeacher(selectedTeacher);
          entityManager.getTransaction().commit();

          loadDisciplineAndEventTypeData();
        }
      }
    } else {
      JOptionPane.showMessageDialog(this, "Please select a discipline and event type to edit.");
    }
  }

  private void deleteDisciplineAndEventType() {
    int selectedRow = disciplineAndEventTypeTable.getSelectedRow();
    if (selectedRow != -1) {
      int id = (int) disciplineAndEventTypeTableModel.getValueAt(selectedRow, 0);
      DisciplineAndEventType disciplineAndEventType = entityManager.find(DisciplineAndEventType.class, id);
      if (disciplineAndEventType != null) {
        entityManager.getTransaction().begin();
        entityManager.remove(disciplineAndEventType);
        entityManager.getTransaction().commit();
        loadDisciplineAndEventTypeData();
      }
    } else {
      JOptionPane.showMessageDialog(this, "Please select a discipline and event type to delete.");
    }
  }


  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new DisciplineAndEventTypeApp().setVisible(true));
  }
}
