package uk.gov.digital.ho.hocs.search.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import uk.gov.digital.ho.hocs.search.api.dto.AddressDto;
import uk.gov.digital.ho.hocs.search.api.dto.CreateCorrespondentRequest;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Document(indexName = "correspondent", type = "correspondent")
public class Correspondent {

    @Id
    private UUID uuid;

    @Field(type = FieldType.Date)
    private LocalDateTime created;

    @Field(type = FieldType.Keyword)
    private String type;

    @Field(type = FieldType.Text)
    private String fullname;

    @Field(type = FieldType.Text)
    private String postcode;

    @Field(type = FieldType.Text)
    private String address1;

    @Field(type = FieldType.Text)
    private String address2;

    @Field(type = FieldType.Text)
    private String address3;

    @Field(type = FieldType.Text)
    private String country;

    @Field(type = FieldType.Text)
    private String telephone;

    @Field(type = FieldType.Keyword)
    private String email;

    @Field(type = FieldType.Keyword)
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
