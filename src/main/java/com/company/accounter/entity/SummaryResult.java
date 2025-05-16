package com.company.accounter.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.entity.annotation.JmixId;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;

import java.util.UUID;

@JmixEntity
public class SummaryResult {
    @InstanceName
    @JmixGeneratedValue
    @JmixId
    private UUID id;

    private Integer created;

    private Integer updated;

    private Integer deleted;

    public SummaryResult(int created, int updated, int deleted) {
        this.created = created;
        this.updated = updated;
        this.deleted = deleted;
    }

    /* public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public Integer getUpdated() {
        return updated;
    }

    public void setUpdated(Integer updated) {
        this.updated = updated;
    }

    public Integer getCreated() {
        return created;
    }

    public void setCreated(Integer created) {
        this.created = created;
    } */

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return String.format("Новая итоговая сформирована. Добавлено: %d, Обновлено: %d, Удалено: %d", created, updated, deleted);
    }


}