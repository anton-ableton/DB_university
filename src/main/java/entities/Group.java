package entities;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "groupp", schema = "public")
@Getter
@Setter
public class Group {
  @Id
//  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "group_id")
  private int groupId;

  @Column(name = "group_num")
  private int groupNum;

  @ManyToOne
  @JoinColumn(name = "faculty_id", referencedColumnName = "faculty_id")
  private Faculty faculty;
}
