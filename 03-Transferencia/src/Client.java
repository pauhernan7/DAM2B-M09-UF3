    import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private static final String DIR_ARRIBADA = "/tmp";
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public void connectar() throws IOException {
        socket = new Socket("localhost", 9999);
        oos = new ObjectOutputStream(socket.getOutputStream());
        ois = new ObjectInputStream(socket.getInputStream());
        System.out.println("Connectant a -> localhost:9999");
        System.out.println("Connexio acceptada: " + socket.getRemoteSocketAddress());
    }

    public void tancarConnexio() throws IOException {
        if (ois != null) ois.close();
        if (oos != null) oos.close();
        if (socket != null) socket.close();
        System.out.println("Connexio tancada.");
    }

    public void rebreFitxers() throws IOException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Nom del fitxer a rebre ('sortir' per sortir): ");
        String nomFitxer = scanner.nextLine();

        if ("sortir".equalsIgnoreCase(nomFitxer)) {
            System.out.println("Sortint...");
            return;
        }

        oos.writeObject(nomFitxer);
        oos.flush();

        byte[] contingut = (byte[]) ois.readObject();
        if (contingut == null) {
            System.out.println("Error: El fitxer no existeix al servidor");
            return;
        }

        System.out.print("Nom del fitxer a guardar: ");
        String desti = scanner.nextLine();
        
        try (FileOutputStream fos = new FileOutputStream(desti)) {
            fos.write(contingut);
            System.out.println("Fitxer rebut i guardat com: " + desti);
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        try {
            client.connectar();
            client.rebreFitxers();
            client.tancarConnexio();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
