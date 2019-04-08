package uk.gov.digital.ho.hocs.search.application;

public enum LogEvent {
    CASE_UPDATE_FAILED,
    CASE_SAVE_FAILED,
    CASE_NOT_FOUND,
    SEARCH_CASE_CREATED,
    SEARCH_CASE_UPDATED,
    SEARCH_CASE_DELETED,
    SEARCH_CORRESPONDENT_ADDED,
    SEARCH_CORRESPONDENT_DELETED,
    SEARCH_TOPIC_ADDED,
    SEARCH_TOPIC_DELETED,
    SEARCH_REQUEST,
    SEARCH_RESPONSE;
    public static final String EVENT = "event_id";
}
