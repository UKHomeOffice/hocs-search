package uk.gov.digital.ho.hocs.search.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.gov.digital.ho.hocs.search.api.dto.SomuItemDto;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class SomuItem {

    private UUID uuid;
    
    private UUID somuUuid;

    private Object data;
    
    public static SomuItem from(SomuItemDto somuItemDto) {
        return new SomuItem(somuItemDto.getUuid(), somuItemDto.getSomuUuid(), somuItemDto.getData());
    }
}
