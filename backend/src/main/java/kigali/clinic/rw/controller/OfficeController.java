package kigali.clinic.rw.controller;

import kigali.clinic.rw.domain.Office;
import kigali.clinic.rw.service.OfficeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/offices")
public class OfficeController {

    @Autowired
    private OfficeService officeService;

    @PostMapping
    public ResponseEntity<Office> createOffice(@RequestBody Office office) {
        return new ResponseEntity<>(officeService.save(office), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Office>> getAllOffices() {
        return new ResponseEntity<>(officeService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Office> getOfficeById(@PathVariable UUID id) {
        Office office = officeService.findById(id);
        if (office != null) {
            return new ResponseEntity<>(office, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Office> updateOffice(@PathVariable UUID id, @RequestBody Office officeDetails) {
        Office existingOffice = officeService.findById(id);
        if (existingOffice == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        existingOffice.setName(officeDetails.getName());
        existingOffice.setOfficeNumber(officeDetails.getOfficeNumber());
        return new ResponseEntity<>(officeService.save(existingOffice), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOffice(@PathVariable UUID id) {
        officeService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/free")
    public ResponseEntity<List<Office>> getFreeOffices() {
        return new ResponseEntity<>(officeService.getFreeOffices(), HttpStatus.OK);
    }

    @GetMapping("/busiest")
    public ResponseEntity<?> getBusiestOffice() {
        Object[] result = officeService.getBusiestOffice();
        if (result == null) {
            return new ResponseEntity<>("No appointments yet", HttpStatus.OK);
        }
        Map<String, Object> response = Map.of(
            "officeName", result[0],
            "officeNumber", result[1],
            "appointmentCount", result[2]
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}