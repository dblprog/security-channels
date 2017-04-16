import java.math.BigInteger;

public class RSAKeyPair
{
  private RSAKey publicKey;
  private RSAKey privateKey;
  private BigInteger p;
  private BigInteger q;
  
  public RSAKeyPair(PRGen paramPRGen, int paramInt)
  {
    this.p = Proj2Util.generatePrime(paramPRGen, paramInt);
    this.q = Proj2Util.generatePrime(paramPRGen, paramInt);
    BigInteger localBigInteger1 = this.p.multiply(this.q);
    
    BigInteger localBigInteger2 = this.p.subtract(BigInteger.ONE);
    BigInteger localBigInteger3 = this.q.subtract(BigInteger.ONE);
    BigInteger localBigInteger4 = localBigInteger2.multiply(localBigInteger3);
    
    BigInteger localBigInteger5 = BigInteger.valueOf(65537L);
    int i = localBigInteger5.bitLength();
    while (!localBigInteger5.gcd(localBigInteger4).equals(BigInteger.ONE)) {
      localBigInteger5 = Proj2Util.generatePrime(paramPRGen, i++);
    }
    BigInteger localBigInteger6 = localBigInteger5.modInverse(localBigInteger4);
    assert (localBigInteger6.multiply(localBigInteger5).mod(localBigInteger4).equals(BigInteger.ONE));
    
    this.publicKey = new RSAKey(localBigInteger5, localBigInteger1);
    this.privateKey = new RSAKey(localBigInteger6, localBigInteger1);
  }
  
  public RSAKey getPublicKey()
  {
    return this.publicKey;
  }
  
  public RSAKey getPrivateKey()
  {
    return this.privateKey;
  }
  
  public BigInteger[] getPrimes()
  {
    BigInteger[] arrayOfBigInteger = new BigInteger[2];
    arrayOfBigInteger[0] = this.p;
    arrayOfBigInteger[1] = this.q;
    return arrayOfBigInteger;
  }
  
  public static void main(String[] paramArrayOfString)
  {
    byte[] arrayOfByte = new byte[32];
    for (int i = 0; i < 32; i++) {
      arrayOfByte[i] = ((byte)i);
    }
    PRGen localPRGen = new PRGen(arrayOfByte);
    RSAKeyPair localRSAKeyPair = new RSAKeyPair(localPRGen, 1024);
  }
}
