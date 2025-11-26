package main.java.com.textquest.api.controller;

import main.java.com.textquest.api.entity.SoundEffect;
import main.java.com.textquest.api.entity.SoundEffect.TriggerType;
import main.java.com.textquest.api.service.SoundEffectService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sound-effects")
public class SoundEffectController {

    private final SoundEffectService service;

    public SoundEffectController(SoundEffectService service) {
        this.service = service;
    }

    @GetMapping
    public List<SoundEffect> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SoundEffect> getById(@PathVariable Long id) {
        return service.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/trigger/{type}")
    public List<SoundEffect> getByTrigger(@PathVariable TriggerType type) {
        return service.getByTrigger(type);
    }

    @GetMapping("/item/{itemId}")
    public List<SoundEffect> getByItem(@PathVariable Long itemId) {
        return service.getByItem(itemId);
    }

    @PostMapping
    public SoundEffect create(@RequestBody SoundEffect soundEffect) {
        return service.save(soundEffect);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SoundEffect> update(
            @PathVariable Long id,
            @RequestBody SoundEffect soundEffect
    ) {
        return service.findById(id)
                .map(existing -> {
                    soundEffect.setId(existing.getId());
                    return ResponseEntity.ok(service.save(soundEffect));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}
