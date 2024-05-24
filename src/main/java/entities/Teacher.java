package entities;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "teacher", schema = "public")
@Getter
@Setter
public class Teacher {
  @Id
//  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "teacher_id")
  private int teacherId;

  @Column(name = "teacher_name")
  private String teacherName;

  @Column(name = "teacher_lastname")
  private String teacherLastname;

  @ManyToOne
  @JoinColumn(name = "category_id", referencedColumnName = "category_id")
  private Category category;

  @ManyToOne
  @JoinColumn(name = "department_id", referencedColumnName = "department_id")
  private Department department;

  @Column(name = "teacher_birth_year")
  private int teacherBirthYear;

  @Column(name = "teacher_gender")
  private String teacherGender;

  @Column(name = "teacher_age")
  private int teacherAge;

  @Column(name = "teacher_have_kids")
  private boolean teacherHaveKids;

  @Column(name = "teacher_salary_amount")
  private int teacherSalaryAmount;

  @Column(name = "teacher_is_graduate")
  private boolean teacherIsGraduate;

  @Column(name = "teacher_has_defended_thesis")
  private boolean teacherHasDefendedThesis;

  @Column(name = "teacher_dissertation_year")
  private Integer teacherDissertationYear;

  @Override
  public String toString() {
    return teacherName + " " + teacherLastname;
  }

}

