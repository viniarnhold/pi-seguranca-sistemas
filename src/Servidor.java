import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor extends Thread {
    private static String FECHAR_SOCKET = "CLOSECONECTION";

    public static void main(String[] args) throws Exception {
        ServerSocket ss = new ServerSocket(8084);
        Socket s = ss.accept();
        while (true) {
            new Thread(() -> {
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    PrintWriter out = new PrintWriter(s.getOutputStream());
                    String msg = in.readLine();
                    System.out.println("Mensagem recebida: " + msg);
                    String resposta = "Resposta: " + msg + " recebida";
                    out.println(resposta);
                    out.flush();
                } catch (Exception e) {
                    System.out.println("Não foi possível estabelecer a conexão");;
                }
            }).start();
    }}
}