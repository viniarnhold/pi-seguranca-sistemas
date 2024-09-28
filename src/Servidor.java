import javax.crypto.Cipher;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class Servidor extends Thread {
    private Socket conexao;
    private static List<OutputStream> conexoes;
    private static String CHAVE_PRIVADA = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAJkgtVB2EqRFTSEuDgT0IpRulyLySmb1VZdndtm+d244lm6qfCTmG5chWYKGhbqp0c7TZdxddigVonupUDNz2WV+9HPbcV66pijInPmNnTas/ugy7D5KnvijQw2rpPEMuF5gjoJY4bdhlJSbHCjDy7Z5gZHeVF/Xf0yiMbu+cwgHAgMBAAECgYAjN0J7nqvq24VBfDX9LahGOqjHgLFbvFBS4ZiTuxn8X0c5bDvgwIrX1vOe8REQPL3jsvpaE6R404Dqr6WiL6JvONQmCp5/l1dZYmuGuQwyy8zQE8wfHCpVLZl2PsrOKmbWZKtbd2KU0K641j6BAp8aombHyTbTcOnkFdculB/xAQJBAOl62wwKP02HV5Mo4HcZC/8o6KAhf2LBGvlHm2Uf7kKLRAK6KLRlFeWskLUw3zCMpsjU5LxKPbe69FKInlcv130CQQCn5ccH2i0v2yLX4kc1uCTWEDm4XBbQ4NUayWB4gYeX0LC6MuvN0+5WV9HPPPbwuwgOIv3Yr/cN4EBmkasSOdzTAkEAk7myAsoxB2LM3EWO0Iw+dPFzTm4jZV59LKBMCA3N+LiZDYiv3IPg+PLYlGwZq2Qy2vsoxqHKrwdRMy9R0CRrGQJBAJTNJadE6xVlzqysg9YNXMBHYxCMtT/sc5Io9ZH3opefQnHTnX8vHCVz8aQM8QKLkGkPBBFeasPmgs0kvwwJMjcCQQCBZBeZa48OfRDh6VF0OA4K3IwCQwH/HkhPAylpSC7gnMyh+K+wJQcn7Cj5mczEGpwOLu9jFylkAkyI2ymypLC/";
    private static PrivateKey privateKey;
    private static String FECHAR_SOCKET = "CLOSECONECTION";
    private static Integer PORT = 8084;

    public Servidor(Socket socket) {
        this.conexao = socket;
    }

    public static void main(String args[]) {
        privateKey = converterPrivateKey(CHAVE_PRIVADA);
        ServerSocket servidor = iniciarServidor();
        conexoes = new ArrayList<>();
        esperarConexoes(servidor);
    }

    public static ServerSocket iniciarServidor() {
        try {
            ServerSocket servidor = new ServerSocket(PORT);
            System.out.printf("Servidor iniciado na porta %s \n", PORT.toString());
            return servidor;
        } catch (Exception e) {
            System.out.println("Houve um erro ao iniciar o servidor: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static void esperarConexoes(ServerSocket server) {
        try {
            while (true) {
                Socket conexao = server.accept();
                Thread t = new Servidor(conexao);
                t.start();
            }
        } catch (Exception e) {
            System.out.println("Houve um erro ao estabelecer conexão " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            InputStream in = this.conexao.getInputStream();
            OutputStream out = this.conexao.getOutputStream();
            conexoes.add(out);

            while (true) {
                byte[] buffer = new byte[1024];
                int bytesLidos = in.read(buffer);
                byte[] mensagemCifrada = Arrays.copyOf(buffer, bytesLidos);
                String mensagem = descriptograr(mensagemCifrada);
                if (mensagem.equals(FECHAR_SOCKET)) {
                    conexoes.remove(out);
                    if (conexoes.isEmpty()) {
                        break;
                    }
                } else {
                    enviarMensagemChat(conexoes, mensagem);
                }
            }
        } catch (Exception e) {
            System.out.println("Falha na conexão com os clientes " + e);
        }
    }

    public void enviarMensagemChat(List<OutputStream> conexoes, String mensagem) {
        byte[] mensagemCriptografada = criptografar(mensagem);
        for (OutputStream conexao : conexoes) {
            try {
                conexao.write(mensagemCriptografada);
                conexao.flush();
            } catch (Exception e) {
                System.out.println("Não foi possível enviar a mensagem");
                e.printStackTrace();
            }
        }
    }

    public static PrivateKey converterPrivateKey(String chavePrivadaString){
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

    public static String descriptograr(byte[] textoCifrado){
        try {
            final Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            String textoDescriptografado = new String(cipher.doFinal(textoCifrado));
            return textoDescriptografado;
        } catch (Exception e){
            System.out.println("Não foi possível iniciar o Cipher.");
            throw new RuntimeException(e);
        }
    }

    public static byte[] criptografar(String mensagem){
        try {
            final Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            byte[] textoCriptografadoBytes = cipher.doFinal(mensagem.getBytes());
            return textoCriptografadoBytes;
        } catch (Exception e){
            System.out.println("Não foi possível iniciar o Cipher.");
            throw new RuntimeException(e);
        }
    }
}