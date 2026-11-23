package kigali.clinic.rw.controller;

import kigali.clinic.rw.domain.Specialization;
import kigali.clinic.rw.service.SpecializationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/specializations")
public class SpecializationController {

    @Autowired
    private SpecializationService specService;

    @PostMapping
    public Specialization create(@RequestBody Specialization spec) {
        return specService.save(spec);
    }

    @GetMapping
    public List<Specialization> getAll() {
        return specService.findAll();
    }

    @GetMapping("/{id}")
    public Specialization getById(@PathVariable UUID id) {
        return specService.findById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        specService.delete(id);
    }

    // B3
    @GetMapping("/unused")
    public ResponseEntity<List<Specialization>> getUnused() {
        return ResponseEntity.ok(specService.getUnusedSpecializations());
    }
}