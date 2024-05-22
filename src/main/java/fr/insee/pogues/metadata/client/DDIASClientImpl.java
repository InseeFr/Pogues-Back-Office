package fr.insee.pogues.metadata.client;

import fr.insee.pogues.metadata.model.ddias.Unit;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DDIASClientImpl implements DDIASClient {

    @Override
    public List<Unit> getUnits() throws Exception {
        return List.of();
    }
}
