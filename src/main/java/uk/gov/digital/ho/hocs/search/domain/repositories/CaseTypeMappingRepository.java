package uk.gov.digital.ho.hocs.search.domain.repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import uk.gov.digital.ho.hocs.search.application.LogEvent;
import uk.gov.digital.ho.hocs.search.domain.exceptions.ApplicationExceptions;
import uk.gov.digital.ho.hocs.search.domain.repositories.entities.CaseTypeMapping;

import java.util.UUID;

@Service
public class CaseTypeMappingRepository extends JsonConfigFileReader {

    private final CaseTypeMapping caseTypeMapping;

    public CaseTypeMappingRepository(ObjectMapper objectMapper) {
        super(objectMapper);

        caseTypeMapping = readValueFromFile(new TypeReference<>() {});
    }

    public String getCaseTypeByShortCode(UUID caseUuid) {
        var caseTypeBytes = caseUuid.toString().substring(34);
        var mappedCaseType = caseTypeMapping.getCaseTypeByCaseUuid(caseTypeBytes);

        if (mappedCaseType == null) {
            throw new ApplicationExceptions.EntityNotFoundException(
                String.format("Case type mapping for short code %s not found", caseTypeBytes),
                LogEvent.CASE_TYPE_MAPPING_NOT_FOUND);
        }
        return mappedCaseType;
    }

    @Override
    String getFileName() {
        return "case-types";
    }

}
