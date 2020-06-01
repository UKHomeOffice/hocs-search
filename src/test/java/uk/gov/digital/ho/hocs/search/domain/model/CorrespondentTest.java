package uk.gov.digital.ho.hocs.search.domain.model;

import org.junit.Test;
import uk.gov.digital.ho.hocs.search.api.dto.AddressDto;
import uk.gov.digital.ho.hocs.search.api.dto.CreateCorrespondentRequest;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CorrespondentTest {

    private AddressDto validAddressDto = new AddressDto("Postcode", "add1", "add2", "add3", "country");
    private CreateCorrespondentRequest validCreateCorrespondentRequest = new CreateCorrespondentRequest(UUID.randomUUID(), LocalDateTime.now(), "LAW", "FULLNAME", validAddressDto, "0", "e", "REF", "ExtKey");

    @Test
    public void shouldCreateCorrespondentConstructor() {

        Correspondent correspondent = Correspondent.from(validCreateCorrespondentRequest);

        assertThat(correspondent.getUuid()).isEqualTo(validCreateCorrespondentRequest.getUuid());
        assertThat(correspondent.getCreated()).isEqualTo(validCreateCorrespondentRequest.getCreated());
        assertThat(correspondent.getType()).isEqualTo(validCreateCorrespondentRequest.getType());
        assertThat(correspondent.getFullname()).isEqualTo(validCreateCorrespondentRequest.getFullname());
        assertThat(correspondent.getPostcode()).isEqualTo(validCreateCorrespondentRequest.getAddress().getPostcode());
        assertThat(correspondent.getAddress1()).isEqualTo(validCreateCorrespondentRequest.getAddress().getAddress1());
        assertThat(correspondent.getAddress2()).isEqualTo(validCreateCorrespondentRequest.getAddress().getAddress2());
        assertThat(correspondent.getAddress3()).isEqualTo(validCreateCorrespondentRequest.getAddress().getAddress3());
        assertThat(correspondent.getCountry()).isEqualTo(validCreateCorrespondentRequest.getAddress().getCountry());
        assertThat(correspondent.getTelephone()).isEqualTo(validCreateCorrespondentRequest.getTelephone());
        assertThat(correspondent.getEmail()).isEqualTo(validCreateCorrespondentRequest.getEmail());
        assertThat(correspondent.getReference()).isEqualTo(validCreateCorrespondentRequest.getReference());
        assertThat(correspondent.getExternalKey()).isEqualTo(validCreateCorrespondentRequest.getExternalKey());
    }

}