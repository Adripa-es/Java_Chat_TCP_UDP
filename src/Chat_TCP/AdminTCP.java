package Chat_Final.Chat_TCP;

public class AdminTCP {
    public static void main(String[] args) {
        //se ejecuta una instancia de cliente pero con el argumento de que es administrador
        ClienteTCP.main(new String[] { Boolean.toString(true) });
    }
}
