package uk.gov.digital.ho.hocs.search.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.digital.ho.hocs.search.api.dto.CreateCaseRequest;
import uk.gov.digital.ho.hocs.search.api.dto.CreateCorrespondentRequest;
import uk.gov.digital.ho.hocs.search.api.dto.UpdateCaseRequest;
import uk.gov.digital.ho.hocs.search.domain.repository.CaseRepository;
import uk.gov.digital.ho.hocs.search.domain.repository.model.CaseData;
import uk.gov.digital.ho.hocs.search.domain.repository.model.Topic;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CaseDataServiceTest {

    @Mock
    private CaseRepository caseRepository;

    private CaseDataService caseDataService;
    private UUID caseUUID = UUID.randomUUID();
    private CreateCaseRequest validCreateCaseRequest = new CreateCaseRequest(UUID.randomUUID(), LocalDateTime.now(), "MIN", "REF", LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
    private UpdateCaseRequest validUpdateCaseRequest = new UpdateCaseRequest(UUID.randomUUID(), LocalDateTime.now(), "MIN", "REF", UUID.randomUUID(), UUID.randomUUID(), LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
    private CreateCorrespondentRequest validCreateCorrespondentRequest = new CreateCorrespondentRequest(UUID.randomUUID(), LocalDateTime.now(), "LAW", "FULLNAME", null, "0", "e", "REF");
    private Topic validTopic = new Topic(UUID.randomUUID(), "VALUE");

    @Before
    public void setup(){
        caseDataService = new CaseDataService(caseRepository);
    }

    @Test
    public void ShouldCallCollaboratorsCreateCase() {

        when(caseRepository.findById(caseUUID)).thenReturn(Optional.of(new CaseData(caseUUID)));

        caseDataService.createCase(caseUUID, validCreateCaseRequest);

        verify(caseRepository, times(1)).findById(caseUUID);
        verify(caseRepository, times(1)).save(any(CaseData.class));

        verifyNoMoreInteractions(caseRepository);
    }

    @Test
    public void ShouldCreateNewIfNotFoundCreateCase() {

        when(caseRepository.findById(caseUUID)).thenReturn(Optional.empty());

        caseDataService.createCase(caseUUID, validCreateCaseRequest);

        verify(caseRepository, times(1)).findById(caseUUID);
        verify(caseRepository, times(1)).save(any(CaseData.class));

        verifyNoMoreInteractions(caseRepository);
    }

    @Test
    public void ShouldCallCollaboratorsUpdateCase() {

        when(caseRepository.findById(caseUUID)).thenReturn(Optional.of(new CaseData(caseUUID)));

        caseDataService.updateCase(caseUUID, validUpdateCaseRequest);

        verify(caseRepository, times(1)).findById(caseUUID);
        verify(caseRepository, times(1)).save(any(CaseData.class));

        verifyNoMoreInteractions(caseRepository);
    }

    @Test
    public void ShouldCreateNewIfNotFoundUpdateCase() {

        when(caseRepository.findById(caseUUID)).thenReturn(Optional.empty());

        caseDataService.updateCase(caseUUID, validUpdateCaseRequest);

        verify(caseRepository, times(1)).findById(caseUUID);
        verify(caseRepository, times(1)).save(any(CaseData.class));

        verifyNoMoreInteractions(caseRepository);
    }

    @Test
    public void ShouldCallCollaboratorsDeleteCase() {

        when(caseRepository.findById(caseUUID)).thenReturn(Optional.of(new CaseData(caseUUID)));

        caseDataService.deleteCase(caseUUID);

        verify(caseRepository, times(1)).findById(caseUUID);
        verify(caseRepository, times(1)).save(any(CaseData.class));

        verifyNoMoreInteractions(caseRepository);
    }

    @Test
    public void ShouldCreateNewIfNotFoundDeleteCase() {

        when(caseRepository.findById(caseUUID)).thenReturn(Optional.empty());

        caseDataService.deleteCase(caseUUID);

        verify(caseRepository, times(1)).findById(caseUUID);
        verify(caseRepository, times(1)).save(any(CaseData.class));

        verifyNoMoreInteractions(caseRepository);
    }

    @Test
    public void ShouldCallCollaboratorsCreateCorrespondent() {

        when(caseRepository.findById(caseUUID)).thenReturn(Optional.of(new CaseData(caseUUID)));

        caseDataService.createCorrespondent(caseUUID, validCreateCorrespondentRequest);

        verify(caseRepository, times(1)).findById(caseUUID);
        verify(caseRepository, times(1)).save(any(CaseData.class));

        verifyNoMoreInteractions(caseRepository);
    }

    @Test
    public void ShouldCreateNewIfNotFoundCreateCorrespondent() {

        when(caseRepository.findById(caseUUID)).thenReturn(Optional.empty());

        caseDataService.createCorrespondent(caseUUID, validCreateCorrespondentRequest);

        verify(caseRepository, times(1)).findById(caseUUID);
        verify(caseRepository, times(1)).save(any(CaseData.class));

        verifyNoMoreInteractions(caseRepository);
    }

    @Test
    public void ShouldCallCollaboratorsDeleteCorrespondent() {

        when(caseRepository.findById(caseUUID)).thenReturn(Optional.of(new CaseData(caseUUID)));

        caseDataService.deleteCorrespondent(caseUUID, validCreateCorrespondentRequest.getUuid());

        verify(caseRepository, times(1)).findById(caseUUID);
        verify(caseRepository, times(1)).save(any(CaseData.class));

        verifyNoMoreInteractions(caseRepository);
    }

    @Test
    public void ShouldCreateNewIfNotFoundDeleteCorrespondent() {

        when(caseRepository.findById(caseUUID)).thenReturn(Optional.empty());

        caseDataService.deleteCorrespondent(caseUUID, validCreateCorrespondentRequest.getUuid());

        verify(caseRepository, times(1)).findById(caseUUID);
        verify(caseRepository, times(1)).save(any(CaseData.class));

        verifyNoMoreInteractions(caseRepository);
    }

    @Test
    public void ShouldCallCollaboratorsCreateTopic() {

        when(caseRepository.findById(caseUUID)).thenReturn(Optional.of(new CaseData(caseUUID)));

        caseDataService.createTopic(caseUUID, validTopic.getUuid());

        verify(caseRepository, times(1)).findById(caseUUID);
        verify(caseRepository, times(1)).save(any(CaseData.class));

        verifyNoMoreInteractions(caseRepository);
    }

    @Test
    public void ShouldCreateNewIfNotFoundCreateTopic() {

        when(caseRepository.findById(caseUUID)).thenReturn(Optional.empty());

        caseDataService.createTopic(caseUUID, validTopic.getUuid());

        verify(caseRepository, times(1)).findById(caseUUID);
        verify(caseRepository, times(1)).save(any(CaseData.class));

        verifyNoMoreInteractions(caseRepository);
    }

    @Test
    public void ShouldCallCollaboratorsDeleteTopic() {

        when(caseRepository.findById(caseUUID)).thenReturn(Optional.of(new CaseData(caseUUID)));

        caseDataService.deleteTopic(caseUUID, validTopic.getUuid());

        verify(caseRepository, times(1)).findById(caseUUID);
        verify(caseRepository, times(1)).save(any(CaseData.class));

        verifyNoMoreInteractions(caseRepository);
    }

    @Test
    public void ShouldCreateNewIfNotFoundDeleteTopic() {

        when(caseRepository.findById(caseUUID)).thenReturn(Optional.empty());

        caseDataService.deleteTopic(caseUUID, validTopic.getUuid());

        verify(caseRepository, times(1)).findById(caseUUID);
        verify(caseRepository, times(1)).save(any(CaseData.class));

        verifyNoMoreInteractions(caseRepository);
    }
}