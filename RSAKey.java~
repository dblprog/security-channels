import java.math.BigInteger;

public class RSAKey
{
  private BigInteger exponent;
  private BigInteger modulus;
  private static final int oaepK0SizeBytes = 32;
  private static final int oaepK1SizeBytes = 32;
  private static final int paddingOverheadSizeBytes = 1;
  private static final boolean RSA_PSS = false;
  
  public RSAKey(BigInteger paramBigInteger1, BigInteger paramBigInteger2)
  {
    this.exponent = paramBigInteger1;
    this.modulus = paramBigInteger2;
  }
  
  public BigInteger getExponent()
  {
    return this.exponent;
  }
  
  public BigInteger getModulus()
  {
    return this.modulus;
  }
  
  public byte[] encodeOaep(byte[] paramArrayOfByte, PRGen paramPRGen)
  {
    byte[] arrayOfByte1 = new byte[paramArrayOfByte.length + 32 + 32];
    byte[] arrayOfByte2 = new byte[32];
    paramPRGen.nextBytes(arrayOfByte2);
    PRGen localPRGen = new PRGen(arrayOfByte2);
    for (int i = 0; i < paramArrayOfByte.length; i++) {
      arrayOfByte1[i] = ((byte)(paramArrayOfByte[i] ^ localPRGen.next(8)));
    }
    for (int i = 0; i < 32; i++) {
      arrayOfByte1[(paramArrayOfByte.length + i)] = ((byte)localPRGen.next(8));
    }
    byte[] arrayOfByte3 = Proj2Util.hash(arrayOfByte1, 0, paramArrayOfByte.length + 32);
    for (int j = 0; j < 32; j++) {
      arrayOfByte1[(paramArrayOfByte.length + 32 + j)] = ((byte)(arrayOfByte2[j] ^ arrayOfByte3[j]));
    }
    return arrayOfByte1;
  }
  
  public byte[] decodeOaep(byte[] paramArrayOfByte)
  {
    byte[] arrayOfByte1 = Proj2Util.hash(paramArrayOfByte, 0, paramArrayOfByte.length - 32);
    byte[] arrayOfByte2 = new byte[32];
    for (int i = 0; i < 32; i++) {
      arrayOfByte2[i] = ((byte)(arrayOfByte1[i] ^ paramArrayOfByte[(paramArrayOfByte.length - 32 + i)]));
    }
    PRGen localPRGen = new PRGen(arrayOfByte2);
    byte[] arrayOfByte3 = new byte[paramArrayOfByte.length - 64];
    for (int j = 0; j < arrayOfByte3.length; j++) {
      arrayOfByte3[j] = ((byte)(paramArrayOfByte[j] ^ localPRGen.next(8)));
    }
    for (int j = 0; j < 32; j++) {
      if (paramArrayOfByte[(arrayOfByte3.length + j)] != (byte)localPRGen.next(8)) {
        return null;
      }
    }
    return arrayOfByte3;
  }
  
  public byte[] addPadding(byte[] paramArrayOfByte)
  {
    int i = paramArrayOfByte.length;
    int j = maxPlaintextLength() + 1;
    assert (i < j);
    
    byte[] arrayOfByte = new byte[j];
    for (int k = 0; k < i; k++) {
      arrayOfByte[k] = paramArrayOfByte[k];
    }
    arrayOfByte[i] = Byte.MIN_VALUE;
    for (int k = i + 1; k < j; k++) {
      arrayOfByte[k] = 0;
    }
    return arrayOfByte;
  }
  
  public byte[] removePadding(byte[] var1) {
        int var2 = this.maxPlaintextLength() + 1;

        assert var1.length == var2;

        int var3 = var2 - 1;

        while((var1[var3] & 255) != 128) {
            --var3;

            assert var3 >= 0;
        }

        byte[] var4 = new byte[var3];

        for(int var5 = 0; var5 < var3; ++var5) {
            var4[var5] = var1[var5];
        }

        return var4;
    }
  
  public byte[] encrypt(byte[] paramArrayOfByte, PRGen paramPRGen)
  {
    if (paramArrayOfByte == null) {
      return null;
    }
    if (paramArrayOfByte.length > maxPlaintextLength()) {
      return null;
    }
    paramArrayOfByte = addPadding(paramArrayOfByte);
    
    byte[] arrayOfByte = encodeOaep(paramArrayOfByte, paramPRGen);
    assert (arrayOfByte.length == maxMessageLength());
    
    BigInteger localBigInteger1 = Proj2Util.bytesToBigInteger(arrayOfByte);
    BigInteger localBigInteger2 = localBigInteger1.modPow(this.exponent, this.modulus);
    return localBigInteger2.toByteArray();
  }
  
  public byte[] decrypt(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {
      throw new NullPointerException();
    }
    BigInteger localBigInteger1 = new BigInteger(paramArrayOfByte);
    BigInteger localBigInteger2 = localBigInteger1.modPow(this.exponent, this.modulus);
    
    byte[] arrayOfByte1 = Proj2Util.bigIntegerToBytes(localBigInteger2, maxMessageLength());
    assert (arrayOfByte1.length == maxMessageLength());
    
    byte[] arrayOfByte2 = decodeOaep(arrayOfByte1);
    if (arrayOfByte2 == null) {
      return null;
    }
    return removePadding(arrayOfByte2);
  }
  
  public byte[] sign(byte[] paramArrayOfByte, PRGen paramPRGen)
  {
    if (paramArrayOfByte == null) {
      return null;
    }
    return hashAndSign(paramArrayOfByte);
  }
  
  public boolean verifySignature(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    if ((paramArrayOfByte1 == null) || (paramArrayOfByte2 == null)) {
      return false;
    }
    return hashAndVerify(paramArrayOfByte1, paramArrayOfByte2);
  }
  
  private byte[] PSSSign(byte[] paramArrayOfByte, PRGen paramPRGen)
  {
    return null;
  }
  
  private boolean PSSVerify(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    return false;
  }
  
  private byte[] makePaddedHash(byte[] paramArrayOfByte)
  {
    byte[] arrayOfByte1 = Proj2Util.hash(paramArrayOfByte);
    byte[] arrayOfByte2 = new byte[maxMessageLength()];
    System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, arrayOfByte1.length);
    return arrayOfByte2;
  }
  
  private byte[] hashAndSign(byte[] paramArrayOfByte)
  {
    BigInteger localBigInteger1 = Proj2Util.bytesToBigInteger(makePaddedHash(paramArrayOfByte));
    BigInteger localBigInteger2 = localBigInteger1.modPow(this.exponent, this.modulus);
    return localBigInteger2.toByteArray();
  }
  
  private boolean hashAndVerify(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    byte[] arrayOfByte1 = makePaddedHash(paramArrayOfByte1);
    
    BigInteger localBigInteger1 = new BigInteger(paramArrayOfByte2);
    BigInteger localBigInteger2 = localBigInteger1.modPow(this.exponent, this.modulus);
    byte[] arrayOfByte2 = Proj2Util.bigIntegerToBytes(localBigInteger2, maxMessageLength());
    
    return compareBytes(arrayOfByte1, arrayOfByte2);
  }
  
  private int maxMessageLength()
  {
    return this.modulus.bitLength() / 8 - 1;
  }
  
  public int maxPlaintextLength()
  {
    return maxMessageLength() - 32 - 32 - 1;
  }
  
  private boolean compareBytes(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    if (paramArrayOfByte1.length != paramArrayOfByte2.length) {
      return false;
    }
    for (int i = 0; i < paramArrayOfByte1.length; i++) {
      if (paramArrayOfByte1[i] != paramArrayOfByte2[i]) {
        return false;
      }
    }
    return true;
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
      RSAKeyPair localRSAKeyPair = new RSAKeyPair(localPRGen, 1024);
      RSAKey localRSAKey1 = localRSAKeyPair.getPublicKey();
      RSAKey localRSAKey2 = localRSAKeyPair.getPrivateKey();
      
      byte[] arrayOfByte3 = new byte[localRSAKey1.maxPlaintextLength() - j];
      localPRGen.nextBytes(arrayOfByte3);
      
      byte[] arrayOfByte4 = localRSAKey1.encrypt(arrayOfByte3, localPRGen);
      assert (arrayOfByte4 != null);
      
      byte[] arrayOfByte5 = localRSAKey2.decrypt(arrayOfByte4);
      assert (arrayOfByte5 != null);
      
      assert (arrayOfByte3.length == arrayOfByte5.length);
      for (int k = 0; k < arrayOfByte3.length; k++) {
        assert (arrayOfByte3[k] == arrayOfByte5[k]);
      }
      byte[] arrayOfByte6 = localRSAKey2.sign(arrayOfByte3, localPRGen);
      assert (localRSAKey1.verifySignature(arrayOfByte3, arrayOfByte6));
    }
  }
}
