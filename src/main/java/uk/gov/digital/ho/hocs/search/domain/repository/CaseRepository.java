package uk.gov.digital.ho.hocs.search.domain.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import uk.gov.digital.ho.hocs.search.domain.repository.model.CaseData;

import java.util.UUID;


@Repository
public interface CaseRepository extends ElasticsearchRepository<CaseData, UUID> {


}