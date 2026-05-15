package com.landregistry.controller;

import com.landregistry.blockchain.Block;
import com.landregistry.dto.ApiResponse;
import com.landregistry.dto.BlockchainInfoDTO;
import com.landregistry.dto.LandTransactionDTO;
import com.landregistry.entity.LandParcel;
import com.landregistry.entity.TransactionRecord;
import com.landregistry.service.LandRegistryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API Controller for the Blockchain-based Land Registry System.
 * Base URL: /api/land
 */
@Slf4j
@RestController
@RequestMapping("/api/land")
@RequiredArgsConstructor
public class LandRegistryController {

    private final LandRegistryService landRegistryService;

    // ─── Land Registration ───────────────────────────────────────────────────

    /**
     * POST /api/land/register
     * Registers a new land parcel on the blockchain.
     * Role: REGISTRAR, ADMIN
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<LandParcel>> registerLand(
            @Valid @RequestBody LandTransactionDTO dto) {
        log.info("POST /api/land/register | Survey: {}", dto.getSurveyNumber());
        LandParcel parcel = landRegistryService.registerLand(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Land parcel registered successfully on blockchain", parcel));
    }

    // ─── Ownership Transfer ──────────────────────────────────────────────────

    /**
     * POST /api/land/transfer/{parcelId}
     * Transfers land ownership (sale/gift deed).
     * Role: REGISTRAR, ADMIN
     */
    @PostMapping("/transfer/{parcelId}")
    @PreAuthorize("hasAnyRole('REGISTRAR', 'ADMIN')")
    public ResponseEntity<ApiResponse<LandParcel>> transferOwnership(
            @PathVariable String parcelId,
            @Valid @RequestBody LandTransactionDTO dto) {
        log.info("POST /api/land/transfer/{}", parcelId);
        LandParcel parcel = landRegistryService.transferOwnership(parcelId, dto);
        return ResponseEntity.ok(ApiResponse.success("Ownership transferred and recorded on blockchain", parcel));
    }

    // ─── Mutation ────────────────────────────────────────────────────────────

    /**
     * POST /api/land/mutate/{parcelId}
     * Mutates (corrects/updates) land record details.
     * Role: REGISTRAR, ADMIN
     */
    @PostMapping("/mutate/{parcelId}")
    @PreAuthorize("hasAnyRole('REGISTRAR', 'ADMIN')")
    public ResponseEntity<ApiResponse<LandParcel>> mutateLand(
            @PathVariable String parcelId,
            @RequestBody LandTransactionDTO dto) {
        log.info("POST /api/land/mutate/{}", parcelId);
        LandParcel parcel = landRegistryService.mutateLandRecord(parcelId, dto);
        return ResponseEntity.ok(ApiResponse.success("Land record mutated and recorded on blockchain", parcel));
    }

    // ─── Encumbrance ─────────────────────────────────────────────────────────

    /**
     * POST /api/land/encumber/{parcelId}
     * Marks land as encumbered (mortgage/lien).
     */
    @PostMapping("/encumber/{parcelId}")
    @PreAuthorize("hasAnyRole('REGISTRAR', 'ADMIN')")
    public ResponseEntity<ApiResponse<LandParcel>> encumberLand(
            @PathVariable String parcelId,
            @RequestBody LandTransactionDTO dto) {
        log.info("POST /api/land/encumber/{}", parcelId);
        LandParcel parcel = landRegistryService.encumberLand(parcelId, dto);
        return ResponseEntity.ok(ApiResponse.success("Encumbrance recorded on blockchain", parcel));
    }

    // ─── Dispute ─────────────────────────────────────────────────────────────

    /**
     * POST /api/land/dispute/{parcelId}
     * Files a dispute on a land parcel.
     */
    @PostMapping("/dispute/{parcelId}")
    @PreAuthorize("hasAnyRole('OFFICER', 'REGISTRAR', 'ADMIN')")
    public ResponseEntity<ApiResponse<LandParcel>> fileDispute(
            @PathVariable String parcelId,
            @RequestBody LandTransactionDTO dto) {
        log.info("POST /api/land/dispute/{}", parcelId);
        LandParcel parcel = landRegistryService.fileDispute(parcelId, dto);
        return ResponseEntity.ok(ApiResponse.success("Dispute filed and recorded on blockchain", parcel));
    }

    // ─── Queries ─────────────────────────────────────────────────────────────

    /**
     * GET /api/land/{parcelId}
     * Fetches land parcel by its unique ID.
     */
    @GetMapping("/{parcelId}")
    public ResponseEntity<ApiResponse<LandParcel>> getParcelById(@PathVariable String parcelId) {
        LandParcel parcel = landRegistryService.getParcelById(parcelId);
        return ResponseEntity.ok(ApiResponse.success("Parcel retrieved successfully", parcel));
    }

    /**
     * GET /api/land/survey/{surveyNumber}
     * Fetches land parcel by survey number.
     */
    @GetMapping("/survey/{surveyNumber}")
    public ResponseEntity<ApiResponse<LandParcel>> getParcelBySurvey(@PathVariable String surveyNumber) {
        LandParcel parcel = landRegistryService.getParcelBySurveyNumber(surveyNumber);
        return ResponseEntity.ok(ApiResponse.success("Parcel retrieved by survey number", parcel));
    }

    /**
     * GET /api/land/owner/{ownerId}
     * Returns all parcels owned by a person.
     */
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<ApiResponse<List<LandParcel>>> getParcelsByOwner(@PathVariable String ownerId) {
        List<LandParcel> parcels = landRegistryService.getParcelsByOwner(ownerId);
        return ResponseEntity.ok(ApiResponse.success("Parcels retrieved for owner: " + ownerId, parcels));
    }

    /**
     * GET /api/land/district/{district}
     * Returns all land parcels in a district.
     */
    @GetMapping("/district/{district}")
    public ResponseEntity<ApiResponse<List<LandParcel>>> getParcelsByDistrict(@PathVariable String district) {
        List<LandParcel> parcels = landRegistryService.getParcelsByDistrict(district);
        return ResponseEntity.ok(ApiResponse.success("Parcels retrieved for district: " + district, parcels));
    }

    /**
     * GET /api/land
     * Returns all land parcels (admin view).
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<LandParcel>>> getAllParcels() {
        List<LandParcel> parcels = landRegistryService.getAllParcels();
        return ResponseEntity.ok(ApiResponse.success("All parcels retrieved", parcels));
    }

    // ─── Transaction History ─────────────────────────────────────────────────

    /**
     * GET /api/land/{parcelId}/history
     * Returns the full transaction history (DB) of a parcel.
     */
    @GetMapping("/{parcelId}/history")
    public ResponseEntity<ApiResponse<List<TransactionRecord>>> getHistory(@PathVariable String parcelId) {
        List<TransactionRecord> history = landRegistryService.getTransactionHistory(parcelId);
        return ResponseEntity.ok(ApiResponse.success("Transaction history retrieved", history));
    }

    /**
     * GET /api/land/{parcelId}/blockchain-history
     * Returns raw blockchain blocks for a parcel.
     */
    @GetMapping("/{parcelId}/blockchain-history")
    public ResponseEntity<ApiResponse<List<Block>>> getBlockchainHistory(@PathVariable String parcelId) {
        List<Block> blocks = landRegistryService.getBlockchainHistoryForParcel(parcelId);
        return ResponseEntity.ok(ApiResponse.success("Blockchain history retrieved", blocks));
    }

    // ─── Blockchain Info ─────────────────────────────────────────────────────

    /**
     * GET /api/land/blockchain/info
     * Returns current blockchain state and statistics.
     */
    @GetMapping("/blockchain/info")
    public ResponseEntity<ApiResponse<BlockchainInfoDTO>> getBlockchainInfo() {
        BlockchainInfoDTO info = landRegistryService.getBlockchainInfo();
        return ResponseEntity.ok(ApiResponse.success("Blockchain info retrieved", info));
    }

    /**
     * GET /api/land/blockchain/all
     * Returns all blocks in the chain. Admin only.
     */
    @GetMapping("/blockchain/all")
    public ResponseEntity<ApiResponse<List<Block>>> getAllBlocks() {
        List<Block> blocks = landRegistryService.getAllBlocks();
        return ResponseEntity.ok(ApiResponse.success("All blockchain blocks retrieved", blocks));
    }

    /**
     * GET /api/land/blockchain/validate
     * Validates blockchain integrity. Admin only.
     */
    @GetMapping("/blockchain/validate")
    public ResponseEntity<ApiResponse<Boolean>> validateChain() {
        boolean valid = landRegistryService.validateBlockchain();
        String msg = valid ? "Blockchain integrity verified — chain is valid" : "WARNING: Blockchain integrity check FAILED";
        return ResponseEntity.ok(ApiResponse.success(msg, valid));
    }
}
