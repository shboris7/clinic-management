package kigali.clinic.rw.service;

import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.domain.Patient;
import kigali.clinic.rw.repository.AppointmentRepository;
import kigali.clinic.rw.repository.DoctorRepository;
import kigali.clinic.rw.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    public Patient save(Patient patient) {
        return patientRepository.save(patient);
    }

    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    public Patient findById(UUID id) {
        return patientRepository.findById(id).orElse(null);
    }

    public Patient update(UUID id, Patient patientDetails) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));
        patient.setFirstName(patientDetails.getFirstName());
        patient.setLastName(patientDetails.getLastName());
        patient.setDateOfBirth(patientDetails.getDateOfBirth());
        patient.setSsNumber(patientDetails.getSsNumber());
        return patientRepository.save(patient);
    }

    public void delete(UUID id) {
        patientRepository.deleteById(id);
    }

    public List<Patient> getPatientsByLastName(String lastName) {
        return patientRepository.findByLastNameIgnoreCaseOrderByFirstNameAsc(lastName);
    }

    public Object getPatientsOfDoctor(UUID doctorId) {
        if (!doctorRepository.existsById(doctorId)) {
            return "The doctor with that id does not exist";
        }
        return patientRepository.findPatientsByDoctorId(doctorId);
    }

    public Object getUpcomingAppointmentsBySsn(String ssNumber) {
        if (!patientRepository.existsBySsNumber(ssNumber)) {
            return "That patient with that ssnumber does not exist";
        }
        List<AppointmentStatus> statuses = Arrays.asList(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED);
        return appointmentRepository.findByPatientSsNumberAndAppointmentDateGreaterThanEqualAndStatusInOrderByAppointmentDateAsc(ssNumber, LocalDate.now(), statuses);
    }
}