package ang.neggaw.kafkas.repositories;

import ang.neggaw.kafkas.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * author by: ANG
 * since: 29/08/2021 14:58
 */

@RepositoryRestResource
public interface PatientRepository extends JpaRepository<Patient, Long> { }
