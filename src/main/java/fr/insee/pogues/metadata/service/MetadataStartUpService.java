package fr.insee.pogues.metadata.service;

import fr.insee.pogues.metadata.model.magma.Operation;
import fr.insee.pogues.metadata.model.magma.Serie;
import fr.insee.pogues.metadata.repository.MetadataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class MetadataStartUpService {

    @Autowired
    MetadataRepository metadataRepository;

    @EventListener
    public void cacheMetadata(ContextRefreshedEvent event) throws Exception {

        long start = System.currentTimeMillis();
        log.info("Caching metadata started !");

        cacheUnits();
        log.info("Units cached !");

        List<Serie> series = cacheSeries();
        series.stream().parallel().forEach(
                serie -> {
                    try {
                        cacheSerie(serie.getId());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        );
        log.info("All series cached !");
        long finish = System.currentTimeMillis();
        log.info("Caching metadata succeed in {} seconds.", (finish - start) / 1000);
    }

    private void cacheUnits() throws Exception {
        metadataRepository.getUnits();
    }

    private List<Serie> cacheSeries() throws Exception {
        return metadataRepository.getSeries();
    }

    private void cacheSerie(String idSerie) throws Exception {
        metadataRepository.getSerieById(idSerie);
        List<Operation> operations = cacheOperationsByIdSerie(idSerie);
        operations.stream().parallel().forEach(operation -> {
            try {
                cacheOperation(operation.getId());;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private List<Operation> cacheOperationsByIdSerie(String idSerie) throws Exception {
        return metadataRepository.getOperationsByIdSerie(idSerie);
    }

    private void cacheOperation(String idOperation) throws Exception {
        metadataRepository.getOperationById(idOperation);
    }


}