package kigali.clinic.rw.service;

import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.domain.Specialization;
import kigali.clinic.rw.repository.AppointmentRepository;
import kigali.clinic.rw.repository.DoctorRepository;
import kigali.clinic.rw.repository.OfficeRepository;
import kigali.clinic.rw.repository.SpecializationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private SpecializationRepository specializationRepository;

    @Autowired
    private OfficeRepository officeRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    public Doctor save(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    public List<Doctor> findAll() {
        return doctorRepository.findAll();
    }

    public Doctor findById(UUID id) {
        return doctorRepository.findById(id).orElse(null);
    }

    public void delete(UUID id) {
        doctorRepository.deleteById(id);
    }

    public List<Doctor> getDoctorsBySpecialization(String name) {
        return doctorRepository.findDoctorsBySpecializationName(name);
    }

    public List<Doctor> getDoctorsWithoutOffice() {
        return doctorRepository.findDoctorsWithoutOffice();
    }

    public String addSpecialization(UUID doctorId, UUID specializationId) {
        Doctor doctor = doctorRepository.findById(doctorId).orElse(null);
        if (doctor == null) {
            return "The doctor with that id does not exist";
        }
        Specialization specialization = specializationRepository.findById(specializationId).orElse(null);
        if (specialization == null) {
            return "That specialization does not exist";
        }
        if (doctor.getSpecializations().contains(specialization)) {
            return "The doctor already has that specialization";
        }
        doctor.getSpecializations().add(specialization);
        doctorRepository.save(doctor);
        return "Specialization added to the doctor";
    }

    public String removeSpecialization(UUID doctorId, UUID specializationId) {
        Doctor doctor = doctorRepository.findById(doctorId).orElse(null);
        if (doctor == null) {
            return "The doctor with that id does not exist";
        }
        Specialization specialization = specializationRepository.findById(specializationId).orElse(null);
        if (specialization == null) {
            return "That specialization does not exist";
        }
        if (!doctor.getSpecializations().contains(specialization)) {
            return "The doctor does not have that specialization";
        }
        doctor.getSpecializations().remove(specialization);
        doctorRepository.save(doctor);
        return "Specialization removed from the doctor";
    }

    public String moveDoctorToOffice(UUID doctorId, String officeNumber) {
        Doctor doctor = doctorRepository.findById(doctorId).orElse(null);
        if (doctor == null) {
            return "The doctor with that id does not exist";
        }
        var office = officeRepository.findByOfficeNumber(officeNumber).orElse(null);
        if (office == null) {
            return "The office with that office number does not exist";
        }
        if (doctorRepository.existsByOfficeOfficeNumber(officeNumber) && (doctor.getOffice() == null || !doctor.getOffice().getOfficeNumber().equals(officeNumber))) {
            return "That office is taken by other doctor";
        }
        doctor.setOffice(office);
        doctorRepository.save(doctor);
        return "Office changed successfully";
    }

    public Object getDoctorAgenda(UUID doctorId, LocalDate date) {
        if (!doctorRepository.existsById(doctorId)) {
            return "The doctor with that id does not exist";
        }
        return appointmentRepository.findByDoctorIdAndAppointmentDateAndStatusNot(doctorId, date, AppointmentStatus.CANCELLED);
    }

    public List<Doctor> searchDoctorsByName(String name) {
        return doctorRepository.searchByName(name);
    }

    public List<Object[]> getDoctorWorkload(LocalDate start, LocalDate end) {
        return doctorRepository.getDoctorWorkload(start, end);
    }
}