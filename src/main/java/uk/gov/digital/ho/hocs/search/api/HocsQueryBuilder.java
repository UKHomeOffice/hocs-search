package uk.gov.digital.ho.hocs.search.api;

import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.*;
import org.springframework.util.StringUtils;
import uk.gov.digital.ho.hocs.search.api.dto.DateRangeDto;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
class HocsQueryBuilder {

    private final BoolQueryBuilder mqb;

    private boolean hasClause = false;

    HocsQueryBuilder(BoolQueryBuilder mqb) {
        this.mqb = mqb;
    }

    HocsQueryBuilder deleted(Boolean deleted) {
        if (deleted != null) {
            log.debug("deleted {} , adding to query", deleted);
            QueryBuilder typeQb = QueryBuilders.matchQuery("deleted", deleted);
            mqb.must(typeQb);
        } else {
            log.debug("deleted was null");
        }
        return this;
    }

    HocsQueryBuilder reference(String reference) {
        if (reference != null && !reference.isEmpty()) {
            log.debug("reference {} , adding to query", reference);
            QueryBuilder typeQb = QueryBuilders.matchQuery("reference", reference);
            mqb.must(typeQb);
            hasClause = true;
        } else {
            log.debug("reference was null or empty");
        }
        return this;
    }

    HocsQueryBuilder caseTypes(List<String> caseTypes) {
        if (caseTypes != null && !caseTypes.isEmpty()) {
            log.debug("caseTypes size {}, adding to query", caseTypes.size());
            QueryBuilder typeQb = QueryBuilders.termsQuery("type", caseTypes);
            mqb.must(typeQb);
            hasClause = true;
        } else {
            log.debug("caseTypes was null or empty");
        }
        return this;
    }

    HocsQueryBuilder dateRange(DateRangeDto dateRangeDto) {
        if (dateRangeDto != null) {
            RangeQueryBuilder rangeQb = QueryBuilders.rangeQuery("dateReceived");
            if (dateRangeDto.getFrom() != null && !dateRangeDto.getFrom().isEmpty()) {
                log.debug("dateRange From {}, adding to query", dateRangeDto.getFrom());
                rangeQb.from(dateRangeDto.getFrom());
            } else {
                log.debug("dateRange From was null or empty");
            }
            if (dateRangeDto.getTo() != null && !dateRangeDto.getTo().isEmpty()) {
                log.debug("dateRange To {}, adding to query", dateRangeDto.getTo());
                rangeQb.to(dateRangeDto.getTo());
            } else {
                log.debug("dateRange To was null or empty");
            }
            if ((dateRangeDto.getFrom() != null && !dateRangeDto.getFrom().isEmpty()) || (dateRangeDto.getTo() != null && !dateRangeDto.getTo().isEmpty())) {
                mqb.must(rangeQb);
                hasClause = true;
            }
        } else {
            log.debug("dateRange was null");
        }

        return this;
    }

    HocsQueryBuilder correspondentName(String correspondentName) {
        if (StringUtils.hasText(correspondentName)) {
            log.debug("CorrespondentName {}, adding to query", correspondentName);
            QueryBuilder fullnameQb = QueryBuilders.matchQuery("currentCorrespondents.fullname", correspondentName).operator(Operator.AND);
            QueryBuilder correspondentQb = QueryBuilders.nestedQuery("currentCorrespondents", fullnameQb, ScoreMode.None);
            mqb.must(correspondentQb);
            hasClause = true;
        } else {
            log.debug("CorrespondentName was null or empty");
        }

        return this;
    }

    HocsQueryBuilder correspondentReference(String correspondentReference) {
        if (StringUtils.hasText(correspondentReference)) {
            log.debug("correspondentReference {}, adding to query", correspondentReference);
            QueryBuilder referenceQb = QueryBuilders.wildcardQuery("currentCorrespondents.reference", "*" + correspondentReference + "*");
            QueryBuilder correspondentQb = QueryBuilders.nestedQuery("currentCorrespondents", referenceQb, ScoreMode.None);
            mqb.must(correspondentQb);
            hasClause = true;
        } else {
            log.debug("correspondentReference was null or empty");
        }

        return this;
    }

    HocsQueryBuilder correspondentExternalKey(String correspondentExternalKey) {
        if (StringUtils.hasText(correspondentExternalKey)) {
            log.debug("correspondentExternalKey {}, adding to query", correspondentExternalKey);
            QueryBuilder fullnameQb = QueryBuilders.matchQuery("currentCorrespondents.externalKey", correspondentExternalKey).operator(Operator.AND);
            QueryBuilder correspondentQb = QueryBuilders.nestedQuery("currentCorrespondents", fullnameQb, ScoreMode.None);
            mqb.must(correspondentQb);
            hasClause = true;
        } else {
            log.debug("correspondentExternalKey was null or empty");
        }

        return this;
    }

    HocsQueryBuilder topic(String topicName) {
        if (topicName != null && !topicName.isEmpty()) {
            log.debug("TopicName {}, adding to query", topicName);
            QueryBuilder topicTextQb = QueryBuilders.matchQuery("currentTopics.text", topicName).operator(Operator.AND);
            QueryBuilder topicQb = QueryBuilders.nestedQuery("currentTopics", topicTextQb, ScoreMode.None);
            mqb.must(topicQb);
            hasClause = true;
        } else {
            log.debug("TopicName was null or empty");
        }
        return this;
    }

    HocsQueryBuilder dataFields(Map<String, String> data) {
        if (data != null && !data.isEmpty()) {
            log.debug("data size {}, adding to query", data.size());
            Set<QueryBuilder> dataQb = data.entrySet().stream().filter(v -> v.getValue() != null && !v.getValue().isEmpty()).map(v -> QueryBuilders.matchQuery("data." + v.getKey(), v.getValue()).operator(Operator.AND)).collect(Collectors.toSet());
            log.debug("filtered data size {}, adding to query", dataQb.size());
            for (QueryBuilder qb : dataQb) {
                mqb.must(qb);
                hasClause = true;
            }
        } else {
            log.debug("Data was null or empty");
        }
        return this;
    }

    HocsQueryBuilder activeOnlyFlag(Boolean activeOnly) {
        if (activeOnly != null && activeOnly) {
            log.debug("activeOnly is true size, adding to query");
            QueryBuilder activeQb = QueryBuilders.boolQuery().mustNot(QueryBuilders.matchQuery("completed", true));
            mqb.must(activeQb);
            hasClause = true;
        } else {
            log.debug("activeOnly was null or false");
        }
        return this;
    }

    BoolQueryBuilder build() {
        QueryBuilder deletedQb = QueryBuilders.matchQuery("deleted", false).operator(Operator.AND);
        this.mqb.must(deletedQb);
        return this.mqb;
    }

    boolean hasClauses() {
        return hasClause;
    }
}
