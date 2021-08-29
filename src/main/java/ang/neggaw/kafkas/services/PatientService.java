package ang.neggaw.kafkas.services;

import ang.neggaw.kafkas.entities.Patient;
import ang.neggaw.kafkas.repositories.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

/**
 * author by: ANG
 * since: 29/08/2021 15:10
 */

@Log4j2
@RequiredArgsConstructor
@Service
public class PatientService {

    private final PatientRepository patientRepository;

    @Bean
    public Consumer<Patient> patientConsumer() {

        return (patient) -> {
            patientRepository.save(patient);
            System.out.println("******************************");
            System.out.println(patient);
            System.out.println("******************************");
        };
    }
}
