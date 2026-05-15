package com.landregistry.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Records each land transaction (registration, transfer, mutation) in the database.
 * Every transaction is anchored to a block in the blockchain.
 */
@Entity
@Table(name = "transaction_records")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Integer transactionId;

    @Column(name = "parcel_id", nullable = false)
    private String parcelId;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Column(name = "from_owner_id")
    private String fromOwnerId;

    @Column(name = "from_owner_name")
    private String fromOwnerName;

    @Column(name = "to_owner_id")
    private String toOwnerId;

    @Column(name = "to_owner_name")
    private String toOwnerName;

    @Column(name = "transaction_value", precision = 15, scale = 2)
    private BigDecimal transactionValue;

    @Column(name = "stamp_duty", precision = 12, scale = 2)
    private BigDecimal stampDuty;

    @Column(name = "registration_fee", precision = 10, scale = 2)
    private BigDecimal registrationFee;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @Column(name = "block_hash", nullable = false)
    private String blockHash;            // Hash of the block containing this transaction

    @Column(name = "block_index", nullable = false)
    private Integer blockIndex;

    @Column(name = "previous_block_hash")
    private String previousBlockHash;

    @Column(name = "initiated_by", nullable = false)
    private String initiatedBy;          // User who initiated the transaction

    @Column(name = "witness_id")
    private String witnessId;

    @Column(name = "notes")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TransactionStatus status;

    public enum TransactionType {
        REGISTRATION,       // First-time registration of land
        TRANSFER,           // Ownership transfer / sale
        MUTATION,           // Legal name change for same owner
        ENCUMBRANCE,        // Mortgage or lien
        RELEASE,            // Release from encumbrance
        CORRECTION,         // Correction of data
        DISPUTE_FILED,      // Dispute raised
        DISPUTE_RESOLVED    // Dispute resolved
    }

    public enum TransactionStatus {
        PENDING, CONFIRMED, FAILED, DISPUTED
    }
}
