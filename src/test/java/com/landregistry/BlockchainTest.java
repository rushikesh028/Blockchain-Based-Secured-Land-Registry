package com.landregistry;

import com.landregistry.blockchain.Block;
import com.landregistry.blockchain.Blockchain;
import com.landregistry.dto.LandTransactionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the core Blockchain logic.
 */
class BlockchainTest {

    private Blockchain blockchain;

    @BeforeEach
    void setUp() {
        blockchain = new Blockchain();
    }

    @Test
    @DisplayName("Genesis block should be created on initialization")
    void testGenesisBlockCreated() {
        assertEquals(1, blockchain.getChainLength());
        Block genesis = blockchain.getChain().get(0);
        assertEquals(0, genesis.getIndex());
        assertEquals("0", genesis.getPreviousHash());
        assertNotNull(genesis.getHash());
    }

    @Test
    @DisplayName("Genesis block hash should be valid")
    void testGenesisBlockHashValid() {
        Block genesis = blockchain.getChain().get(0);
        assertTrue(genesis.isValid(), "Genesis block should be valid");
    }

    @Test
    @DisplayName("New transaction block should be added to chain")
    void testAddTransaction() {
        LandTransactionDTO dto = buildSampleTransaction();
        Block block = blockchain.addTransaction(dto);

        assertEquals(2, blockchain.getChainLength());
        assertEquals(1, block.getIndex());
        assertNotNull(block.getHash());
        assertNotNull(block.getData());
        assertTrue(block.getData().contains("SRV-001"));
    }

    @Test
    @DisplayName("Blockchain should be valid after adding transactions")
    void testChainValidAfterAdditions() {
        blockchain.addTransaction(buildSampleTransaction());
        blockchain.addTransaction(buildSampleTransaction());

        assertTrue(blockchain.isChainValid(), "Chain should be valid after adding blocks");
    }

    @Test
    @DisplayName("Tampering with a block should invalidate the chain")
    void testTamperingInvalidatesChain() {
        blockchain.addTransaction(buildSampleTransaction());
        blockchain.addTransaction(buildSampleTransaction());

        // Tamper: directly modify the data in block 1
        blockchain.getChain().get(1).setData("TAMPERED DATA - Fraudulent record");

        assertFalse(blockchain.isChainValid(), "Tampered chain should be invalid");
    }

    @Test
    @DisplayName("Block hashes should be linked correctly")
    void testBlockLinking() {
        blockchain.addTransaction(buildSampleTransaction());
        blockchain.addTransaction(buildSampleTransaction());

        Block block0 = blockchain.getChain().get(0);
        Block block1 = blockchain.getChain().get(1);
        Block block2 = blockchain.getChain().get(2);

        assertEquals(block0.getHash(), block1.getPreviousHash(), "Block 1 should link to Block 0");
        assertEquals(block1.getHash(), block2.getPreviousHash(), "Block 2 should link to Block 1");
    }

    @Test
    @DisplayName("Mined blocks should have hash with required difficulty prefix")
    void testProofOfWork() {
        blockchain.addTransaction(buildSampleTransaction());
        Block block = blockchain.getChain().get(1);
        assertTrue(block.getHash().startsWith("00"),
                "Mined block hash should start with '00' (difficulty=2)");
    }

    @Test
    @DisplayName("Transaction history should be retrievable by parcel ID")
    void testGetTransactionsByParcel() {
        LandTransactionDTO dto = buildSampleTransaction();
        dto.setParcelId("PARCEL-XYZ-123");
        blockchain.addTransaction(dto);

        var blocks = blockchain.getTransactionsByLandParcel("PARCEL-XYZ-123");
        assertEquals(1, blocks.size());
    }

    @Test
    @DisplayName("Block SHA-256 hash should be deterministic")
    void testHashDeterminism() {
        String input = "test-data-12345";
        String hash1 = Block.sha256(input);
        String hash2 = Block.sha256(input);
        assertEquals(hash1, hash2, "SHA-256 should produce the same hash for the same input");
        assertEquals(64, hash1.length(), "SHA-256 hash should be 64 hex characters");
    }

    // ─── Helper ──────────────────────────────────────────────

    private LandTransactionDTO buildSampleTransaction() {
        return LandTransactionDTO.builder()
                .transactionId("TXN-001")
                .parcelId("DIST-abc123")
                .surveyNumber("SRV-001")
                .transactionType("REGISTRATION")
                .toOwnerId("OWNER-001")
                .toOwnerName("Rajesh Kumar")
                .district("Mumbai")
                .village("Andheri")
                .state("Maharashtra")
                .areaSqFt(new BigDecimal("1200.00"))
                .marketValue(new BigDecimal("5000000.00"))
                .landType("RESIDENTIAL")
                .transactionDate(LocalDateTime.now())
                .initiatedBy("registrar")
                .build();
    }
}
