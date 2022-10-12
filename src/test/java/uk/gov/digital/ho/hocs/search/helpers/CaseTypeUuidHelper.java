package uk.gov.digital.ho.hocs.search.helpers;

import java.util.UUID;

public class CaseTypeUuidHelper {

    public static UUID generateCaseTypeUuid(String caseShortCode) {
        return UUID.fromString(UUID.randomUUID().toString().substring(0, 34) + caseShortCode);
    }

}
