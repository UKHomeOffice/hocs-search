package uk.gov.digital.ho.hocs.search.client.elasticsearchclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import uk.gov.digital.ho.hocs.search.domain.model.CaseData;

import java.util.Set;
import java.util.UUID;

public class ElasticSearchSingularClient extends BaseElasticSearchClient {

    public ElasticSearchSingularClient(ObjectMapper objectMapper, RestHighLevelClient client, String aliasPrefix) {
        super(objectMapper, client, aliasPrefix);
    }

    @Override
    public CaseData findById(UUID uuid) {
        return findById(getAlias(), uuid);
    }

    @Override
    public void save(CaseData caseData) {
        save(getAlias(), caseData);
    }

    @Override
    public void update(CaseData caseData) {
        update(getAlias(), caseData);
    }

    @Override
    public void update(Set<String> keys, CaseData caseData) {
        update(getAlias(), keys, caseData);
    }

    @Override
    public Set<UUID> search(BoolQueryBuilder query, int resultsLimit) {
        return search(getAlias(), query, resultsLimit);
    }

    private String getAlias() {
        return String.format("%s-case", aliasPrefix);
    }

}
