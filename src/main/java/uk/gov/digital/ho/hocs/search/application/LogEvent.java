package uk.gov.digital.ho.hocs.search.application;

public enum LogEvent {
    CASE_NOT_FOUND,
    CASE_SAVE_FAILED,
    CASE_UPDATE_FAILED,
    NULL_SEARCH_MESSAGE_TYPE,
    SEARCH_CASE_CREATED,
    SEARCH_CASE_COMPLETED,
    SEARCH_CASE_DELETED,
    SEARCH_CASE_UPDATED,
    SEARCH_CORRESPONDENT_CREATED,
    SEARCH_CORRESPONDENT_DELETED,
    SEARCH_CORRESPONDENT_UPDATED,
    SEARCH_TOPIC_CREATED,
    SEARCH_TOPIC_DELETED,
    SEARCH_REQUEST,
    SEARCH_RESPONSE,
    SOMU_ITEM_CREATED,
    SOMU_ITEM_DELETED,
    SOMU_ITEM_UPDATED,
    UNKNOWN_SEARCH_MESSAGE_TYPE;

    public static final String EVENT = "event_id";
}
