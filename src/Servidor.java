import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Servidor extends Thread {
    private Socket conexao;
    private static List<PrintWriter> conexoes;
    private static String FECHAR_SOCKET = "CLOSECONECTION";
    private static Integer PORT = 8084;

    public Servidor(Socket socket) {
        this.conexao = socket;
    }

    public static void main(String args[]) {
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
            BufferedReader in = new BufferedReader(new InputStreamReader(this.conexao.getInputStream()));
            PrintWriter out = new PrintWriter(this.conexao.getOutputStream());
            conexoes.add(out);

            while (true) {
                String mensagem = in.readLine();
                if (mensagem.equals(FECHAR_SOCKET)) {
                    conexoes.remove(out);
                } else {
                    System.out.println(mensagem);
                    enviarMensagemChat(conexoes, mensagem);
                }
            }
        } catch (Exception e) {
            System.out.println("Falha na conexão com os clientes " + e);
        }
    }

    public void enviarMensagemChat(List<PrintWriter> conexoes, String mensagem) {
        for (PrintWriter conexao : conexoes) {
            try {
                conexao.println(mensagem);
                conexao.flush();
            } catch (Exception e) {
                System.out.println("Não foi possível enviar a mensagem");
                e.printStackTrace();
            }
        }
    }
}