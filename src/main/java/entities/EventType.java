package entities;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "event_type", schema = "public")
@Getter
@Setter
public class EventType {
  @Id
//  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "event_type_id")
  private int eventTypeId;

  @Column(name = "event_type_name")
  private String eventTypeName;

  @Column(name = "event_num_of_hours")
  private int eventNumOfHours;

  @OneToMany(mappedBy = "eventType", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<DisciplineAndEventType> disciplineAndEventTypes;

  @Override
  public String toString() {
    return eventTypeName;
  }

}
