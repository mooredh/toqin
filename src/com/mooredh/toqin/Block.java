package com.mooredh.toqin;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Block {
    public Block(String previousHash) {
        this.timestamp = (new Date()).getTime();
        this.previousHash = previousHash;
        this.nonce = 0;
        this.transactions = new ArrayList<>();
        this.hash = this.calculateHash();
    }

    private Long timestamp;
    private List<Transaction> transactions;
    private String merkleRoot;
    private String hash;
    private String previousHash;
    private Integer nonce;

    public String calculateHash() {
        return StringUtil.hashString(this.nonce + this.timestamp.toString() + this.merkleRoot + this.previousHash);
    }

    public void mineBlock(int difficulty) {
        merkleRoot = StringUtil.getMerkleRoot(transactions);
        while(!this.hash.substring(0, difficulty).equals("0".repeat(difficulty))) {
            this.nonce++;
            this.hash = this.calculateHash();
        }


    }

    public Long getTimestamp() {
        return timestamp;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public boolean addTransaction(Transaction transaction) {
        if(transaction == null) return false;
        if(!previousHash.equals("0")) {
            if(!transaction.processTransaction()) {
                return false;
            }
        }
        this.transactions.add(transaction);
        return true;
    }

    public String getHash() {
        return hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }
}
