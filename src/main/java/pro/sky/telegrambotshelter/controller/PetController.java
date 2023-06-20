package pro.sky.telegrambotshelter.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.telegrambotshelter.model.Pet;
import pro.sky.telegrambotshelter.service.PetService;

import java.util.List;

@RestController
@RequestMapping("pet")
public class PetController {
    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @PostMapping
    public ResponseEntity<Pet> createPet(@RequestBody Pet pet) {
        return ResponseEntity.ok(petService.createPet(pet));
    }

    @PutMapping
    public ResponseEntity<Pet> updatePet(@RequestBody Pet pet) {
        return petService.updatePet(pet)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    ResponseEntity<List<Pet>> getAllPets(@RequestParam(required = false) Long shelterId) {
        if (shelterId == null)
            return ResponseEntity.ok(petService.getAllPets());
        return ResponseEntity.ok(petService.getAllByShelterId(shelterId));
    }

    @GetMapping("{id}")
    ResponseEntity<Pet> getPetById(@PathVariable Long id) {
        return petService.getPetById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deletePet(@PathVariable Long id){
        petService.deletePet(id);
    }

}
