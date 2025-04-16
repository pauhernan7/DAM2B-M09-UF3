import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 7777;

        try (Socket socket = new Socket(host, port)) {
            PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);

            salida.println("Prova d'enviament 1");
            salida.println("Prova d'enviament 2");
            salida.println("Adéu!");

            salida.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
