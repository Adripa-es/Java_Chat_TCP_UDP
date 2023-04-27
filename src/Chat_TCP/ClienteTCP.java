package Chat_Final.Chat_TCP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

import Chat_Final.InterfacesGraficas.InterfazAdmin;
import Chat_Final.InterfacesGraficas.InterfazClientes;

public class ClienteTCP {
    private static final String SERVER_IP = "localhost";
    private static final int PORT = 1234;
    private static String nickName;
    private static Socket socket;
    private static BufferedReader in;
    private static PrintWriter out;

    private static boolean esAdmin = false;

    private InterfazAdmin ChatAdmin = null;
    private InterfazClientes chatCliente = null;

    public static void main(String[] args) {
        // Comprobar si se ejecuta desde el Cliente o el Admin
        try {
            esAdmin = Boolean.parseBoolean(args[0]);
            System.out.println("Administrador ejecutandose...");
        } catch (Exception e) {
            System.out.println("Cliente ejecutandose...");
        }

        new ClienteTCP();
    }

    public ClienteTCP() {
        Scanner teclado = new Scanner(System.in);
        boolean connect = false;
        while (!connect) {
            try {
                socket = new Socket(SERVER_IP, PORT);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                while (true) {
                    String line = in.readLine();
                    if (line.startsWith("CHATFULL")) {
                        throw new Exception("CHATFULL");
                        /*
                         * Se lanza una nueva excepción de tipo Exception con el mensaje "CHATFULL".
                         * Esto causará que el control salga del bloque try
                         * y entre en el bloque catch correspondiente.
                         * En este caso, el bloque catch simplemente
                         * imprime un mensaje en la consola indicando
                         * que se está intentando reconectar
                         * y establece la variable connect en falso
                         * para que el bucle while continúe y se intente reconectar de nuevo.
                         */

                    } else if (line.startsWith("SUBMITNAME")) {
                        nickName = PedirNombre();
                        out.println(nickName);
                    } else if (line.startsWith("NAMEACCEPTED")) {
                        connect = true;

                        // Crear la interfaz de usuario
                        if (esAdmin) {
                            ChatAdmin = new InterfazAdmin(this);
                            ChatAdmin.setNickName(nickName);
                            actualizarListaUsuarios();
                        } else {
                            chatCliente = new InterfazClientes(this);
                            chatCliente.setNickName(nickName);
                        }

                        break;
                    } else if (line.startsWith("NICKNAMEEXISTS")) {
                        nickName = PedirNombre();
                    }
                }

                new ChatReader().start();

                while (true) {
                    String mensajePropio = teclado.nextLine();
                    out.println(mensajePropio);
                }
            } catch (Exception e) {
                // cuando no se pueda conectar, ya sea por el servidor o por el chat lleno
                System.out.println("Intentando reconectar..." + e);
                mostrarVentanaDeEspera();
                connect = false;
            }
        }
        teclado.close();
    }

    private class ChatReader extends Thread {
        public void run() {
            try {
                String mensaje;
                while ((mensaje = in.readLine()) != null) {

                    // si el usuario es admin, se le actualizará la lista de usuarios
                    if (esAdmin) {
                        // cada vez que la lista de usuarios cambia (osea se conecta/desconecta
                        // alguien), se solicita la lista
                        if (mensaje.endsWith(" se ha desconectado del chat.")
                                || (mensaje.endsWith(" se ha conectado al chat."))) {
                            actualizarListaUsuarios();
                        }
                        // se imprimirá el mensaje a nivel grafico si es admin
                        ChatAdmin.actualizarCuadroMensajes(mensaje);
                    } else {
                        // se imprimirá el mensaje a nivel grafico si es cliente
                        chatCliente.actualizarCuadroMensajes(mensaje);
                    }

                }
                JOptionPane.showMessageDialog(null, "Se ha perdido la conexión");
                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void mostrarVentanaDeEspera() {
        JOptionPane.showMessageDialog(null,
                "Hubo un error de conexion o el chat está lleno, reconectando en 10 segundos...",
                "Atención !!",
                JOptionPane.INFORMATION_MESSAGE);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static String PedirNombre() {
        if (esAdmin) {
            return "(ADMIN) " + JOptionPane.showInputDialog("Introduce tu nickname: ");
        }else{
            return JOptionPane.showInputDialog("Introduce tu nickname: ");
        }
       
    }

    public static PrintWriter getOut() {
        return out;
    }

    public void actualizarListaUsuarios() {
        try {
            // Enviar solicitud para obtener lista de usuarios conectados
            out.println("/getUsers");

            // Recibir la respuesta del servidor con la lista de usuarios conectados
            // en un unico stringaa
            String response = in.readLine();

            // separamos la lista de elementos del string, metiendolos en un arraylist
            String[] userArray = response.split(",");
            ArrayList<String> listaUsuariosConectados = new ArrayList<>(Arrays.asList(userArray));

            // Actualizar la lista en la interfaz
            DefaultListModel<String> model = new DefaultListModel<>();
            for (String usuario : listaUsuariosConectados) {
                model.addElement(usuario);
            }
            InterfazAdmin.listaUsuarios.setModel(model);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
