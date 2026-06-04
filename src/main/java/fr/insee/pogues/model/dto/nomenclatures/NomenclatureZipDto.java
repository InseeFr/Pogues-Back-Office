package fr.insee.pogues.model.dto.nomenclatures;

/**
 * Model that represents nomenclature for zip metadata download business case
 * @param id
 * @param label
 * @param filename
 */
public record NomenclatureZipDto(
        String id,
        String label,
        String filename) {
}
