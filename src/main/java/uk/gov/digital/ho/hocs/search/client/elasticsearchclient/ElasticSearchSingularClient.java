package uk.gov.digital.ho.hocs.search.client.elasticsearchclient;

import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ElasticSearchSingularClient extends BaseElasticSearchClient {

    private final String aliasPrefix;

    public ElasticSearchSingularClient(RestHighLevelClient client, String aliasPrefix, int resultsLimit) {
        super(client, resultsLimit);
        this.aliasPrefix = aliasPrefix;
    }

    @Override
    public Map<String, Object> findById(UUID uuid, String type) {
        return findById(getAlias(), uuid);
    }

    @Override
    public void update(UUID uuid, String type, Map<String, Object> caseData) {
        update(getAlias(), uuid, caseData);
    }

    @Override
    public List<Map<String, Object>> search(BoolQueryBuilder query) {
        return search(getAlias(), query);
    }

    private String getAlias() {
        return String.format("%s-case", aliasPrefix);
    }

}
