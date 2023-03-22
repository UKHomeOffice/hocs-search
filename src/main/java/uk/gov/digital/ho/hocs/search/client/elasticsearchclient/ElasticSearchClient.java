package uk.gov.digital.ho.hocs.search.client.elasticsearchclient;

import org.opensearch.index.query.BoolQueryBuilder;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ElasticSearchClient {

    Map<String, Object> findById(UUID uuid, String type);

    void update(UUID uuid, String type, Map<String, Object> caseData);

    List<Map<String, Object>> search(BoolQueryBuilder query);

}
