package Chat_Final.Chat_UDP;

public class AdminUDP {
    public static void main(String[] args) {
        //se ejecuta una instancia de cliente pero con el argumento de que es administrador
        ClienteUDP.main(new String[] { Boolean.toString(true) });
    }
}
