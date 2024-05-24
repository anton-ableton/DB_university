package entities;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "dissertation", schema = "public")
@Getter
@Setter
public class Dissertation {
  @Id
//  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "dissertation_id")
  private int dissertationId;

  @Column(name = "dissertation_title")
  private String dissertationTitle;

  @Column(name = "dissertation_type")
  private String dissertationType;

  @ManyToOne
  @JoinColumn(name = "teacher_id", referencedColumnName = "teacher_id")
  private Teacher teacher;
}
