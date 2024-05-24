package entities;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "exam", schema = "public")
@Getter
@Setter
public class Exam {
  @Id
//  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "exam_id")
  private int examId;

  @ManyToOne
  @JoinColumn(name = "discipline_id", referencedColumnName = "discipline_id")
  private Discipline discipline;

  @ManyToOne
  @JoinColumn(name = "student_id", referencedColumnName = "student_id")
  private Student student;

  @ManyToOne
  @JoinColumn(name = "teacher_id", referencedColumnName = "teacher_id")
  private Teacher teacher;

  @Column(name = "exam_grade")
  private String examGrade;
}
