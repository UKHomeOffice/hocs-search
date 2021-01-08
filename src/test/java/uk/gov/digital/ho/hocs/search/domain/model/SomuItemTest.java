package uk.gov.digital.ho.hocs.search.domain.model;

import org.junit.Test;
import uk.gov.digital.ho.hocs.search.api.dto.*;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SomuItemTest {

    private final UUID somuItemUUID = UUID.randomUUID();
    private final UUID somuTypeUUID = UUID.randomUUID();
    private final SomuItemDto validSomuItemDto = new SomuItemDto(somuItemUUID, somuTypeUUID, "{}");

    @Test
    public void shouldCreateCaseDataConstructor() {
        SomuItem somuItem = SomuItem.from(validSomuItemDto);

        assertThat(somuItem.getUuid()).isEqualTo(somuItemUUID);
        assertThat(somuItem.getSomuUuid()).isEqualTo(somuTypeUUID);
        assertThat(somuItem.getData()).isEqualTo("{}");
    }
}
