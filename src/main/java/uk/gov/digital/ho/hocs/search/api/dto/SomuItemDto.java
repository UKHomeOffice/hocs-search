package uk.gov.digital.ho.hocs.search.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SomuItemDto {

    @JsonProperty("uuid")
    private UUID uuid;

    @JsonProperty("somuTypeUuid")
    private UUID somuTypeUuid;

    @JsonProperty("data")
    private Object data;
    
}
