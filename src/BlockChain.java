import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

public class BlockChain {

    public static ArrayList<Block> blockchain = new ArrayList<>();
    // List of unspent transactions
    public static HashMap<String, TransactionOutput> UTXOs = new HashMap<>();
    public static int difficulty = 3;
    public static float minimumTransaction = 0.1f;
    public static Wallet walletA;
    public static Wallet walletB;
    public static Transaction genesisTransaction;

    public static void main(String[] args) {
        // Setup Bouncy Castle as a security Provider
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        // Create wallets
        walletA = new Wallet();
        walletB = new Wallet();
        Wallet coinbase = new Wallet();

        // Create genesis transaction, which sends 100 coin to walletA
        genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 100f, null);
        genesisTransaction.generateSignature(coinbase.privateKey);
        genesisTransaction.transactionId = "0";
        genesisTransaction.outputs.add(new TransactionOutput(
                genesisTransaction.recipient, genesisTransaction.value, genesisTransaction.transactionId));
        UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

        System.out.println("Creating and mining genesis block...");
        Block genesis = new Block("0");
        genesis.addTransaction(genesisTransaction);
        addBlock(genesis);

        // testing
        Block block1 = new Block(genesis.hash);
        System.out.println("\nWallet A balance: " + walletA.getBalance());
        System.out.println("\nWallet A is attempting to send funds (40) to Wallet B...");
        block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
        addBlock(block1);
        System.out.println("\nWallet A's balance: " + walletA.getBalance());
        System.out.println("Wallet B's balance: " + walletB.getBalance());

        Block block2 = new Block(block1.hash);
        System.out.println("\nWallet A is attempting to send more funds (1000) than it has...");
        block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
        addBlock(block2);
        System.out.println("\nWallet A's balance: " + walletA.getBalance());
        System.out.println("Wallet B's balance: " + walletB.getBalance());

        Block block3 = new Block(block2.hash);
        System.out.println("\nWallet B is attempting to send funds (20) to Wallet A...");
        block3.addTransaction(walletB.sendFunds(walletA.publicKey, 20f));
        System.out.println("\nWallet A's balance: " + walletA.getBalance());
        System.out.println("Wallet B's balance: " + walletB.getBalance());

        isChainValid();
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

    public static void addBlock(Block newBlock) {
        newBlock.mineBlock(difficulty);
        blockchain.add(newBlock);
    }
}
