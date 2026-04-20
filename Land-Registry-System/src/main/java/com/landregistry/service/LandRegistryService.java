package com.landregistry.service;

import com.landregistry.blockchain.Block;
import com.landregistry.blockchain.Blockchain;
import com.landregistry.dto.BlockchainInfoDTO;
import com.landregistry.dto.LandTransactionDTO;
import com.landregistry.entity.LandParcel;
import com.landregistry.entity.TransactionRecord;
import com.landregistry.exception.DuplicateParcelException;
import com.landregistry.exception.LandParcelNotFoundException;
import com.landregistry.repository.LandParcelRepository;
import com.landregistry.repository.TransactionRecordRepository;
import com.landregistry.util.ParcelIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Core service for land registry operations.
 * Each mutation goes through the blockchain first, then updates the DB cache.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LandRegistryService {

    private final Blockchain blockchain;
    private final LandParcelRepository parcelRepository;
    private final TransactionRecordRepository transactionRepository;

    // ─── Registration ────────────────────────────────────────────────────────

    /**
     * Registers a new land parcel on the blockchain and persists a DB record.
     *
     * @param dto The transaction details for registration
     * @return The created LandParcel entity
     */
    @Transactional
    public LandParcel registerLand(LandTransactionDTO dto) {
        log.info("Registering new land parcel: survey={}", dto.getSurveyNumber());

        if (parcelRepository.existsBySurveyNumber(dto.getSurveyNumber())) {
            throw new DuplicateParcelException("Land parcel with survey number already registered: " + dto.getSurveyNumber());
        }

        // Generate unique parcel ID
        String parcelId = ParcelIdGenerator.generate(dto.getDistrict(), dto.getVillage(), dto.getSurveyNumber());
        dto.setParcelId(parcelId);
        dto.setTransactionId(UUID.randomUUID().toString());
        dto.setTransactionType("REGISTRATION");
        dto.setTransactionDate(LocalDateTime.now());

        // Compute stamp duty and registration fee
        BigDecimal stampDuty = dto.getMarketValue().multiply(new BigDecimal("0.05"));
        BigDecimal regFee = dto.getMarketValue().multiply(new BigDecimal("0.01"));
        dto.setStampDuty(stampDuty);
        dto.setRegistrationFee(regFee);

        // Write to blockchain
        Block block = blockchain.addTransaction(dto);
        log.info("Block mined for REGISTRATION | Block #{} | Hash: {}", block.getIndex(), block.getHash());

        // Persist LandParcel entity (DB cache)
        LandParcel parcel = LandParcel.builder()
                .parcelId(parcelId)
                .surveyNumber(dto.getSurveyNumber())
                .district(dto.getDistrict())
                .village(dto.getVillage())
                .state(dto.getState())
                .areaSqFt(dto.getAreaSqFt())
                .marketValue(dto.getMarketValue())
                .landType(dto.getLandType())
                .description(dto.getDescription())
                .currentOwnerId(dto.getToOwnerId())
                .currentOwnerName(dto.getToOwnerName())
                .status(LandParcel.LandStatus.REGISTERED)
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .blockchainHash(block.getHash())
                .blockIndex(block.getIndex())
                .registrationDate(LocalDateTime.now())
                .build();

        parcelRepository.save(parcel);

        // Persist TransactionRecord
        saveTransactionRecord(dto, block, TransactionRecord.TransactionType.REGISTRATION);

        return parcel;
    }

    // ─── Ownership Transfer ──────────────────────────────────────────────────

    /**
     * Transfers ownership of a land parcel from one person to another.
     *
     * @param parcelId The parcel to transfer
     * @param dto      Transfer details including buyer info and value
     * @return Updated LandParcel
     */
    @Transactional
    public LandParcel transferOwnership(String parcelId, LandTransactionDTO dto) {
        log.info("Transferring ownership | Parcel: {} | From: {} | To: {}", parcelId, dto.getFromOwnerId(), dto.getToOwnerId());

        LandParcel parcel = parcelRepository.findById(parcelId)
                .orElseThrow(() -> new LandParcelNotFoundException("Parcel not found: " + parcelId));

        if (parcel.getStatus() == LandParcel.LandStatus.DISPUTED) {
            throw new IllegalStateException("Cannot transfer land parcel under dispute: " + parcelId);
        }
        if (parcel.getStatus() == LandParcel.LandStatus.ENCUMBERED) {
            throw new IllegalStateException("Cannot transfer encumbered land parcel (mortgage/lien active): " + parcelId);
        }
        if (!parcel.getCurrentOwnerId().equals(dto.getFromOwnerId())) {
            throw new IllegalStateException("Seller is not the current owner of parcel: " + parcelId);
        }

        dto.setParcelId(parcelId);
        dto.setTransactionId(UUID.randomUUID().toString());
        dto.setTransactionType("TRANSFER");
        dto.setTransactionDate(LocalDateTime.now());
        dto.setSurveyNumber(parcel.getSurveyNumber());

        BigDecimal value = dto.getMarketValue() != null ? dto.getMarketValue() : parcel.getMarketValue();
        dto.setMarketValue(value);
        dto.setStampDuty(value.multiply(new BigDecimal("0.05")));
        dto.setRegistrationFee(value.multiply(new BigDecimal("0.01")));

        // Write to blockchain
        Block block = blockchain.addTransaction(dto);
        log.info("Block mined for TRANSFER | Block #{} | Hash: {}", block.getIndex(), block.getHash());

        // Update DB cache
        parcel.setCurrentOwnerId(dto.getToOwnerId());
        parcel.setCurrentOwnerName(dto.getToOwnerName());
        parcel.setMarketValue(value);
        parcel.setStatus(LandParcel.LandStatus.REGISTERED);
        parcel.setBlockchainHash(block.getHash());
        parcel.setBlockIndex(block.getIndex());
        parcel.setLastTransferDate(LocalDateTime.now());

        parcelRepository.save(parcel);
        saveTransactionRecord(dto, block, TransactionRecord.TransactionType.TRANSFER);

        return parcel;
    }

    // ─── Mutation ────────────────────────────────────────────────────────────

    /**
     * Mutation: Legal name or record correction (same owner, no sale).
     */
    @Transactional
    public LandParcel mutateLandRecord(String parcelId, LandTransactionDTO dto) {
        log.info("Mutating land record | Parcel: {}", parcelId);

        LandParcel parcel = parcelRepository.findById(parcelId)
                .orElseThrow(() -> new LandParcelNotFoundException("Parcel not found: " + parcelId));

        dto.setParcelId(parcelId);
        dto.setTransactionId(UUID.randomUUID().toString());
        dto.setTransactionType("MUTATION");
        dto.setTransactionDate(LocalDateTime.now());
        dto.setSurveyNumber(parcel.getSurveyNumber());
        dto.setFromOwnerId(parcel.getCurrentOwnerId());
        dto.setFromOwnerName(parcel.getCurrentOwnerName());

        Block block = blockchain.addTransaction(dto);
        log.info("Block mined for MUTATION | Block #{} | Hash: {}", block.getIndex(), block.getHash());

        // Update only mutable fields
        if (dto.getToOwnerName() != null) parcel.setCurrentOwnerName(dto.getToOwnerName());
        if (dto.getDescription() != null) parcel.setDescription(dto.getDescription());
        parcel.setBlockchainHash(block.getHash());
        parcel.setBlockIndex(block.getIndex());

        parcelRepository.save(parcel);
        saveTransactionRecord(dto, block, TransactionRecord.TransactionType.MUTATION);

        return parcel;
    }

    // ─── Encumbrance ─────────────────────────────────────────────────────────

    /**
     * Marks a land parcel as encumbered (mortgage/lien applied).
     */
    @Transactional
    public LandParcel encumberLand(String parcelId, LandTransactionDTO dto) {
        log.info("Encumbering parcel: {}", parcelId);

        LandParcel parcel = parcelRepository.findById(parcelId)
                .orElseThrow(() -> new LandParcelNotFoundException("Parcel not found: " + parcelId));

        dto.setParcelId(parcelId);
        dto.setTransactionId(UUID.randomUUID().toString());
        dto.setTransactionType("ENCUMBRANCE");
        dto.setTransactionDate(LocalDateTime.now());

        Block block = blockchain.addTransaction(dto);

        parcel.setStatus(LandParcel.LandStatus.ENCUMBERED);
        parcel.setBlockchainHash(block.getHash());
        parcel.setBlockIndex(block.getIndex());

        parcelRepository.save(parcel);
        saveTransactionRecord(dto, block, TransactionRecord.TransactionType.ENCUMBRANCE);

        return parcel;
    }

    // ─── Dispute ─────────────────────────────────────────────────────────────

    /**
     * Files a dispute on a land parcel.
     */
    @Transactional
    public LandParcel fileDispute(String parcelId, LandTransactionDTO dto) {
        log.info("Filing dispute on parcel: {}", parcelId);

        LandParcel parcel = parcelRepository.findById(parcelId)
                .orElseThrow(() -> new LandParcelNotFoundException("Parcel not found: " + parcelId));

        dto.setParcelId(parcelId);
        dto.setTransactionId(UUID.randomUUID().toString());
        dto.setTransactionType("DISPUTE_FILED");
        dto.setTransactionDate(LocalDateTime.now());

        Block block = blockchain.addTransaction(dto);

        parcel.setStatus(LandParcel.LandStatus.DISPUTED);
        parcel.setBlockchainHash(block.getHash());
        parcel.setBlockIndex(block.getIndex());

        parcelRepository.save(parcel);
        saveTransactionRecord(dto, block, TransactionRecord.TransactionType.DISPUTE_FILED);

        return parcel;
    }

    // ─── Queries ─────────────────────────────────────────────────────────────

    public LandParcel getParcelById(String parcelId) {
        return parcelRepository.findById(parcelId)
                .orElseThrow(() -> new LandParcelNotFoundException("Parcel not found: " + parcelId));
    }

    public LandParcel getParcelBySurveyNumber(String surveyNumber) {
        return parcelRepository.findBySurveyNumber(surveyNumber)
                .orElseThrow(() -> new LandParcelNotFoundException("Parcel not found with survey number: " + surveyNumber));
    }

    public List<LandParcel> getParcelsByOwner(String ownerId) {
        return parcelRepository.findByCurrentOwnerId(ownerId);
    }

    public List<LandParcel> getParcelsByDistrict(String district) {
        return parcelRepository.findByDistrict(district);
    }

    public List<LandParcel> getAllParcels() {
        return parcelRepository.findAll();
    }

    public List<TransactionRecord> getTransactionHistory(String parcelId) {
        return transactionRepository.findByParcelIdOrderByTransactionDateDesc(parcelId);
    }

    public List<Block> getBlockchainHistoryForParcel(String parcelId) {
        return blockchain.getTransactionsByLandParcel(parcelId);
    }

    // ─── Blockchain Info ─────────────────────────────────────────────────────

    public BlockchainInfoDTO getBlockchainInfo() {
        Block latest = blockchain.getLatestBlock();
        return BlockchainInfoDTO.builder()
                .chainLength(blockchain.getChainLength())
                .chainValid(blockchain.isChainValid())
                .latestBlockHash(latest.getHash())
                .latestBlockTimestamp(latest.getTimestamp())
                .latestBlockIndex(latest.getIndex())
                .difficulty(2)
                .totalTransactions(transactionRepository.count())
                .build();
    }

    public List<Block> getAllBlocks() {
        return blockchain.getChain();
    }

    public boolean validateBlockchain() {
        return blockchain.isChainValid();
    }

    // ─── Internal Helpers ────────────────────────────────────────────────────

    private void saveTransactionRecord(LandTransactionDTO dto, Block block, TransactionRecord.TransactionType type) {
        TransactionRecord record = TransactionRecord.builder()
                .parcelId(dto.getParcelId())
                .transactionType(type)
                .fromOwnerId(dto.getFromOwnerId())
                .fromOwnerName(dto.getFromOwnerName())
                .toOwnerId(dto.getToOwnerId())
                .toOwnerName(dto.getToOwnerName())
                .transactionValue(dto.getMarketValue())
                .stampDuty(dto.getStampDuty())
                .registrationFee(dto.getRegistrationFee())
                .transactionDate(dto.getTransactionDate())
                .blockHash(block.getHash())
                .blockIndex(block.getIndex())
                .previousBlockHash(block.getPreviousHash())
                .initiatedBy(dto.getInitiatedBy() != null ? dto.getInitiatedBy() : "SYSTEM")
                .witnessId(dto.getWitnessId())
                .notes(dto.getNotes())
                .status(TransactionRecord.TransactionStatus.CONFIRMED)
                .build();

        transactionRepository.save(record);
    }
}
