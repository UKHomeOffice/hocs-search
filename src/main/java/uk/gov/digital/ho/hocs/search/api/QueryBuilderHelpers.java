package uk.gov.digital.ho.hocs.search.api;

import lombok.extern.slf4j.Slf4j;
import org.opensearch.index.query.ExistsQueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.index.query.WildcardQueryBuilder;

@Slf4j
class QueryBuilderHelpers {

    static ExistsQueryBuilder fieldExists(String fieldName) {
        return QueryBuilders.existsQuery(fieldName);
    }

    static WildcardQueryBuilder nonEmptyField(String fieldName) {
        return QueryBuilders.wildcardQuery(fieldName, "*");
    }

}
