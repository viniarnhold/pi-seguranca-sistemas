import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Cliente extends Thread {

    private static String FECHAR_SOCKET = "CLOSECONECTION";
    private static String CHAVE_PUBLICA = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCZILVQdhKkRU0hLg4E9CKUbpci8kpm9VWXZ3bZvnduOJZuqnwk5huXIVmChoW6qdHO02XcXXYoFaJ7qVAzc9llfvRz23FeuqYoyJz5jZ02rP7oMuw+Sp74o0MNq6TxDLheYI6CWOG3YZSUmxwow8u2eYGR3lRf139MojG7vnMIBwIDAQAB";
    private static PublicKey publicKey;
    private static Socket socket;
    private static boolean conexaoEstabelecida;
    private static BufferedReader in;
    private static PrintWriter out;
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
                    String mensagem = FECHAR_SOCKET;
                    out.println(mensagem);
                    out.flush();
                    in.close();
                    out.close();
                    socket.close();
                    conexaoEstabelecida = false;
                    System.exit(0);
                } catch (Exception erro) {
                    System.err.println("Erro ao fechar a conexão: " + erro.getMessage());
                }
            }
        });
        try {
            Socket socket = new Socket("localhost", 8084);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());
            conexaoEstabelecida = true;
            Thread thread = new Cliente(socket);
            thread.start();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(tela, "Não foi possível estabelecer conexão" + e);
        }
    }

    static ActionListener acaoBotaoEnviar = new ActionListener() {
        public void actionPerformed(ActionEvent arg0) {
            if (arg0.getActionCommand().equals("Enviar")) {
                String mensagem = tela.campoDigitacao.getText();
                if (mensagem.isBlank()) {
                    JOptionPane.showMessageDialog(tela, "É necessário digitar uma mensagem para ser enviada!");
                } else {
                    try {
                        out.println(mensagem);
                        out.flush();
                        tela.campoDigitacao.setText("");
                    } catch (Exception exception) {
                        JOptionPane.showMessageDialog(tela, "Não foi possível enviar a mensagem" + exception);
                    }
                }
            }
        }
    };

    public void run() {
        try {
            BufferedReader entrada =
                    new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            while (conexaoEstabelecida) {
                String novaMensagem = entrada.readLine();
                tela.chat.setText(tela.chat.getText() + novaMensagem + "\n");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(tela, "Não foi possível receber a mensagem do servidor" + e);
        }
    }

    public static PublicKey converterPublicKey(String chavePublicaString){
        byte[] encoded = Base64.getDecoder().decode(chavePublicaString);
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e){
            System.out.println("Não foi possível converter a chave pública.");
            throw new RuntimeException(e);
        }
    }
}
