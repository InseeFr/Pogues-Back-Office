package fr.insee.pogues.mapper;

import fr.insee.pogues.model.*;
import fr.insee.pogues.model.dto.articulations.ArticulationDTO;
import fr.insee.pogues.model.dto.articulations.ArticulationItemDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ArticulationMapperTest {

    @Test
    @DisplayName("Should convert DTO articulation into model")
    void toModel_success(){
        // Given a DTO articulation
        ArticulationDTO articulationDTO = new ArticulationDTO(List.of(
                new ArticulationItemDTO("my label", "my value"),
                new ArticulationItemDTO("my other label", "my other value")
        ));
        Articulation expected = new Articulation();
        Item item1 = new Item();
        item1.setLabel("my label");
        item1.setValue("my value");
        item1.setType(ValueTypeEnum.VTL);
        expected.getItems().add(item1);
        Item item2 = new Item();
        item2.setLabel("my other label");
        item2.setValue("my other value");
        item2.setType(ValueTypeEnum.VTL);
        expected.getItems().add(item2);

        // When we convert it to Pogues model
        Articulation res = ArticulationMapper.toModel(articulationDTO);

        // It is correctly converted
        assertThat(res).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("Should convert model articulation into DTO")
    void toDTO_success() {
        // Given a model articulation
        Articulation articulation = new Articulation();
        Item item1 = new Item();
        item1.setLabel("my label");
        item1.setValue("my value");
        item1.setType(ValueTypeEnum.VTL);
        articulation.getItems().add(item1);
        Item item2 = new Item();
        item2.setLabel("my other label");
        item2.setValue("my other value");
        item2.setType(ValueTypeEnum.VTL);
        articulation.getItems().add(item2);

        ArticulationDTO expected = new ArticulationDTO(List.of(
                new ArticulationItemDTO("my label", "my value"),
                new ArticulationItemDTO("my other label", "my other value")
        ));

        // When we convert it to DTO
        ArticulationDTO res = ArticulationMapper.toDTO(articulation);

        // It is correctly converted
        assertThat(res).usingRecursiveComparison().isEqualTo(expected);
    }

}
