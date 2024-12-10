package ai.zenguard;

public enum Detector {
    PROMPT_INJECTION("prompt_injection"),
    PII("pii"),
    SECRETS("secrets"),
    ALLOWED_TOPICS("allowed_subjects"),
    BANNED_TOPICS("banned_subjects"),
    KEYWORDS("keywords");

    private final String value;

    Detector(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
