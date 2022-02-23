package fr.insee.pogues.metadata.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.insee.pogues.exceptions.PoguesClientException;
import fr.insee.pogues.exceptions.PoguesException;
import fr.insee.pogues.metadata.model.ColecticaItem;
import fr.insee.pogues.metadata.model.ColecticaItemRefList;
import fr.insee.pogues.metadata.model.Context;
import fr.insee.pogues.metadata.model.DataCollectionOut;
import fr.insee.pogues.metadata.model.Label;
import fr.insee.pogues.metadata.model.Operation;
import fr.insee.pogues.metadata.model.OperationOut;
import fr.insee.pogues.metadata.model.Serie;
import fr.insee.pogues.metadata.model.SerieOut;
import fr.insee.pogues.metadata.model.Unit;
import fr.insee.pogues.metadata.repository.MetadataRepository;
import fr.insee.pogues.utils.UserInputValidation;

@Service
public class MetadataServiceImpl implements MetadataService {

    private static final Logger logger = LogManager.getLogger(MetadataServiceImpl.class);

    @Autowired
    MetadataRepository metadataRepository;	
	
    @Override
    public ColecticaItem getItem(String id) throws Exception {
        return metadataRepository.findById(id);
    }

    @Override
    public ColecticaItemRefList getChildrenRef(String id) throws Exception {
        return metadataRepository.getChildrenRef(id);
    }

    @Override
    public List<ColecticaItem> getItems(ColecticaItemRefList refs) throws Exception {
        return metadataRepository.getItems(refs);
    }

    @Override
    public List<Unit> getUnits() throws Exception {
        return metadataRepository.getUnits();
    }

    @Override
    public String getDDIDocument(String id) throws Exception {
        return metadataRepository.getDDIDocument(id);
    }

	@Override
	public String getCodeList(String id) throws Exception {
		String codeList = metadataRepository.getCodeList(id);
		return null;
	}
	
	@Override
	public List<SerieOut> getSeries() throws Exception {
		List<SerieOut> seriesOut = new ArrayList<>();
		metadataRepository.getSeries().stream().forEach(serie -> {
			if (!"0".equals(serie.getNbOperation())) {
				SerieOut ser = new SerieOut();
				ser.setLabels(serie.getLabels());
				ser.setId(serie.getId());
				if (serie.getFrequence() != null) {
					ser.setFrequence(serie.getFrequence().getId());
				}
				seriesOut.add(ser);
			}
		});
		logger.info("Series found : {}", seriesOut.size());
		return seriesOut;
	}
	
	@Override
	public List<OperationOut> getOperationsBySerieId(String id) throws PoguesClientException, PoguesException {
		if (UserInputValidation.validateSerieId(id)) {
			List<OperationOut> operationsOut = metadataRepository.getOperationsBySerieId(id).stream().map(op -> {
				OperationOut opOut = new OperationOut();
				opOut.setLabels(op.getLabels());
				opOut.setId(op.getId());
				if (op.getSerie() != null) {
					opOut.setParent(op.getSerie().getId());
				}
				return opOut;
			}).collect(Collectors.toList());
			logger.info("Operations found : {}", operationsOut.size());
			return operationsOut;
		} else {
			throw new PoguesException(500,"Invalid identifier","Identifier "+id+" is invalid");
		}
	}
	
	@Override
	public List<DataCollectionOut> getDataCollectionsByOperationId(String id) throws Exception {
		if (UserInputValidation.validateSerieId(id)) {
			Operation op = metadataRepository.getOperationById(id);
			if (op == null) {
				throw new PoguesException(404, "Not found", String.format("No operation found for identifier %s", id));
			}
			Serie serie = metadataRepository.getSerieById(op.getSerie().getId());
			List<DataCollectionOut> dcOut = new ArrayList<>();
			if (serie.getFrequence() == null) {
				throw new PoguesException(500, "Internal server error",
						String.format("Frequence unavailable in Metadata API for serie %s", op.getSerie().getId()));
			} else {
				switch (serie.getFrequence().getId()) {
				case "A":
				case "C":
				case "P":
					dcOut.add(new DataCollectionOut(op.getLabels(),
							String.format("%s-dc%s1", op.getId(), serie.getFrequence().getId()), op.getId()));
					break;
				case "Q":
					dcOut = generateDataCollections(4, "T", op);
					break;
				case "T":
					dcOut = generateDataCollections(6, "B", op);
					break;
				case "M":
					dcOut = generateDataCollections(12, "M", op);
					break;
				default:
					if (serie.getFrequence().getId() != null) {
						logger.error("Invalid frequence {}", serie.getFrequence().getId());
					} else {
						logger.error("No frequence");
					}
				}
				return dcOut;
			}
		} else {
			throw new PoguesException(500, "Invalid identifier", "Identifier " + id + " is invalid");
		}
	}

	public List<DataCollectionOut> generateDataCollections(int nbDataCollections, String suffix, Operation op) {
		List<DataCollectionOut> dcOut = new ArrayList<>();
		IntStream.range(1, nbDataCollections + 1).forEach(i -> {
			List<Label> newLabels = new ArrayList<>();
			IntStream.range(0, op.getLabels().size()).forEach(j -> 
				newLabels.add(new Label(op.getLabels().get(j).getLanguage(),
						String.format("%s %s%s", op.getLabels().get(j).getContent(), suffix, i)))
			);
			dcOut.add(new DataCollectionOut(newLabels, String.format("%s-dc%s%s", op.getId(), suffix, i), op.getId()));
		});
		return dcOut;
	}
	
	@Override
	public Context getContextFromDataCollection(String id) throws Exception {
		if (UserInputValidation.validateDataCollectionId(id)) {
			final String regex = "(s\\d{4})(-dc[A-Z]\\d+)";

			final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
			final Matcher matcher = pattern.matcher(id);
			Context context = new Context();
			if (matcher.find()) {
				Operation op = metadataRepository.getOperationById(matcher.group(1));
				context.setDataCollectionId(id);
				context.setOperationId(op.getId());
				context.setSerieId(op.getSerie().getId());
			} else {
				logger.error("No match found");
				throw new PoguesException(400, "Bad Request", "Data Collection identifier is invalid");
			}
			return context;
		} else {
			throw new PoguesException(500, "Invalid identifier", "Identifier " + id + " is invalid");
		}
	}
	
	
	
	
}
