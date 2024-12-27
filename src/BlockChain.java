import com.google.gson.GsonBuilder;

import java.util.ArrayList;

public class BlockChain {

    public static ArrayList<Block> blockchain = new ArrayList<>();

    public static void main(String[] args) {
        // Add blocks to blockchain ArrayList
        blockchain.add(new Block("Hi, I'm the first block", "0"));
        blockchain.add(new Block("Yo, I'm the second block", blockchain.get(blockchain.size() - 1).hash));
        blockchain.add(new Block("Hey, I'm the third block", blockchain.get(blockchain.size() - 1).hash));

        String blockchainJSON = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
        System.out.println(blockchainJSON);
    }
}
