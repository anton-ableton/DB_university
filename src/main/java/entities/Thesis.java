package entities;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "thesis", schema = "public")
@Getter
@Setter
public class Thesis {
  @Id
//  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "thesis_id")
  private int thesisId;

  @Column(name = "title")
  private String title;

  @ManyToOne
  @JoinColumn(name = "student_id", referencedColumnName = "student_id")
  private Student student;

  @ManyToOne
  @JoinColumn(name = "teacher_id", referencedColumnName = "teacher_id")
  private Teacher teacher;
}
