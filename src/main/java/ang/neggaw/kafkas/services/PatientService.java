package ang.neggaw.kafkas.services;

import ang.neggaw.kafkas.entities.Patient;
import ang.neggaw.kafkas.repositories.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * author by: ANG
 * since: 29/08/2021 15:10
 */

@Log4j2
@RequiredArgsConstructor
@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private long idPatientSupplier = 0;

    @Bean
    public Consumer<Patient> patientConsumer() {

        return (patient) -> {
//            patientRepository.save(patient);
            System.out.println("******************************");
            System.out.println(patient);
            System.out.println("******************************");
        };
    }

    @Bean
    public Supplier<Patient> patientSupplier() {
        return () -> Patient
                .builder()
                .idPatient(idPatientSupplier++)
                .name("Patient-" + idPatientSupplier)
                .appointment(newRandom(1, 31) + "/" + newRandom(1, 12) + "/2022 " + newRandom(8, 11) + ":" + newRandom(0, 59))
                .appointmentDuration((7 + new Random().nextInt(59)) + " min")
                .doctorName("Dr-" + (7 + new Random().nextInt(17)))
                .build();
    }

    private int newRandom(int first, int end) {
        return first + new Random().nextInt(end);
    }

    @Bean
    public Function<Patient, Patient> patientPatientFunction() {
        return patient -> {
            int month = Integer.parseInt(patient.getAppointment().split("/")[1]);
            if (month <= 6) {
                patient.setAppointmentDuration("Unknown");
                patientRepository.save(patient);
                return patient;
            }
            return null;
        };
    }

    @Bean
    public Function<KStream<String, Patient>, KStream<String, Long>> kStreamFunction() {
        return patient -> patient
                .filter((s, p) -> Integer.parseInt(p.getDoctorName().split("-")[1]) > 6)
                .map((s, p) -> new KeyValue<>(p.getDoctorName(), 0L))
                .groupBy((data, idPatient) -> data, Grouped.with(Serdes.String(), Serdes.Long()))
                .windowedBy(TimeWindows.of(5000))
                .count(Materialized.as("patientCount"))
                .toStream()
                .map((k, v) -> new KeyValue<>(k.window().startTime() + " :: " + k.window().endTime() + " ::: " + k.key() + " => ", v));
    }

}
