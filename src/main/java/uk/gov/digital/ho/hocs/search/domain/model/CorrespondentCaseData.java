package uk.gov.digital.ho.hocs.search.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.digital.ho.hocs.search.api.dto.CorrespondentDetailsDto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
@Data
public class CorrespondentCaseData {

    private Set<Correspondent> currentCorrespondents = new HashSet<>();

    private Set<Correspondent> allCorrespondents = new HashSet<>();

    public void addCorrespondent(CorrespondentDetailsDto correspondentDetailsDto) {
        Correspondent correspondent = Correspondent.from(correspondentDetailsDto);
        this.currentCorrespondents.add(correspondent);
        this.allCorrespondents.add(correspondent);
    }

    public void removeCorrespondent(UUID correspondentUUID) {
        this.currentCorrespondents.removeIf(c -> c.getUuid().equals(correspondentUUID));
    }

    public void updateCorrespondent(CorrespondentDetailsDto correspondentDetailsDto) {
        Correspondent updatedCorrespondent = Correspondent.from(correspondentDetailsDto);
        List<Correspondent> toRemove = new ArrayList<>();
        for (Correspondent correspondent : currentCorrespondents) {
            if (correspondent.getUuid().equals(updatedCorrespondent.getUuid())) {
                toRemove.add(correspondent);
            }
        }
        this.currentCorrespondents.removeAll(toRemove);
        this.currentCorrespondents.add(updatedCorrespondent);
        this.allCorrespondents.add(updatedCorrespondent);
    }

}
