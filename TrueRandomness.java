import java.security.SecureRandom;

public class TrueRandomness
{
  public static final int NumBytes = 16;
  private static boolean alreadyUsed = false;
  
  public static byte[] get()
  {
    assert (!alreadyUsed);
    
    byte[] arrayOfByte = new byte[16];
    SecureRandom localSecureRandom = new SecureRandom();
    localSecureRandom.nextBytes(arrayOfByte);
    alreadyUsed = true;
    return arrayOfByte;
  }
}
