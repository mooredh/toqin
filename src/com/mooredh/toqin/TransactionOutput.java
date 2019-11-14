package com.mooredh.toqin;

import java.security.PublicKey;

public class TransactionOutput {
    private String id;
    private PublicKey recipient; //also known as the new owner of these coins.
    private Double amount; //the amount of coins they own
    private String parentTransactionId; //the id of the transaction this output was created in

    public TransactionOutput(PublicKey recipient, Double amount, String parentTransactionId) {
        this.recipient = recipient;
        this.amount = amount;
        this.parentTransactionId = parentTransactionId;
        this.id = StringUtil.hashString(StringUtil.getStringFromKey(recipient)+amount+parentTransactionId);
    }

    //Check if coin belongs to you
    public boolean isMine(PublicKey publicKey) {
        return (publicKey == recipient);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getParentTransactionId() {
        return parentTransactionId;
    }

    public void setParentTransactionId(String parentTransactionId) {
        this.parentTransactionId = parentTransactionId;
    }
}
