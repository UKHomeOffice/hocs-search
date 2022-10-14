package uk.gov.digital.ho.hocs.search.domain.repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import uk.gov.digital.ho.hocs.search.application.LogEvent;
import uk.gov.digital.ho.hocs.search.domain.exceptions.ApplicationExceptions;

import java.io.IOException;
import java.io.InputStream;

@Service
public abstract class JsonConfigFileReader {

    protected final ObjectMapper objectMapper;

    protected JsonConfigFileReader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    protected <T> T readValueFromFile(TypeReference<T> reference) {
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(
            String.format("config/%s.json", getFileName()))) {
            return objectMapper.readValue(in, reference);
        } catch (IOException e) {
            throw new ApplicationExceptions.ConfigFileReadException(
                String.format("Unable to read file: %s into type %s", getFileName(), reference.getType()),
                LogEvent.CONFIG_PARSE_FAILURE);
        }
    }

    abstract String getFileName();

}
