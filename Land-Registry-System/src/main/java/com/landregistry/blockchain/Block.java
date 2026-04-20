package com.landregistry.blockchain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;

/**
 * Represents a single block in the blockchain.
 * Each block stores land transaction data and is cryptographically linked
 * to the previous block via its hash.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Block {

    private int index;
    private long timestamp;
    private String previousHash;
    private String hash;
    private String data;           // JSON-encoded transaction data
    private int nonce;             // Proof-of-work nonce
    private String merkleRoot;     // Root hash of transactions

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public Block(int index, String previousHash, String data) {
        this.index = index;
        this.timestamp = Instant.now().toEpochMilli();
        this.previousHash = previousHash;
        this.data = data;
        this.nonce = 0;
        this.merkleRoot = computeMerkleRoot(data);
        this.hash = calculateHash();
    }

    /**
     * Calculates the SHA-256 hash of the block's contents.
     */
    public String calculateHash() {
        String input = index + timestamp + previousHash + data + nonce + merkleRoot;
        return sha256(input);
    }

    /**
     * Performs Proof-of-Work mining.
     * Adjusts nonce until hash starts with the required number of zeros.
     */
    public void mineBlock(int difficulty) {
        String target = "0".repeat(difficulty);
        while (!hash.startsWith(target)) {
            nonce++;
            hash = calculateHash();
        }
        System.out.println("Block #" + index + " mined! Hash: " + hash);
    }

    /**
     * Computes a simple Merkle root from the data string.
     * In production, this would operate over an array of transactions.
     */
    private String computeMerkleRoot(String data) {
        if (data == null || data.isEmpty()) return sha256("empty");
        return sha256(data);
    }

    public boolean isValid() {
        return hash != null && hash.equals(calculateHash());
    }

    public static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes("UTF-8"));
            return HexFormat.of().formatHex(hashBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error computing SHA-256 hash", e);
        }
    }
}
