package com.company.accounter.entity;

import io.jmix.core.DeletePolicy;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.entity.annotation.OnDeleteInverse;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.data.annotation.CreatedBy;

import java.util.UUID;

@JmixEntity
@Table(name = "NEEDS_1", indexes = {
        @Index(name = "IDX_NEEDS_1_PERIOD", columnList = "PERIOD_ID"),
        @Index(name = "IDX_NEEDS_1_KIND", columnList = "KIND_ID"),
        @Index(name = "IDX_NEEDS_1_RECIPIENT_USER", columnList = "RECIPIENT_USER_ID")
})
@Entity
public class Needs {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @NotNull
    @OnDeleteInverse(DeletePolicy.DENY)
    @JoinColumn(name = "PERIOD_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Period period;

    @Positive
    @NotNull
    @Column(name = "AMOUNT", nullable = false)
    private Integer amount;

    @Lob
    @Column(name = "JUSTIFICATION")
    private String justification;

    @OnDeleteInverse(DeletePolicy.UNLINK)
    @JoinColumn(name = "CREATED_BY_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    @CreatedBy
    private User createdBy;

    @NotNull
    @OnDeleteInverse(DeletePolicy.UNLINK)
    @JoinColumn(name = "RECIPIENT_USER_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User recipientUser;

    @NotNull
    @Column(name = "RECORD_TYPE", nullable = false)
    private String recordType = RecordType.USER.getId();

    @Column(name = "APPROVED")
    private Boolean approved = false;

    @Column(name = "ACCOUNTED")
    private Boolean accounted = false;

    @NotNull
    @OnDeleteInverse(DeletePolicy.DENY)
    @JoinColumn(name = "KIND_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private NeedKind kind;

    public void setRecordType(RecordType RecordType) {
        this.recordType = RecordType == null ? null : RecordType.getId();
    }

    public RecordType getRecordType() {
        return recordType == null ? null : com.company.accounter.entity.RecordType.fromId(recordType);
    }

    public User getRecipientUser() {
        return recipientUser;
    }

    public void setRecipientUser(User recipientUser) {
        this.recipientUser = recipientUser;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public Boolean getAccounted() {
        return accounted;
    }

    public void setAccounted(Boolean accounted) {
        this.accounted = accounted;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public NeedKind getKind() {
        return kind;
    }

    public void setKind(NeedKind kind) {
        this.kind = kind;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

}