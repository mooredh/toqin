package com.mooredh.toqin;

import java.security.PublicKey;
import java.util.*;

public class Blockchain {
    public Blockchain(Block genesisBlock) {
        this.blocks = new ArrayList<>();
        this.difficulty = 5;
        genesisBlock.mineBlock(this.difficulty);
        this.blocks.add(genesisBlock);
    }

    private List<Block> blocks;
    public static Wallet coinbase = new Wallet();
    private Integer difficulty;
    public static Double minimumTransaction = 0.00001d;
    public static Map<String, TransactionOutput> UTXOs = new HashMap<>();

    public Block getLatestBlock() {
        return this.blocks.get(this.blocks.size() - 1);
    }

    public void addBlock(Block latestBlock, PublicKey miner) {
        Transaction genesisTransaction = new Transaction(Blockchain.coinbase.getPublicKey(), miner, 0.01d, null);
        genesisTransaction.generateSignature(Blockchain.coinbase.getPrivateKey());
        genesisTransaction.setTransactionId(genesisTransaction.calculateHash());
        genesisTransaction.addOutput(new TransactionOutput(genesisTransaction.getRecipient(), genesisTransaction.getAmount(), genesisTransaction.getTransactionId()));
        Blockchain.UTXOs.put(genesisTransaction.getOutputs().get(0).getId(), genesisTransaction.getOutputs().get(0));
        latestBlock.setPreviousHash(this.getLatestBlock().getHash());
        latestBlock.mineBlock(this.difficulty);
        this.blocks.add(latestBlock);
    }

    public boolean isChainValid() {
        for (int i = 1; i < this.blocks.size(); i++) {
            Block currentBlock = this.blocks.get(i);
            Block previousBlock = this.blocks.get(i - 1);

            Map<String,TransactionOutput> tempUTXOs = new HashMap<>();
            Transaction genesisTransaction = this.blocks.get(0).getTransactions().get(0);
            tempUTXOs.put(genesisTransaction.getOutputs().get(0).getId(), genesisTransaction.getOutputs().get(0));


            if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
                return false;
            }

            if (!currentBlock.getPreviousHash().equals(previousBlock.getHash())) {
                return false;
            }

            if(!currentBlock.getHash().substring( 0, difficulty).equals("0".repeat(this.difficulty))) {
                return false;
            }

            TransactionOutput tempOutput;
            for(int t=0; t <currentBlock.getTransactions().size(); t++) {
                Transaction currentTransaction = currentBlock.getTransactions().get(t);

                if(!currentTransaction.verifiySignature()) {
                    System.out.println("#Signature on Transaction(" + t + ") is Invalid");
                    return false;
                }
                if(!currentTransaction.getInputsValue().equals(currentTransaction.getOutputsValue())) {
                    System.out.println("#Inputs are note equal to outputs on Transaction(" + t + ")");
                    return false;
                }

                for(TransactionInput input: currentTransaction.getInputs()) {
                    tempOutput = tempUTXOs.get(input.getTransactionOutputId());

                    if(tempOutput == null) {
                        System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
                        return false;
                    }

                    if(!input.getUTXO().getAmount().equals(tempOutput.getAmount())) {
                        System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
                        return false;
                    }

                    tempUTXOs.remove(input.getTransactionOutputId());
                }

                for(TransactionOutput output: currentTransaction.getOutputs()) {
                    tempUTXOs.put(output.getId(), output);
                }

                if( currentTransaction.getOutputs().get(0).getRecipient() != currentTransaction.getRecipient()) {
                    System.out.println("#Transaction(" + t + ") output reciepient is not who it should be");
                    return false;
                }
                if( currentTransaction.getOutputs().get(1).getRecipient() != currentTransaction.getSender()) {
                    System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
                    return false;
                }

            }
        }

        return true;
    }
}
