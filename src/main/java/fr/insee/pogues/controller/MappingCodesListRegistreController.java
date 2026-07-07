package fr.insee.pogues.controller;

import fr.insee.pogues.configuration.auth.AuthorityPrivileges;
import fr.insee.pogues.domain.entity.db.MappingCodesListRegistreDB;
import fr.insee.pogues.mapper.MappingCodesListRegistreMapper;
import fr.insee.pogues.model.dto.mapping.codes.list.registre.MappingCodesListRegistreCreateDTO;
import fr.insee.pogues.model.dto.mapping.codes.list.registre.MappingCodesListRegistreResponseDTO;
import fr.insee.pogues.model.dto.mapping.codes.list.registre.MappingCodesListRegistreUpdateDTO;
import fr.insee.pogues.service.registrymapping.MappingCodesListRegistryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mapping-codeslist")
@RequiredArgsConstructor
public class MappingCodesListRegistreController {

    private final MappingCodesListRegistryService service;
    private final MappingCodesListRegistreMapper mapper;

    @PostMapping
    @PreAuthorize(AuthorityPrivileges.HAS_ADMIN_PRIVILEGES)
    public ResponseEntity<MappingCodesListRegistreResponseDTO> create(
            @Valid @RequestBody MappingCodesListRegistreCreateDTO dto) {

        MappingCodesListRegistreDB mapping =
                service.create(
                        dto.poguesCodesListId(),
                        dto.registreCodesListId()
                );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toDto(mapping));
    }

    @GetMapping
    @PreAuthorize(AuthorityPrivileges.HAS_USER_PRIVILEGES)
    public ResponseEntity<List<MappingCodesListRegistreResponseDTO>> getAll() {

        List<MappingCodesListRegistreResponseDTO> response =
                service.getAll()
                        .stream()
                        .map(mapper::toDto)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize(AuthorityPrivileges.HAS_ADMIN_PRIVILEGES)
    public ResponseEntity<MappingCodesListRegistreResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody MappingCodesListRegistreUpdateDTO dto) {

        MappingCodesListRegistreDB updatedMapping =
                service.update(
                        id,
                        dto.registreCodesListId()
                );

        return ResponseEntity.ok(mapper.toDto(updatedMapping));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(AuthorityPrivileges.HAS_ADMIN_PRIVILEGES)
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}