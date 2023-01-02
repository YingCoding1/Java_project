package com.skillup.infrastructure;

public interface RecordDomainMapping<R,D> {
    D toDomain(R r);
    R toRecord(D d);
}
