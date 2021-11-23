package uk.gov.digital.ho.hocs.search.application.queue;

public enum DataChangeType {
    CASE_CREATED("CASE_CREATED"),
    CASE_COMPLETED("CASE_COMPLETED"),
    CASE_DELETED("CASE_DELETED"),
    CASE_UPDATED("CASE_UPDATED"),
    CASE_TOPIC_CREATED("CASE_TOPIC_CREATED"),
    CASE_TOPIC_DELETED("CASE_TOPIC_DELETED"),
    CORRESPONDENT_CREATED("CORRESPONDENT_CREATED"),
    CORRESPONDENT_DELETED("CORRESPONDENT_DELETED"),
    CORRESPONDENT_UPDATED("CORRESPONDENT_UPDATED"),
    SOMU_ITEM_CREATED("SOMU_ITEM_CREATED"),
    SOMU_ITEM_DELETED("SOMU_ITEM_DELETED"),
    SOMU_ITEM_UPDATED("SOMU_ITEM_UPDATED");

    public String value;

    DataChangeType(final String value){
        this.value = value;
    }

    public static DataChangeType fromString(String text) {
        for (DataChangeType e : DataChangeType.values()) {
            if (e.value.equalsIgnoreCase(text)) {
                return e;
            }
        }
        return null;
    }
}
