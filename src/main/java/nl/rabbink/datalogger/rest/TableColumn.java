package nl.rabbink.datalogger.rest;

import java.util.HashMap;
import java.util.Map;

public enum TableColumn {

    TIMESTAMP(0),
    VALUE(1);

    private static final Map<Integer, TableColumn> lookup = new HashMap<>();

    private final int datatablesColumnId;

    static {
        for (TableColumn tableColumn : TableColumn.values()) {
            lookup.put(tableColumn.datatablesColumnId, tableColumn);
        }
    }

    private TableColumn(int id) {
        this.datatablesColumnId = id;
    }

    public int getId() {
        return datatablesColumnId;
    }

    public static TableColumn getByDatatablesColumnId(int datatablesColumnId) {
        TableColumn type = lookup.get(Integer.valueOf(datatablesColumnId));
        if (type == null) {
            throw new IllegalArgumentException("Unknown TableColumn: " + datatablesColumnId);
        }
        return type;
    }

}
