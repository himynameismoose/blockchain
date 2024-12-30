import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class Transaction {
    public String transactionId; // hash of transaction
    public PublicKey sender; // sender's address/public key
    public PublicKey recipient; // recipient's address/public key
    public float value;
    public byte[] signature; // this is to prevent anyone else from spending funds

    public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
    public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

    // transactions counter
    private static int sequence = 0;

    public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
        this.sender = from;
        this.recipient = to;
        this.value = value;
        this.inputs = inputs;
    }

    // Calculate the transaction hash (id)
    private String calculateHash() {
        // increment the sequence to avoid identical transactions
        sequence++;
        return StringUtil.applySHA256(
                StringUtil.getStringFromKey(sender) +
                        StringUtil.getStringFromKey(recipient) +
                        Float.toString(value) +
                        sequence);
    }

    // Signs all the data we do not wish to be tampered with
    public void generateSignature(PrivateKey privateKey) {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + Float.toString(value);
        signature = StringUtil.applyECDSASig(privateKey, data);
    }

    // Verifies the data signed has not been tampered with
    public boolean verifySignature() {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + Float.toString(value);

        return StringUtil.verifyECDSASig(sender, data, signature);
    }

    // Return true if new transaction is created
    public boolean processTransaction() {
        if (!verifySignature()) {
            System.out.println("Transaction Signature failed to verify");
            return false;
        }

        // Get unspent Transaction inputs
        for (TransactionInput ti : inputs) {
            ti.UTXO = BlockChain.UTXOs.get(ti.transactionOutputId);
        }

        // Check if transaction is valid
        if (getInputsValue() < BlockChain.minimumTransaction) {
            System.out.println("Transaction Inputs too small: " + getInputsValue());
            return false;
        }

        // Generate transaction outputs
        float leftOver = getInputsValue() - value;
        transactionId = calculateHash();
        // send value to recipient
        outputs.add(new TransactionOutput(this.recipient, value, transactionId));
        // send left over 'change' back to sender
        outputs.add(new TransactionOutput(this.sender, leftOver, transactionId));

        // add outputs to unspent list
        for (TransactionOutput to: outputs) {
            BlockChain.UTXOs.put(to.id, to);
        }

        // remove transaction inputs from UTXO list as spent
        for (TransactionInput ti : inputs) {
            if (ti.UTXO == null) {
                // if transaction is not found, skip
                continue;
            }

            BlockChain.UTXOs.remove(ti.UTXO.id);
        }

        return true;
    }

    // return sum of inputs (UTXOs) values
    public float getInputsValue() {
        float total = 0;

        for (TransactionInput ti : inputs) {
            if (ti.UTXO == null) {
                // if transaction is not found, skip
                continue;
            }

            total += ti.UTXO.value;
        }

        return total;
    }

    // return sum of outputs
    public float getOutputsValue() {
        float total = 0;

        for (TransactionOutput to : outputs) {
            total += to.value;
        }

        return total;
    }
}
