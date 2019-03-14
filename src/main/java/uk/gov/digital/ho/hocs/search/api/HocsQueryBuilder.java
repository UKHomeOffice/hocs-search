package uk.gov.digital.ho.hocs.search.api;

import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.*;
import uk.gov.digital.ho.hocs.search.api.dto.DateRangeDto;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
class HocsQueryBuilder {

    private BoolQueryBuilder mqb;

    HocsQueryBuilder(BoolQueryBuilder mqb) {
        this.mqb = mqb;
    }

    HocsQueryBuilder caseTypes(List<String> caseTypes) {
        if(caseTypes != null && !caseTypes.isEmpty()) {
            log.debug("caseTypes size {}, adding to query", caseTypes.size());
            QueryBuilder typeQb = QueryBuilders.termsQuery("type", caseTypes);
            mqb.must(typeQb);
        } else {
            log.debug("caseTypes was null or empty");
        }
        return this;
    }

    HocsQueryBuilder dateRange(DateRangeDto dateRangeDto) {
        if(dateRangeDto != null) {
            RangeQueryBuilder rangeQb = QueryBuilders.rangeQuery("created");
            if(dateRangeDto.getFrom() != null && !dateRangeDto.getFrom().isEmpty()) {
                log.debug("dateRange From {}, adding to query", dateRangeDto.getFrom());
                rangeQb.from(dateRangeDto.getFrom());
            } else {
                log.debug("dateRange From was null or empty");
            }
            if(dateRangeDto.getTo() != null && !dateRangeDto.getTo().isEmpty()) {
                log.debug("dateRange To {}, adding to query", dateRangeDto.getTo());
                rangeQb.to(dateRangeDto.getTo());
            } else {
                log.debug("dateRange To was null or empty");
            }
            if((dateRangeDto.getFrom() != null && !dateRangeDto.getFrom().isEmpty()) || (dateRangeDto.getTo() != null && !dateRangeDto.getTo().isEmpty())) {
                mqb.must(rangeQb);
            }
        } else {
            log.debug("dateRange was null");
        }

        return this;
    }

    HocsQueryBuilder correspondent(String correspondentName) {
        if(correspondentName != null && !correspondentName.isEmpty()) {
            log.debug("CorrespondentName {}, adding to query", correspondentName);
            QueryBuilder fullnameQb = QueryBuilders.matchQuery("currentCorrespondents.fullname", correspondentName).operator(Operator.AND);
            QueryBuilder correspondentQb = QueryBuilders.nestedQuery("currentCorrespondents", fullnameQb, ScoreMode.None);
            mqb.must(correspondentQb);
        } else {
            log.debug("CorrespondentName was null or empty");
        }

        return this;
    }

    HocsQueryBuilder topic(String topicName) {
        if(topicName != null && !topicName.isEmpty()) {
            log.debug("TopicName {}, adding to query", topicName);
            QueryBuilder topicTextQb = QueryBuilders.matchQuery("currentTopics.text", topicName).operator(Operator.AND);
            QueryBuilder topicQb = QueryBuilders.nestedQuery("currentTopics", topicTextQb, ScoreMode.None);
            mqb.must(topicQb);
        } else {
            log.debug("TopicName was null or empty");
        }
        return this;
    }

    HocsQueryBuilder dataFields(Map<String, String> data) {
        if(data != null && !data.isEmpty()) {
            log.debug("data size {}, adding to query", data.size());
            Set<QueryBuilder> dataQb = data.entrySet().stream().filter(v -> v.getValue() != null && !v.getValue().isEmpty()).map(v -> QueryBuilders.matchQuery(v.getKey(), v.getValue()).operator(Operator.AND)).collect(Collectors.toSet());
            log.debug("filtered data size {}, adding to query", dataQb.size());
            for (QueryBuilder qb : dataQb) {
                mqb.must(qb);
            }
        } else {
            log.debug("Data was null or empty");
        }
        return this;
    }

    HocsQueryBuilder activeOnlyFlag(Boolean activeOnly) {
        if(activeOnly != null && activeOnly) {
            log.debug("activeOnly is true size, adding to query");
            QueryBuilder activeQb = QueryBuilders.matchQuery("deleted", false).operator(Operator.AND);
            mqb.must(activeQb);
        } else {
            log.debug("activeOnly was null or false");
        }
        return this;
    }

    BoolQueryBuilder build(){
        return this.mqb;
    }
}
