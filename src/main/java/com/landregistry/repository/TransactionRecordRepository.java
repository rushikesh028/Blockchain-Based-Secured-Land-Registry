package com.landregistry.repository;

import com.landregistry.entity.TransactionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
 public interface TransactionRecordRepository extends JpaRepository<TransactionRecord, Integer> {

    List<TransactionRecord> findByParcelIdOrderByTransactionDateDesc(String parcelId);

    List<TransactionRecord> findByToOwnerIdOrderByTransactionDateDesc(String ownerId);

    List<TransactionRecord> findByFromOwnerIdOrderByTransactionDateDesc(String ownerId);

    Optional<TransactionRecord> findByBlockHash(String blockHash);

    List<TransactionRecord> findByTransactionType(TransactionRecord.TransactionType type);

    List<TransactionRecord> findByStatus(TransactionRecord.TransactionStatus status);

    @Query("SELECT tr FROM TransactionRecord tr WHERE tr.toOwnerId = :ownerId OR tr.fromOwnerId = :ownerId ORDER BY tr.transactionDate DESC")
    List<TransactionRecord> findAllByOwnerId(@Param("ownerId") String ownerId);

    @Query("SELECT COUNT(tr) FROM TransactionRecord tr WHERE tr.parcelId = :parcelId")
    long countByParcelId(@Param("parcelId") String parcelId);

    boolean existsByBlockHash(String blockHash);
}
