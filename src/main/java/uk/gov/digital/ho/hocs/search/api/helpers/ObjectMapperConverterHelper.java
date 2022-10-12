package uk.gov.digital.ho.hocs.search.api.helpers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ObjectMapperConverterHelper {

    private ObjectMapperConverterHelper() {}

    public static Map<String, Object> convertObjectToMap(ObjectMapper objectMapper, Object object) {
        return removeRedundantMappings(objectMapper.convertValue(object, new TypeReference<>() {}));
    }

    private static Map<String, Object> removeRedundantMappings(Map<String, Object> map) {
        String completed = "completed";
        Map<String, Object> result = new HashMap<>(map);

        result.values().removeAll(Arrays.asList("", null));

        if (result.containsKey(completed) && !((boolean) result.get(completed))) {
            result.remove(completed);
        }

        return result;
    }

}
