import javax.crypto.Cipher;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

public class Cliente extends Thread {

    private static String FECHAR_SOCKET = "SAIU DO CHAT";
    private static String CHAVE_PUBLICA = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCZILVQdhKkRU0hLg4E9CKUbpci8kpm9VWXZ3bZvnduOJZuqnwk5huXIVmChoW6qdHO02XcXXYoFaJ7qVAzc9llfvRz23FeuqYoyJz5jZ02rP7oMuw+Sp74o0MNq6TxDLheYI6CWOG3YZSUmxwow8u2eYGR3lRf139MojG7vnMIBwIDAQAB";
    private static PublicKey publicKey;
    private static Socket socket;
    private static String identificacao;
    private static boolean conexaoEstabelecida;
    private static InputStream in;
    private static OutputStream out;
    private static Tela tela;

    public Cliente(Socket socket) {
        this.socket = socket;
    }

    public static void main(String[] args) throws Exception {
        publicKey = converterPublicKey(CHAVE_PUBLICA);
        tela = new Tela();
        tela.setLocationRelativeTo(null);
        tela.setVisible(true);
        tela.botaoEnviar.addActionListener(acaoBotaoEnviar);
        tela.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    if (conexaoEstabelecida) {
                        enviarMensagem(FECHAR_SOCKET);
                        in.close();
                        out.close();
                        socket.close();
                        conexaoEstabelecida = false;
                    }
                    System.exit(0);
                } catch (Exception erro) {
                    System.err.println("Erro ao fechar a conexão: " + erro);
                }
            }
        });
        try {
            Socket socket = new Socket("localhost", 8084);
            out = socket.getOutputStream();
            conexaoEstabelecida = true;
            identificacao = JOptionPane.showInputDialog(tela, "Insira sua identificação:");
            while(identificacao.isBlank()){
                JOptionPane.showMessageDialog(tela, "A identificação não pode ser vazia!");
                identificacao = JOptionPane.showInputDialog(tela, "Insira sua identificação:");
            }
            JOptionPane.showMessageDialog(tela, "Seja bem vindo: " + identificacao);
            Thread thread = new Cliente(socket);
            thread.start();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(tela, "Não foi possível estabelecer conexão: " + e);
        }
    }

    static ActionListener acaoBotaoEnviar = new ActionListener() {
        public void actionPerformed(ActionEvent arg0) {
            if (arg0.getActionCommand().equals("Enviar")) {
                String mensagem = tela.campoDigitacao.getText();
                if (mensagem.isBlank()) {
                    JOptionPane.showMessageDialog(tela, "É necessário digitar uma mensagem para ser enviada!");
                } else {
                    enviarMensagem(mensagem);
                    tela.campoDigitacao.setText("");
                }
            }
        }
    };

    public void run() {
        try {
            in = this.socket.getInputStream();
            while (conexaoEstabelecida) {
                byte[] buffer = new byte[1024];
                int bytesLidos = in.read(buffer);
                byte[] mensagemCifrada = Arrays.copyOf(buffer, bytesLidos);
                String mensagemDecifrada = descriptograr(mensagemCifrada);
                tela.chat.setText(tela.chat.getText() + mensagemDecifrada + "\n");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(tela, "Não foi possível receber a mensagem do servidor: " + e);
        }
    }

    public static void enviarMensagem(String mensagem){
        try {
            byte[] mensagemCifrada = criptografar(identificacao.concat(": ").concat(mensagem));
            out.write(mensagemCifrada);
            out.flush();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(tela, "Não foi possível enviar a mensagem: " + e);
        }
    }

    public static PublicKey converterPublicKey(String chavePublicaString) {
        byte[] encoded = Base64.getDecoder().decode(chavePublicaString);
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(tela, "Não foi possível converter a chave pública: " + e);
            throw new RuntimeException(e);
        }
    }

    public static byte[] criptografar(String mensagem) {
        try {
            final Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(mensagem.getBytes());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(tela, "Não foi possível criptografar a mensagem: " + e);
            throw new RuntimeException(e);
        }
    }

    public static String descriptograr(byte[] textoCifrado) {
        try {
            final Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            return new String(cipher.doFinal(textoCifrado));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(tela, "Não foi possível descriptografar a mensagem: " + e);
            throw new RuntimeException(e);
        }
    }
}
