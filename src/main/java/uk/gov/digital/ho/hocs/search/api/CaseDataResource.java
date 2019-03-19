package uk.gov.digital.ho.hocs.search.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.digital.ho.hocs.search.api.dto.CreateCaseRequest;
import uk.gov.digital.ho.hocs.search.api.dto.SearchRequest;

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

    @PostMapping(value = "/case")
    ResponseEntity<List<String>> search(@RequestBody SearchRequest request) {
        List<String> results = caseDataService.search(request);
        return ResponseEntity.ok(results);
    }
}