package uk.gov.digital.ho.hocs.search.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class DeleteCaseRequest {

    @JsonProperty("caseUUID")
    private UUID caseUUID;

    @JsonProperty("deleted")
    private Boolean deleted;

}
