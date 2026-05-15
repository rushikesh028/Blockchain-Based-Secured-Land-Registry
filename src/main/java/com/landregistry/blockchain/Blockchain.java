package com.landregistry.blockchain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.landregistry.dto.LandTransactionDTO;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Core Blockchain class managing the chain of land registry blocks.
 * Implements a simplified blockchain with Proof-of-Work consensus.
 */
@Component
public class Blockchain {

    @Getter
    private final List<Block> chain;

    private static final int DIFFICULTY = 2; // Number of leading zeros required
    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    public Blockchain() {
        this.chain = new ArrayList<>();
        chain.add(createGenesisBlock());
    }

    /**
     * Creates the very first block (Genesis Block).
     */
    private Block createGenesisBlock() {
        Block genesis = new Block(0, "0", "Genesis Block - Land Registry System Initialized");
        genesis.mineBlock(DIFFICULTY);
        return genesis;
    }

    /**
     * Returns the most recently added block.
     */
    public Block getLatestBlock() {
        return chain.get(chain.size() - 1);
    }

    /**
     * Adds a new land transaction to the blockchain.
     *
     * @param transaction The land transaction data
     * @return The newly created block
     */
    public Block addTransaction(LandTransactionDTO transaction) {
        try {
            String transactionData = MAPPER.writeValueAsString(transaction);
            Block newBlock = new Block(chain.size(), getLatestBlock().getHash(), transactionData);
            newBlock.mineBlock(DIFFICULTY);
            chain.add(newBlock);
            return newBlock;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize transaction data", e);
        }
    }

    /**
     * Validates the entire blockchain for integrity.
     * Checks that each block's hash is correct and properly linked.
     *
     * @return true if the chain is valid, false otherwise
     */
    public boolean isChainValid() {
        for (int i = 1; i < chain.size(); i++) {
            Block current = chain.get(i);
            Block previous = chain.get(i - 1);

            // Verify current block's hash
            if (!current.getHash().equals(current.calculateHash())) {
                System.err.println("Block #" + i + " hash is invalid!");
                return false;
            }

            // Verify chain linkage
            if (!current.getPreviousHash().equals(previous.getHash())) {
                System.err.println("Block #" + i + " is not properly linked to Block #" + (i - 1) + "!");
                return false;
            }
        }
        return true;
    }

    /**
     * Retrieves the full transaction history for a specific land parcel.
     *
     * @param landParcelId The ID of the land parcel
     * @return List of blocks related to that parcel
     */
    public List<Block> getTransactionsByLandParcel(String landParcelId) {
        List<Block> results = new ArrayList<>();
        for (Block block : chain) {
            if (block.getData() != null && block.getData().contains(landParcelId)) {
                results.add(block);
            }
        }
        return results;
    }

    /**
     * Retrieves a block by its index.
     */
    public Optional<Block> getBlockByIndex(int index) {
        if (index >= 0 && index < chain.size()) {
            return Optional.of(chain.get(index));
        }
        return Optional.empty();
    }

    /**
     * Retrieves a block by its hash.
     */
    public Optional<Block> getBlockByHash(String hash) {
        return chain.stream()
                .filter(b -> b.getHash().equals(hash))
                .findFirst();
    }

    /**
     * Returns the current length of the chain.
     */
    public int getChainLength() {
        return chain.size();
    }
}
