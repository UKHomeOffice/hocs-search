package uk.gov.digital.ho.hocs.search.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.gov.digital.ho.hocs.search.api.dto.AddressDto;
import uk.gov.digital.ho.hocs.search.api.dto.CorrespondentDetailsDto;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Getter
public class Correspondent {

    private UUID uuid;

    private LocalDateTime created;

    private String type;

    private String fullname;

    private String postcode;

    private String address1;

    private String address2;

    private String address3;

    private String country;

    private String telephone;

    private String email;

    private String reference;

    private String externalKey;

    private Correspondent(CorrespondentDetailsDto correspondentDetailsDto) {
        this.uuid = correspondentDetailsDto.getUuid();
        this.created = correspondentDetailsDto.getCreated();
        this.type = correspondentDetailsDto.getType();
        this.fullname = correspondentDetailsDto.getFullname();

        AddressDto address = correspondentDetailsDto.getAddress();
        if (address != null) {
            this.postcode = address.getPostcode();
            this.address1 = address.getAddress1();
            this.address2 = address.getAddress2();
            this.address3 = address.getAddress3();
            this.country = address.getCountry();
        }

        this.telephone = correspondentDetailsDto.getTelephone();
        this.email = correspondentDetailsDto.getEmail();
        this.reference = correspondentDetailsDto.getReference();
        this.externalKey = correspondentDetailsDto.getExternalKey();
    }

    public static Correspondent from(CorrespondentDetailsDto correspondentDetailsDto) {
        return new Correspondent(correspondentDetailsDto);
    }

}
