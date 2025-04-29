import java.io.*;
import java.net.*;

public class ServidorXat {
    private static final int PORT = 9999;
    private static final String HOST = "localhost";
    private static final String MSG_SORTIR = "sortir";
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    public void iniciarServidor() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("Servidor iniciat a " + HOST + ":" + PORT);
    }

    public void pararServidor() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
            System.out.println("Servidor aturat.");
        }
    }

    private String getNom(ObjectInputStream input, ObjectOutputStream output) throws IOException, ClassNotFoundException {
        output.writeObject("Escriu el teu nom:");
        return (String) input.readObject();
    }

    public static void main(String[] args) {
        try {
            ServidorXat servidor = new ServidorXat();
            servidor.iniciarServidor();
            
            servidor.clientSocket = servidor.serverSocket.accept();
            System.out.println("Client connectat: " + servidor.clientSocket.getRemoteSocketAddress());
            
            servidor.output = new ObjectOutputStream(servidor.clientSocket.getOutputStream());
            servidor.input = new ObjectInputStream(servidor.clientSocket.getInputStream());
            
            String nomClient = servidor.getNom(servidor.input, servidor.output);
            System.out.println("Nom rebut: " + nomClient);
            
            FilServidorXat fil = new FilServidorXat(servidor.input);
            Thread filThread = new Thread(fil);
            System.out.println("Fil de xat creat.");
            filThread.start();
            System.out.println("Fil de " + nomClient + " iniciat");
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String missatge;
            while (true) {
                System.out.print("Missatge ('sortir' per tancar): ");
                missatge = reader.readLine();
                servidor.output.writeObject(missatge);
                if (missatge.equalsIgnoreCase(MSG_SORTIR)) {
                    break;
                }
            }
            
            filThread.join();
            servidor.clientSocket.close();
            servidor.pararServidor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}