package uk.gov.digital.ho.hocs.search.application.queue;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class CreateAuditDto {

    @JsonProperty(value = "caseUUID")
    private UUID caseUUID;

    @JsonProperty(value = "data")
    private String data;

    @JsonProperty(value = "type")
    private String type;

}

