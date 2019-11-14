package com.mooredh.toqin;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;

public class Main {


    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());
        // write your code here
        Wallet walletA = new Wallet();
        Wallet walletB = new Wallet();
        Wallet walletC = new Wallet();

        Transaction genesisTransaction = new Transaction(Blockchain.coinbase.getPublicKey(), walletA.getPublicKey(), 100d, null);
        genesisTransaction.generateSignature(Blockchain.coinbase.getPrivateKey());
        genesisTransaction.setTransactionId("0");
        genesisTransaction.addOutput(new TransactionOutput(genesisTransaction.getRecipient(), genesisTransaction.getAmount(), genesisTransaction.getTransactionId()));
        Blockchain.UTXOs.put(genesisTransaction.getOutputs().get(0).getId(), genesisTransaction.getOutputs().get(0));

        System.out.println("Creating and Mining Genesis block... ");
        Block genesis = new Block("0");
        genesis.addTransaction(genesisTransaction);

        Blockchain blockchain = new Blockchain(genesis);

        System.out.println(blockchain.isChainValid());

        Block block1 = new Block(genesis.getHash());
        System.out.println("WalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletA is Attempting to send 40 toqins to WalletB...");
        block1.addTransaction(walletA.sendFunds(walletB.getPublicKey(), 40d));
        System.out.println("This block is being mined by wallet C");
        blockchain.addBlock(block1, walletC.getPublicKey());
        System.out.println("WalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());
        System.out.println("WalletC's balance is: " + walletC.getBalance());

        System.out.println(blockchain.isChainValid());

        Block block2 = new Block(block1.getHash());
        System.out.println("WalletB is Attempting to send 25 toqins to WalletC...");
        block1.addTransaction(walletB.sendFunds(walletC.getPublicKey(), 25d));
        System.out.println("This block is being mined by wallet A");
        blockchain.addBlock(block2, walletA.getPublicKey());
        System.out.println("WalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());
        System.out.println("WalletC's balance is: " + walletC.getBalance());

        System.out.println(blockchain.isChainValid());
    }
}
