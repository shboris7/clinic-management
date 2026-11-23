package kigali.clinic.rw.service;

import kigali.clinic.rw.domain.Office;
import kigali.clinic.rw.repository.OfficeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OfficeService {

    @Autowired
    private OfficeRepository offRepo;

    public Office save(Office office) {
        return offRepo.save(office);
    }

    public List<Office> findAll() {
        return offRepo.findAll();
    }

    public Office findById(UUID id) {
        return offRepo.findById(id).orElse(null);
    }

    public void delete(UUID id) {
        offRepo.deleteById(id);
    }

    public List<Office> getFreeOffices() {
        return offRepo.findFreeOffices();
    }

    public Object[] getBusiestOffice() {
        List<Object[]> result = offRepo.findBusiestOffice();
        return result.isEmpty() ? null : result.get(0);
    }
}