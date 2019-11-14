package com.mooredh.toqin;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Wallet {
    public Wallet() {
        this.generateKeyPair();
    }

    private PrivateKey privateKey;
    private PublicKey publicKey;
    private Map<String,TransactionOutput> UTXOs = new HashMap<>();

    public Double getBalance() {
        Double total = 0d;
        for (TransactionOutput UTXO : Blockchain.UTXOs.values()){
            if(UTXO.isMine(publicKey)) {
                UTXOs.put(UTXO.getId(),UTXO);
                total += UTXO.getAmount();
            }
        }
        return total;
    }

    public Transaction sendFunds(PublicKey _recipient,Double amount) {
        if(getBalance() < amount) {
            System.out.println("#Not Enough funds to send transaction. Transaction Discarded.");
            return null;
        }

        //create array list of inputs
        List<TransactionInput> inputs = new ArrayList<>();

        Double total = 0d;
        for (TransactionOutput UTXO: UTXOs.values()){
            total += UTXO.getAmount();
            inputs.add(new TransactionInput(UTXO.getId()));
            if(total > amount) break;
        }

        Transaction newTransaction = new Transaction(this.publicKey, _recipient , amount, inputs);
        newTransaction.generateSignature(this.privateKey);

        for(TransactionInput input: inputs){
            UTXOs.remove(input.getTransactionOutputId());
        }

        return newTransaction;
    }

    private void generateKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecGenParameterSpec = new ECGenParameterSpec("prime192v1");
            keyPairGenerator.initialize(ecGenParameterSpec, random);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            this.privateKey = keyPair.getPrivate();
            this.publicKey = keyPair.getPublic();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }
}
