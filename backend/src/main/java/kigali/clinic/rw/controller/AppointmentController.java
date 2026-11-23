package kigali.clinic.rw.controller;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService apptService;

    @PostMapping("/save")
    public ResponseEntity<?> create(@RequestBody Appointment appt) {
        Object result = apptService.save(appt);
        if (result instanceof String) {
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Appointment>> getAll() {
        return ResponseEntity.ok(apptService.findAll());
    }

    @GetMapping("/stats/by-status")
    public ResponseEntity<List<Object[]>> getStatsByStatus() {
        return ResponseEntity.ok(apptService.getAppointmentsStatsByStatus());
    }

    @GetMapping("/by-status")
    public ResponseEntity<List<Appointment>> getByStatus(@RequestParam AppointmentStatus status) {
        return ResponseEntity.ok(apptService.getAppointmentsByStatus(status));
    }

    @GetMapping("/between")
    public ResponseEntity<List<Appointment>> getBetween(@RequestParam String start, @RequestParam String end) {
        return ResponseEntity.ok(apptService.getAppointmentsBetween(LocalDate.parse(start), LocalDate.parse(end)));
    }

    @PatchMapping("/cancel-day")
    public ResponseEntity<String> cancelAppointmentsOnDay(
            @RequestParam UUID doctorId,
            @RequestParam String date) {
        int count = apptService.cancelAppointmentsOnDate(doctorId, LocalDate.parse(date));
        return ResponseEntity.ok(count + " appointments cancelled");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Appointment> getById(@PathVariable UUID id) {
        Appointment appt = apptService.findById(id);
        return appt != null ? ResponseEntity.ok(appt) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Appointment> update(@PathVariable UUID id, @RequestBody Appointment details) {
        Appointment result = apptService.update(id, details);
        return result != null ? ResponseEntity.ok(result) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        String msg = apptService.delete(id);
        if (msg.contains("does not exist")) {
            return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        }
        if (msg.contains("has prescriptions")) {
            return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(msg, HttpStatus.OK);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<String> updateStatus(@PathVariable UUID id, @RequestParam AppointmentStatus status) {
        String msg = apptService.updateStatus(id, status);
        if (msg.contains("does not exist")) {
            return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        }
        if (msg.contains("Cannot change")) {
            return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(msg, HttpStatus.OK);
    }

    @PatchMapping("/{id}/reschedule")
    public ResponseEntity<String> reschedule(@PathVariable UUID id, @RequestParam LocalDate newDate) {
        String msg = apptService.reschedule(id, newDate);
        if (msg.contains("does not exist")) {
            return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        }
        if (msg.contains("cannot be rescheduled") || msg.contains("in the past")) {
            return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
        }
        if (msg.contains("already booked")) {
            return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(msg, HttpStatus.OK);
    }

    @GetMapping("/doctor/{doctorId}/agenda")
    public ResponseEntity<?> getDoctorAgenda(@PathVariable UUID doctorId, @RequestParam LocalDate date) {
        Object result = apptService.getDoctorAgenda(doctorId, date);
        if (result instanceof String) {
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/patient/{ssNumber}")
    public ResponseEntity<List<Appointment>> getPatientAppointmentsBySsn(@PathVariable String ssNumber) {
        return new ResponseEntity<>(apptService.getPatientAppointmentsBySsn(ssNumber), HttpStatus.OK);
    }
}