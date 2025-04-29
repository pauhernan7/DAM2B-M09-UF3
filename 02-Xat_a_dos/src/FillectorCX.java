import java.io.*;

public class FillectorCX implements Runnable {
    private ObjectInputStream input;

    public FillectorCX(ObjectInputStream input) {
        this.input = input;
    }

    @Override
    public void run() {
        try {
            String missatge;
            while ((missatge = (String) input.readObject()) != null) {
                System.out.println("Rebut: " + missatge);
                if (missatge.equalsIgnoreCase("sortir")) {
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Connexió tancada.");
        }
    }
}