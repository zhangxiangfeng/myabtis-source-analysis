package org.apache.ibatis.domain.account;

import java.io.Serializable;
import java.util.Date;

/**
 * Author
 *
 * @author simon
 * @create 2018-05-21 下午6:30
 **/
public class Account implements Serializable {

    protected Long tableId;

    protected String name;

    protected Long blance;

    protected Date lastUpdate;

    public Long getTableId() {
        return tableId;
    }

    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getBlance() {
        return blance;
    }

    public void setBlance(Long blance) {
        this.blance = blance;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
