package entities;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "faculty", schema = "public")
@Getter
@Setter
public class Faculty {
  @Id
//  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "faculty_id")
  private int facultyId;

  @Column(name = "faculty_name")
  private String facultyName;

  @Override
  public String toString() {
    return facultyName;
  }

}