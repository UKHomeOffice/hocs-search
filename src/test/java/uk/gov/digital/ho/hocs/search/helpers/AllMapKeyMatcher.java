package uk.gov.digital.ho.hocs.search.helpers;

import org.mockito.ArgumentMatcher;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AllMapKeyMatcher implements ArgumentMatcher<Map<String, Object>> {

    private final Set<String> keysToMatch;

    public AllMapKeyMatcher(String... keys) {
        keysToMatch = Arrays.stream(keys).collect(Collectors.toSet());
    }

    @Override
    public boolean matches(Map<String, Object> argument) {
        var foundCount = keysToMatch.stream().filter(argument::containsKey).count();

        return keysToMatch.size() == foundCount && argument.keySet().size() == foundCount;
    }

}
