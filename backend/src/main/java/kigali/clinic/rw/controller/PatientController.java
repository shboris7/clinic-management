package kigali.clinic.rw.controller;

import kigali.clinic.rw.domain.Patient;
import kigali.clinic.rw.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @PostMapping
    public Patient create(@RequestBody Patient patient) {
        return patientService.save(patient);
    }

    @GetMapping
    public List<Patient> getAll() {
        return patientService.findAll();
    }

    @GetMapping("/{id}")
    public Patient getById(@PathVariable UUID id) {
        return patientService.findById(id);
    }

    @PutMapping("/{id}")
    public Patient update(@PathVariable UUID id, @RequestBody Patient patient) {
        return patientService.update(id, patient);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        patientService.delete(id);
    }

    @GetMapping("/by-last-name")
    public ResponseEntity<List<Patient>> getByLastName(@RequestParam String lastName) {
        return ResponseEntity.ok(patientService.getPatientsByLastName(lastName));
    }

    @GetMapping("/of-doctor/{doctorId}")
    public ResponseEntity<?> getPatientsOfDoctor(@PathVariable UUID doctorId) {
        Object result = patientService.getPatientsOfDoctor(doctorId);
        if (result instanceof String) {
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{ssNumber}/upcoming")
    public ResponseEntity<?> getUpcomingAppointments(@PathVariable String ssNumber) {
        Object result = patientService.getUpcomingAppointmentsBySsn(ssNumber);
        if (result instanceof String) {
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(result);
    }
}