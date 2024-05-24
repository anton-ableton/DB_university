package entities;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "discipline", schema = "public")
@Getter
@Setter
public class Discipline {
  @Id
//  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "discipline_id")
  private int disciplineId;

  @Column(name = "discipline_name")
  private String disciplineName;

  @Column(name = "discipline_control_form")
  private String disciplineControlForm;

  @Column(name = "discipline_year")
  private int disciplineYear;

  @Column(name = "discipline_semester")
  private int disciplineSemester;

  @OneToMany(mappedBy = "discipline", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<DisciplineAndEventType> disciplineAndEventTypes;

  @Override
  public String toString() {
    return disciplineName;
  }

}
