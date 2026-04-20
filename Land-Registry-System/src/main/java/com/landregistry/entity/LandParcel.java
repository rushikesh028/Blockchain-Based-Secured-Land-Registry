package com.landregistry.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a land parcel in the database.
 * The database acts as an indexed cache of the blockchain state
 * for faster querying, while the blockchain holds the authoritative record.
 */
@Entity
@Table(name = "land_parcels")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LandParcel {

    @Id
    @Column(name = "parcel_id", unique = true, nullable = false)
    private String parcelId;

    @NotBlank
    @Column(name = "survey_number", unique = true, nullable = false)
    private String surveyNumber;

    @NotBlank
    @Column(name = "district", nullable = false)
    private String district;

    @NotBlank
    @Column(name = "village", nullable = false)
    private String village;

    @NotBlank
    @Column(name = "state", nullable = false)
    private String state;

    @NotNull
    @Column(name = "area_sq_ft", nullable = false, precision = 12, scale = 2)
    private BigDecimal areaSqFt;

    @NotNull
    @Column(name = "market_value", nullable = false, precision = 15, scale = 2)
    private BigDecimal marketValue;

    @NotBlank
    @Column(name = "land_type", nullable = false)
    private String landType;  // RESIDENTIAL, COMMERCIAL, AGRICULTURAL, INDUSTRIAL

    @Column(name = "description")
    private String description;

    @NotBlank
    @Column(name = "current_owner_id", nullable = false)
    private String currentOwnerId;

    @NotBlank
    @Column(name = "current_owner_name", nullable = false)
    private String currentOwnerName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LandStatus status;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "blockchain_hash")
    private String blockchainHash;        // Hash of the block recording this registration

    @Column(name = "registration_date", nullable = false)
    private LocalDateTime registrationDate;

    @Column(name = "last_transfer_date")
    private LocalDateTime lastTransferDate;

    @Column(name = "block_index")
    private Integer blockIndex;

    public enum LandStatus {
        REGISTERED, UNDER_TRANSFER, DISPUTED, ENCUMBERED, RELEASED
    }
}
