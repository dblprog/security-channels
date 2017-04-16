import java.util.Random;

public class PRGen
  extends Random
{
  public static final int SeedSizeBits = 256;
  public static final int SeedSizeBytes = 32;
  private PRF prf;
  private static byte[] ByteArrayZero = { 0 };
  private static byte[] ByteArrayOne = { 1 };
  private static final int ChunkSize = 32;
  private byte[] bufferedBytes = new byte[32];
  private int indexInBuf = 32;
  
  public PRGen(byte[] paramArrayOfByte)
  {
    assert (paramArrayOfByte.length == 32);
    this.prf = new PRF(paramArrayOfByte);
  }
  
  private void refillBuffer()
  {
    this.bufferedBytes = this.prf.eval(ByteArrayZero);
    this.indexInBuf = 0;
    this.prf = new PRF(this.prf.eval(ByteArrayOne));
  }
  
  protected int next(int paramInt)
  {
    assert (paramInt > 0);
    assert (paramInt <= 32);
    
    int i = 0;
    int j = paramInt;
    while (paramInt > 0)
    {
      if (this.indexInBuf >= 32) {
        refillBuffer();
      }
      i = i * 256 + (0xFF & this.bufferedBytes[this.indexInBuf]);
      this.indexInBuf += 1;
      paramInt -= 8;
    }
    if (this.indexInBuf > 0) {
      refillBuffer();
    }
    if (j == 31) {
      i &= 0x7FFFFFFF;
    } else if (j < 31) {
      i &= (1 << j) - 1;
    }
    return i;
  }
}
