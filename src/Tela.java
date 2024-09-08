import javax.swing.*;
import java.awt.*;

public class Tela extends JFrame {

    public JTextArea chat;
    public JButton botaoEnviar;
    public JTextField campoDigitacao;

    public Tela() {
        super("PI - Segurança em Sistemas - Vinícius Arnhold");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
        campoDigitacao.setBounds(10, 640, 1120, 30);
        campoDigitacao.setFont(new Font("Arial", Font.BOLD, 16));
        add(campoDigitacao);

        // Botão Enviar
        botaoEnviar = new JButton("Enviar");
        botaoEnviar.setBounds(1140, 640, 115, 30);
        botaoEnviar.setBackground(Color.LIGHT_GRAY);
        botaoEnviar.setActionCommand("Enviar");
        add(botaoEnviar);
    }
}
