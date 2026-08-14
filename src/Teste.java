import javax.crypto.Cipher;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Teste {

    private static String ALGORITMO = "RSA";
    private static String CHAVE_PUBLICA = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCZILVQdhKkRU0hLg4E9CKUbpci8kpm9VWXZ3bZvnduOJZuqnwk5huXIVmChoW6qdHO02XcXXYoFaJ7qVAzc9llfvRz23FeuqYoyJz5jZ02rP7oMuw+Sp74o0MNq6TxDLheYI6CWOG3YZSUmxwow8u2eYGR3lRf139MojG7vnMIBwIDAQAB";
    private static String CHAVE_PRIVADA = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAJkgtVB2EqRFTSEuDgT0IpRulyLySmb1VZdndtm+d244lm6qfCTmG5chWYKGhbqp0c7TZdxddigVonupUDNz2WV+9HPbcV66pijInPmNnTas/ugy7D5KnvijQw2rpPEMuF5gjoJY4bdhlJSbHCjDy7Z5gZHeVF/Xf0yiMbu+cwgHAgMBAAECgYAjN0J7nqvq24VBfDX9LahGOqjHgLFbvFBS4ZiTuxn8X0c5bDvgwIrX1vOe8REQPL3jsvpaE6R404Dqr6WiL6JvONQmCp5/l1dZYmuGuQwyy8zQE8wfHCpVLZl2PsrOKmbWZKtbd2KU0K641j6BAp8aombHyTbTcOnkFdculB/xAQJBAOl62wwKP02HV5Mo4HcZC/8o6KAhf2LBGvlHm2Uf7kKLRAK6KLRlFeWskLUw3zCMpsjU5LxKPbe69FKInlcv130CQQCn5ccH2i0v2yLX4kc1uCTWEDm4XBbQ4NUayWB4gYeX0LC6MuvN0+5WV9HPPPbwuwgOIv3Yr/cN4EBmkasSOdzTAkEAk7myAsoxB2LM3EWO0Iw+dPFzTm4jZV59LKBMCA3N+LiZDYiv3IPg+PLYlGwZq2Qy2vsoxqHKrwdRMy9R0CRrGQJBAJTNJadE6xVlzqysg9YNXMBHYxCMtT/sc5Io9ZH3opefQnHTnX8vHCVz8aQM8QKLkGkPBBFeasPmgs0kvwwJMjcCQQCBZBeZa48OfRDh6VF0OA4K3IwCQwH/HkhPAylpSC7gnMyh+K+wJQcn7Cj5mczEGpwOLu9jFylkAkyI2ymypLC/";

    public static final String PATH_CHAVE_PRIVADA = "C:\\Users\\Vinic\\OneDrive\\Documentos\\GitHub\\pi-seguranca-sistemas\\src\\keys\\private.key";
    public static final String PATH_CHAVE_PUBLICA = "C:\\Users\\Vinic\\OneDrive\\Documentos\\GitHub\\pi-seguranca-sistemas\\src\\keys\\public.key";

    public static void main(String[] args) throws NoSuchAlgorithmException {
        String mensagem = "teste";

        //geraChave();

        PublicKey chavePublica = convertStringToPublicKey(CHAVE_PUBLICA);
        PrivateKey chavePrivada = convertStringToPrivateKey(CHAVE_PRIVADA);

        byte[] criptografado = criptografar(chavePublica, mensagem);
        String descriptograr = descriptograr(chavePrivada, criptografado);

    }

    public static PublicKey convertStringToPublicKey(String chavePublicaString){
        byte[] encoded = Base64.getDecoder().decode(chavePublicaString);
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITMO);
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e){
            System.out.println("Não foi possível converter a chave pública.");
            throw new RuntimeException(e);
        }
    }

    public static PrivateKey convertStringToPrivateKey(String chavePrivadaString){
        try{
            byte[] keyBytes = Base64.getDecoder().decode(chavePrivadaString);

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e){
            System.out.println("Não foi possível converter a chave privada.");
            throw new RuntimeException(e);
        }
    }

    public static byte[] criptografar(PublicKey publicKey, String mensagem){
        try {
            final Cipher cipher = Cipher.getInstance(ALGORITMO);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] textoCriptografadoBytes = cipher.doFinal(mensagem.getBytes());
            System.out.println(textoCriptografadoBytes);
            return textoCriptografadoBytes;
        } catch (Exception e){
            System.out.println("Não foi possível iniciar o Cipher.");
            throw new RuntimeException(e);
        }
    }

    public static String descriptograr(PrivateKey privateKey, byte[] textoCifrado){
        try {
            final Cipher cipher = Cipher.getInstance(ALGORITMO);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            String textoDescriptografado = new String(cipher.doFinal(textoCifrado));
            System.out.println(textoDescriptografado);
            return textoDescriptografado;
        } catch (Exception e){
            System.out.println("Não foi possível iniciar o Cipher.");
            throw new RuntimeException(e);
        }
    }

    public static void geraChave() {
        try {
            final KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITMO);
            keyGen.initialize(1024);
            final KeyPair key = keyGen.generateKeyPair();

            PublicKey publicKey = key.getPublic();
            PrivateKey privateKey = key.getPrivate();

            String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            String privateKeyString = Base64.getEncoder().encodeToString(privateKey.getEncoded());
            System.out.println("Chave Pública RSA de 1024 bits:");
            System.out.println(publicKeyString);

            System.out.println("Chave Private RSA de 1024 bits:");
            System.out.println(privateKeyString);

            File chavePrivadaFile = new File(PATH_CHAVE_PRIVADA);
            File chavePublicaFile = new File(PATH_CHAVE_PUBLICA);

            // Cria os arquivos para armazenar a chave Privada e a chave Publica
            if (chavePrivadaFile.getParentFile() != null) {
                chavePrivadaFile.getParentFile().mkdirs();
            }

            chavePrivadaFile.createNewFile();

            if (chavePublicaFile.getParentFile() != null) {
                chavePublicaFile.getParentFile().mkdirs();
            }

            chavePublicaFile.createNewFile();

            // Salva a Chave Pública no arquivo
            ObjectOutputStream chavePublicaOS = new ObjectOutputStream(
                    new FileOutputStream(chavePublicaFile));
            chavePublicaOS.writeObject(key.getPublic());
            chavePublicaOS.close();

            // Salva a Chave Privada no arquivo
            ObjectOutputStream chavePrivadaOS = new ObjectOutputStream(
                    new FileOutputStream(chavePrivadaFile));
            chavePrivadaOS.writeObject(key.getPrivate());
            chavePrivadaOS.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
