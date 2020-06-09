package uk.gov.digital.ho.hocs.search.domain.model;

import org.junit.Test;
import uk.gov.digital.ho.hocs.search.api.dto.AddressDto;
import uk.gov.digital.ho.hocs.search.api.dto.CorrespondentDetailsDto;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CorrespondentTest {

    private AddressDto validAddressDto = new AddressDto("Postcode", "add1", "add2", "add3", "country");
    private CorrespondentDetailsDto validCorrespondentDetailsDto = new CorrespondentDetailsDto(UUID.randomUUID(), LocalDateTime.now(), "LAW", "FULLNAME", validAddressDto, "0", "e", "REF", "ExtKey");

    @Test
    public void shouldCreateCorrespondentConstructor() {

        Correspondent correspondent = Correspondent.from(validCorrespondentDetailsDto);

        assertThat(correspondent.getUuid()).isEqualTo(validCorrespondentDetailsDto.getUuid());
        assertThat(correspondent.getCreated()).isEqualTo(validCorrespondentDetailsDto.getCreated());
        assertThat(correspondent.getType()).isEqualTo(validCorrespondentDetailsDto.getType());
        assertThat(correspondent.getFullname()).isEqualTo(validCorrespondentDetailsDto.getFullname());
        assertThat(correspondent.getPostcode()).isEqualTo(validCorrespondentDetailsDto.getAddress().getPostcode());
        assertThat(correspondent.getAddress1()).isEqualTo(validCorrespondentDetailsDto.getAddress().getAddress1());
        assertThat(correspondent.getAddress2()).isEqualTo(validCorrespondentDetailsDto.getAddress().getAddress2());
        assertThat(correspondent.getAddress3()).isEqualTo(validCorrespondentDetailsDto.getAddress().getAddress3());
        assertThat(correspondent.getCountry()).isEqualTo(validCorrespondentDetailsDto.getAddress().getCountry());
        assertThat(correspondent.getTelephone()).isEqualTo(validCorrespondentDetailsDto.getTelephone());
        assertThat(correspondent.getEmail()).isEqualTo(validCorrespondentDetailsDto.getEmail());
        assertThat(correspondent.getReference()).isEqualTo(validCorrespondentDetailsDto.getReference());
        assertThat(correspondent.getExternalKey()).isEqualTo(validCorrespondentDetailsDto.getExternalKey());
    }

}