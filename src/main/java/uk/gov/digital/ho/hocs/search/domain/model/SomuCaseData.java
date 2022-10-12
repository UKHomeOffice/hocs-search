package uk.gov.digital.ho.hocs.search.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
@Data
public class SomuCaseData {

    private Set<SomuItem> allSomuItems = new HashSet<>();

    public void addSomuItem(SomuItem somuItem) {
        this.allSomuItems.add(somuItem);
    }

    public void updateSomuItem(SomuItem somuItem) {
        this.allSomuItems.removeIf(x -> x.getUuid().equals(somuItem.getUuid()));
        this.allSomuItems.add(somuItem);
    }

    public void removeSomuItem(UUID somuItemUuid) {
        this.allSomuItems.removeIf(c -> c.getUuid().equals(somuItemUuid));
    }

}
