import java.security.PublicKey;

public class TransactionOutput {
    public String id;
    public PublicKey recipient; // owner of the coins
    public float value; // amount of coins owned
    public String parentTransactionId; // id of transaction (output) created

    public TransactionOutput(PublicKey recipient, float value, String parentTransactionId) {
        this.recipient = recipient;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.id = StringUtil.applySHA256(StringUtil.getStringFromKey(recipient) + Float.toString(value) + parentTransactionId);
    }

    // Check if coin belongs to owner
    public boolean isMine(PublicKey publicKey) {
        return publicKey == recipient;
    }
}
