import java.math.BigInteger;

public class KeyExchange
{
  public static final int OutputSizeBits = 256;
  public static final int OutputSizeBytes = 32;
  private static final BigInteger g = DHParams.g;
  private static final BigInteger p = DHParams.p;
  private static final BigInteger pMinusOne = p.subtract(BigInteger.ONE);
  private BigInteger x;
  private BigInteger gx;
  
  public KeyExchange(PRGen paramPRGen)
  {
    do
    {
      this.x = new BigInteger(pMinusOne.bitLength() - 1, paramPRGen);
      this.gx = g.modPow(this.x, p);
    } while ((this.gx.equals(BigInteger.ONE)) || (this.gx.equals(pMinusOne)));
  }
  
  public byte[] prepareOutMessage()
  {
    return this.gx.toByteArray();
  }
  
  public byte[] processInMessage(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {
      throw new NullPointerException();
    }
    BigInteger localBigInteger = new BigInteger(paramArrayOfByte).modPow(this.x, p);
    if ((localBigInteger.compareTo(BigInteger.ONE) <= 0) || (localBigInteger.compareTo(pMinusOne) >= 0)) {
      return null;
    }
    return Proj2Util.hash(localBigInteger.toByteArray());
  }
  
  public static void main(String[] paramArrayOfString)
  {
    byte[] arrayOfByte1 = new byte[32];
    byte[] arrayOfByte2 = TrueRandomness.get();
    for (int i = 0; i < 16; i++) {
      arrayOfByte1[i] = arrayOfByte2[i];
    }
    PRGen localPRGen = new PRGen(arrayOfByte1);
    for (int j = 0; j < 42; j++)
    {
      KeyExchange localKeyExchange1 = new KeyExchange(localPRGen);
      KeyExchange localKeyExchange2 = new KeyExchange(localPRGen);
      byte[] arrayOfByte3 = localKeyExchange1.processInMessage(localKeyExchange2.prepareOutMessage());
      byte[] arrayOfByte4 = localKeyExchange2.processInMessage(localKeyExchange1.prepareOutMessage());
      assert (arrayOfByte3.length == arrayOfByte4.length);
      for (int k = 0; k < arrayOfByte3.length; k++) {
        assert (arrayOfByte3[k] == arrayOfByte4[k]);
      }
    }
  }
}
