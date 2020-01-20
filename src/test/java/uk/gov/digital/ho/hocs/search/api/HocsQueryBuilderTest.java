package uk.gov.digital.ho.hocs.search.api;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.digital.ho.hocs.search.api.dto.DateRangeDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;


@RunWith(MockitoJUnitRunner.class)
public class HocsQueryBuilderTest {

    private BoolQueryBuilder bqb;

    @Before
    public void setup() {
        this.bqb = Mockito.spy(QueryBuilders.boolQuery());
    }

    @Test
    public void shouldAddReference() {
        String reference = "reference123";

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb);
        hocsQueryBuilder.reference(reference);

        Mockito.verify(bqb).must(any(QueryBuilder.class));
        Mockito.verifyNoMoreInteractions(bqb);

        assertThat(bqb.toString()).contains(reference);
    }

    @Test
    public void shouldNotAddBlankReference() {
        String reference = "";

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb);
        hocsQueryBuilder.reference(reference);

        Mockito.verifyNoMoreInteractions(bqb);
    }

    @Test
    public void shouldNotAddNullReference() {
        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb);
        hocsQueryBuilder.reference(null);

        Mockito.verifyNoMoreInteractions(bqb);
    }

    @Test
    public void shouldAddCaseTypes() {
        List<String> caseTypes = new ArrayList<>();
        caseTypes.add("ANYTYPE");

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb);
        hocsQueryBuilder.caseTypes(caseTypes);

        Mockito.verify(bqb).must(any(QueryBuilder.class));

        assertThat(bqb.toString()).contains("ANYTYPE");
    }

    @Test
    public void shouldNotAddNoCaseTypes() {
        List<String> caseTypes = new ArrayList<>();

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb);
        hocsQueryBuilder.caseTypes(caseTypes);

        Mockito.verifyNoMoreInteractions(bqb);
    }

    @Test
    public void shouldNotAddNullCaseTypes() {

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb);
        hocsQueryBuilder.caseTypes(null);

        Mockito.verifyNoMoreInteractions(bqb);
    }

    @Test
    public void shouldAddCorrespondent() {
        String correspondent = "MYNAME";

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb);
        hocsQueryBuilder.correspondent(correspondent);

        Mockito.verify(bqb).must(any(QueryBuilder.class));

        assertThat(bqb.toString()).contains("MYNAME");
    }

    @Test
    public void shouldNotAddNoCorrespondent() {
        String correspondent = "";

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb);
        hocsQueryBuilder.correspondent(correspondent);

        Mockito.verifyNoMoreInteractions(bqb);
    }

    @Test
    public void shouldNotAddNullCorrespondent() {

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb);
        hocsQueryBuilder.correspondent(null);

        Mockito.verifyNoMoreInteractions(bqb);
    }

    @Test
    public void shouldAddTopic() {
        String topic = "MYNAME";

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb);
        hocsQueryBuilder.topic(topic);

        Mockito.verify(bqb).must(any(QueryBuilder.class));

        assertThat(bqb.toString()).contains("MYNAME");
    }

    @Test
    public void shouldNotAddNoTopic() {
        String topic = "";

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb);
        hocsQueryBuilder.topic(topic);

        Mockito.verifyNoMoreInteractions(bqb);
    }

    @Test
    public void shouldNotAddNullTopic() {

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb);
        hocsQueryBuilder.topic(null);

        Mockito.verifyNoMoreInteractions(bqb);
    }

    @Test
    public void shouldAddData() {
        Map<String, String> data = new HashMap<>();
        data.put("dataKey", "dataValue");

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb);
        hocsQueryBuilder.dataFields(data);

        Mockito.verify(bqb).must(any(QueryBuilder.class));

        assertThat(bqb.toString()).contains("data.dataKey");
        assertThat(bqb.toString()).contains("dataValue");
    }

    @Test
    public void shouldNotAddNoData() {
        Map<String, String> data = new HashMap<>();

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb);
        hocsQueryBuilder.dataFields(data);

        Mockito.verifyNoMoreInteractions(bqb);
    }

    @Test
    public void shouldNotAddNullData() {

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb);
        hocsQueryBuilder.dataFields(null);

        Mockito.verifyNoMoreInteractions(bqb);
    }

    @Test
    public void shouldAddActiveFalse() {
        Boolean activeOnly = false;

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb);
        hocsQueryBuilder.activeOnlyFlag(activeOnly);

        Mockito.verifyNoMoreInteractions(bqb);

    }

    @Test
    public void shouldAddActiveTrue() {
        Boolean activeOnly = true;

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb);
        hocsQueryBuilder.activeOnlyFlag(activeOnly);

        Mockito.verify(bqb).must(any(QueryBuilder.class));

    }

    @Test
    public void shouldNotAddActive() {

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb);
        hocsQueryBuilder.dataFields(null);

        Mockito.verifyNoMoreInteractions(bqb);
    }

    @Test
    public void shouldAddDateRange() {
        DateRangeDto dateRangeDto = new DateRangeDto("fromDate", "toDate");

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb);
        hocsQueryBuilder.dateRange(dateRangeDto);

        Mockito.verify(bqb).must(any(QueryBuilder.class));

        assertThat(bqb.toString()).contains("fromDate");
        assertThat(bqb.toString()).contains("toDate");
    }

    @Test
    public void shouldAddDateRangeFromEmpty() {
        DateRangeDto dateRangeDto = new DateRangeDto("", "toDate");

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb);
        hocsQueryBuilder.dateRange(dateRangeDto);

        Mockito.verify(bqb).must(any(QueryBuilder.class));

        assertThat(bqb.toString()).contains("toDate");
    }

    @Test
    public void shouldAddDateRangeFromNull() {
        DateRangeDto dateRangeDto = new DateRangeDto(null, "toDate");

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb);
        hocsQueryBuilder.dateRange(dateRangeDto);

        Mockito.verify(bqb).must(any(QueryBuilder.class));

        assertThat(bqb.toString()).contains("toDate");
    }

    @Test
    public void shouldAddDateRangeToEmpty() {
        DateRangeDto dateRangeDto = new DateRangeDto("fromDate", "");

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb);
        hocsQueryBuilder.dateRange(dateRangeDto);

        Mockito.verify(bqb).must(any(QueryBuilder.class));

        assertThat(bqb.toString()).contains("fromDate");
    }

    @Test
    public void shouldAddDateRangeToNull() {
        DateRangeDto dateRangeDto = new DateRangeDto("fromDate", null);

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb);
        hocsQueryBuilder.dateRange(dateRangeDto);

        Mockito.verify(bqb).must(any(QueryBuilder.class));

        assertThat(bqb.toString()).contains("fromDate");
    }

    @Test
    public void shouldNotAddDateRangeActive() {

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb);
        hocsQueryBuilder.dateRange(null);

        Mockito.verifyNoMoreInteractions(bqb);
    }
}