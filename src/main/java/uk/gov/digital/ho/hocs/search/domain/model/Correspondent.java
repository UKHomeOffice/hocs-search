package uk.gov.digital.ho.hocs.search.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.gov.digital.ho.hocs.search.api.dto.AddressDto;
import uk.gov.digital.ho.hocs.search.api.dto.CreateCorrespondentRequest;

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

    private Correspondent(CreateCorrespondentRequest createCorrespondentRequest) {
        this.uuid = createCorrespondentRequest.getUuid();
        this.created = createCorrespondentRequest.getCreated();
        this.type = createCorrespondentRequest.getType();
        this.fullname = createCorrespondentRequest.getFullname();

        AddressDto address = createCorrespondentRequest.getAddress();
        if (address != null) {
            this.postcode = address.getPostcode();
            this.address1 = address.getAddress1();
            this.address2 = address.getAddress2();
            this.address3 = address.getAddress3();
            this.country = address.getCountry();
        }

        this.telephone = createCorrespondentRequest.getTelephone();
        this.email = createCorrespondentRequest.getEmail();
        this.reference = createCorrespondentRequest.getReference();
        this.externalKey = createCorrespondentRequest.getExternalKey();
    }

    public static Correspondent from(CreateCorrespondentRequest createCorrespondentRequest) {
        return new Correspondent(createCorrespondentRequest);
    }

}
