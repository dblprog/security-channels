import java.io.PrintStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;

public class PRF
{
  public static final int KeySizeBits = 256;
  public static final int KeySizeBytes = 32;
  public static final int OutputSizeBits = 256;
  public static final int OutputSizeBytes = 32;
  private static final String AlgorithmName = "HmacSHA256";
  private Mac mac;
  
  public PRF(byte[] paramArrayOfByte)
  {
    assert (paramArrayOfByte.length == 32);
    try
    {
      this.mac = Mac.getInstance("HmacSHA256");
      KeyGenerator localKeyGenerator = KeyGenerator.getInstance("HmacSHA256");
      SecureRandom localSecureRandom = SecureRandom.getInstance("SHA1PRNG");
      localSecureRandom.setSeed(paramArrayOfByte);
      localKeyGenerator.init(256, localSecureRandom);
      SecretKey localSecretKey = localKeyGenerator.generateKey();
      this.mac.init(localSecretKey);
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      localNoSuchAlgorithmException.printStackTrace(System.err);
    }
    catch (InvalidKeyException localInvalidKeyException)
    {
      localInvalidKeyException.printStackTrace(System.err);
    }
  }
  
  public synchronized void update(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    this.mac.update(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public synchronized void update(byte[] paramArrayOfByte)
  {
    update(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public synchronized void eval(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3)
    throws ShortBufferException
  {
    this.mac.update(paramArrayOfByte1, paramInt1, paramInt2);
    this.mac.doFinal(paramArrayOfByte2, paramInt3);
  }
  
  public synchronized byte[] eval(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    try
    {
      byte[] arrayOfByte = new byte[32];
      eval(paramArrayOfByte, paramInt1, paramInt2, arrayOfByte, 0);
      return arrayOfByte;
    }
    catch (ShortBufferException localShortBufferException)
    {
      localShortBufferException.printStackTrace(System.err);
    }
    return null;
  }
  
  public byte[] eval(byte[] paramArrayOfByte)
  {
    return eval(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public static void main(String[] paramArrayOfString)
  {
    byte[] arrayOfByte1 = new byte[32];
    for (int i = 0; i < 32; i++) {
      arrayOfByte1[i] = ((byte)i);
    }
    byte[] arrayOfByte2 = new byte[57];
    for (int j = 0; j < arrayOfByte2.length; j++) {
      arrayOfByte2[j] = ((byte)j);
    }
    byte[] arrayOfByte3 = new byte[61];
    for (int k = 0; k < arrayOfByte3.length; k++) {
      arrayOfByte3[k] = ((byte)(k + 73));
    }
    PRF localPRF1 = new PRF(arrayOfByte1);
    byte[] arrayOfByte4 = localPRF1.eval(arrayOfByte2);
    assert (arrayOfByte4.length == 32);
    byte[] arrayOfByte5 = localPRF1.eval(arrayOfByte3);
    assert (arrayOfByte5.length == 32);
    assert (!arrayOfByte4.equals(arrayOfByte5));
    
    PRF localPRF2 = new PRF(arrayOfByte1);
    byte[] arrayOfByte6 = localPRF2.eval(arrayOfByte2);
    assert (arrayOfByte6.length == 32);
    for (int m = 0; m < arrayOfByte4.length; m++) {
      assert (arrayOfByte4[m] == arrayOfByte6[m]);
    }
    System.out.println("OK");
  }
}
