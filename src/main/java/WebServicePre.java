
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class WebServicePre {
    private IvParameterSpec ivspec;
    private SecretKeySpec keyspec;
    private Cipher cipher;

    public WebServicePre(byte[] randBytes) {
        ivspec = new IvParameterSpec(randBytes);
//        keyspec = new SecretKeySpec(getK(context).getBytes(), "AES/CBC/NoPadding");

        try {
            cipher = Cipher.getInstance("AES/CBC/NoPadding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            //e.printStackTrace();
        }
    }

    public WebServicePre() {
        try {
            cipher = Cipher.getInstance("AES/CBC/NoPadding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            //e.printStackTrace();
        }
    }

    public byte[] encrypt(String text) throws Exception {
        if (text == null || text.length() == 0)
            throw new Exception("Empty string");

        byte[] enc = null;

        try {
            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
            enc = cipher.doFinal(padString(text).getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new Exception("[encrypt] " + e.getMessage());
        }
        return enc;
    }

    public String decrypt(String cipherText,String key,String iv) throws Exception {
        if (cipherText == null || cipherText.length() == 0)
            throw new Exception("Empty string");

        byte[] dec = null;

        try {
            ivspec = new IvParameterSpec(iv.getBytes());
            keyspec = new SecretKeySpec(key.getBytes(), "AES/CBC/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);

            byte[] _decrypted = cipher.doFinal(hexToBytes(cipherText));
            dec = removeTrailingNulls(_decrypted);
        } catch (Exception e) {
            throw new Exception("[decrypt] " + e.getMessage());
        }
        return Arrays.toString(dec);
    }

    public static String bytesToHex(byte[] data) {
        if (data == null) {
            return null;
        }

        int len = data.length;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < len; i++) {
            if ((data[i] & 0xFF) < 16)
                sb.append("0" + Integer.toHexString(data[i] & 0xFF));
            else
                sb.append(Integer.toHexString(data[i] & 0xFF));
        }
        return sb.toString();
    }

    public static byte[] hexToBytes(String str) {
        if (str == null) {
            return null;
        } else if (str.length() < 2) {
            return null;
        } else {
            int len = str.length() / 2;
            byte[] buffer = new byte[len];
            for (int i = 0; i < len; i++) {
                buffer[i] = (byte) Integer.parseInt(str.substring(i * 2, i * 2 + 2), 16);
            }
            return buffer;
        }
    }

    private static String padString(String source) {
        char paddingChar = ' ';
        int size = 16;
        int x = source.length() % size;
        int padLength = size - x;

        for (int i = 0; i < padLength; i++) {
            source += paddingChar;
        }

        return source;
    }

    public static byte[] removeTrailingNulls(byte[] source) {
        int i = source.length;
        while (source[i - 1] == 0x00) {
            i--;
        }
        byte[] result = new byte[i];
        System.arraycopy(source, 0, result, 0, i);
        return result;
    }

//    public static String getK(Context context) {
//        int[] _array = context.getResources().getIntArray(R.array.key);
//        StringBuilder stringBuilder = new StringBuilder();
//        for (int value : _array) {
//            int _x = value % 9;
//            stringBuilder.append(String.valueOf(_x));
//        }
//        return stringBuilder.toString();
//    }

}
