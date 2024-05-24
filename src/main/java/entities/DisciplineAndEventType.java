package entities;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "discipline_and_event_type", schema = "public")
@Getter
@Setter
public class DisciplineAndEventType {
  @Id
//  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private int id;

  @ManyToOne
  @JoinColumn(name = "discipline", referencedColumnName = "discipline_id")
  private Discipline discipline;

  @ManyToOne
  @JoinColumn(name = "event_type", referencedColumnName = "event_type_id")
  private EventType eventType;

  @ManyToOne
  @JoinColumn(name = "teacher_id", referencedColumnName = "teacher_id")
  private Teacher teacher;

}
