package kigali.clinic.rw.repository;

import kigali.clinic.rw.domain.Office;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OfficeRepository extends JpaRepository<Office, UUID> {

    List<Office> findByName(String name);

    Optional<Office> findByOfficeNumber(String officeNumber);

    List<Office> findByOfficeNumberStartsWith(String startNumber);

    List<Office> findByOfficeNumberEndsWith(String endNumber);

    @Query("SELECT o.name, o.officeNumber, COUNT(a) FROM Office o JOIN o.doctors d JOIN d.appointments a GROUP BY o.id ORDER BY COUNT(a) DESC")
    List<Object[]> findBusiestOffice();

    @Query("SELECT o FROM Office o WHERE NOT EXISTS (SELECT d FROM Doctor d WHERE d.office = o) ORDER BY o.officeNumber ASC")
    List<Office> findFreeOffices();

@Query("SELECT COUNT(o) FROM Office o WHERE NOT EXISTS (SELECT d FROM Doctor d WHERE d.office = o)")
long countFreeOffices();
}
