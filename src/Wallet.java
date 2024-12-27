import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallet {
    public PrivateKey privateKey;
    public PublicKey publicKey;
    public HashMap<String, TransactionOutput> UTXOs = new HashMap<>();

    public Wallet() {
        generateKeyPair();
    }

    public void generateKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecGenParameterSpec = new ECGenParameterSpec("prime192v1");

            // Initiate the key generator and generate a KeyPair
            keyPairGenerator.initialize(ecGenParameterSpec, secureRandom);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            // Set the public and private keys from the keyPair
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // return balance and store UTXOs owned by this wallet
    public float getBalance() {
        float total = 0;

        for (Map.Entry<String, TransactionOutput> item : BlockChain.UTXOs.entrySet()) {
            TransactionOutput UTXO = item.getValue();

            // if output belongs
            if (UTXO.isMine(publicKey)) {
                UTXOs.put(UTXO.id, UTXO);
                total += UTXO.value;
            }
        }

        return total;
    }

    // generate and return a new transaction from this wallet
    public Transaction sendFunds(PublicKey recipient, float value) {
        // get balance and check funds
        if (getBalance() < value) {
            System.out.println("Not enough funds to send transaction. Transaction discarded.");

            return null;
        }

        // create ArrayList of inputs
        ArrayList<TransactionInput> inputs = new ArrayList<>();
        float total = 0;

        for (Map.Entry<String, TransactionOutput> item : UTXOs.entrySet()) {
            TransactionOutput UTXO = item.getValue();
            total += UTXO.value;
            inputs.add(new TransactionInput(UTXO.id));

            if (total > value) {
                break;
            }
        }

        Transaction newTransaction = new Transaction(publicKey, recipient, value, inputs);
        newTransaction.generateSignature(privateKey);

        for (TransactionInput input : inputs) {
            UTXOs.remove(input.transactionOutputId);
        }

        return newTransaction;
    }
}
