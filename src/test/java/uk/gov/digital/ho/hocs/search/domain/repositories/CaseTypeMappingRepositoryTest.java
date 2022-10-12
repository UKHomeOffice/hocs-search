package uk.gov.digital.ho.hocs.search.domain.repositories;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.digital.ho.hocs.search.domain.exceptions.ApplicationExceptions;
import uk.gov.digital.ho.hocs.search.helpers.CaseTypeUuidHelper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("localstack")
class CaseTypeMappingRepositoryTest {

    @Autowired
    private CaseTypeMappingRepository caseTypeMappingRepository;

    @Test
    void expectExceptionIfTypeNotFound() {
        var caseUuid = CaseTypeUuidHelper.generateCaseTypeUuid("ff");

        assertThatThrownBy(() -> caseTypeMappingRepository.getCaseTypeByShortCode(caseUuid)).isInstanceOf(
            ApplicationExceptions.EntityNotFoundException.class).hasMessageContaining(
            "Case type mapping for short code ff not found");
    }

    @Test
    void validCaseTypeIsReturnedWhenMappingFound() {
        var caseUuid = CaseTypeUuidHelper.generateCaseTypeUuid("A1");

        assertThat(caseTypeMappingRepository.getCaseTypeByShortCode(caseUuid)).isEqualTo("MIN");
    }

}
