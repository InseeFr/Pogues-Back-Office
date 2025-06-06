package fr.insee.pogues.webservice.rest;


import fr.insee.pogues.metadata.model.ddias.Unit;
import fr.insee.pogues.metadata.model.pogues.DataCollection;
import fr.insee.pogues.metadata.model.pogues.DataCollectionContext;
import fr.insee.pogues.metadata.service.MetadataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "7. Search to MetaData repository")
@Slf4j
public class MetadataController {

    @Autowired
    MetadataService metadataService;

    @GetMapping("meta-data/units")
    @Operation(operationId = "getUnits", summary = "Get units measure", description = "This will give a list of objects containing the uri and the label for all units", responses = {
            @ApiResponse(content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Unit.class)))) })
    public ResponseEntity<List<Unit>> getUnits() throws Exception {
        List<Unit> units = metadataService.getUnits();
        return ResponseEntity.status(HttpStatus.OK).body(units);
    }

    @GetMapping("search/series")
    @Operation(operationId = "getSeries", summary = "Get all series", description = "This will give a list of series via magma", responses = {
            @ApiResponse(content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = DataCollection.class)))) })
    public ResponseEntity<List<DataCollection>> getSeries() throws Exception {
        List<DataCollection> series = metadataService.getSeries();
        return ResponseEntity.status(HttpStatus.OK).body(series);
    }

    @GetMapping("search/series/{id}/operations")
    @Operation(operationId = "getOperationsBySerie", summary = "Get operations by serie id", description = "This will give a list of operations according to serie id via magma", responses = {
            @ApiResponse(content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = DataCollection.class)))) })
    public ResponseEntity<List<DataCollection>> getOperationsBySerie(@PathVariable(value = "id") String id) throws Exception {
        List<DataCollection> operations = metadataService.getOperationsByIdSerie(id);
        return ResponseEntity.status(HttpStatus.OK).body(operations);
    }

    @GetMapping("search/operations/{id}/collections")
    @Operation(operationId = "getCollectionsByOperation", summary = "Get dataCollection by serie collection", description = "This will give a list of data-collections according to operation id via magma", responses = {
            @ApiResponse(content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = DataCollection.class)))) })
    public ResponseEntity<List<DataCollection>> getDataCollectionByOperation(@PathVariable(value = "id") String id) throws Exception {
        List<DataCollection> operations = metadataService.getColletionsByIdOperation(id);
        return ResponseEntity.status(HttpStatus.OK).body(operations);
    }

    @GetMapping("search/context/collection/{id}")
    @Operation(operationId = "getCollectionContextFromIdCollection", summary = "Get dataCollection context by data-collection id", description = "This will give a the context of data-collection according its id", responses = {
            @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = DataCollectionContext.class))) })
    public ResponseEntity<DataCollectionContext> getCollectionContextFromIdCollection(@PathVariable(value = "id") String id) throws Exception {
        DataCollectionContext dataCollectionContext = metadataService.getCollectionContextFromIdCollection(id);
        return ResponseEntity.status(HttpStatus.OK).body(dataCollectionContext);
    }
}
