package uk.gov.digital.ho.hocs.search.client.elasticsearchclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import uk.gov.digital.ho.hocs.search.domain.model.CaseData;

import java.util.Set;
import java.util.UUID;

public class ElasticSearchMultipleClient extends BaseElasticSearchClient {

    public ElasticSearchMultipleClient(ObjectMapper objectMapper, RestHighLevelClient client, String aliasPrefix) {
        super(objectMapper, client, aliasPrefix);
    }

    @Override
    public CaseData findById(UUID uuid) {
        return findById(getReadAlias(), uuid);
    }

    @Override
    public void save(CaseData caseData) {
        save(getAlias(caseData), caseData);
    }

    @Override
    public void update(CaseData caseData) {
        update(getAlias(caseData), caseData);
    }

    @Override
    public void update(Set<String> keys, CaseData caseData) {
        update(getAlias(caseData), keys, caseData);
    }

    @Override
    public Set<UUID> search(BoolQueryBuilder query, int resultsLimit) {
        return search(getReadAlias(), query, resultsLimit);
    }

    private String getAlias(CaseData caseData) {
        return String.format("%s-%s", aliasPrefix, caseData.getType().toLowerCase());
    }

    private String getReadAlias() {
        return String.format("%s-read", aliasPrefix);
    }

}
