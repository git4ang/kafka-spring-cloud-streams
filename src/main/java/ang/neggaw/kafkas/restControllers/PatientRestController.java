package ang.neggaw.kafkas.restControllers;

import ang.neggaw.kafkas.entities.Patient;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
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
    private final InteractiveQueryService interactiveQueryService;

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

    @GetMapping(value = "/publish/analytics", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Map<String, Long>> patientAnalytics() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(sequence -> {
                    Map<String, Long> stringLongMap = new HashMap<>();
                    ReadOnlyWindowStore<String, Long> windowStore = interactiveQueryService.getQueryableStore("patientCount", QueryableStoreTypes.windowStore());
                    Instant now = Instant.now();
                    Instant from = now.minusMillis(5000);
                    KeyValueIterator<Windowed<String>, Long> keyValueIterator = windowStore.fetchAll(from, now);
//                    WindowStoreIterator<Long> windowStoreIterator = windowStore.fetch("Dr-11", Instant.now().minusMillis(5000), Instant.now());

//                    while (keyValueIterator.hasNext()) {
//                        KeyValue<Windowed<String>, Long> next = keyValueIterator.next();
//                        stringLongMap.put(next.key.key(), next.value);
//                    }

                    keyValueIterator.forEachRemaining(keyValue -> stringLongMap.put(keyValue.key.key(), keyValue.value));
                    return stringLongMap;
                }).share();
    }
}
