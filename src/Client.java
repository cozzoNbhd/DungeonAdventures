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

        // String hostname = args.length > 0 ? args[0] : "time.nist.gov";

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
                    else continue;

                }

                break;



            }





            /*
            d.writeUTF(s);
            d.writeInt(data[0]);
            d.flush();
            d.writeInt(data[1]);
            d.flush();

            -----

            InputStream in = socket.getInputStream();

            StringBuilder time = new StringBuilder();
            InputStreamReader reader = new InputStreamReader(in, "ASCII");

            for (int c = reader.read(); c != -1; c = reader.read())
                time.append((char) c);
            */

            // --------------------
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

