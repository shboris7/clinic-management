package kigali.clinic.rw.service;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.repository.AppointmentRepository;
import kigali.clinic.rw.repository.DoctorRepository;
import kigali.clinic.rw.repository.PrescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository apptRepo;

    @Autowired
    private PrescriptionRepository prescRepo;

    @Autowired
    private DoctorRepository doctorRepo;

    public Object save(Appointment appt) {
        if (appt.getAppointmentDate().isBefore(LocalDate.now())) {
            return "Appointment date cannot be in the past";
        }
        appt.setStatus(AppointmentStatus.SCHEDULED);
        return apptRepo.save(appt);
    }

    public List<Appointment> findAll() {
        return apptRepo.findAll();
    }

    public Appointment findById(UUID id) {
        return apptRepo.findById(id).orElse(null);
    }

    public Appointment update(UUID id, Appointment details) {
        Appointment appt = apptRepo.findById(id).orElse(null);
        if (appt == null) return null;
        appt.setAppointmentDate(details.getAppointmentDate());
        appt.setReason(details.getReason());
        appt.setPatient(details.getPatient());
        appt.setDoctor(details.getDoctor());
        return apptRepo.save(appt);
    }

    public String delete(UUID id) {
        Appointment appt = apptRepo.findById(id).orElse(null);
        if (appt == null) {
            return "The appointment with that id does not exist";
        }
        if (prescRepo.existsByAppointmentId(id)) {
            return "That appointment has prescriptions, delete them first";
        }
        apptRepo.deleteById(id);
        return "Appointment deleted successfully";
    }

    public List<Appointment> getAppointmentsByStatus(AppointmentStatus status) {
        return apptRepo.findByStatusOrderByAppointmentDateAsc(status);
    }

    public List<Appointment> getAppointmentsBetween(LocalDate start, LocalDate end) {
        return apptRepo.findByAppointmentDateBetweenOrderByAppointmentDateAsc(start, end);
    }

    public List<Object[]> getAppointmentsStatsByStatus() {
        return apptRepo.countAppointmentsByStatus();
    }

    @Transactional
    public int cancelAppointmentsOnDate(UUID doctorId, LocalDate date) {
        return apptRepo.cancelAppointmentsForDoctorOnDate(doctorId, date);
    }

    public String updateStatus(UUID id, AppointmentStatus newStatus) {
        Appointment appt = apptRepo.findById(id).orElse(null);
        if (appt == null) {
            return "The appointment with that id does not exist";
        }
        AppointmentStatus current = appt.getStatus();
        boolean valid = false;
        if (current == AppointmentStatus.SCHEDULED && (newStatus == AppointmentStatus.CONFIRMED || newStatus == AppointmentStatus.CANCELLED)) {
            valid = true;
        }
        if (current == AppointmentStatus.CONFIRMED && (newStatus == AppointmentStatus.COMPLETED || newStatus == AppointmentStatus.CANCELLED)) {
            valid = true;
        }
        if (!valid) {
            return "Cannot change status from " + current + " to " + newStatus;
        }
        appt.setStatus(newStatus);
        apptRepo.save(appt);
        return "Appointment status updated successfully";
    }

    public String reschedule(UUID id, LocalDate newDate) {
        Appointment appt = apptRepo.findById(id).orElse(null);
        if (appt == null) {
            return "The appointment with that id does not exist";
        }
        if (appt.getStatus() == AppointmentStatus.COMPLETED || appt.getStatus() == AppointmentStatus.CANCELLED) {
            return "A " + appt.getStatus() + " appointment cannot be rescheduled";
        }
        if (newDate.isBefore(LocalDate.now())) {
            return "Appointment date cannot be in the past";
        }
        if (apptRepo.existsByDoctorIdAndAppointmentDateAndStatusNot(appt.getDoctor().getId(), newDate, AppointmentStatus.CANCELLED)) {
            return "Doctor is already booked on that date";
        }
        appt.setAppointmentDate(newDate);
        appt.setStatus(AppointmentStatus.SCHEDULED);
        apptRepo.save(appt);
        return "Appointment rescheduled successfully";
    }

    public Object getDoctorAgenda(UUID doctorId, LocalDate date) {
        if (!doctorRepo.existsById(doctorId)) {
            return "The doctor with that id does not exist";
        }
        return apptRepo.findByDoctorIdAndAppointmentDateAndStatusNot(doctorId, date, AppointmentStatus.CANCELLED);
    }

    public List<Appointment> getPatientAppointmentsBySsn(String ssNumber) {
        return apptRepo.findByPatientSsNumberOrderByAppointmentDateAsc(ssNumber);
    }
}