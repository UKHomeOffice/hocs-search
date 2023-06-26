package uk.gov.digital.ho.hocs.search.helpers;

import org.opensearch.script.Script;
import org.opensearch.script.ScriptType;
import org.mockito.ArgumentMatcher;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ScriptMatcher implements ArgumentMatcher<Script> {

    private final List<ArgumentMatcher<String>> scriptMatchers;

    private final List<ArgumentMatcher<Map<String, Object>>> paramMatchers;

    public ScriptMatcher(
        List<ArgumentMatcher<String>> scriptMatchers, List<ArgumentMatcher<Map<String, Object>>> paramMatchers
                        ) {
        this.scriptMatchers = scriptMatchers;
        this.paramMatchers = paramMatchers;
    }

    @Override
    public boolean matches(Script argument) {
        return argument.getType() == ScriptType.INLINE && Objects.equals(
            argument.getLang(), Script.DEFAULT_SCRIPT_LANG) && scriptMatchers
            .stream()
            .allMatch(matcher -> matcher.matches(argument.getIdOrCode())) && paramMatchers
            .stream()
            .allMatch(matcher -> matcher.matches(argument.getParams()));
    }

}
