package uk.gov.digital.ho.hocs.search.domain.repository.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.UUID;

@AllArgsConstructor
@Document(indexName = "case", type = "caseData")
public class Topic {

    @Id
    @Getter
    private UUID uuid;

    @Getter
    private String value;

}
