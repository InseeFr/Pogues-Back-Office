package fr.insee.pogues.controller;

import fr.insee.pogues.domain.entity.db.MappingCodesListRegistreDB;
import fr.insee.pogues.service.MappingCodesListRegistreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/mapping-codeslist")
@RequiredArgsConstructor
public class MappingCodesListRegistreController {

    private final MappingCodesListRegistreService service;

    @PostMapping
    public ResponseEntity<MappingCodesListRegistreDB> create(
            @RequestBody Map<String, String> body) {

        String poguesCodesListId = body.get("poguesCodesListId");
        UUID registreCodesListId = UUID.fromString(body.get("registreCodesListId"));

        MappingCodesListRegistreDB mapping =
                service.create(poguesCodesListId, registreCodesListId);

        return ResponseEntity.ok(mapping);
    }

    @GetMapping
    public ResponseEntity<List<MappingCodesListRegistreDB>> getAll() {

        return ResponseEntity.ok(service.getAll());
    }
}