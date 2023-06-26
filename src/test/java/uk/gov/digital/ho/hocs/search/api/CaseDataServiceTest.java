package uk.gov.digital.ho.hocs.search.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.internal.matchers.Contains;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.digital.ho.hocs.search.api.dto.CorrespondentDetailsDto;
import uk.gov.digital.ho.hocs.search.api.dto.CreateCaseRequest;
import uk.gov.digital.ho.hocs.search.api.dto.CreateTopicRequest;
import uk.gov.digital.ho.hocs.search.api.dto.DeleteCaseRequest;
import uk.gov.digital.ho.hocs.search.api.dto.DeleteTopicRequest;
import uk.gov.digital.ho.hocs.search.api.dto.SearchRequest;
import uk.gov.digital.ho.hocs.search.api.dto.SomuItemDto;
import uk.gov.digital.ho.hocs.search.api.dto.UpdateCaseRequest;
import uk.gov.digital.ho.hocs.search.api.elastic.scripts.CorrespondentScriptService;
import uk.gov.digital.ho.hocs.search.api.helpers.ObjectMapperConverterHelper;
import uk.gov.digital.ho.hocs.search.client.CaseQueryFactory;
import uk.gov.digital.ho.hocs.search.client.OpenSearchClient;
import uk.gov.digital.ho.hocs.search.domain.model.CaseData;
import uk.gov.digital.ho.hocs.search.domain.repositories.CaseTypeMappingRepository;
import uk.gov.digital.ho.hocs.search.domain.repositories.FieldQueryTypeMappingRepository;
import uk.gov.digital.ho.hocs.search.helpers.AllMapKeyMatcher;
import uk.gov.digital.ho.hocs.search.helpers.CaseTypeUuidHelper;
import uk.gov.digital.ho.hocs.search.helpers.ScriptMatcher;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CaseDataServiceTest {

    private final CreateCaseRequest validCreateCaseRequest = new CreateCaseRequest(
        CaseTypeUuidHelper.generateCaseTypeUuid("a1"), LocalDateTime.now(), "MIN", "REF", null,
        LocalDate.now().plusDays(1), LocalDate.now().plusDays(2), new HashMap<>());

    private final UpdateCaseRequest validUpdateCaseRequest = new UpdateCaseRequest(
        CaseTypeUuidHelper.generateCaseTypeUuid("a1"), LocalDateTime.now(), "MIN", "REF", UUID.randomUUID(),
        UUID.randomUUID(), LocalDate.now().plusDays(1), LocalDate.now().plusDays(2), new HashMap<>(), null);

    private final CorrespondentDetailsDto validCorrespondentDetailsDto = new CorrespondentDetailsDto(UUID.randomUUID(),
        LocalDateTime.now(), "LAW", "FULLNAME", null, "0", "e", "REF", "ExtKey");

    private final CreateTopicRequest validCreateTopicRequest = new CreateTopicRequest(UUID.randomUUID(), "Test Topic");

    private final DeleteTopicRequest validDeleteTopicRequest = new DeleteTopicRequest(UUID.randomUUID(), "Test Topic");

    private final SomuItemDto validSomuItemDto = new SomuItemDto(UUID.randomUUID(), UUID.randomUUID(), "{\"Test\": 1}");

    private ObjectMapper objectMapper;

    @Mock
    private OpenSearchClient openSearchClient;

    @Mock
    private CaseTypeMappingRepository caseTypeMappingRepository;

    @Mock
    private FieldQueryTypeMappingRepository fieldQueryTypeMappingRepository;

    private CaseDataService caseDataService;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper()
            .setDateFormat(new SimpleDateFormat("yyyy-MM-dd"))
            .registerModule(new JavaTimeModule());

        CorrespondentScriptService correspondentScriptService = new CorrespondentScriptService(objectMapper);

        caseDataService = new CaseDataService(objectMapper, openSearchClient, caseTypeMappingRepository,
            new CaseQueryFactory(fieldQueryTypeMappingRepository), correspondentScriptService
        );
    }

    @Test
    void createCase() {
        CaseData caseData = new CaseData(validCreateCaseRequest);

        caseDataService.createCase(validCreateCaseRequest.getUuid(), validCreateCaseRequest);

        verify(openSearchClient).update(validCreateCaseRequest.getType(), validCreateCaseRequest.getUuid(),
            ObjectMapperConverterHelper.convertObjectToMap(objectMapper, caseData));
    }

    @Test
    void updateCase() {
        CaseData caseData = new CaseData(validUpdateCaseRequest);

        caseDataService.updateCase(validUpdateCaseRequest.getUuid(), validUpdateCaseRequest);

        verify(openSearchClient).update(validUpdateCaseRequest.getType(), validUpdateCaseRequest.getUuid(),
            ObjectMapperConverterHelper.convertObjectToMap(objectMapper, caseData));
    }

    @Test
    void deleteCase() {
        DeleteCaseRequest deleteCaseRequest = new DeleteCaseRequest(CaseTypeUuidHelper.generateCaseTypeUuid("a1"),
            true);
        when(caseTypeMappingRepository.getCaseTypeByShortCode(deleteCaseRequest.getCaseUUID())).thenReturn("MIN");

        caseDataService.deleteCase(deleteCaseRequest.getCaseUUID(), deleteCaseRequest);

        verify(openSearchClient).update("MIN", deleteCaseRequest.getCaseUUID(),
            ObjectMapperConverterHelper.convertObjectToMap(objectMapper, Map.of("deleted", true)));
    }

    @Test
    void completeCase() {
        UUID caseUuid = CaseTypeUuidHelper.generateCaseTypeUuid("a1");
        when(caseTypeMappingRepository.getCaseTypeByShortCode(caseUuid)).thenReturn("MIN");

        caseDataService.completeCase(caseUuid);

        verify(openSearchClient).update("MIN", caseUuid,
            ObjectMapperConverterHelper.convertObjectToMap(objectMapper, Map.of("completed", true)));
    }

    @Test
    void shouldCallCollaboratorsCreateCorrespondent() {
        UUID caseUUID = CaseTypeUuidHelper.generateCaseTypeUuid("a1");

        when(caseTypeMappingRepository.getCaseTypeByShortCode(caseUUID)).thenReturn("MIN");

        caseDataService.createCorrespondent(caseUUID, validCorrespondentDetailsDto);

        verify(openSearchClient).update(eq("MIN"), eq(caseUUID), argThat(
            new ScriptMatcher(List.of(new Contains("allCorrespondents"), new Contains("currentCorrespondents")),
                List.of(new AllMapKeyMatcher("correspondent"))
            )));
    }

    @Test
    void shouldCallCollaboratorsDeleteCorrespondent() {
        UUID caseUUID = CaseTypeUuidHelper.generateCaseTypeUuid("a1");

        when(caseTypeMappingRepository.getCaseTypeByShortCode(caseUUID)).thenReturn("MIN");

        caseDataService.deleteCorrespondent(caseUUID, validCorrespondentDetailsDto);

        verify(openSearchClient).update(eq("MIN"), eq(caseUUID),
            argThat(new AllMapKeyMatcher("allCorrespondents", "currentCorrespondents")));
    }

    @Test
    void shouldCallUpdateCorrespondent() {
        UUID caseUUID = CaseTypeUuidHelper.generateCaseTypeUuid("a1");

        when(caseTypeMappingRepository.getCaseTypeByShortCode(caseUUID)).thenReturn("MIN");

        caseDataService.updateCorrespondent(caseUUID, validCorrespondentDetailsDto);

        verify(openSearchClient).update(eq("MIN"), eq(caseUUID),
            argThat(new AllMapKeyMatcher("allCorrespondents", "currentCorrespondents")));
    }

    @Test
    void shouldCallCollaboratorsCreateTopic() {
        UUID caseUUID = CaseTypeUuidHelper.generateCaseTypeUuid("a1");

        when(caseTypeMappingRepository.getCaseTypeByShortCode(caseUUID)).thenReturn("MIN");
        when(openSearchClient.findById("MIN", caseUUID)).thenReturn(Map.of());

        caseDataService.createTopic(caseUUID, validCreateTopicRequest);

        verify(openSearchClient).findById("MIN", caseUUID);
        verify(openSearchClient).update(eq("MIN"), eq(caseUUID),
            argThat(new AllMapKeyMatcher("allTopics", "currentTopics")));
    }

    @Test
    void shouldCallCollaboratorsDeleteTopic() {
        UUID caseUUID = CaseTypeUuidHelper.generateCaseTypeUuid("a1");

        when(caseTypeMappingRepository.getCaseTypeByShortCode(caseUUID)).thenReturn("MIN");
        when(openSearchClient.findById("MIN", caseUUID)).thenReturn(Map.of());

        caseDataService.deleteTopic(caseUUID, validDeleteTopicRequest);

        verify(openSearchClient).findById("MIN", caseUUID);
        verify(openSearchClient).update(eq("MIN"), eq(caseUUID),
            argThat(new AllMapKeyMatcher("allTopics", "currentTopics")));
    }

    @Test
    void shouldCreateSomuItemIfNotExists() {
        UUID caseUUID = CaseTypeUuidHelper.generateCaseTypeUuid("a1");

        when(caseTypeMappingRepository.getCaseTypeByShortCode(caseUUID)).thenReturn("MIN");
        when(openSearchClient.findById("MIN", caseUUID)).thenReturn(Map.of());

        caseDataService.createSomuItem(caseUUID, validSomuItemDto);

        verify(openSearchClient).findById("MIN", caseUUID);
        verify(openSearchClient).update(eq("MIN"), eq(caseUUID), argThat(new AllMapKeyMatcher("allSomuItems")));
    }

    @Test
    void shouldDeleteSomuItemIfExists() {
        UUID caseUUID = CaseTypeUuidHelper.generateCaseTypeUuid("a1");

        when(caseTypeMappingRepository.getCaseTypeByShortCode(caseUUID)).thenReturn("MIN");
        when(openSearchClient.findById("MIN", caseUUID)).thenReturn(Map.of());

        caseDataService.deleteSomuItem(caseUUID, validSomuItemDto);

        verify(openSearchClient).findById("MIN", caseUUID);
        verify(openSearchClient).update(eq("MIN"), eq(caseUUID), argThat(new AllMapKeyMatcher("allSomuItems")));
    }

    @Test
    void shouldUpdateSomuItemIfExists() {
        UUID caseUUID = CaseTypeUuidHelper.generateCaseTypeUuid("a1");

        when(caseTypeMappingRepository.getCaseTypeByShortCode(caseUUID)).thenReturn("MIN");
        when(openSearchClient.findById("MIN", caseUUID)).thenReturn(Map.of());

        caseDataService.updateSomuItem(caseUUID, validSomuItemDto);

        verify(openSearchClient).findById("MIN", caseUUID);
        verify(openSearchClient).update(eq("MIN"), eq(caseUUID), argThat(new AllMapKeyMatcher("allSomuItems")));
    }

    @Test
    void shouldNotSearchIfNoParams() {
        SearchRequest searchRequest = new SearchRequest();
        caseDataService.search(searchRequest);

        verify(openSearchClient, times(0)).search(any());
    }

}
