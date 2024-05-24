package entities;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "category", schema = "public")
@Getter
@Setter
public class Category {
  @Id
//  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "category_id")
  private int categoryId;

  @Column(name = "category_name")
  private String categoryName;
}
