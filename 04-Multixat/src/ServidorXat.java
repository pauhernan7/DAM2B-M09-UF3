import java.io.*;
import java.net.*;
import java.util.*;

public class ServidorXat {
    public static final int PORT = 9999;
    public static final String HOST = "localhost";
    public static final String MSG_SORTIR = "sortir";
    private boolean sortir = false;

    private Hashtable<String, GestorClients> clients = new Hashtable<>();
    private ServerSocket serverSocket;

    public void servidorAEscoltar() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("Servidor iniciat a " + HOST + ":" + PORT);
        while (!sortir) {
            Socket socket = serverSocket.accept();
            System.out.println("Client connectat: " + socket.getRemoteSocketAddress());
            GestorClients gestor = new GestorClients(socket, this);
            gestor.start();
        }
    }

    public void pararServidor() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
    }

    public void finalitzarXat() {
        enviarMissatgeGrup("Servidor", MSG_SORTIR);
        clients.clear();
        System.out.println("DEBUG: multicast sortir");
        sortir = true;
    }

    public void afegirClient(GestorClients gc) {
        clients.put(gc.getNom(), gc);
        enviarMissatgeGrup("Servidor", "Entra: " + gc.getNom());
        System.out.println("DEBUG: multicast Entra: " + gc.getNom());
    }

    public void eliminarClient(String nom) {
        if (clients.containsKey(nom)) {
            clients.remove(nom);
            enviarMissatgeGrup("Servidor", "Surt: " + nom);
            System.out.println("DEBUG: multicast Surt: " + nom);
        }
    }

    public void enviarMissatgeGrup(String remitent, String msg) {
        String missatge = Missatge.getMissatgeGrup(remitent, msg);
        for (GestorClients gc : clients.values()) {
            gc.enviarMissatge(remitent, missatge);
        }
    }

    public void enviarMissatgePersonal(String destinatari, String remitent, String msg) {
        GestorClients gc = clients.get(destinatari);
        if (gc != null) {
            String msgFormatejat = Missatge.getMissatgePersonal(remitent, msg);
            gc.enviarMissatge(remitent, msgFormatejat);
            System.out.println("Missatge personal per (" + destinatari + ") de (" + remitent + "): " + msg);
        }
    }

    public static void main(String[] args) throws IOException {
        ServidorXat sx = new ServidorXat();
        Thread serverThread = new Thread(() -> {
            try {
                sx.servidorAEscoltar();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        serverThread.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                sx.finalitzarXat();
                sx.pararServidor();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }
}