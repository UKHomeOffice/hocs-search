package uk.gov.digital.ho.hocs.search.domain.repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import uk.gov.digital.ho.hocs.search.domain.repositories.entities.FieldQueryTypeMapping;

@Service
public class FieldQueryTypeMappingRepository extends JsonConfigFileReader {

    private final FieldQueryTypeMapping fieldQueryTypeMapping;

    public FieldQueryTypeMappingRepository(ObjectMapper objectMapper) {
        super(objectMapper);

        fieldQueryTypeMapping = readValueFromFile(new TypeReference<>() {});
    }

    public String getQueryTypeByFieldLabel(String fieldLabel) {
        return fieldQueryTypeMapping.getQueryTypeByFieldLabel(fieldLabel);
    }

    @Override
    String getFileName() {
        return "field-query-types";
    }

}
