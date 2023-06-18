package pro.sky.telegrambotshelter.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.telegrambotshelter.model.Shelter;
import pro.sky.telegrambotshelter.service.ShelterService;

import java.util.List;

@RestController
@RequestMapping("shelter")
public class ShelterController {

    private final ShelterService shelterService;

    public ShelterController(ShelterService shelterService) {
        this.shelterService = shelterService;
    }


    @PostMapping
    public ResponseEntity<Shelter> createShelter(@RequestBody Shelter shelter) {
        return ResponseEntity.ok(shelterService.createShelter(shelter));
    }

    @PutMapping
    public ResponseEntity<Shelter> updateShelter(@RequestBody Shelter shelter) {
        return shelterService.updateShelter(shelter)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Shelter>> getAllShelters(@RequestParam(required = false) String shelterType) {
        if (shelterType == null)
            return ResponseEntity.ok(shelterService.getAllShelters());
        return ResponseEntity.ok(shelterService.getAllByShelterType(shelterType));
    }

    @GetMapping("{id}")
    public ResponseEntity<Shelter> getShelterById(@PathVariable Long id) {
        return shelterService.getShelterById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteShelter(@PathVariable Long id){
        shelterService.deleteShelter(id);
    }


}
