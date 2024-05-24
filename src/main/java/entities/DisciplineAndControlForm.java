package entities;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "discipline_and_control_form", schema = "public")
@Getter
@Setter
public class DisciplineAndControlForm {
  @Id
//  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private int id;

  @ManyToOne
  @JoinColumn(name = "discipline_id", referencedColumnName = "discipline_id")
  private Discipline discipline;

  @ManyToOne
  @JoinColumn(name = "control_form_id", referencedColumnName = "control_form_id")
  private ControlForm controlForm;
}
