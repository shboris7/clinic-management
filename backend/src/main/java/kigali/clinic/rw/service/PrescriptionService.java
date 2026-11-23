package kigali.clinic.rw.service;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.domain.Prescription;
import kigali.clinic.rw.repository.AppointmentRepository;
import kigali.clinic.rw.repository.PrescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PrescriptionService {

    @Autowired
    private PrescriptionRepository prescRepo;

    @Autowired
    private AppointmentRepository apptRepo;

    public String save(Prescription presc, UUID appointmentId) {
        Appointment appt = apptRepo.findById(appointmentId).orElse(null);
        if (appt == null) {
            return "The appointment with that id does not exist";
        }
        if (appt.getStatus() != AppointmentStatus.COMPLETED) {
            return "Prescriptions are only for COMPLETED appointments";
        }
        presc.setAppointment(appt);
        presc.setIssuedDate(LocalDate.now());
        prescRepo.save(presc);
        return "Prescription saved successfully";
    }

    public Object getByAppointment(UUID appointmentId) {
        if (!apptRepo.existsById(appointmentId)) {
            return "The appointment with that id does not exist";
        }
        return prescRepo.findByAppointmentId(appointmentId);
    }

    public List<Prescription> getByPatient(String ssNumber) {
        return prescRepo.findByPatientSsNumberOrderByIssuedDateDesc(ssNumber);
    }

    public String update(UUID id, Prescription details) {
        Prescription presc = prescRepo.findById(id).orElse(null);
        if (presc == null) {
            return "The prescription with that id does not exist";
        }
        presc.setMedicineName(details.getMedicineName());
        presc.setDosage(details.getDosage());
        presc.setDurationDays(details.getDurationDays());
        prescRepo.save(presc);
        return "Prescription updated successfully";
    }

    public String delete(UUID id) {
        if (!prescRepo.existsById(id)) {
            return "The prescription with that id does not exist";
        }
        prescRepo.deleteById(id);
        return "Prescription deleted successfully";
    }

    public List<Object[]> getTopMedicines() {
        return prescRepo.findTopMedicines();
    }
}