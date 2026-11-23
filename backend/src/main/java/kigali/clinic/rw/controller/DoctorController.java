package kigali.clinic.rw.controller;

import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @PostMapping
    public Doctor create(@RequestBody Doctor doctor) {
        return doctorService.save(doctor);
    }

    @GetMapping
    public List<Doctor> getAll() {
        return doctorService.findAll();
    }

    @GetMapping("/{id}")
    public Doctor getById(@PathVariable UUID id) {
        return doctorService.findById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        doctorService.delete(id);
    }

    @GetMapping("/by-specialization")
    public ResponseEntity<List<Doctor>> getBySpecialization(@RequestParam String name) {
        return ResponseEntity.ok(doctorService.getDoctorsBySpecialization(name));
    }

    @GetMapping("/without-office")
    public ResponseEntity<List<Doctor>> getWithoutOffice() {
        return ResponseEntity.ok(doctorService.getDoctorsWithoutOffice());
    }

    @PutMapping("/{doctorId}/specializations/{specializationId}")
    public ResponseEntity<String> addSpecialization(@PathVariable UUID doctorId, @PathVariable UUID specializationId) {
        String msg = doctorService.addSpecialization(doctorId, specializationId);
        if (msg.contains("does not exist")) {
            return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        }
        if (msg.contains("already has")) {
            return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(msg, HttpStatus.OK);
    }

    @DeleteMapping("/{doctorId}/specializations/{specializationId}")
    public ResponseEntity<String> removeSpecialization(@PathVariable UUID doctorId, @PathVariable UUID specializationId) {
        String msg = doctorService.removeSpecialization(doctorId, specializationId);
        if (msg.contains("does not exist")) {
            return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        }
        if (msg.contains("does not have")) {
            return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(msg, HttpStatus.OK);
    }

    @PatchMapping("/{doctorId}/office")
    public ResponseEntity<String> moveDoctorToOffice(@PathVariable UUID doctorId, @RequestParam String officeNumber) {
        String msg = doctorService.moveDoctorToOffice(doctorId, officeNumber);
        if (msg.contains("does not exist")) {
            return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        }
        if (msg.contains("taken by other")) {
            return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(msg, HttpStatus.OK);
    }

    @GetMapping("/{doctorId}/agenda")
    public ResponseEntity<?> getDoctorAgenda(@PathVariable UUID doctorId, @RequestParam LocalDate date) {
        Object result = doctorService.getDoctorAgenda(doctorId, date);
        if (result instanceof String) {
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Doctor>> searchDoctors(@RequestParam String name) {
        return new ResponseEntity<>(doctorService.searchDoctorsByName(name), HttpStatus.OK);
    }

    @GetMapping("/workload")
    public ResponseEntity<List<Object[]>> getDoctorWorkload(@RequestParam LocalDate start, @RequestParam LocalDate end) {
        return ResponseEntity.ok(doctorService.getDoctorWorkload(start, end));
    }
}