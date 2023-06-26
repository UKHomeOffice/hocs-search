package uk.gov.digital.ho.hocs.search.api.elastic.scripts;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.opensearch.script.Script;
import org.opensearch.script.ScriptType;
import org.springframework.stereotype.Service;
import uk.gov.digital.ho.hocs.search.api.helpers.ObjectMapperConverterHelper;
import uk.gov.digital.ho.hocs.search.domain.model.Correspondent;

import java.util.Map;

@Service
public class CorrespondentScriptService {

    private final ObjectMapper objectMapper;

    public CorrespondentScriptService(ObjectMapper objectMapper) {this.objectMapper = objectMapper;}

    public Script upsertCorrespondentScript(Correspondent correspondent) {
        return new Script(ScriptType.INLINE, Script.DEFAULT_SCRIPT_LANG, """
           if(ctx._source.currentCorrespondents != null) {
               ArrayList al = new ArrayList();
               for(correspondent in ctx._source.currentCorrespondents) {
                   if(correspondent.uuid != params.correspondent.uuid) {
                       al.add(correspondent)
                   }
               }
               al.add(params.correspondent);
               ctx._source.currentCorrespondents = al;
           } else {
               ctx._source.currentCorrespondents = [params.correspondent];
           }

           if(ctx._source.allCorrespondents != null) {
               ArrayList al = new ArrayList();
               for(correspondent in ctx._source.allCorrespondents) {
                   if(correspondent.uuid != params.correspondent.uuid) {
                       al.add(correspondent)
                   }
               }
               al.add(params.correspondent);
               ctx._source.allCorrespondents = al;
           } else {
               ctx._source.allCorrespondents = [params.correspondent];
           }
           """,
            Map.of(
               "correspondent", ObjectMapperConverterHelper.convertObjectToMap(objectMapper, correspondent)
                  )
        );
    }

}
