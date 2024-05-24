package entities;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "control_form", schema = "public")
@Getter
@Setter
public class ControlForm {
  @Id
//  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "control_form_id")
  private int controlFormId;

  @Column(name = "control_form_name")
  private String controlFormName;
}
