public class AuthDecryptor
{
    public static final int KeySizeBits = 256;
    public static final int KeySizeBytes = 32;
    public static final int NonceSizeBytes = 8;
    private StreamCipher cipher;
    private PRF integrityPrf;
    
    public AuthDecryptor(byte[] paramArrayOfByte)
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
    
    public byte[] decrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, boolean paramBoolean)
    {
        int i = paramArrayOfByte1.length - 32;
        if (paramBoolean)
        {
            i -= 8;
            this.integrityPrf.update(paramArrayOfByte1, i + 32, 8);
            this.cipher.setNonce(paramArrayOfByte1, i + 32);
        }
        else
        {
            this.integrityPrf.update(paramArrayOfByte2);
            this.cipher.setNonce(paramArrayOfByte2);
        }
        byte[] arrayOfByte1 = this.integrityPrf.eval(paramArrayOfByte1, 0, i);
        for (int j = 0; j < 32; j++) {
            if (paramArrayOfByte1[(j + i)] != arrayOfByte1[j]) {
                return null;
            }
        }
        byte[] arrayOfByte2 = new byte[i];
        this.cipher.cryptBytes(paramArrayOfByte1, 0, arrayOfByte2, 0, i);
        return arrayOfByte2;
    }
}
