import java.io.PrintStream;
import java.math.BigInteger;

public class Proj2Util
{
  public static final int HashSizeBits = 256;
  public static final int HashSizeBytes = 32;
  private static final int PRIME_GEN_CERTAINTY = 128;
  private static final byte[] HASH_PRF_KEY = new byte[32];
  
  public static BigInteger generatePrime(PRGen paramPRGen, int paramInt)
  {
    return new BigInteger(paramInt, 128, paramPRGen);
  }
  
  public static byte[] hash(byte[] paramArrayOfByte)
  {
    return hash(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public static byte[] hash(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    PRF localPRF = new PRF(HASH_PRF_KEY);
    return localPRF.eval(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public static BigInteger bytesToBigInteger(byte[] paramArrayOfByte)
  {
    return new BigInteger(1, paramArrayOfByte);
  }
  
  public static byte[] bigIntegerToBytes(BigInteger paramBigInteger, int paramInt)
  {
    assert (paramBigInteger.compareTo(BigInteger.ZERO) >= 0);
    
    byte[] arrayOfByte1 = paramBigInteger.toByteArray();
    if (arrayOfByte1.length == paramInt) {
      return arrayOfByte1;
    }
    byte[] arrayOfByte2 = new byte[paramInt];
    int i;
    if (arrayOfByte1.length > paramInt)
    {
      assert (arrayOfByte1.length == paramInt + 1);
      for (i = 0; i < paramInt; i++) {
        arrayOfByte2[i] = arrayOfByte1[(i + 1)];
      }
    }
    else
    {
      i = paramInt - arrayOfByte1.length;
      for (int j = 0; j < paramInt; j++) {
        arrayOfByte2[j] = (j < i ? 0 : arrayOfByte1[(j - i)]);
      }
    }
    return arrayOfByte2;
  }
  
  public static void main(String[] paramArrayOfString)
  {
    BigInteger[] arrayOfBigInteger = { BigInteger.ZERO, BigInteger.ONE, BigInteger.TEN };
    for (int i = 0; i < arrayOfBigInteger.length; i++)
    {
      BigInteger localBigInteger = bytesToBigInteger(bigIntegerToBytes(arrayOfBigInteger[i], 40));
      if (!arrayOfBigInteger[i].equals(localBigInteger)) {
        System.out.println(arrayOfBigInteger[i].toString() + " " + localBigInteger.toString());
      }
    }
    System.out.println("OK");
  }
}
