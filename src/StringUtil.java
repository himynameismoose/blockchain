import java.security.MessageDigest;

public class StringUtil {
    // Apply SHA256 to a String and return result
    public static String applySHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // Apply SHA256 to input
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            // This will contain hash as hexidecimal
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);

                if (hex.length() == 1) {
                    hexString.append('0');
                }

                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
