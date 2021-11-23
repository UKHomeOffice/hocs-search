package uk.gov.digital.ho.hocs.search.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.digital.ho.hocs.search.api.dto.SearchRequest;

import java.util.Set;
import java.util.UUID;

@Slf4j
@RestController
class CaseDataResource {

    private final CaseDataService caseDataService;

    @Autowired
    public CaseDataResource(CaseDataService caseDataService) {
        this.caseDataService = caseDataService;
    }

    @PostMapping(value = "/case")
    ResponseEntity<Set<UUID>> search(@RequestBody SearchRequest request) {
        Set<UUID> results = caseDataService.search(request);
        return ResponseEntity.ok(results);
    }
}