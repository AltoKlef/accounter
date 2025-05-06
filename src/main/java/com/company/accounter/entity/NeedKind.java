package com.company.accounter.entity;


import io.jmix.core.DeletePolicy;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.entity.annotation.OnDeleteInverse;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@JmixEntity
@Table(name = "NEED_KIND", indexes = {
        @Index(name = "IDX_NEED_KIND_TYPE", columnList = "TYPE_ID")
})
@Entity
public class NeedKind {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @NotNull
    @OnDeleteInverse(DeletePolicy.UNLINK)
    @JoinColumn(name = "TYPE_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private com.company.accounter.entity.NeedType type;

    @NotNull
    @InstanceName
    @Column(name = "NAME", nullable = false)
    private String name;

    @NotNull
    @NotBlank
    @Column(name = "UNIT", nullable = false, length = 64)
    private String unit;

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public com.company.accounter.entity.NeedType getType() {
        return type;
    }

    public void setType(com.company.accounter.entity.NeedType type) {
        this.type = type;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

}