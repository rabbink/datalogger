package nl.rabbink.datalogger.rest;

public enum SortOrder {

    ASC, DESC;

    public static SortOrder findByName(String name) {
        for (SortOrder v : values()) {
            if (v.name().equalsIgnoreCase(name)) {
                return v;
            }
        }
        throw new IllegalArgumentException("Unknown SortOrder: " + name);
    }

}
