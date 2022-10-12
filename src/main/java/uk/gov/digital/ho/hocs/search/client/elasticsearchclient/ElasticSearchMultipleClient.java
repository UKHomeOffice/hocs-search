package uk.gov.digital.ho.hocs.search.client.elasticsearchclient;

import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ElasticSearchMultipleClient extends BaseElasticSearchClient {

    public ElasticSearchMultipleClient(RestHighLevelClient client,
                                       String aliasPrefix,
                                       int resultsLimit) {
        super(client, aliasPrefix, resultsLimit);
    }

    @Override
    public Map<String, Object> findById(UUID uuid, String type) {
        return findById(getTypeReadAlias(type), uuid);
    }

    @Override
    public void update(UUID uuid, String type, Map<String, Object> caseData) {
        update(getAlias(type), uuid, caseData);
    }

    @Override
    public List<Map<String, Object>> search(BoolQueryBuilder query) {
        return search(getReadAlias(), query);
    }

    private String getAlias(String type) {
        return String.format("%s-%s", aliasPrefix, type.toLowerCase());
    }

    private String getReadAlias() {
        return String.format("%s-read", aliasPrefix);
    }

    private String getTypeReadAlias(String type) {
        return String.format("%s-%s-read", aliasPrefix, type.toLowerCase());
    }

}
