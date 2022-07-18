// 2009-06-04
// HWStatistic, LicenseStatistic, PeraLS, ThreeDES, Portal.
package portal;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;

/*
	public static byte[] encryptMode(byte[] keybyte, byte[] src) 
	public static byte[] decryptMode(byte[] keybyte, byte[] src)
	public static String byte2hex(byte[] b) 
	public static String encryptKeyToFile( String key, String filePathName )
	public static String decryptKeyFromFile( String filePathName )
*/
public class ThreeDES {

    private static final String Algorithm = "DESede";  // Algorithm: DES, DESede, Blowfish. 
	public static final byte[] keyBytes = {(byte)0x11, (byte)0x22, (byte)0x4F, (byte)0x57, (byte)0x88, (byte)0x10, (byte)0x40, (byte)0x38, (byte)0x28, (byte)0x25, (byte)0x79, (byte)0x51, (byte)0xCB, (byte)0xDD, (byte)0x55, (byte)0x66, (byte)0x77, (byte)0x29, (byte)0x74, (byte)0x98, (byte)0x31, (byte)0x40, (byte)0x36, (byte)0xE2}; 
	
	public static byte[] encryptMode(byte[] keybyte, byte[] src) 
	{
       try {
            SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);
            Cipher c1 = Cipher.getInstance(Algorithm);
            c1.init(Cipher.ENCRYPT_MODE, deskey);
            return c1.doFinal(src);
        } catch (java.security.NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (javax.crypto.NoSuchPaddingException e2) {
            e2.printStackTrace();
        } catch (java.lang.Exception e3) {
            e3.printStackTrace();
        }
        return null;
    }

	public static byte[] decryptMode(byte[] keybyte, byte[] src) 
	{
        try {
            SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);
            Cipher c1 = Cipher.getInstance(Algorithm);
            c1.init(Cipher.DECRYPT_MODE, deskey);
            return c1.doFinal(src);
        } catch (java.security.NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (javax.crypto.NoSuchPaddingException e2) {
            e2.printStackTrace();
        } catch (java.lang.Exception e3) {
            e3.printStackTrace();
        }
        return null;
    }

	public static String byte2hex(byte[] b) 
	{
        String hs="";
        String stmp="";
        for (int n=0;n<b.length;n++) {
            stmp=(java.lang.Integer.toHexString(b[n] & 0XFF));
            if (stmp.length()==1) hs=hs+"0"+stmp;
            else hs=hs+stmp;
            if (n<b.length-1)  hs=hs+":";
        }
        return hs.toUpperCase();
    }

	public static String encryptKeyToFile( byte[] key, String filePathName )
	{
		RandomAccessFile file = null;
		try{
			File file2 = new File( filePathName );
			if (file2.exists()) {
				file2.delete();
				file2.createNewFile();
			}	
			file = new RandomAccessFile(file2,"rw");
			file.seek(0);
			byte[] encryptKey = encryptMode(keyBytes,key);
			for(int i=0;i<encryptKey.length;i++)
			{
				file.writeByte(encryptKey[i]);
			}
			file.close();
		}catch(IOException e){ 
			System.out.println(e);
			return "fail";
		}
		return "success";					
	}
	
	public static String decryptKeyFromFile( String filePathName )
	{
		String key = new String("NULL");
		RandomAccessFile file = null;
		try{
			file = new RandomAccessFile( filePathName , "r" );
			file.seek(0);
			byte[] key2 = new byte[(int)file.length()];
			file.read(key2);
			key = new String(decryptMode(keyBytes,key2));
			file.close();
		}catch(IOException e){ 
			System.out.println("public static String decryptKeyFromFile( String filePathName )"+e);
		}		
		return key ;
	}

    public static void main(String[] args)
    {
        ThreeDES td = new ThreeDES();
/*
        String szSrc = "This is a 3DES test.";
        System.out.println(szSrc);
        byte[] encoded = td.encryptMode(td.keyBytes, szSrc.getBytes());
        System.out.println(new String(encoded));
        byte[] srcBytes = td.decryptMode(td.keyBytes, encoded);
        System.out.println(new String(srcBytes));
*/
/*
		String dbPassword = new String("pera2008");
		System.out.println(dbPassword);
		System.out.println(dbPassword.getBytes());
		byte[] encoded = td.encryptMode(td.keyBytes, dbPassword.getBytes());
		System.out.println(new String(encoded));
		System.out.println(encoded);
		byte[] srcBytes = td.decryptMode(td.keyBytes, encoded);
		System.out.println(new String(srcBytes));
		System.out.println(srcBytes);
*/
        String dbPassword = new String("pera2008");
        td.encryptKeyToFile(dbPassword.getBytes(),"D:/HPCClient/WorkSpace/Portal/WebContent/password");
		System.out.println(ThreeDES.decryptKeyFromFile("D:/HPCClient/WorkSpace/Portal/WebContent/password"));
	}
}

