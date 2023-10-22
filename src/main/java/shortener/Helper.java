package shortener;

import java.math.BigInteger;
import java.security.SecureRandom;

public class Helper {
    static public String generateRandomString(){
        final int LIMIT = 36;
        SecureRandom random = new SecureRandom();
        BigInteger bigInteger = new BigInteger(130, random);
        return bigInteger.toString(LIMIT);
    }

    static public void printMessage(String message){
        System.out.println(message);
    }
}
