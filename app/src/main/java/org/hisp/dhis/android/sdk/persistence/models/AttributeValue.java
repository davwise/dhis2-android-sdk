package org.hisp.dhis.android.sdk.persistence.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.hisp.dhis.android.sdk.controllers.metadata.MetaDataController;
import org.hisp.dhis.android.sdk.persistence.Dhis2Database;

/**
 * @author Ignacio Foche Pérez on 09.11.15.
 */
@Table(databaseName = Dhis2Database.NAME)
public class AttributeValue extends BaseModel {

    @Column
    @PrimaryKey(autoincrement = true)
    long id;

    @Column
    @ForeignKey(references = {@ForeignKeyReference(columnName = "attributeId",
            columnType = String.class,
            foreignColumnName = "id")},
            saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE)
    Attribute attribute;

    @JsonProperty("value")
    @Column(name = "value")
    String value;

    @JsonProperty("created")
    //@JsonIgnore
    @Column(name = "created")
    String created;

    @JsonProperty("lastUpdated")
    //@JsonIgnore
    @Column(name = "lastUpdated")
    String lastUpdated;

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
