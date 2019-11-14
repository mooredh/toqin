package com.mooredh.toqin;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class StringUtil {
    public static String hashString(String originalString) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            byte[] encodedHash = digest.digest(originalString.getBytes(StandardCharsets.UTF_8));

            return bytesToHex(encodedHash);
        } catch (NoSuchAlgorithmException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static  String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte hashByte : hash) {
            String hex = Integer.toHexString(0xff & hashByte);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }

    public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
        Signature dsa;
        byte[] output;
        try {
            dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initSign(privateKey);
            byte[] strByte = input.getBytes();
            dsa.update(strByte);
            output = dsa.sign();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return output;
    }

    public static String getMerkleRoot(List<Transaction> transactions) {
        int count = transactions.size();
        List<String> previousTreeLayer = new ArrayList<>();
        for(Transaction transaction : transactions) {
            previousTreeLayer.add(transaction.getTransactionId());
        }
        List<String> treeLayer = previousTreeLayer;
        while(count > 1) {
            treeLayer = new ArrayList<>();
            for(int i=1; i < previousTreeLayer.size(); i++) {
                treeLayer.add(hashString(previousTreeLayer.get(i-1) + previousTreeLayer.get(i)));
            }
            count = treeLayer.size();
            previousTreeLayer = treeLayer;
        }
        return (treeLayer.size() == 1) ? treeLayer.get(0) : "";
    }

    //Verifies a String signature
    public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
        try {
            Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(data.getBytes());
            return ecdsaVerify.verify(signature);
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getStringFromKey(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }
}
