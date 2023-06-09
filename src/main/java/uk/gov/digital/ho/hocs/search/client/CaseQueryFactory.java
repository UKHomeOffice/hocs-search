package uk.gov.digital.ho.hocs.search.client;

import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.Operator;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.index.query.RangeQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import uk.gov.digital.ho.hocs.search.api.dto.DateRangeDto;
import uk.gov.digital.ho.hocs.search.domain.repositories.FieldQueryTypeMappingRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static uk.gov.digital.ho.hocs.search.client.helpers.QueryBuilderHelpers.fieldExists;
import static uk.gov.digital.ho.hocs.search.client.helpers.QueryBuilderHelpers.nonEmptyField;

@Slf4j
@Service
public class CaseQueryFactory {

    private final FieldQueryTypeMappingRepository fieldQueryTypeMappingRepository;

    public CaseQueryFactory(FieldQueryTypeMappingRepository fieldQueryTypeMappingRepository) {
        this.fieldQueryTypeMappingRepository = fieldQueryTypeMappingRepository;
    }

    public CaseQuery createCaseQuery() {
        return new CaseQuery(fieldQueryTypeMappingRepository);
    }

    public static class CaseQuery {

        private final BoolQueryBuilder mqb;

        private final FieldQueryTypeMappingRepository fieldQueryTypeMappingRepository;

        private final List<String> migratedCaseTypeMappings = List.of("comp", "comp2", "bf", "bf2", "pogr", "pogr2", "iedet", "to");

        private boolean hasClause = false;

        public CaseQuery(FieldQueryTypeMappingRepository fieldQueryTypeMappingRepository) {
            this.mqb = QueryBuilders.boolQuery();
            this.fieldQueryTypeMappingRepository = fieldQueryTypeMappingRepository;
        }

        public CaseQuery reference(String reference, String type) {
            if (!StringUtils.hasText(reference)) {
                log.debug("reference was null or empty");
                return this;
            }

            if (!StringUtils.hasText(type)) {
                log.debug("type was null or empty");
                return this;
            }

            log.debug("reference {} , adding to query", reference);

            QueryBuilder typeQb =
                QueryBuilders.wildcardQuery("reference", String.format("*%s*", reference));

            if (migratedCaseTypeMappings.contains(type.toLowerCase())) {
                typeQb = QueryBuilders.boolQuery()
                    .should(typeQb)
                    .should(QueryBuilders.matchQuery("migratedReference", reference));
            }

            mqb.must(typeQb);
            hasClause = true;
            return this;
        }

        public CaseQuery caseTypes(List<String> caseTypes) {
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

        public CaseQuery dateRange(DateRangeDto dateRangeDto) {
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

        public CaseQuery correspondentAddress1(String correspondentAddress1) {
            if (StringUtils.hasText(correspondentAddress1)) {
                log.debug("CorrespondentAddress {}, adding to query", correspondentAddress1);
                QueryBuilder addressQb = QueryBuilders.matchQuery("currentCorrespondents.address1",
                    correspondentAddress1).operator(Operator.AND);
                QueryBuilder correspondentQb = QueryBuilders.nestedQuery("currentCorrespondents", addressQb,
                    ScoreMode.None);
                mqb.must(correspondentQb);
                hasClause = true;
            } else {
                log.debug("CorrespondentAddress was null or empty");
            }

            return this;
        }

        public CaseQuery correspondentEmail(String correspondentEmail) {
            if (StringUtils.hasText(correspondentEmail)) {
                log.debug("CorrespondentEmail {}, adding to query", correspondentEmail);
                QueryBuilder emailQb = QueryBuilders.matchQuery("currentCorrespondents.email", correspondentEmail).operator(
                    Operator.AND);
                QueryBuilder correspondentQb = QueryBuilders.nestedQuery("currentCorrespondents", emailQb, ScoreMode.None);
                mqb.must(correspondentQb);
                hasClause = true;
            } else {
                log.debug("CorrespondentEmail was null or empty");
            }

            return this;
        }

        public CaseQuery correspondentName(String correspondentName) {
            if (StringUtils.hasText(correspondentName)) {
                log.debug("CorrespondentName {}, adding to query", correspondentName);
                QueryBuilder fullnameQb = QueryBuilders.matchQuery("currentCorrespondents.fullname",
                    correspondentName).operator(Operator.AND);
                QueryBuilder correspondentQb = QueryBuilders.nestedQuery("currentCorrespondents", fullnameQb,
                    ScoreMode.None);
                mqb.must(correspondentQb);
                hasClause = true;
            } else {
                log.debug("CorrespondentName was null or empty");
            }

            return this;
        }

        public CaseQuery correspondentNameNotMember(String correspondentNameNotMember) {
            if (StringUtils.hasText(correspondentNameNotMember)) {
                log.debug("CorrespondentNameNotMember {}, adding to query", correspondentNameNotMember);
                QueryBuilder fullnameQb = QueryBuilders.matchQuery("currentCorrespondents.fullname",
                    correspondentNameNotMember).operator(Operator.AND);
                QueryBuilder typeQb = QueryBuilders.matchQuery("currentCorrespondents.type", "MEMBER").operator(
                    Operator.AND);

                BoolQueryBuilder correspondentBqb = new BoolQueryBuilder();
                correspondentBqb.must(fullnameQb);
                correspondentBqb.mustNot(typeQb);

                QueryBuilder correspondentQb = QueryBuilders.nestedQuery("currentCorrespondents", correspondentBqb,
                    ScoreMode.None);
                mqb.must(correspondentQb);
                hasClause = true;
            } else {
                log.debug("CorrespondentNameNotMember was null or empty");
            }

            return this;
        }

        public CaseQuery correspondentPostcode(String correspondentPostcode) {
            if (StringUtils.hasText(correspondentPostcode)) {
                log.debug("CorrespondentPostcode {}, adding to query", correspondentPostcode);
                QueryBuilder postcodeQb = QueryBuilders.matchQuery("currentCorrespondents.postcode",
                    correspondentPostcode).operator(Operator.AND);
                QueryBuilder correspondentQb = QueryBuilders.nestedQuery("currentCorrespondents", postcodeQb,
                    ScoreMode.None);
                mqb.must(correspondentQb);
                hasClause = true;
            } else {
                log.debug("CorrespondentPostcode was null or empty");
            }

            return this;
        }

        public CaseQuery correspondentReference(String correspondentReference) {
            if (StringUtils.hasText(correspondentReference)) {
                log.debug("correspondentReference {}, adding to query", correspondentReference);
                QueryBuilder referenceQb = QueryBuilders.wildcardQuery("currentCorrespondents.reference",
                    "*" + correspondentReference + "*");
                QueryBuilder correspondentQb = QueryBuilders.nestedQuery("currentCorrespondents", referenceQb,
                    ScoreMode.None);
                mqb.must(correspondentQb);
                hasClause = true;
            } else {
                log.debug("correspondentReference was null or empty");
            }

            return this;
        }

        public CaseQuery correspondentExternalKey(String correspondentExternalKey) {
            if (StringUtils.hasText(correspondentExternalKey)) {
                log.debug("correspondentExternalKey {}, adding to query", correspondentExternalKey);
                QueryBuilder fullnameQb = QueryBuilders.matchQuery("currentCorrespondents.externalKey",
                    correspondentExternalKey).operator(Operator.AND);
                QueryBuilder correspondentQb = QueryBuilders.nestedQuery("currentCorrespondents", fullnameQb,
                    ScoreMode.None);
                mqb.must(correspondentQb);
                hasClause = true;
            } else {
                log.debug("correspondentExternalKey was null or empty");
            }

            return this;
        }

        public CaseQuery topic(String topicName) {
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

        public CaseQuery privateOfficeTeam(String privateOfficeTeam) {
            if (privateOfficeTeam != null && !privateOfficeTeam.isEmpty()) {
                log.debug("Private office team {}, adding to query", privateOfficeTeam);

                BoolQueryBuilder[] privateOfficeQueries = { QueryBuilders.boolQuery().must(
                    QueryBuilders.matchQuery("data.PrivateOfficeOverridePOTeamUUID", privateOfficeTeam).operator(
                        Operator.AND)),
                    QueryBuilders.boolQuery().mustNot(fieldExists("data.PrivateOfficeOverridePOTeamUUID")).must(
                        QueryBuilders.matchQuery("data.OverridePOTeamUUID", privateOfficeTeam).operator(Operator.AND)),
                    QueryBuilders.boolQuery().mustNot(nonEmptyField("data.PrivateOfficeOverridePOTeamUUID")).must(
                        QueryBuilders.matchQuery("data.OverridePOTeamUUID", privateOfficeTeam).operator(Operator.AND)),
                    QueryBuilders.boolQuery().mustNot(fieldExists("data.PrivateOfficeOverridePOTeamUUID")).mustNot(
                        fieldExists("data.OverridePOTeamUUID")).must(
                        QueryBuilders.matchQuery("data.POTeamUUID", privateOfficeTeam).operator(Operator.AND)),
                    QueryBuilders.boolQuery().mustNot(fieldExists("data.PrivateOfficeOverridePOTeamUUID")).mustNot(
                        nonEmptyField("data.OverridePOTeamUUID")).must(
                        QueryBuilders.matchQuery("data.POTeamUUID", privateOfficeTeam).operator(Operator.AND)),
                    QueryBuilders.boolQuery().mustNot(nonEmptyField("data.PrivateOfficeOverridePOTeamUUID")).mustNot(
                        fieldExists("data.OverridePOTeamUUID")).must(
                        QueryBuilders.matchQuery("data.POTeamUUID", privateOfficeTeam).operator(Operator.AND)),
                    QueryBuilders.boolQuery().mustNot(nonEmptyField("data.PrivateOfficeOverridePOTeamUUID")).mustNot(
                        nonEmptyField("data.OverridePOTeamUUID")).must(
                        QueryBuilders.matchQuery("data.POTeamUUID", privateOfficeTeam).operator(Operator.AND)) };

                BoolQueryBuilder privateOfficeFilter = new BoolQueryBuilder();
                Arrays.stream(privateOfficeQueries).forEach(privateOfficeFilter::should);

                mqb.must(privateOfficeFilter);
                hasClause = true;
            } else {
                log.debug("Private office team was null or empty");
            }
            return this;
        }

        public CaseQuery dataFields(Map<String, String> data) {
            if (data != null && !data.isEmpty()) {
                log.debug("data size {}, adding to query", data.size());

                Map<String, String> dataMap = data.entrySet().stream().filter(v -> v.getValue() != null && !v.getValue().isEmpty()).collect(Collectors.toMap(v -> v.getKey(), v -> v.getValue()));
                Set<QueryBuilder> dataQb = new HashSet<>();
                for (Map.Entry<String, String> entry : dataMap.entrySet()) {
                    String fieldQueryType = fieldQueryTypeMappingRepository.getQueryTypeByFieldLabel(entry.getKey());
                    if (fieldQueryType != null && fieldQueryType.equals("wildcard")) {
                        dataQb.add(QueryBuilders.wildcardQuery("data." + entry.getKey(), String.format("*%s*", entry.getValue())));
                    } else {
                        dataQb.add(QueryBuilders.matchQuery("data." + entry.getKey(), entry.getValue()).operator(Operator.AND));
                    }
                }

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

        public CaseQuery activeOnlyFlag(Boolean activeOnly) {
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

        public BoolQueryBuilder build() {
            QueryBuilder deletedQb = QueryBuilders.boolQuery().mustNot(QueryBuilders.matchQuery("deleted", true).operator(Operator.AND));
            this.mqb.must(deletedQb);
            return this.mqb;
        }

        public boolean hasClauses() {
            return hasClause;
        }


    }

}
