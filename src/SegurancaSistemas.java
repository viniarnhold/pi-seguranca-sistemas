import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SegurancaSistemas extends JFrame {

    private static String FECHAR_SOCKET = "CLOSECONECTION";

    private JTextArea chat;
    private JButton botaoEnviar;
    private JTextField campoDigitacao;
    private static Socket socket;
    static BufferedReader in;
    static PrintWriter out;
    public static SegurancaSistemas tela;

    public SegurancaSistemas(){
        super("PI - Segurança em Sistemas - Vinícius Arnhold");
        setSize(1280, 720);
        setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
        getContentPane().setBackground(Color.GRAY);
        setLayout(null);

        // Área de visualização do chat
        chat = new JTextArea();
        chat.setBounds(10, 10, 1245, 620);
        chat.setFont(new Font("Arial", Font.PLAIN, 16));
        chat.setEditable(false);
        chat.setLineWrap(true);
        chat.setWrapStyleWord(true);
        add(chat);

        // Área de digitação de mensagem
        campoDigitacao = new JTextField();
        campoDigitacao.setBounds(10, 640, 1120, 30 );
        campoDigitacao.setFont(new Font("Arial", Font.BOLD, 16));
        add(campoDigitacao);

        // Botão Enviar
        botaoEnviar = new JButton( "Enviar");
        botaoEnviar.setBounds(1140, 640, 115, 30 );
        botaoEnviar.setBackground(Color.LIGHT_GRAY);
        botaoEnviar.setActionCommand("Enviar");
        botaoEnviar.addActionListener(acaoBotaoEnviar);
        add(botaoEnviar);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                fecharSocket();
            }
        });
    }

    public static void main(String[] args) throws Exception {
        SegurancaSistemas tela = new SegurancaSistemas();
        tela.setLocationRelativeTo(null);
        tela.setVisible(true);
        socket = new Socket("localhost", 8084);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream());

    }

    ActionListener acaoBotaoEnviar = new ActionListener(){
        public void actionPerformed(ActionEvent arg0) {
            if( arg0.getActionCommand().equals( "Enviar" ) ) {
                // Obter a String do campo JTextField
                String mensagem = campoDigitacao.getText();
                // Validação campo em branco
                if(mensagem.isBlank()){
                    JOptionPane.showMessageDialog(tela, "É necessário digitar uma mensagem para ser enviada!");
                } else {
                    try {
                        out.println(mensagem);
                        out.flush();
                        String novaMensagem = in.readLine();
                        campoDigitacao.setText("");
                        chat.setText(chat.getText() + novaMensagem + "\n");
                    } catch (Exception exception){
                        JOptionPane.showMessageDialog(tela, "Não foi possível enviar a mensagem" + exception);
                    }
                }
            }
        }
    };
    // Método para fechar o socket e os streams
    private void fecharSocket() {
        try {
            out.println(FECHAR_SOCKET);
            out.flush();
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (Exception e) {
            System.err.println("Erro ao fechar a conexão: " + e.getMessage());
        }
    }
}
