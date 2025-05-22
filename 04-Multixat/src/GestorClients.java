import java.io.*;
import java.net.*;

public class GestorClients extends Thread {
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private ServidorXat servidor;
    private String nom;
    private boolean sortir = false;

    public GestorClients(Socket socket, ServidorXat servidor) {
        this.socket = socket;
        this.servidor = servidor;
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.flush();
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("Error creant streams");
        }
    }

    public String getNom() {
        return nom;
    }

    public void enviarMissatge(String remitent, String msg) {
        try {
            oos.writeObject(msg);
            oos.flush();
        } catch (IOException e) {
            System.out.println("Error enviant missatge.");
        }
    }

    public void run() {
        try {
            while (!sortir) {
                String missatge = (String) ois.readObject();
                processaMissatge(missatge);
            }
            socket.close();
        } catch (Exception e) {
            sortir = true;
        }
    }

    public void processaMissatge(String missatge) {
        String codi = Missatge.getCodiMissatge(missatge);
        String[] parts = Missatge.getPartsMissatge(missatge);

        if (codi == null || parts == null) return;

        switch (codi) {
            case Missatge.CODI_CONECTAR:
                nom = parts[1];
                servidor.afegirClient(this);
                System.out.println(nom + " connectat.");
                break;
            case Missatge.CODI_SORTIR_CLIENT:
                sortir = true;
                servidor.eliminarClient(nom);
                break;
            case Missatge.CODI_SORTIR_TOTS:
                sortir = true;
                servidor.finalitzarXat();
                break;
            case Missatge.CODI_MSG_PERSONAL:
                servidor.enviarMissatgePersonal(parts[1], nom, parts[2]);
                break;
            case Missatge.CODI_MSG_GRUP:
                servidor.enviarMissatgeGrup(nom, parts[2]);
                break;
            default:
                System.out.println("Codi desconegut: " + codi);
        }
    }
}