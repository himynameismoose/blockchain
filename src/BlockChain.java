import com.google.gson.GsonBuilder;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

public class BlockChain {

    public static ArrayList<Block> blockchain = new ArrayList<>();
    // List of unspent transactions
    public static HashMap<String, TransactionOutput> UTXOs = new HashMap<>();
    public static int difficulty = 5;
    public static Wallet walletA;
    public static Wallet walletB;

    public static void main(String[] args) {
        // Setup Bouncy Castle as a security Provider
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        // Create wallets
        walletA = new Wallet();
        walletB = new Wallet();

        // Test public and private keys
        System.out.println("Private and public keys:");
        System.out.println(StringUtil.getStringFromKey(walletA.privateKey));
        System.out.println(StringUtil.getStringFromKey(walletA.publicKey));

        // Create a test transaction from walletA to walletB
        Transaction transaction = new Transaction(walletA.publicKey, walletB.publicKey, 5, null);
        transaction.generateSignature(walletA.privateKey);

        // Verify the signature works and verify it from the public key
        System.out.println("Is signature verified: " + transaction.verifySignature());
    }

    public static void blockchainTest() {
        // Add blocks to blockchain ArrayList
        blockchain.add(new Block("Hi, I'm the first block", "0"));
        System.out.println("Trying to Mine Block 1...");
        blockchain.get(0).mineBlock(difficulty);

        blockchain.add(new Block("Yo, I'm the second block", blockchain.get(blockchain.size() - 1).hash));
        System.out.println("Trying to Mine Block 2...");
        blockchain.get(1).mineBlock(difficulty);

        blockchain.add(new Block("Hey, I'm the third block", blockchain.get(blockchain.size() - 1).hash));
        System.out.println("Trying to Mine Block 3...");
        blockchain.get(2).mineBlock(difficulty);

        System.out.println("\nBlockchain is Valid: " + isChainValid());

        String blockchainJSON = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
        System.out.println("\nThe block chain: ");
        System.out.println(blockchainJSON);
    }

    public static Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');

        // Loop through blockchain to check hashes
        for (int i = 1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i - 1);

            // Compare registered hash and calculated hash
            if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
                System.out.println("Current Hashes not equal");
                return false;
            }

            // Compare previous hash and registered previous hash
            if (!previousBlock.hash.equals(currentBlock.previousHash)) {
                System.out.println("Previous Hashes not equal");
                return false;
            }

            // Check if hash is solved
            if (!currentBlock.hash.substring(0, difficulty).equals(hashTarget)) {
                System.out.println("This block hasn't been mined");
                return false;
            }
        }

        return true;
    }
}
