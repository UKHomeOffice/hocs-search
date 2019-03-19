package uk.gov.digital.ho.hocs.search.application;

public enum LogEvent {
    AUDIT_EVENT_CREATED,
    AUDIT_FAILED,
    REST_HELPER_NOT_FOUND,
    REST_HELPER_INTERNAL_SERVER_ERROR,
    REST_HELPER_MALFORMED_RESPONSE,
    INFO_CLIENT_GET_TOPIC_SUCCESS,
    INFO_CLIENT_GET_TOPIC_FAILURE,
    UNCAUGHT_EXCEPTION;
    public static final String EVENT = "event_id";
}
