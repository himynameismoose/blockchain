import java.util.Date;

public class Block {
    public String hash;
    public String previousHash;
    private String data; // message
    private long timeStamp; // in ms

    // constructor
    public Block(String data, String previousHash) {
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }

    public String calculateHash() {
        return StringUtil.applySHA256(previousHash + Long.toString(timeStamp) + data);
    }
}
