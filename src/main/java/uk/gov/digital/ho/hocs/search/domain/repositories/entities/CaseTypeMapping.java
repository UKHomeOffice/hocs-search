package uk.gov.digital.ho.hocs.search.domain.repositories.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;

public class CaseTypeMapping {

    @JsonValue
    private final Map<String, String> caseTypeMappings;

    @JsonCreator
    public CaseTypeMapping(Map<String, String> caseTypeMappings) {
        this.caseTypeMappings = caseTypeMappings;
    }

    public String getCaseTypeByCaseUuid(String shortCode) {
        return caseTypeMappings.get(shortCode);
    }

}
