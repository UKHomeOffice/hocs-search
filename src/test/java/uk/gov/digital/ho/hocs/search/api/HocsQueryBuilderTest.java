package uk.gov.digital.ho.hocs.search.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import uk.gov.digital.ho.hocs.search.api.dto.DateRangeDto;
import uk.gov.digital.ho.hocs.search.domain.repositories.FieldQueryTypeMappingRepository;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class HocsQueryBuilderTest {

    private BoolQueryBuilder bqb;

    @Mock
    private FieldQueryTypeMappingRepository fieldQueryTypeMappingRepository;

    @BeforeEach
    public void setup() {
        this.bqb = Mockito.spy(QueryBuilders.boolQuery());
    }

    @Test
    public void shouldAddReference() {
        String reference = "reference123";

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb, fieldQueryTypeMappingRepository);
        hocsQueryBuilder.reference(reference, null);

        Mockito.verify(bqb).must(any(QueryBuilder.class));
        Mockito.verifyNoMoreInteractions(bqb);

        assertThat(bqb.toString()).contains(reference);
    }

    @Test
    public void shouldAddNumericReferenceWithSingleCaseType() {
        String reference = "123";
        List<String> caseTypes = Collections.singletonList("TYPE");

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb, fieldQueryTypeMappingRepository);
        hocsQueryBuilder.reference(reference, caseTypes);

        Mockito.verify(bqb).must(any(QueryBuilder.class));
        Mockito.verifyNoMoreInteractions(bqb);

        assertThat(bqb.toString()).contains("TYPE/*123*");
    }

    @Test
    public void shouldAddNonNumericReferenceWithSingleCaseType() {
        String reference = "reference123";
        List<String> caseTypes = Collections.singletonList("TYPE");

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb, fieldQueryTypeMappingRepository);
        hocsQueryBuilder.reference(reference, caseTypes);

        Mockito.verify(bqb).must(any(QueryBuilder.class));
        Mockito.verifyNoMoreInteractions(bqb);

        assertThat(bqb.toString()).contains("*reference123*");
        assertThat(bqb.toString()).doesNotContain("TYPE/*reference123*");
    }

    @Test
    public void shouldAddReferenceWithMultipleCaseTypes() {
        String reference = "reference123";
        List<String> caseTypes = Arrays.asList("TYPE", "TYPE2");

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb, fieldQueryTypeMappingRepository);
        hocsQueryBuilder.reference(reference, caseTypes);

        Mockito.verify(bqb).must(any(QueryBuilder.class));
        Mockito.verifyNoMoreInteractions(bqb);

        assertThat(bqb.toString()).contains("*reference123*");
        assertThat(bqb.toString()).doesNotContain("TYPE/*reference123*");
    }

    @Test
    public void shouldNotAddBlankReference() {
        String reference = "";

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb, fieldQueryTypeMappingRepository);
        hocsQueryBuilder.reference(reference, null);

        Mockito.verifyNoMoreInteractions(bqb);
    }

    @Test
    public void shouldNotAddNullReference() {
        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb, fieldQueryTypeMappingRepository);
        hocsQueryBuilder.reference(null, null);

        Mockito.verifyNoMoreInteractions(bqb);
    }

    @Test
    public void shouldAddCaseTypes() {
        List<String> caseTypes = new ArrayList<>();
        caseTypes.add("ANYTYPE");

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb, fieldQueryTypeMappingRepository);
        hocsQueryBuilder.caseTypes(caseTypes);

        Mockito.verify(bqb).must(any(QueryBuilder.class));

        assertThat(bqb.toString()).contains("ANYTYPE");
    }

    @Test
    public void shouldNotAddNoCaseTypes() {
        List<String> caseTypes = new ArrayList<>();

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb, fieldQueryTypeMappingRepository);
        hocsQueryBuilder.caseTypes(caseTypes);

        Mockito.verifyNoMoreInteractions(bqb);
    }

    @Test
    public void shouldNotAddNullCaseTypes() {

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb, fieldQueryTypeMappingRepository);
        hocsQueryBuilder.caseTypes(null);

        Mockito.verifyNoMoreInteractions(bqb);
    }

    @Test
    public void shouldAddCorrespondentAddress1() {
        String correspondentAddress1 = "Address1";

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb, fieldQueryTypeMappingRepository);
        hocsQueryBuilder.correspondentAddress1(correspondentAddress1);

        Mockito.verify(bqb).must(any(QueryBuilder.class));

        assertThat(bqb.toString()).contains("Address1");
    }

    @Test
    public void shouldNotAddNoCorrespondentAddress1() {
        String correspondentAddress1 = "";

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb, fieldQueryTypeMappingRepository);
        hocsQueryBuilder.correspondentAddress1(correspondentAddress1);

        Mockito.verifyNoMoreInteractions(bqb);
    }

    @Test
    public void shouldNotAddNullCorrespondentAddress1() {

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb, fieldQueryTypeMappingRepository);
        hocsQueryBuilder.correspondentAddress1(null);

        Mockito.verifyNoMoreInteractions(bqb);
    }

    @Test
    public void shouldAddCorrespondentEmail() {
        String correspondentEmail = "EMAIL";

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb, fieldQueryTypeMappingRepository);
        hocsQueryBuilder.correspondentEmail(correspondentEmail);

        Mockito.verify(bqb).must(any(QueryBuilder.class));

        assertThat(bqb.toString()).contains("EMAIL");
    }

    @Test
    public void shouldNotAddNoCorrespondentEmail() {
        String correspondentEmail = "";

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb, fieldQueryTypeMappingRepository);
        hocsQueryBuilder.correspondentEmail(correspondentEmail);

        Mockito.verifyNoMoreInteractions(bqb);
    }

    @Test
    public void shouldNotAddNullCorrespondentEmail() {

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb, fieldQueryTypeMappingRepository);
        hocsQueryBuilder.correspondentEmail(null);

        Mockito.verifyNoMoreInteractions(bqb);
    }

    @Test
    public void shouldAddCorrespondentName() {
        String correspondentName = "MYNAME";

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb, fieldQueryTypeMappingRepository);
        hocsQueryBuilder.correspondentName(correspondentName);

        Mockito.verify(bqb).must(any(QueryBuilder.class));

        assertThat(bqb.toString()).contains("MYNAME");
    }

    @Test
    public void shouldNotAddNoCorrespondentName() {
        String correspondentName = "";

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb, fieldQueryTypeMappingRepository);
        hocsQueryBuilder.correspondentName(correspondentName);

        Mockito.verifyNoMoreInteractions(bqb);
    }

    @Test
    public void shouldNotAddNullCorrespondentName() {

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb, fieldQueryTypeMappingRepository);
        hocsQueryBuilder.correspondentName(null);

        Mockito.verifyNoMoreInteractions(bqb);
    }

    @Test
    public void shouldAddCorrespondentNameNotMember() {
        String correspondentNameNotMember = "BOB";

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb, fieldQueryTypeMappingRepository);
        hocsQueryBuilder.correspondentNameNotMember(correspondentNameNotMember);

        Mockito.verify(bqb).must(any(QueryBuilder.class));
        assertThat(bqb.toString()).contains("BOB");
        assertThat(bqb.toString()).contains("must_not");
        assertThat(bqb.toString()).contains("MEMBER");
    }

    @Test
    public void shouldAddCorrespondentPostcode() {
        String correspondentPostcode = "Postcode";

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb, fieldQueryTypeMappingRepository);
        hocsQueryBuilder.correspondentPostcode(correspondentPostcode);

        Mockito.verify(bqb).must(any(QueryBuilder.class));

        assertThat(bqb.toString()).contains("Postcode");
    }

    @Test
    public void shouldNotAddNoCorrespondentPostcode() {
        String correspondentPostcode = "";

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb, fieldQueryTypeMappingRepository);
        hocsQueryBuilder.correspondentPostcode(correspondentPostcode);

        Mockito.verifyNoMoreInteractions(bqb);
    }

    @Test
    public void shouldNotAddNullCorrespondentPostcode() {

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb, fieldQueryTypeMappingRepository);
        hocsQueryBuilder.correspondentPostcode(null);

        Mockito.verifyNoMoreInteractions(bqb);
    }

    @Test
    public void shouldAddCorrespondentReference() {
        String correspondentReference = "MYReference";

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb, fieldQueryTypeMappingRepository);
        hocsQueryBuilder.correspondentReference(correspondentReference);

        Mockito.verify(bqb).must(any(QueryBuilder.class));

        assertThat(bqb.toString()).contains("MYReference");
    }

    @Test
    public void shouldNotAddNoCorrespondentReference() {
        String correspondentReference = "";

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb, fieldQueryTypeMappingRepository);
        hocsQueryBuilder.correspondentReference(correspondentReference);

        Mockito.verifyNoMoreInteractions(bqb);
    }

    @Test
    public void shouldNotAddNullCorrespondentReference() {

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb, fieldQueryTypeMappingRepository);
        hocsQueryBuilder.correspondentReference(null);

        Mockito.verifyNoMoreInteractions(bqb);
    }

    @Test
    public void shouldAddCorrespondentExternalKey() {
        String correspondentExternalKey = "MYExternalKey";

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb, fieldQueryTypeMappingRepository);
        hocsQueryBuilder.correspondentExternalKey(correspondentExternalKey);

        Mockito.verify(bqb).must(any(QueryBuilder.class));

        assertThat(bqb.toString()).contains("MYExternalKey");
    }

    @Test
    public void shouldNotAddNoCorrespondentExternalKey() {
        String correspondentExternalKey = "";

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb, fieldQueryTypeMappingRepository);
        hocsQueryBuilder.correspondentExternalKey(correspondentExternalKey);

        Mockito.verifyNoMoreInteractions(bqb);
    }

    @Test
    public void shouldNotAddNullCorrespondentExternalKey() {

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb, fieldQueryTypeMappingRepository);
        hocsQueryBuilder.correspondentExternalKey(null);

        Mockito.verifyNoMoreInteractions(bqb);
    }

    @Test
    public void shouldAddTopic() {
        String topic = "MYNAME";

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb, fieldQueryTypeMappingRepository);
        hocsQueryBuilder.topic(topic);

        Mockito.verify(bqb).must(any(QueryBuilder.class));

        assertThat(bqb.toString()).contains("MYNAME");
    }

    @Test
    public void shouldNotAddNoTopic() {
        String topic = "";

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb, fieldQueryTypeMappingRepository);
        hocsQueryBuilder.topic(topic);

        Mockito.verifyNoMoreInteractions(bqb);
    }

    @Test
    public void shouldNotAddNullTopic() {

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb, fieldQueryTypeMappingRepository);
        hocsQueryBuilder.topic(null);

        Mockito.verifyNoMoreInteractions(bqb);
    }

    @Test
    public void shouldAddData() {
        Map<String, String> data = new HashMap<>();
        data.put("dataKey", "dataValue");

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb, fieldQueryTypeMappingRepository);
        hocsQueryBuilder.dataFields(data);

        Mockito.verify(bqb).must(any(QueryBuilder.class));

        assertThat(bqb.toString()).contains("data.dataKey");
        assertThat(bqb.toString()).contains("dataValue");
    }

    @Test
    public void shouldAddWildcardData() {
        Map<String, String> data = new HashMap<>();
        data.put("dataKey", "dataValue");
        Mockito.when(fieldQueryTypeMappingRepository.getQueryTypeByFieldLabel("dataKey")).thenReturn("wildcard");

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb, fieldQueryTypeMappingRepository);
        hocsQueryBuilder.dataFields(data);

        Mockito.verify(bqb).must(any(QueryBuilder.class));

        assertThat(bqb.toString()).contains("wildcard");
        assertThat(bqb.toString()).contains("data.dataKey");
        assertThat(bqb.toString()).contains("dataValue");
    }

    @Test
    public void shouldNotAddNoData() {
        Map<String, String> data = new HashMap<>();

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb, fieldQueryTypeMappingRepository);
        hocsQueryBuilder.dataFields(data);

        Mockito.verifyNoMoreInteractions(bqb);
    }

    @Test
    public void shouldNotAddNullData() {

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb, fieldQueryTypeMappingRepository);
        hocsQueryBuilder.dataFields(null);

        Mockito.verifyNoMoreInteractions(bqb);
    }

    @Test
    public void shouldAddActiveFalse() {
        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb, fieldQueryTypeMappingRepository);
        hocsQueryBuilder.activeOnlyFlag(false);

        Mockito.verifyNoMoreInteractions(bqb);
    }

    @Test
    public void shouldAddActiveTrue() {
        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb, fieldQueryTypeMappingRepository);
        hocsQueryBuilder.activeOnlyFlag(true);

        Mockito.verify(bqb).must(any(QueryBuilder.class));
    }

    @Test
    public void shouldNotAddActive() {
        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb, fieldQueryTypeMappingRepository);
        hocsQueryBuilder.dataFields(null);

        Mockito.verifyNoMoreInteractions(bqb);
    }

    @Test
    public void shouldAddDateRange() {
        DateRangeDto dateRangeDto = new DateRangeDto("fromDate", "toDate");

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb, fieldQueryTypeMappingRepository);
        hocsQueryBuilder.dateRange(dateRangeDto);

        Mockito.verify(bqb).must(any(QueryBuilder.class));

        assertThat(bqb.toString()).contains("fromDate");
        assertThat(bqb.toString()).contains("toDate");
    }

    @Test
    public void shouldAddDateRangeFromEmpty() {
        DateRangeDto dateRangeDto = new DateRangeDto("", "toDate");

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb, fieldQueryTypeMappingRepository);
        hocsQueryBuilder.dateRange(dateRangeDto);

        Mockito.verify(bqb).must(any(QueryBuilder.class));

        assertThat(bqb.toString()).contains("toDate");
    }

    @Test
    public void shouldAddDateRangeFromNull() {
        DateRangeDto dateRangeDto = new DateRangeDto(null, "toDate");

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb, fieldQueryTypeMappingRepository);
        hocsQueryBuilder.dateRange(dateRangeDto);

        Mockito.verify(bqb).must(any(QueryBuilder.class));

        assertThat(bqb.toString()).contains("toDate");
    }

    @Test
    public void shouldAddDateRangeToEmpty() {
        DateRangeDto dateRangeDto = new DateRangeDto("fromDate", "");

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb, fieldQueryTypeMappingRepository);
        hocsQueryBuilder.dateRange(dateRangeDto);

        Mockito.verify(bqb).must(any(QueryBuilder.class));

        assertThat(bqb.toString()).contains("fromDate");
    }

    @Test
    public void shouldAddDateRangeToNull() {
        DateRangeDto dateRangeDto = new DateRangeDto("fromDate", null);

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb, fieldQueryTypeMappingRepository);
        hocsQueryBuilder.dateRange(dateRangeDto);

        Mockito.verify(bqb).must(any(QueryBuilder.class));

        assertThat(bqb.toString()).contains("fromDate");
    }

    @Test
    public void shouldNotAddDateRangeActive() {

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb, fieldQueryTypeMappingRepository);
        hocsQueryBuilder.dateRange(null);

        Mockito.verifyNoMoreInteractions(bqb);
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

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb, fieldQueryTypeMappingRepository);
        hocsQueryBuilder.privateOfficeTeam(privateOfficeTeamUuid);

        // Retrieve the overarching core must query
        Mockito.verify(bqb).must(any(QueryBuilder.class));
        assertThat(bqb.must().size()).isEqualTo(1);
        BoolQueryBuilder boolQueryBuilder = (BoolQueryBuilder) bqb.must().get(0);

        // Private office has 7 underlying should queries
        assertThat(boolQueryBuilder.should().size()).isEqualTo(7);
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
        String privateOfficeTeamUuid = "";

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb, fieldQueryTypeMappingRepository);
        hocsQueryBuilder.privateOfficeTeam(privateOfficeTeamUuid);

        Mockito.verifyNoMoreInteractions(bqb);
    }

    @Test
    public void shouldNotAddNullPrivateOfficeTeam() {
        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb, fieldQueryTypeMappingRepository);
        hocsQueryBuilder.privateOfficeTeam(null);

        Mockito.verifyNoMoreInteractions(bqb);
    }

}
