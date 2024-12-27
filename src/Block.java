import java.util.Date;

public class Block {
    public String hash;
    public String previousHash;
    private String data; // message
    private long timeStamp; // in ms
    private int nonce;

    // constructor
    public Block(String data, String previousHash) {
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }

    // Calculate new hash based on block contents
    public String calculateHash() {
        return StringUtil.applySHA256(previousHash + Long.toString(timeStamp) + Integer.toString(nonce) + data);
    }

    public void mineBlock(int difficulty) {
        // Create a String with difficulty * "0"
        String target = new String(new char[difficulty]).replace('\0', '0');
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }

        System.out.println("Block Mined!!!: " + hash);
    }
}
