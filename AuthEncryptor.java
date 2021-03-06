public class AuthEncryptor
{
  public static final int KeySizeBits = 256;
  public static final int KeySizeBytes = 32;
  public static final int NonceSizeBytes = 8;
  private StreamCipher cipher;
  private PRF integrityPrf;
  
  public AuthEncryptor(byte[] paramArrayOfByte)
  {
    assert (paramArrayOfByte.length == 32);
    
    PRGen localPRGen = new PRGen(paramArrayOfByte);
    
    byte[] arrayOfByte1 = new byte[32];
    byte[] arrayOfByte2 = new byte[32];
    
    localPRGen.nextBytes(arrayOfByte1);
    localPRGen.nextBytes(arrayOfByte2);
    
    this.cipher = new StreamCipher(arrayOfByte1);
    this.integrityPrf = new PRF(arrayOfByte2);
  }
  
  public byte[] encrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, boolean paramBoolean)
  {
    int i = paramArrayOfByte1.length + 32;
    if (paramBoolean) {
      i += 8;
    }
    byte[] arrayOfByte1 = new byte[i];
    
    this.cipher.setNonce(paramArrayOfByte2);
    this.cipher.cryptBytes(paramArrayOfByte1, 0, arrayOfByte1, 0, paramArrayOfByte1.length);
    this.integrityPrf.update(paramArrayOfByte2);
    byte[] arrayOfByte2 = this.integrityPrf.eval(arrayOfByte1, 0, paramArrayOfByte1.length);
    System.arraycopy(arrayOfByte2, 0, arrayOfByte1, paramArrayOfByte1.length, 32);
    if (paramBoolean) {
      System.arraycopy(paramArrayOfByte2, 0, arrayOfByte1, paramArrayOfByte1.length + 32, paramArrayOfByte2.length);
    }
    return arrayOfByte1;
  }
  
  public static void main(String[] paramArrayOfString)
  {
    byte[] arrayOfByte1 = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31 };
    byte[] arrayOfByte2 = { 100, 101, 102, 103, 104, 105, 106, 107 };
    
    PRGen localPRGen = new PRGen(arrayOfByte1);
    byte[] arrayOfByte3 = new byte[32];
    localPRGen.nextBytes(arrayOfByte3);
    
    byte[] arrayOfByte4 = new byte[18];
    
    AuthEncryptor localAuthEncryptor = new AuthEncryptor(arrayOfByte3);
    AuthDecryptor localAuthDecryptor = new AuthDecryptor(arrayOfByte3);
    for (int i = 0; i < 13; i++)
    {
      localPRGen.nextBytes(arrayOfByte4);
      byte[] arrayOfByte5 = localAuthEncryptor.encrypt(arrayOfByte4, arrayOfByte2, true);
      byte[] arrayOfByte6 = localAuthDecryptor.decrypt(arrayOfByte5, arrayOfByte2, true);
      assert (arrayOfByte4.length == arrayOfByte6.length);
      for (int j = 0; j < arrayOfByte4.length; j++) {
        assert (arrayOfByte4[j] == arrayOfByte6[j]);
      }
      localPRGen.nextBytes(arrayOfByte4);
      arrayOfByte5 = localAuthEncryptor.encrypt(arrayOfByte4, arrayOfByte2, false);
      arrayOfByte6 = localAuthDecryptor.decrypt(arrayOfByte5, arrayOfByte2, false);
      assert (arrayOfByte4.length == arrayOfByte6.length);
      for (int j = 0; j < arrayOfByte4.length; j++) {
        assert (arrayOfByte4[j] == arrayOfByte6[j]);
      }
    }
  }
}
