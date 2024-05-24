package entities;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "student", schema = "public")
@Getter
@Setter
public class Student {
  @Id
//  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "student_id")
  private int studentId;

  @Column(name = "student_name")
  private String studentName;

  @Column(name = "student_lastname")
  private String studentLastname;

  @ManyToOne
  @JoinColumn(name = "group_id", referencedColumnName = "group_id")
  private Group group;

  @Column(name = "student_course")
  private int studentCourse;

  @Column(name = "student_birth_year")
  private int studentBirthYear;

  @Column(name = "student_gender")
  private String studentGender;

  @Column(name = "student_age")
  private int studentAge;

  @Column(name = "student_have_kids")
  private boolean studentHaveKids;

  @Column(name = "student_have_scolarship")
  private boolean studentHaveScolarship;

  @Column(name = "student_scolarship_amount")
  private int studentScolarshipAmount;

  @Override
  public String toString() {
    return studentName + " " + studentLastname;
  }

}
