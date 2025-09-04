package fr.insee.pogues.mapper;


import fr.insee.pogues.model.*;
import fr.insee.pogues.model.dto.articulations.ArticulationDTO;
import fr.insee.pogues.model.dto.articulations.ArticulationItemDTO;

import java.util.List;

public class ArticulationMapper {

    private ArticulationMapper() {}

    /**
     * Compute the articulation into its Pogues model.
     * @param articulationDTO Articulation to convert
     */
    public static Articulation toModel(ArticulationDTO articulationDTO) {
        Articulation articulation = new Articulation();
        List<Item> items = articulationDTO.getItems().stream().map(ArticulationMapper::toModel).toList();
        articulation.getItems().addAll(items);
        return articulation;
    }

    /**
     * Compute the articulation item into its Pogues model, setting its type as VTL.
     * @param articulationItemDTO Articulation item to convert
     */
    private static Item toModel(ArticulationItemDTO articulationItemDTO) {
        Item item = new Item();
        item.setLabel(articulationItemDTO.getLabel());
        item.setValue(articulationItemDTO.getValue());
        item.setType(ValueTypeEnum.VTL);
        return item;
    }

    /**
     * Compute the articulation into its DTO
     * @param articulation Articulation to convert
     */
    public static ArticulationDTO toDTO(Articulation articulation) {
        if (articulation == null) return new ArticulationDTO();

        List<ArticulationItemDTO> itemsDTO = articulation.getItems().stream().map(ArticulationMapper::toDTO).toList();
        return new ArticulationDTO(itemsDTO);
    }

    private static ArticulationItemDTO toDTO(Item articulationItem) {
        return new ArticulationItemDTO(articulationItem.getLabel(), articulationItem.getValue());
    }

}
