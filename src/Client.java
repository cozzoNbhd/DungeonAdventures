import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws UnknownHostException {

        InetAddress host = InetAddress.getLocalHost();

        Socket socket = null;
        String s = "User";

        try {

            socket = new Socket(host.getHostAddress(), 13);

            socket.setSoTimeout(15000);

            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());

            Scanner sc = new Scanner(System.in);
            boolean done;
            int n;

            String msg1 = in.readUTF();
            System.out.println(msg1);

            while (true) {

                // Combatterai con un mostro di lvl ....
                String msg2 = in.readUTF();
                System.out.println(msg2);

                while (true) {

                    // quale comando vuoi inviare?
                    String msg3 = in.readUTF();
                    System.out.println(msg3);

                    // ---------- Inserimento input ------------
                    done = false;
                    n = -1;

                    while (!done) {
                        try {
                            n = sc.nextInt();
                            if (n == 1 || n == 2 || n == 3 || n == 4) done = true;
                        }
                        catch (InputMismatchException e) {
                            System.out.println("Il valore inserito NON è un intero!");
                            sc.nextLine();
                        }
                    }

                    // Invio valore inserito al Server
                    out.writeInt(n);

                    String msgRisposta = in.readUTF();
                    System.out.println(msgRisposta);

                    n = in.readInt();
                    if (n == -1) break;
                    else if (n == 1) {
                        msg1 = in.readUTF();
                        System.out.println(msg1);

                        done = false;
                        n = -1;

                        while (!done) {
                            try {
                                n = sc.nextInt();
                                if (n == 0 || n == 1) done = true;
                            }
                            catch (InputMismatchException e) {
                                System.out.println("Il valore inserito NON è valido!");
                                sc.nextLine();
                            }
                        }

                        out.writeInt(n);

                        n = in.readInt();
                        break;


                    } else continue;

                }

                if (n == -1) break;
                else if (n == 200) continue;


            }

            String msgFinale = in.readUTF();
            System.out.println(msgFinale);

        } catch (IOException ex) {
            System.out.println("could not connect to time.nist.gov");
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ex){}
            }
        }
    }
}

