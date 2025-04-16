import java.io.*;
import java.net.*;

public class Servidor {
    public static void main(String[] args) {
        int port = 7777;

        try (ServerSocket servidor = new ServerSocket(port)) {
            System.out.println("Servidor en marxa a localhost:" + port);
            System.out.println("Esperant connexions a localhost:" + port);

            Socket client = servidor.accept();
            System.out.println("Client connectat: " + client.getInetAddress());

            BufferedReader entrada = new BufferedReader(new InputStreamReader(client.getInputStream()));

            String missatge;
            while ((missatge = entrada.readLine()) != null) {
                System.out.println("Rebut: " + missatge);
                if (missatge.equalsIgnoreCase("Adéu!")) break;
            }

            entrada.close();
            client.close();
            System.out.println("Servidor tancat.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
