package com.landregistry.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing the current state of the blockchain.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlockchainInfoDTO {
    private int chainLength;
    private boolean chainValid;
    private String latestBlockHash;
    private long latestBlockTimestamp;
    private int latestBlockIndex;
    private int difficulty;
    private long totalTransactions;
}
