package entities;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "department", schema = "public")
@Getter
@Setter
public class Department {
  @Id
//  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "department_id")
  private int departmentId;

  @Column(name = "department_name")
  private String departmentName;

  @ManyToOne
  @JoinColumn(name = "faculty_id", referencedColumnName = "faculty_id")
  private Faculty faculty;

  @Override
  public String toString() {
    return departmentName;
  }
}
