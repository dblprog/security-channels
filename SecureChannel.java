
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SecureChannel extends InsecureChannel {
    // This is just like an InsecureChannel, except that it provides 
    //    authenticated encryption for the messages that pass
    //    over the channel.   It also guarantees that messages are delivered 
    //    on the receiving end in the same order they were sent (returning
    //    null otherwise).  Also, when the channel is first set up,
    //    the client authenticates the server's identity, and the necessary
    //    steps are taken to detect any man-in-the-middle (and to close the
    //    connection if a MITM is detected).
    //
    // The code provided here is not secure --- all it does is pass through
    //    calls to the underlying InsecureChannel.
    
    private AuthEncryptor ae;
    private AuthDecryptor ad;
    private PRGen prg;
    private static int randLength = PRGen.SeedSizeBytes;
    private long msgCountSent;          // 2^64 messages should be sufficiently many; 
    private long msgCountRecv;     // it would take years at sending 1 message/millisecond to overflow. 

    
    public SecureChannel(InputStream inStr, OutputStream outStr, 
                         PRGen rand, boolean iAmServer,
                         RSAKey serverKey) throws IOException {
        // if iAmServer==false, then serverKey is the server's *public* key
        // if iAmServer==true, then serverKey is the server's *private* key
        
        // code common to both cases
        super(inStr, outStr);                      
        KeyExchange dh = new KeyExchange(rand);           
        
        byte[] randComm = new byte[randLength];   
        rand.nextBytes(randComm);         
        prg = new PRGen(randComm);
        
        byte[] c = dh.prepareOutMessage();
        super.sendMessage(c);
        
        if(iAmServer) {
            byte[] sign = serverKey.sign(c, rand);
            super.sendMessage(sign);            
        } 
        // common code 
        byte[] in = super.receiveMessage();
        byte[] x = dh.processInMessage(in);
        
        if(!iAmServer) { // iAmClient
            byte[] signature = super.receiveMessage();
            boolean isAuthentic = serverKey.verifySignature(in, signature);
            if(!isAuthentic) close();
            }
        
        // hash g^(ab)mod(p) to derive the master secret k
        // use secret to create authencrypt and authdecrypt
        ae = new AuthEncryptor(Proj2Util.hash(x));      
        ad = new AuthDecryptor(Proj2Util.hash(x)); 
        msgCountRecv = 0;
        msgCountSent = 0;
    }
    
    public void sendMessage(byte[] message) throws IOException {
        byte[] nonce = new byte[ae.NonceSizeBytes];
        this.prg.nextBytes(nonce);  // generate a nonce for AE  
        
        byte[] messageOut = new byte[(message.length+8)];
        byte[] countBytes = new byte[8];
        msgCountSent++; 
        
        for(int i = 0; i < 8; i++) 
            countBytes[i] = (byte) (msgCountSent >>> (56-8*i));
        
        System.arraycopy(countBytes, 0, messageOut, message.length, 8);
        System.arraycopy(message, 0, messageOut, 0, message.length);
        byte[] encrypted = ae.encrypt(messageOut, nonce, true);
        super.sendMessage(encrypted);
    }
    
    public byte[] receiveMessage() throws IOException {
        byte[] in = super.receiveMessage();   
        msgCountRecv++;
        byte[] decrypted = ad.decrypt(in, null, true);
        if (decrypted == null) close(); 
        long msgCountIn = 0;
        
        for(int i = 0; i < 8; i++) 
            msgCountIn |= (decrypted[decrypted.length-8+i] << 56-(8*i));       
        if(msgCountIn != (msgCountRecv)) 
            return null;
        byte[] ret = new byte[decrypted.length-8];
        System.arraycopy(decrypted,0,ret, 0, ret.length);
        return ret;
    }
}