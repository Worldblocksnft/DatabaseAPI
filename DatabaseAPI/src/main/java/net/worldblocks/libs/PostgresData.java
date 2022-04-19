package net.worldblocks.libs;

import lombok.Getter;

public class PostgresData {

    @Getter private String id;
    @Getter private Object data;
    public PostgresData(String id, Object data) {
        this.id = id;
        this.data = data;
    }

}
