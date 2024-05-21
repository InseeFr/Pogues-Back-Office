package fr.insee.pogues.webservice.rest;


import fr.insee.pogues.metadata.model.ddias.Unit;
import fr.insee.pogues.metadata.service.MetadataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/meta-data")
@Tag(name = "3. Search to MetaData repository")
@Slf4j
public class MetadataController {


    @Autowired
    MetadataService metadataService;

    @GetMapping("units")
    @Operation(operationId = "getUnits", summary = "Get units measure", description = "This will give a list of objects containing the uri and the label for all units", responses = {
            @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(type = "List", implementation = Unit.class))) })
    public ResponseEntity<List<Unit>> getUnits() throws Exception {
        List<Unit> units = new ArrayList<>();
        try {
            units = metadataService.getUnits();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body(units);
    }
}
