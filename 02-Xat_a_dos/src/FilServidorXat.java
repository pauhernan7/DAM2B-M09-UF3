import java.io.*;

public class FilServidorXat implements Runnable {
    private ObjectInputStream input;
    private static final String MSG_SORTIR = "sortir";

    public FilServidorXat(ObjectInputStream input) {
        this.input = input;
    }

    @Override
    public void run() {
        try {
            String missatge;
            while ((missatge = (String) input.readObject()) != null) {
                System.out.println("Rebut: " + missatge);
                if (missatge.equalsIgnoreCase(MSG_SORTIR)) {
                    break;
                }
            }
            System.out.println("Fil de xat finalitzat.");
        } catch (Exception e) {
            System.out.println("El client ha tancat la connexió.");
        }
    }
}
