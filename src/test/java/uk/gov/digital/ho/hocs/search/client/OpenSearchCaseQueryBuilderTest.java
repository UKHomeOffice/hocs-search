package uk.gov.digital.ho.hocs.search.client;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensearch.index.query.BoolQueryBuilder;
import uk.gov.digital.ho.hocs.search.api.dto.DateRangeDto;
import uk.gov.digital.ho.hocs.search.domain.repositories.FieldQueryTypeMappingRepository;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

@ExtendWith(MockitoExtension.class)
public class OpenSearchCaseQueryBuilderTest {

    @Mock
    private FieldQueryTypeMappingRepository fieldQueryTypeMappingRepository;

    private CaseQueryFactory caseQueryFactory;

    private BoolQueryBuilder baseQuery;

    @BeforeEach
    public void setup() {
        this.caseQueryFactory = new CaseQueryFactory(fieldQueryTypeMappingRepository);
        this.baseQuery = caseQueryFactory.createCaseQuery().build();
    }

    @Test
    public void shouldAddReferenceWithSingleCaseType() {
        String reference = "reference123";
        String caseType = "TYPE";

        BoolQueryBuilder query =
            caseQueryFactory.createCaseQuery()
                .reference(reference, caseType).build();

        assertThat(query.toString()).contains("*reference123*");
    }

    @Test
    public void shouldNotAddBlankReference() {
        BoolQueryBuilder query =
            caseQueryFactory.createCaseQuery()
                .reference("", null).build();

        assertThat(query.must().size()).isEqualTo(1);
    }

    @Test
    public void shouldNotAddNullReference() {
        BoolQueryBuilder query =
            caseQueryFactory.createCaseQuery()
                .reference(null, null).build();

        assertThat(query.must().size()).isEqualTo(1);
    }

    @Test
    public void shouldNotAddBlankType() {
        BoolQueryBuilder query =
            caseQueryFactory.createCaseQuery()
                .reference("TEST", "").build();

        assertThat(query.must().size()).isEqualTo(1);
    }

    @Test
    public void shouldNotAddNullType() {
        BoolQueryBuilder query =
            caseQueryFactory.createCaseQuery()
                .reference("TEST", null).build();

        assertThat(query.must().size()).isEqualTo(1);
    }

    @Test
    public void shouldAddCaseTypes() {
        String caseType = "ANYTYPE";
        BoolQueryBuilder query =
            caseQueryFactory.createCaseQuery()
                .caseTypes(List.of(caseType)).build();

        assertThat(query.toString()).contains(caseType);
    }

    @Test
    public void shouldNotAddNoCaseTypes() {
        BoolQueryBuilder query =
            caseQueryFactory.createCaseQuery()
                .caseTypes(List.of()).build();

        assertThat(query.must().size()).isEqualTo(1);
    }

    @Test
    public void shouldNotAddNullCaseTypes() {
        BoolQueryBuilder query =
            caseQueryFactory.createCaseQuery()
                .caseTypes(null).build();

        assertThat(query.must().size()).isEqualTo(1);
    }

    @Test
    public void shouldAddCorrespondentAddress1() {
        String correspondentAddress1 = "Address1";

        BoolQueryBuilder query =
            caseQueryFactory.createCaseQuery()
                .correspondentAddress1(correspondentAddress1).build();

        assertThat(query.toString()).contains(correspondentAddress1);
    }

    @Test
    public void shouldNotAddNoCorrespondentAddress1() {
        BoolQueryBuilder query =
            caseQueryFactory.createCaseQuery()
                .correspondentAddress1("").build();

        assertThat(query.must().size()).isEqualTo(1);
    }

    @Test
    public void shouldNotAddNullCorrespondentAddress1() {
        BoolQueryBuilder query =
            caseQueryFactory.createCaseQuery()
                .correspondentAddress1(null).build();

        assertThat(query.must().size()).isEqualTo(1);
    }

    @Test
    public void shouldAddCorrespondentEmail() {
        String correspondentEmail = "EMAIL";

        BoolQueryBuilder query =
            caseQueryFactory.createCaseQuery()
                .correspondentEmail(correspondentEmail).build();

        assertThat(query.toString()).contains(correspondentEmail);
    }

    @Test
    public void shouldNotAddNoCorrespondentEmail() {
        BoolQueryBuilder query =
            caseQueryFactory.createCaseQuery()
                .correspondentEmail("").build();

        assertThat(query.must().size()).isEqualTo(1);
    }

    @Test
    public void shouldNotAddNullCorrespondentEmail() {
        BoolQueryBuilder query =
            caseQueryFactory.createCaseQuery()
                .correspondentEmail(null).build();

        assertThat(query.must().size()).isEqualTo(1);
    }

    @Test
    public void shouldAddCorrespondentName() {
        String correspondentName = "MYNAME";

        BoolQueryBuilder query =
            caseQueryFactory.createCaseQuery()
                .correspondentName(correspondentName).build();

        assertThat(query.toString()).contains(correspondentName);
    }

    @Test
    public void shouldNotAddNoCorrespondentName() {
        BoolQueryBuilder query =
            caseQueryFactory.createCaseQuery()
                .correspondentName("").build();

        assertThat(query.must().size()).isEqualTo(1);
    }

    @Test
    public void shouldNotAddNullCorrespondentName() {
        BoolQueryBuilder query =
            caseQueryFactory.createCaseQuery()
                .correspondentName(null).build();

        assertThat(query.must().size()).isEqualTo(1);
    }

    @Test
    public void shouldAddCorrespondentNameNotMember() {
        String correspondentNameNotMember = "BOB";

        BoolQueryBuilder query =
            caseQueryFactory.createCaseQuery()
                .correspondentNameNotMember(correspondentNameNotMember).build();

        assertThat(query.toString()).contains(correspondentNameNotMember);
        assertThat(query.toString()).contains("must_not");
        assertThat(query.toString()).contains("MEMBER");
    }

    @Test
    public void shouldAddCorrespondentPostcode() {
        String correspondentPostcode = "Postcode";

        BoolQueryBuilder query =
            caseQueryFactory.createCaseQuery()
                .correspondentPostcode(correspondentPostcode).build();

        assertThat(query.toString()).contains(correspondentPostcode);
    }

    @Test
    public void shouldNotAddNoCorrespondentPostcode() {
        BoolQueryBuilder query =
            caseQueryFactory.createCaseQuery()
                .correspondentPostcode("").build();

        assertThat(query.must().size()).isEqualTo(1);
    }

    @Test
    public void shouldNotAddNullCorrespondentPostcode() {
        BoolQueryBuilder query =
            caseQueryFactory.createCaseQuery()
                .correspondentPostcode(null).build();

        assertThat(query.must().size()).isEqualTo(1);
    }

    @Test
    public void shouldAddCorrespondentReference() {
        String correspondentReference = "MYReference";

        BoolQueryBuilder query =
            caseQueryFactory.createCaseQuery()
                .correspondentReference(correspondentReference).build();

        assertThat(query.toString()).contains(correspondentReference);
    }

    @Test
    public void shouldNotAddNoCorrespondentReference() {
        BoolQueryBuilder query =
            caseQueryFactory.createCaseQuery()
                .correspondentReference("").build();

        assertThat(query.must().size()).isEqualTo(1);
    }

    @Test
    public void shouldNotAddNullCorrespondentReference() {
        BoolQueryBuilder query =
            caseQueryFactory.createCaseQuery()
                .correspondentReference(null).build();

        assertThat(query.must().size()).isEqualTo(1);
    }

    @Test
    public void shouldAddCorrespondentExternalKey() {
        String correspondentExternalKey = "MYExternalKey";

        BoolQueryBuilder query =
            caseQueryFactory.createCaseQuery()
                .correspondentExternalKey(correspondentExternalKey).build();

        assertThat(query.toString()).contains(correspondentExternalKey);
    }

    @Test
    public void shouldNotAddNoCorrespondentExternalKey() {
        BoolQueryBuilder query =
            caseQueryFactory.createCaseQuery()
                .correspondentExternalKey("").build();

        assertThat(query.must().size()).isEqualTo(1);
    }

    @Test
    public void shouldNotAddNullCorrespondentExternalKey() {
        BoolQueryBuilder query =
            caseQueryFactory.createCaseQuery()
                .correspondentExternalKey(null).build();

        assertThat(query.must().size()).isEqualTo(1);
    }

    @Test
    public void shouldAddTopic() {
        String topic = "MYNAME";

        BoolQueryBuilder query =
            caseQueryFactory.createCaseQuery()
                .topic(topic).build();

        assertThat(query.toString()).contains(topic);
    }

    @Test
    public void shouldNotAddNoTopic() {
        BoolQueryBuilder query =
            caseQueryFactory.createCaseQuery()
                .topic("").build();

        assertThat(query.must().size()).isEqualTo(1);
    }

    @Test
    public void shouldNotAddNullTopic() {
        BoolQueryBuilder query =
            caseQueryFactory.createCaseQuery()
                .topic(null).build();

        assertThat(query.must().size()).isEqualTo(1);
    }

    @Test
    public void shouldAddData() {
        Map<String, String> data = new HashMap<>();
        data.put("dataKey", "dataValue");

        BoolQueryBuilder query =
            caseQueryFactory.createCaseQuery()
                .dataFields(data).build();

        assertThat(query.toString()).contains("data.dataKey");
        assertThat(query.toString()).contains("dataValue");
    }

    @Test
    public void shouldAddWildcardData() {
        Map<String, String> data = new HashMap<>();
        data.put("dataKey", "dataValue");
        Mockito.when(fieldQueryTypeMappingRepository.getQueryTypeByFieldLabel("dataKey")).thenReturn("wildcard");

        BoolQueryBuilder query =
            caseQueryFactory.createCaseQuery()
                .dataFields(data).build();

        assertThat(query.toString()).contains("wildcard");
        assertThat(query.toString()).contains("data.dataKey");
        assertThat(query.toString()).contains("dataValue");
    }

    @Test
    public void shouldNotAddNoData() {
        Map<String, String> data = new HashMap<>();

        BoolQueryBuilder query =
            caseQueryFactory.createCaseQuery()
                .dataFields(data).build();

        assertThat(query.must().size()).isEqualTo(1);
    }

    @Test
    public void shouldNotAddNullData() {
        BoolQueryBuilder query =
            caseQueryFactory.createCaseQuery()
                .dataFields(null).build();

        assertThat(query.must().size()).isEqualTo(1);
    }

    @Test
    public void shouldAddActiveFalse() {
        BoolQueryBuilder query =
            caseQueryFactory.createCaseQuery()
                .activeOnlyFlag(false).build();

        assertThat(query.must().size()).isEqualTo(1);

    }

    @Test
    public void shouldAddActiveTrue() {
        BoolQueryBuilder query =
            caseQueryFactory.createCaseQuery()
                .activeOnlyFlag(true).build();

        assertThat(query.toString()).contains("completed");
        assertThat(query.toString()).contains("true");
    }

    @Test
    public void shouldNotAddActive() {
        BoolQueryBuilder query =
            caseQueryFactory.createCaseQuery()
                .activeOnlyFlag(null).build();

        assertThat(query.must().size()).isEqualTo(1);
    }

    @Test
    public void shouldAddDateRange() {
        DateRangeDto dateRangeDto = new DateRangeDto("fromDate", "toDate");

        BoolQueryBuilder query =
            caseQueryFactory.createCaseQuery()
                .dateRange(dateRangeDto).build();

        assertThat(query.toString()).contains("fromDate");
        assertThat(query.toString()).contains("toDate");
    }

    @Test
    public void shouldAddDateRangeFromEmpty() {
        DateRangeDto dateRangeDto = new DateRangeDto("", "toDate");

        BoolQueryBuilder query =
            caseQueryFactory.createCaseQuery()
                .dateRange(dateRangeDto).build();

        assertThat(query.toString()).contains("toDate");
    }

    @Test
    public void shouldAddDateRangeFromNull() {
        DateRangeDto dateRangeDto = new DateRangeDto(null, "toDate");

        BoolQueryBuilder query =
            caseQueryFactory.createCaseQuery()
                .dateRange(dateRangeDto).build();

        assertThat(query.toString()).contains("toDate");
    }

    @Test
    public void shouldAddDateRangeToEmpty() {
        DateRangeDto dateRangeDto = new DateRangeDto("fromDate", "");

        BoolQueryBuilder query =
            caseQueryFactory.createCaseQuery()
                .dateRange(dateRangeDto).build();

        assertThat(query.toString()).contains("fromDate");
    }

    @Test
    public void shouldAddDateRangeToNull() {
        DateRangeDto dateRangeDto = new DateRangeDto("fromDate", null);

        BoolQueryBuilder query =
            caseQueryFactory.createCaseQuery()
                .dateRange(dateRangeDto).build();

        assertThat(query.toString()).contains("fromDate");
    }

    @Test
    public void shouldNotAddDateRangeActive() {
        BoolQueryBuilder query =
            caseQueryFactory.createCaseQuery()
                .dateRange(null).build();

        assertThat(query.must().size()).isEqualTo(1);
    }

    @Test
    public void shouldAddPrivateOfficeTeam() {
        final String privateOfficeTeamUuid = UUID.randomUUID().toString();
        final List<Map<String, String>> privateOfficeJsonPaths = List.of(
            Map.of("$['bool']['must'][0]['match']['data.PrivateOfficeOverridePOTeamUUID']['query']",
                privateOfficeTeamUuid),
            Map.of("$['bool']['must'][0]['match']['data.OverridePOTeamUUID']['query']", privateOfficeTeamUuid,
                "$['bool']['must_not'][0]['exists']['field']", "data.PrivateOfficeOverridePOTeamUUID"),
            Map.of("$['bool']['must'][0]['match']['data.OverridePOTeamUUID']['query']", privateOfficeTeamUuid,
                "$['bool']['must_not'][0]['wildcard']['data.PrivateOfficeOverridePOTeamUUID']['wildcard']", "*"),
            Map.of("$['bool']['must'][0]['match']['data.POTeamUUID']['query']", privateOfficeTeamUuid,
                "$['bool']['must_not'][0]['exists']['field']", "data.PrivateOfficeOverridePOTeamUUID",
                "$['bool']['must_not'][1]['exists']['field']", "data.OverridePOTeamUUID"),
            Map.of("$['bool']['must'][0]['match']['data.POTeamUUID']['query']", privateOfficeTeamUuid,
                "$['bool']['must_not'][0]['exists']['field']", "data.PrivateOfficeOverridePOTeamUUID",
                "$['bool']['must_not'][1]['wildcard']['data.OverridePOTeamUUID']['wildcard']", "*"),
            Map.of("$['bool']['must'][0]['match']['data.POTeamUUID']['query']", privateOfficeTeamUuid,
                "$['bool']['must_not'][0]['wildcard']['data.PrivateOfficeOverridePOTeamUUID']['wildcard']", "*",
                "$['bool']['must_not'][1]['exists']['field']", "data.OverridePOTeamUUID"),
            Map.of("$['bool']['must'][0]['match']['data.POTeamUUID']['query']", privateOfficeTeamUuid,
                "$['bool']['must_not'][0]['wildcard']['data.PrivateOfficeOverridePOTeamUUID']['wildcard']", "*",
                "$['bool']['must_not'][1]['wildcard']['data.OverridePOTeamUUID']['wildcard']", "*"));

        BoolQueryBuilder query = caseQueryFactory.createCaseQuery()
            .privateOfficeTeam(privateOfficeTeamUuid).build();

        assertThat(query.must()).hasSize(2);

        BoolQueryBuilder boolQueryBuilder = (BoolQueryBuilder) query.must().get(0);

        // Private office has 7 underlying should queries
        assertThat(boolQueryBuilder.should()).hasSize(7);
        for (int i = 0; i < boolQueryBuilder.should().size(); i++) {
            BoolQueryBuilder shouldQuery = (BoolQueryBuilder) boolQueryBuilder.should().get(i);

            Map<String, String> jsonPaths = privateOfficeJsonPaths.get(i);

            for (Map.Entry<String, String> entry : jsonPaths.entrySet()) {
                assertThatJson(shouldQuery.toString()).inPath(entry.getKey()).isString().isEqualTo(entry.getValue());
            }
        }
    }

    @Test
    public void shouldNotAddEmptyPrivateOfficeTeam() {
        BoolQueryBuilder query =
            caseQueryFactory.createCaseQuery()
                .privateOfficeTeam("").build();

        assertThat(query.must().size()).isEqualTo(1);
    }

    @Test
    public void shouldNotAddNullPrivateOfficeTeam() {
        BoolQueryBuilder query =
            caseQueryFactory.createCaseQuery()
                .privateOfficeTeam(null).build();

        assertThat(query.must().size()).isEqualTo(1);
    }

}
