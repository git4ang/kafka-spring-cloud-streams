package ang.neggaw.kafkas.restControllers;

import ang.neggaw.kafkas.entities.Patient;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

/**
 * author by: ANG
 * since: 29/08/2021 15:10
 */

@Log4j2
@RequiredArgsConstructor
@RequestMapping(value = "/api/patients")
@RestController
public class PatientRestController {

    private final StreamBridge streamBridge;

    @GetMapping(value = "/publish/{topic}/{name}")
    public Patient publish(@PathVariable(value = "topic") String topic,
                           @PathVariable(value = "name") String name) {

        Patient patient = Patient
                .builder()
                .idPatient((long) (7 + new Random().nextInt(17)))
                .name(name)
                .appointment(newRandom(1, 31) + "/" + newRandom(1, 11) + "/2022 " + newRandom(8, 11) + ":" + newRandom(0, 59))
                .appointmentDuration((7 + new Random().nextInt(59)) + " min")
                .doctorName("Dr-" + (11 + new Random().nextInt(99)))
                .build();

        streamBridge.send(topic, patient);
        return patient;
    }

    private int newRandom(int first, int end) {
        return first + new Random().nextInt(end);
    }
}
