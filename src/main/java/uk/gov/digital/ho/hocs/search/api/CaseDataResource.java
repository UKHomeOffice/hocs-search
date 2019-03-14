package uk.gov.digital.ho.hocs.search.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.digital.ho.hocs.search.api.dto.CreateCaseRequest;
import uk.gov.digital.ho.hocs.search.api.dto.CreateCorrespondentRequest;
import uk.gov.digital.ho.hocs.search.api.dto.DateRangeDto;
import uk.gov.digital.ho.hocs.search.api.dto.SearchRequest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
class CaseDataResource {

    private final CaseDataService caseDataService;

    @Autowired
    public CaseDataResource(CaseDataService caseDataService) {
        this.caseDataService = caseDataService;
    }

    @PostMapping(value = "/case/{caseUUID}")
    ResponseEntity createCase(@PathVariable UUID caseUUID, @RequestBody CreateCaseRequest request) {
        caseDataService.createCase(caseUUID, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/case")
    ResponseEntity<List<String>> search(@RequestBody SearchRequest request) {

        CreateCorrespondentRequest createCorrespondentRequest = new CreateCorrespondentRequest(UUID.randomUUID(), LocalDateTime.now(),"type",  "First Last", null, "342","34342", "4324");
        caseDataService.createCorrespondent(UUID.fromString("20bccf45-3849-49b0-8cc7-888baeb89134"), createCorrespondentRequest);
        DateRangeDto dateRange = new DateRangeDto("2017-09-01T18:25:43Z", "2020-09-01T18:25:43Z");
        List<String> results = caseDataService.search(new SearchRequest(Arrays.asList("MIN", "DTEN"), dateRange, "First Last", "Nick Test Topic", null, null));
        //List<String> results = caseDataService.search(request);

        return ResponseEntity.ok(results);
    }
}