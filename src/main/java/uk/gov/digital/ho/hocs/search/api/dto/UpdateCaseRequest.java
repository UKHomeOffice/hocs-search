package uk.gov.digital.ho.hocs.search.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdateCaseRequest {

    @JsonProperty("uuid")
    private UUID uuid;

    @JsonProperty("created")
    private LocalDateTime created;

    @JsonProperty("type")
    private String type;

    @JsonProperty("reference")
    private String reference;

    @JsonProperty("primaryTopic")
    private UUID primaryTopic;

    @JsonProperty("primaryCorrespondent")
    private UUID primaryCorrespondent;

    @JsonProperty("caseDeadline")
    private LocalDate caseDeadline;

    @JsonProperty("dateReceived")
    private LocalDate dateReceived;

}