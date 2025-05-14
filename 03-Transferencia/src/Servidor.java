// Servidor.java
import java.io.*;
import java.net.*;

public class Servidor {
    private static final int PORT = 9999;
    private static final String HOST = "localhost";
    private ServerSocket serverSocket;

    public Socket connectar() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("Acceptant connexions en -> " + HOST + ":" + PORT);
        System.out.println("Esperant connexio...");
        Socket socket = serverSocket.accept();
        System.out.println("Connexio acceptada: " + socket.getRemoteSocketAddress());
        return socket;
    }

    public void tancarConnexio(Socket socket) throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
            System.out.println("Tancant connexió amb el client: " + socket.getRemoteSocketAddress());
        }
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
    }

    public void enviarFitxers(Socket socket) throws IOException {
        try (ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {

            System.out.println("Esperant el nom del fitxer del client...");
            String nomFitxer = (String) ois.readObject();

            if (nomFitxer == null || nomFitxer.isEmpty()) {
                System.out.println("Nom del fitxer buit o nul. Sortint...");
                return;
            }

            System.out.println("Nomfitxer rebut: " + nomFitxer);
            Fitxer fitxer = new Fitxer(nomFitxer);
            
            try {
                byte[] contingut = fitxer.getContingut();
                oos.writeObject(contingut);
                oos.flush();
                System.out.println("Contingut del fitxer a enviar: " + contingut.length + " bytes");
                System.out.println("Fitxer enviat al client: " + nomFitxer);
            } catch (FileNotFoundException e) {
                System.out.println("Error llegint el fitxer del client: " + e.getMessage());
                oos.writeObject(null);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        try {
            Socket socket = servidor.connectar();
            servidor.enviarFitxers(socket);
            servidor.tancarConnexio(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
