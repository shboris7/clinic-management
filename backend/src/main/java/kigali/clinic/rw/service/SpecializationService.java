package kigali.clinic.rw.service;

import kigali.clinic.rw.domain.Specialization;
import kigali.clinic.rw.repository.SpecializationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SpecializationService {

    @Autowired
    private SpecializationRepository specRepository;

    public Specialization save(Specialization spec) { return specRepository.save(spec); }
    public List<Specialization> findAll() { return specRepository.findAll(); }
    public Specialization findById(UUID id) { return specRepository.findById(id).orElse(null); }
    public void delete(UUID id) { specRepository.deleteById(id); }

    public List<Specialization> getUnusedSpecializations() {
        return specRepository.findUnusedSpecializations();
    }
}