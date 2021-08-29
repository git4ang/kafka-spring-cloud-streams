package ang.neggaw.kafkas.entities;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

/**
 * author by: ANG
 * since: 29/08/2021 14:58
 */

@Entity
@Table(name = "patients")
@AllArgsConstructor
@RequiredArgsConstructor
@Setter
@Getter
@Builder
public class Patient implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPatient;

    private String name;

    private String appointment;

    private String appointmentDuration;

    private String doctorName;

    @Override
    public String toString() {
        return "Patient{" +
                "idPatient : " + idPatient +
                ", name : '" + name + '\'' +
                ", appointment : " + appointment +
                ", Duration of appointment : " + appointmentDuration +
                ", doctorName : '" + doctorName + '\'' +
                '}';
    }
}