package uk.gov.digital.ho.hocs.search.domain.repositories.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import uk.gov.digital.ho.hocs.search.domain.repositories.JsonConfigFileReader;

import java.util.Map;

public class FieldQueryTypeMapping {

    @JsonValue
    private final Map<String, String> fieldQueryTypeMappings;

    @JsonCreator
    public FieldQueryTypeMapping(Map<String, String> fieldQueryTypeMappings) {
        this.fieldQueryTypeMappings = fieldQueryTypeMappings;
    }

    public String getQueryTypeByFieldLabel(String fieldLabel) {
        return fieldQueryTypeMappings.get(fieldLabel);
    }
}
