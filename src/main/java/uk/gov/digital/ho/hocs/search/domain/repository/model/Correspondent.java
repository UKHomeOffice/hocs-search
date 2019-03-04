package uk.gov.digital.ho.hocs.search.domain.repository.model;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import uk.gov.digital.ho.hocs.search.api.dto.AddressDto;
import uk.gov.digital.ho.hocs.search.api.dto.CreateCorrespondentRequest;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(indexName = "case", type = "caseData")
public class Correspondent {

    @Id
    @Getter
    private UUID uuid;

    @Getter
    private LocalDateTime created;

    @Getter
    private String type;

    @Getter
    private String fullname;

    @Getter
    private String postcode;

    @Getter
    private String address1;

    @Getter
    private String address2;

    @Getter
    private String address3;

    @Getter
    private String country;

    @Getter
    private String telephone;

    @Getter
    private String email;

    @Getter
    private String reference;

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
        this.reference = createCorrespondentRequest.getReference();    }

    public static Correspondent from(CreateCorrespondentRequest createCorrespondentRequest) {
        return new Correspondent(createCorrespondentRequest);
    }

}
