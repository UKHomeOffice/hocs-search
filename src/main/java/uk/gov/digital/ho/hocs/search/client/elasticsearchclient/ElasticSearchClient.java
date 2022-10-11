package uk.gov.digital.ho.hocs.search.client.elasticsearchclient;

import org.elasticsearch.index.query.BoolQueryBuilder;
import uk.gov.digital.ho.hocs.search.domain.model.CaseData;

import java.util.Set;
import java.util.UUID;

public interface ElasticSearchClient {

    CaseData findById(UUID uuid);

    void save(CaseData caseData);

    void update(CaseData caseData);

    void update(Set<String> keys, CaseData caseData);

    Set<UUID> search(BoolQueryBuilder query, int resultsLimit);

}
