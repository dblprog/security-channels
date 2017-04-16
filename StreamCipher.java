public class StreamCipher
{
  public static final int KeySizeBits = 256;
  public static final int KeySizeBytes = 32;
  public static final int NonceSizeBits = 64;
  public static final int NonceSizeBytes = 8;
  private PRF seedGen;
  private PRGen prg;
  
  public StreamCipher(byte[] paramArrayOfByte)
  {
    assert (paramArrayOfByte.length == 32);
    
    this.seedGen = new PRF(paramArrayOfByte);
  }
  
  public void setNonce(byte[] paramArrayOfByte, int paramInt)
  {
    this.prg = new PRGen(this.seedGen.eval(paramArrayOfByte, paramInt, 8));
  }
  
  public void setNonce(byte[] paramArrayOfByte)
  {
    assert (paramArrayOfByte.length == 8);
    
    setNonce(paramArrayOfByte, 0);
  }
  
  public byte cryptByte(byte paramByte)
  {
    byte b = (byte) this.prg.next(8);
    return (byte)(paramByte ^ b);
  }
  
  public void cryptBytes(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, int paramInt2, int paramInt3)
  {
    for (int i = 0; i < paramInt3; i++) {
      paramArrayOfByte2[(paramInt2 + i)] = cryptByte(paramArrayOfByte1[(paramInt1 + i)]);
    }
  }
}
