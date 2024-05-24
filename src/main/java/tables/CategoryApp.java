package tables;

import entities.Category;

import javax.persistence.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CategoryApp extends JFrame {

  private EntityManagerFactory entityManagerFactory;
  private EntityManager entityManager;

  private JTable categoryTable;
  private DefaultTableModel categoryTableModel;

  public CategoryApp() {
    initializeDatabase();
    initComponents();
    loadCategoryData();
  }

  private void initializeDatabase() {
    entityManagerFactory = Persistence.createEntityManagerFactory("FacultyPU");
    entityManager = entityManagerFactory.createEntityManager();
  }

  private void initComponents() {
    setTitle("Category Management");
    setSize(400, 300);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    JPanel panel = new JPanel(new BorderLayout());
    getContentPane().add(panel);

    // Table
    categoryTableModel = new DefaultTableModel();
    categoryTable = new JTable(categoryTableModel);
    JScrollPane scrollPane = new JScrollPane(categoryTable);
    panel.add(scrollPane, BorderLayout.CENTER);

    // Buttons
    JPanel buttonPanel = new JPanel(new FlowLayout());
    panel.add(buttonPanel, BorderLayout.SOUTH);

    JButton addButton = new JButton("Add");
    addButton.addActionListener(e -> addCategory());
    buttonPanel.add(addButton);

    JButton editButton = new JButton("Edit");
    editButton.addActionListener(e -> editCategory());
    buttonPanel.add(editButton);

    JButton deleteButton = new JButton("Delete");
    deleteButton.addActionListener(e -> deleteCategory());
    buttonPanel.add(deleteButton);
  }

  private void loadCategoryData() {
    categoryTableModel.setRowCount(0);

    List<Category> categories = entityManager.createQuery("SELECT c FROM Category c", Category.class).getResultList();
    categoryTableModel.setColumnIdentifiers(new String[]{"ID", "Category"});
    for (Category category : categories) {
      categoryTableModel.addRow(new Object[]{category.getCategoryId(), category.getCategoryName()});
    }
  }

  private void addCategory() {
    int categoryId = JsonUtils.getField("category_id");
    String categoryName = JOptionPane.showInputDialog(this, "Enter category name:");
    if (categoryName != null && !categoryName.isEmpty()) {
      JsonUtils.incrementField("category_id");
      entityManager.getTransaction().begin();

      Category newCategory = new Category();
      newCategory.setCategoryId(categoryId);
      newCategory.setCategoryName(categoryName);

      entityManager.persist(newCategory);

      entityManager.getTransaction().commit();

      loadCategoryData();
    }
  }

  private void editCategory() {
    int selectedRow = categoryTable.getSelectedRow();
    if (selectedRow != -1) {
      int categoryId = (int) categoryTableModel.getValueAt(selectedRow, 0);
      String newName = JOptionPane.showInputDialog(this, "Enter new name:");
      if (newName != null && !newName.isEmpty()) {
        entityManager.getTransaction().begin();
        Category category = entityManager.find(Category.class, categoryId);
        category.setCategoryName(newName);
        entityManager.getTransaction().commit();
        loadCategoryData();
      }
    } else {
      JOptionPane.showMessageDialog(this, "Please select a category to edit.");
    }
  }

//  private void deleteCategory() {
//    int selectedRow = categoryTable.getSelectedRow();
//    if (selectedRow != -1) {
//      int categoryId = (int) categoryTableModel.getValueAt(selectedRow, 0);
//      if (hasAssociatedEntities("Teacher", "category", categoryId)) {
//        JOptionPane.showMessageDialog(this, "Cannot delete category with associated teachers.");
//        return;
//      }
//      int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this category?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
//      if (option == JOptionPane.YES_OPTION) {
//        entityManager.getTransaction().begin();
//        Category category = entityManager.find(Category.class, categoryId);
//        entityManager.remove(category);
//        entityManager.getTransaction().commit();
//        loadCategoryData();
//      }
//    } else {
//      JOptionPane.showMessageDialog(this, "Please select a category to delete.");
//    }
//  }

  private void deleteCategory() {
    int selectedRow = categoryTable.getSelectedRow();
    if (selectedRow != -1) {
      int categoryId = (int) categoryTableModel.getValueAt(selectedRow, 0);
      int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this category?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
      if (option == JOptionPane.YES_OPTION) {
        if (hasAssociatedEntities("Teacher", "category.categoryId", categoryId)) {
          JOptionPane.showMessageDialog(this, "Cannot delete category. There are teachers associated with this category.");
          return;
        }

        entityManager.getTransaction().begin();
        Category category = entityManager.find(Category.class, categoryId);
        entityManager.remove(category);
        entityManager.getTransaction().commit();
        loadCategoryData();
      }
    } else {
      JOptionPane.showMessageDialog(this, "Please select a category to delete.");
    }
  }

  private boolean hasAssociatedEntities(String entityName, String fieldName, int id) {
    String query = String.format("SELECT COUNT(e) FROM %s e WHERE e.%s = :categoryId", entityName, fieldName);
    Long count = entityManager.createQuery(query, Long.class)
            .setParameter("categoryId", id)
            .getSingleResult();
    return count > 0;
  }


  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new CategoryApp().setVisible(true));
  }
}
