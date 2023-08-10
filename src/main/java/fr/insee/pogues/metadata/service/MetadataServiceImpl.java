package fr.insee.pogues.metadata.service;

import fr.insee.pogues.exception.IllegalFlowControlException;
import fr.insee.pogues.exception.PoguesException;
import fr.insee.pogues.metadata.model.*;
import fr.insee.pogues.metadata.repository.MetadataRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Service
public class MetadataServiceImpl implements MetadataService {

    private static final Logger logger = LogManager.getLogger(MetadataServiceImpl.class);
    
    private static final String IDSERIE_PATTERN="^[a-z]\\d{4}$";
    private static final String INVALID_IDENTIFER = "Invalid identifier";
    private static final String MESSAGE_INVALID_IDENTIFIER = "Identifier %s is invalid";

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
		if (id.matches("\\b[0-9a-f]{8}\\b-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-\\b[0-9a-f]{12}\\b")) {
		return metadataRepository.getCodeList(id);
		} else {
			throw new PoguesException(500,INVALID_IDENTIFER,String.format(MESSAGE_INVALID_IDENTIFIER,id));
		}
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
	public List<OperationOut> getOperationsBySerieId(String id) throws PoguesException, IllegalFlowControlException.PoguesClientException {
		if (id.matches(IDSERIE_PATTERN)) {
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
			throw new PoguesException(500,INVALID_IDENTIFER,String.format(MESSAGE_INVALID_IDENTIFIER,id));
		}
	}
	
	@Override
	public List<DataCollectionOut> getDataCollectionsByOperationId(String id) throws Exception {
		if (id.matches(IDSERIE_PATTERN)) {
			Operation op = metadataRepository.getOperationById(id);
			if (op == null) {
				throw new PoguesException(404, "Not found", String.format("No operation found for identifier %s", id));
			}
			Serie serie = metadataRepository.getSerieById(op.getSerie().getId());
			List<DataCollectionOut> dcOut = new ArrayList<>();
			if (serie.getFrequence().getId() == null) {
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
						throw new PoguesException(500, "Internal Servor Error", String.format("Invalid frequence %s", id));
					} else {
						throw new PoguesException(500, "Internal Servor Error", String.format("No frequence"));
					}
				}
				return dcOut;
			}
		} else {
			throw new PoguesException(500, INVALID_IDENTIFER, String.format(MESSAGE_INVALID_IDENTIFIER,id));
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
		if (id.matches("^[a-z]\\d{4}-dc[A-Z]\\d{1,2}$")) {
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
			throw new PoguesException(500, INVALID_IDENTIFER, String.format(MESSAGE_INVALID_IDENTIFIER,id));
		}
	}

}
