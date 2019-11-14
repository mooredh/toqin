package com.mooredh.toqin;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class Transaction {
    public Transaction(PublicKey sender, PublicKey recipient, Double amount, List<TransactionInput> inputs) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.inputs = inputs;
    }

    private PublicKey sender;
    private PublicKey recipient;
    private Double amount;
    private String transactionId;
    private byte[] signature;
    private List<TransactionInput> inputs;
    private List<TransactionOutput> outputs = new ArrayList<>();

    private static Integer sequence = 0;

    public String calculateHash() {
        sequence++;
        return  StringUtil.hashString(StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + amount + sequence);
    }

    public void generateSignature(PrivateKey privateKey) {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + this.amount;
        signature = StringUtil.applyECDSASig(privateKey, data);
    }

    public boolean verifiySignature() {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + this.amount;
        return StringUtil.verifyECDSASig(sender, data, signature);
    }

    public boolean processTransaction() {
        if (!this.verifiySignature()) return false;

        for(TransactionInput input : inputs) {
            input.setUTXO(Blockchain.UTXOs.get(input.getTransactionOutputId()));
        }

        //check if transaction is valid:
        if (getInputsValue() < Blockchain.minimumTransaction) {
            return false;
        }

        Double leftOver = getInputsValue() - this.amount;
        this.transactionId = this.calculateHash();
        this.outputs.add(new TransactionOutput(this.recipient, this.amount, this.transactionId));
        this.outputs.add(new TransactionOutput(this.sender, leftOver, this.transactionId));

        for(TransactionOutput o : outputs) {
            Blockchain.UTXOs.put(o.getId() , o);
        }

        for(TransactionInput input : inputs) {
            if(input.getUTXO() == null) continue;
            Blockchain.UTXOs.remove(input.getUTXO().getId());
        }

        return true;
    }

    public Double getInputsValue() {
        Double total = 0d;
        for(TransactionInput i : inputs) {
            if(i.getUTXO() == null) continue; //if Transaction can't be found skip it
            total += i.getUTXO().getAmount();
        }
        return total;
    }

    public Double getOutputsValue() {
        Double total = 0d;
        for(TransactionOutput o : outputs) {
            total += o.getAmount();
        }
        return total;
    }

    public PublicKey getSender() {
        return sender;
    }

    public void setSender(PublicKey sender) {
        this.sender = sender;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public List<TransactionInput> getInputs() {
        return inputs;
    }

    public void addInput(TransactionInput input) {
        this.inputs.add(input);
    }

    public List<TransactionOutput> getOutputs() {
        return outputs;
    }

    public void addOutput(TransactionOutput output) {
        this.outputs.add(output);
    }

    public PublicKey getRecipient() {
        return recipient;
    }

    public void setRecipient(PublicKey recipient) {
        this.recipient = recipient;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
