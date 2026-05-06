package fr.insee.pogues.domain.entity.db;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "mapping_codes_list_registre")
@Getter
@Setter
@NoArgsConstructor
public class MappingCodesListRegistreDB {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pogues_codes_list_id", nullable = false, unique = true, updatable = false)
    private String poguesCodesListId;

    @Column(name = "registre_codes_list_id", nullable = false)
    private UUID registreCodesListId;
}