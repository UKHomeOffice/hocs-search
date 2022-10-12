package uk.gov.digital.ho.hocs.search.domain.exceptions;

import uk.gov.digital.ho.hocs.search.application.LogEvent;

public interface ApplicationExceptions {

    class ResourceException extends RuntimeException {

        private final LogEvent event;

        ResourceException(String msg, LogEvent event, Object... args) {
            super(String.format(msg, args));
            this.event = event;
        }

        public LogEvent getEvent() {
            return event;
        }

    }

    class EntityNotFoundException extends RuntimeException {

        private final LogEvent event;

        public EntityNotFoundException(String msg, LogEvent event, Object... args) {
            super(String.format(msg, args));
            this.event = event;
        }

        public LogEvent getEvent() {
            return event;
        }

    }

    class InvalidEventTypeException extends RuntimeException {

        private final LogEvent event;

        public InvalidEventTypeException(String msg, LogEvent event, Object... args) {
            super(String.format(msg, args));
            this.event = event;
        }

        public LogEvent getEvent() {
            return event;
        }

    }

    class ConfigFileReadException extends RuntimeException {

        private final LogEvent event;

        private final LogEvent exception;

        public ConfigFileReadException(String msg, LogEvent event, Object... args) {
            super(String.format(msg, args));
            this.event = event;
            this.exception = null;
        }

        public LogEvent getEvent() {
            return event;
        }

        public LogEvent getException() {return exception;}

    }

    class ResourceServerException extends ResourceException {

        public ResourceServerException(String msg, LogEvent event, Object... args) {
            super(msg, event, args);
        }

    }

}
