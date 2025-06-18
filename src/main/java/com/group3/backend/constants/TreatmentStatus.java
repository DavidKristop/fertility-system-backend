package com.group3.backend.constants;

public enum TreatmentStatus {
    CANCEL("Cancel"),
    IN_PROGRESS("In Progress"),
    COMPLETE("Complete");

    private final String displayName;

    TreatmentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static TreatmentStatus fromString(String text) {
        for (TreatmentStatus status : TreatmentStatus.values()) {
            if (status.getDisplayName().equalsIgnoreCase(text)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No TreatmentStatus found for text: " + text);
    }
}
