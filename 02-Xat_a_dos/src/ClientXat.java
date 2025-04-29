import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClientXat {
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private static final String MSG_SORTIR = "sortir";

    public void connecta() throws IOException {
        socket = new Socket("localhost", 9999);
        System.out.println("Client connectat a localhost:9999");
        
        output = new ObjectOutputStream(socket.getOutputStream());
        input = new ObjectInputStream(socket.getInputStream());
        System.out.println("Flux d'entrada i sortida creat.");
    }

    public void enviarMissatge(String missatge) throws IOException {
        output.writeObject(missatge);
    }

    public void tancarClient() throws IOException {
        if (socket != null) {
            socket.close();
            System.out.println("Client tancat.");
        }
    }

    public static void main(String[] args) {
        try {
            ClientXat client = new ClientXat();
            client.connecta();
            
            FillectorCX filLectura = new FillectorCX(client.input);
            Thread filThread = new Thread(filLectura);
            filThread.start();
            System.out.println("Fil de lectura iniciat");
            
            Scanner scanner = new Scanner(System.in);
            String missatge;
            while (true) {
                System.out.print("Missatge ('sortir' per tancar): ");
                missatge = scanner.nextLine();
                client.enviarMissatge(missatge);
                System.out.println("Enviant missatge: " + missatge);
                if (missatge.equalsIgnoreCase(MSG_SORTIR)) {
                    break;
                }
            }
            
            scanner.close();
            filThread.join();
            client.tancarClient();
        } catch (Exception e) {
            System.out.println("El servidor ha tancat la connexió.");
        }
    }
}