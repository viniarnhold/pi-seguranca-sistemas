import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Cliente extends Thread {

    private static String FECHAR_SOCKET = "CLOSECONECTION";
    private static Socket socket;
    private static boolean conexaoEstabelecida;
    private static BufferedReader in;
    private static PrintWriter out;
    private static Tela tela;

    public Cliente(Socket socket) {
        this.socket = socket;
    }

    public static void main(String[] args) throws Exception {
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
}
