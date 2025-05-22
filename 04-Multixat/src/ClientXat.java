import java.io.*;
import java.net.*;
import java.util.*;

public class ClientXat {
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    public boolean sortir = false;

    public void connecta() {
        try {
            socket = new Socket("localhost", 9999);
            System.out.println("Client connectat a localhost:9999");
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.flush();
            System.out.println("Flux d'entrada i sortida creat.");
        } catch (IOException e) {
            System.out.println("Error connectant-se.");
        }
    }

    public void enviarMissatge(String msg) {
        try {
            System.out.println("Enviant missatge: " + msg);
            oos.writeObject(msg);
            oos.flush();
        } catch (IOException e) {
            System.out.println("Error enviant missatge.");
        }
    }

    public void tancarClient() {
        try {
            System.out.println("Tancant client...");
            if (ois != null) ois.close();
            System.out.println("Flux d'entrada tancat.");
            if (oos != null) oos.close();
            System.out.println("Flux de sortida tancat.");
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.out.println("Error tancant client.");
        }
    }

    public void llegir() {
        try {
            ois = new ObjectInputStream(socket.getInputStream());
            System.out.println("DEBUG: Iniciant rebuda de missatges...");
            while (!sortir) {
                String msg = (String) ois.readObject();
                String codi = Missatge.getCodiMissatge(msg);
                String[] parts = Missatge.getPartsMissatge(msg);

                if (codi == null || parts == null) continue;

                switch (codi) {
                    case Missatge.CODI_SORTIR_TOTS:
                        sortir = true;
                        System.out.println("Tancant tots els clients.");
                        break;
                    case Missatge.CODI_MSG_PERSONAL:
                        System.out.println("Missatge de (" + parts[1] + "): " + parts[2]);
                        break;
                    case Missatge.CODI_MSG_GRUP:
                        System.out.println("Missatge grupal de (" + parts[1] + "): " + parts[2]);
                        break;
                    default:
                        System.out.println("Codi desconegut rebut.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error rebent missatge. Sortint...");
            sortir = true;
        } finally {
            tancarClient();
        }
    }

    public void ajuda() {
        System.out.println("---------------------");
        System.out.println("Comandes disponibles:");
        System.out.println("  1.- Conectar al servidor (primer pass obligatori)");
        System.out.println("  2.- Enviar missatge personal");
        System.out.println("  3.- Enviar missatge al grup");
        System.out.println("  4.- (o línia en blanc)-> Sortir del client");
        System.out.println("  5.- Finalitzar tothom");
        System.out.println("---------------------");
    }

    public String getLinea(Scanner sc, String msg, boolean obligatori) {
        String linia;
        do {
            System.out.print(msg);
            linia = sc.nextLine().trim();
        } while (obligatori && linia.isEmpty());
        return linia;
    }

    public static void main(String[] args) {
        ClientXat client = new ClientXat();
        Scanner sc = new Scanner(System.in);
        client.connecta();

        Thread t = new Thread(() -> client.llegir());
        t.start();

        while (!client.sortir) {
            client.ajuda();
            String opcio = sc.nextLine().trim();
            if (opcio.isEmpty()) {
                client.sortir = true;
                client.enviarMissatge(Missatge.getMissatgeSortirClient("Adéu"));
                break;
            }

            switch (opcio) {
                case "1":
                    String nom = client.getLinea(sc, "Introdueix el nom: ", true);
                    client.enviarMissatge(Missatge.getMissatgeConectar(nom));
                    break;
                case "2":
                    String destinatari = client.getLinea(sc, "Destinatari:: ", true);
                    String msgPers = client.getLinea(sc, "Missatge a enviar: ", true);
                    client.enviarMissatge(Missatge.getMissatgePersonal(destinatari, msgPers));
                    break;
                case "3":
                    String msgGrup = client.getLinea(sc, "Missatge a enviar: ", true);
                    client.enviarMissatge(Missatge.getMissatgeGrup("Usuari", msgGrup));
                    break;
                case "5":
                    client.enviarMissatge(Missatge.getMissatgeSortirTots("Adéu"));
                    client.sortir = true;
                    break;
                default:
                    System.out.println("Opció no vàlida.");
            }
        }

        sc.close();
        client.tancarClient();
    }
}