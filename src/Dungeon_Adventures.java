import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Dungeon_Adventures {
    public final static int PORT = 13;

    public static class Giocatore {
        private int lvlSalute;
        private int qtPozione;

        public Giocatore() {
            int x = (int) (Math.random() * (100 - 10)) + 10;
            int y = (int) (Math.random() * (20 - 1)) + 1;
            this.lvlSalute = x;
            this.qtPozione = y;
        }

        public int getLvlSalute() {
            return lvlSalute;
        }

        public int getQtPozione() {
            return qtPozione;
        }

        public int combattiMostro(Mostro m) {

            int risultato = -1;

            int salutePersaGiocatore = (int) (Math.random() * ((this.lvlSalute) - 1)) + 1;
            int salutePersaMostro = (int) (Math.random() * (m .lvlSalute - 1)) + 1;

            this.lvlSalute -= salutePersaGiocatore;
            m.lvlSalute -= salutePersaMostro;

            if (this.lvlSalute <= 0 || m.lvlSalute <= 0) {
                if (this.lvlSalute <= 0 && m.lvlSalute <= 0) risultato = 0;
                if (m.lvlSalute <= 0) risultato = 1;
                if (this.lvlSalute <= 0) risultato = 2;
            }

            return risultato;
        }

        public int beviPozione() {
            if (this.qtPozione == 0) return -1;
            int saluteRecuperata = (int) ((Math.random() * ((this.qtPozione) - 1)) + 1) * 5;
            this.lvlSalute += saluteRecuperata;
            this.qtPozione--;
            if (this.lvlSalute > 100) this.lvlSalute = 100;
            return 0;
        }

     }

    public static class Mostro {
        private int lvlSalute;

        public Mostro() {
            int z = (int) (Math.random() * (100 - 10)) + 10;
            this.lvlSalute = z;
        }

        public int getLvlSalute() {
            return lvlSalute;
        }
    }

    public static class GestioneClient implements Runnable {
        private Socket connection;
        public GestioneClient(Socket connection) {
            this.connection = connection;
        }

        public void run() {

            System.out.println("Ho ricevuto una richiesta da " +  this.connection.toString());

            try {

                DataOutputStream out = new DataOutputStream(this.connection.getOutputStream());
                DataInputStream in = new DataInputStream(this.connection.getInputStream());

                Giocatore g = new Giocatore();
                int val = -1;
                boolean done = false;
                int numero_mostri = 0;
                boolean perso = false;

                out.writeUTF("Il tuo livello di salute e' di " + g.lvlSalute + " e la tua quantita' di pozione e' di " + g.qtPozione);
                out.flush();

                int ris, i = 0;

                while (true) {

                    Mostro m = new Mostro();

                    out.writeUTF("Combatterai con un mostro con livello di salute " + m.lvlSalute);
                    out.flush();

                    while (true) {

                        if (i == 0) {
                            out.writeUTF("Quale comando vuoi eseguire?");
                            out.flush();
                        } else {
                            out.writeUTF("Qual e' la tua prossima mossa?");
                            out.flush();
                        }


                        val = in.readInt();

                        System.out.println("IL client ha risposto con il valore " + val);

                        if (val == 1) {
                            ris = g.combattiMostro(m);
                            if (ris == 0) {
                                out.writeUTF("Il combattimento e' finito in parita'!");
                                out.flush();
                                break;
                            }
                            if (ris == 1) {
                                numero_mostri++;
                                out.writeUTF("Congratulazioni! Hai vinto il duello!");
                                out.flush();
                                break;
                            }
                            if (ris == 2) {
                                out.writeUTF("Mi dispiace, il mostro ti ha battuto, non sei degno di rigiocare!");
                                out.flush();
                                perso = true;
                                break;
                            }
                            out.writeUTF("Dopo il combattimento il tuo livello di salute e' di " + g.getLvlSalute() +
                                             " \nIl livello di salute del mostro e' di " + m.getLvlSalute());
                        }

                        if (val == 2) {
                            ris = g.beviPozione();
                            if (ris == -1) {
                                out.writeUTF("Mi dispiace, hai esaurito le pozioni!");
                                out.flush();
                            } else {
                                out.writeUTF("Adesso il tuo livello di salute e' " + g.getLvlSalute() +
                                             " \nTi restano " + g.getQtPozione() + " pozioni da utilizzare!");
                                out.flush();
                            }
                        }

                        if (val == 3) {
                            out.writeUTF("Codardo");
                            out.flush();
                            break;
                        }

                        out.writeInt(200);
                        out.flush();

                    }

                    if (perso == true) {
                        out.writeInt(-1);
                        out.flush();
                        break;
                    } else {
                        out.writeInt(1);
                        out.flush();

                        out.writeUTF("Premi '1' per giocare ancora, '0' altrimenti.");
                        out.flush();

                        val = in.readInt();

                        if (val == 0) {
                            out.writeInt(-1);
                            out.flush();
                            break;
                        } else if (val == 1) {
                            out.writeInt(200);
                            out.flush();
                        }
                    }


                }

                out.writeUTF("Numero di mostri uccisi: " + numero_mostri);

                System.out.println("Connessione con " + this.connection.toString() + " terminata");
                this.connection.close();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        try (ServerSocket listener = new ServerSocket(PORT)) {
            ExecutorService pool = Executors.newCachedThreadPool();
            System.out.println("In attesa di nuovi giocatori...");
            while (true) {
                pool.execute(new GestioneClient(listener.accept()));
            }
        }  catch (IOException ex) {
            System.err.println(ex);
        }
    }
}