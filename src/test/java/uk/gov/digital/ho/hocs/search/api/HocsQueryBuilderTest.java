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

    BoolQueryBuilder bqb;

    @Before
    public void setup() {
        this.bqb = Mockito.spy(QueryBuilders.boolQuery());
    }

    @Test
    public void ShouldAddCaseTypes() {
        List<String> caseTypes = new ArrayList<>();
        caseTypes.add("ANYTYPE");

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb);
        hocsQueryBuilder.caseTypes(caseTypes);

        Mockito.verify(bqb).must(any(QueryBuilder.class));

        assertThat(bqb.toString()).contains("ANYTYPE");
    }

    @Test
    public void ShouldNotAddNoCaseTypes() {
        List<String> caseTypes = new ArrayList<>();

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb);
        hocsQueryBuilder.caseTypes(caseTypes);

        Mockito.verifyNoMoreInteractions(bqb);
    }

    @Test
    public void ShouldNotAddNullCaseTypes() {

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb);
        hocsQueryBuilder.caseTypes(null);

        Mockito.verifyNoMoreInteractions(bqb);
    }

    @Test
    public void ShouldAddCorrespondent() {
        String correspondent = "MYNAME";

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb);
        hocsQueryBuilder.correspondent(correspondent);

        Mockito.verify(bqb).must(any(QueryBuilder.class));

        assertThat(bqb.toString()).contains("MYNAME");
    }

    @Test
    public void ShouldNotAddNoCorrespondent() {
        String correspondent = "";

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb);
        hocsQueryBuilder.correspondent(correspondent);

        Mockito.verifyNoMoreInteractions(bqb);
    }

    @Test
    public void ShouldNotAddNullCorrespondent() {

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb);
        hocsQueryBuilder.correspondent(null);

        Mockito.verifyNoMoreInteractions(bqb);
    }

    @Test
    public void ShouldAddTopic() {
        String topic = "MYNAME";

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb);
        hocsQueryBuilder.topic(topic);

        Mockito.verify(bqb).must(any(QueryBuilder.class));

        assertThat(bqb.toString()).contains("MYNAME");
    }

    @Test
    public void ShouldNotAddNoTopic() {
        String topic = "";

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb);
        hocsQueryBuilder.topic(topic);

        Mockito.verifyNoMoreInteractions(bqb);
    }

    @Test
    public void ShouldNotAddNullTopic() {

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb);
        hocsQueryBuilder.topic(null);

        Mockito.verifyNoMoreInteractions(bqb);
    }

    @Test
    public void ShouldAddData() {
        Map<String, String> data = new HashMap<>();
        data.put("dataKey", "dataValue");

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb);
        hocsQueryBuilder.dataFields(data);

        Mockito.verify(bqb).must(any(QueryBuilder.class));

        assertThat(bqb.toString()).contains("data.dataKey");
        assertThat(bqb.toString()).contains("dataValue");
    }

    @Test
    public void ShouldNotAddNoData() {
        Map<String, String> data = new HashMap<>();

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb);
        hocsQueryBuilder.dataFields(data);

        Mockito.verifyNoMoreInteractions(bqb);
    }

    @Test
    public void ShouldNotAddNullData() {

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb);
        hocsQueryBuilder.dataFields(null);

        Mockito.verifyNoMoreInteractions(bqb);
    }

    @Test
    public void ShouldAddActiveFalse() {
        Boolean activeOnly = false;

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb);
        hocsQueryBuilder.activeOnlyFlag(activeOnly);

        Mockito.verifyNoMoreInteractions(bqb);

    }

    @Test
    public void ShouldAddActiveTrue() {
        Boolean activeOnly = true;

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb);
        hocsQueryBuilder.activeOnlyFlag(activeOnly);

        Mockito.verify(bqb).must(any(QueryBuilder.class));

    }

    @Test
    public void ShouldNotAddActive() {

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb);
        hocsQueryBuilder.dataFields(null);

        Mockito.verifyNoMoreInteractions(bqb);
    }

    @Test
    public void ShouldAddDateRange() {
        DateRangeDto dateRangeDto = new DateRangeDto("fromDate", "toDate");

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb);
        hocsQueryBuilder.dateRange(dateRangeDto);

        Mockito.verify(bqb).must(any(QueryBuilder.class));

        assertThat(bqb.toString()).contains("fromDate");
        assertThat(bqb.toString()).contains("toDate");
    }

    @Test
    public void ShouldAddDateRangeFromEmpty() {
        DateRangeDto dateRangeDto = new DateRangeDto("", "toDate");

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb);
        hocsQueryBuilder.dateRange(dateRangeDto);

        Mockito.verify(bqb).must(any(QueryBuilder.class));

        assertThat(bqb.toString()).contains("toDate");
    }

    @Test
    public void ShouldAddDateRangeFromNull() {
        DateRangeDto dateRangeDto = new DateRangeDto(null, "toDate");

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb);
        hocsQueryBuilder.dateRange(dateRangeDto);

        Mockito.verify(bqb).must(any(QueryBuilder.class));

        assertThat(bqb.toString()).contains("toDate");
    }

    @Test
    public void ShouldAddDateRangeToEmpty() {
        DateRangeDto dateRangeDto = new DateRangeDto("fromDate", "");

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb);
        hocsQueryBuilder.dateRange(dateRangeDto);

        Mockito.verify(bqb).must(any(QueryBuilder.class));

        assertThat(bqb.toString()).contains("fromDate");
    }

    @Test
    public void ShouldAddDateRangeToNull() {
        DateRangeDto dateRangeDto = new DateRangeDto("fromDate", null);

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb);
        hocsQueryBuilder.dateRange(dateRangeDto);

        Mockito.verify(bqb).must(any(QueryBuilder.class));

        assertThat(bqb.toString()).contains("fromDate");
    }

    @Test
    public void ShouldNotAddDateRangeActive() {

        HocsQueryBuilder hocsQueryBuilder = new HocsQueryBuilder(bqb);
        hocsQueryBuilder.dateRange(null);

        Mockito.verifyNoMoreInteractions(bqb);
    }
}