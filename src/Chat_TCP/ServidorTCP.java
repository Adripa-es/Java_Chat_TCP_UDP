package Chat_Final.Chat_TCP;

import java.io.*;
import java.net.*;
import java.util.*;

public class ServidorTCP {
    private static final int PORT = 1234;
    // Creamos una lista de usuarios conectados, utilizando una estructura Set que
    // no permite duplicados. La lista se ordena automáticamente cada vez que hay
    // cambios, gracias a la clase ordenarLista.
    public static Set<String> listaUsuariosConectados = new TreeSet<>(new ordenarLista());
    private static Set<PrintWriter> writers = new HashSet<>();
    private static Set<Handler> handlers = new HashSet<>();

    private static String identificadorAdmin = "(ADMIN) ";

    public static void main(String[] args) throws Exception {
        // Imprimimos mensaje de inicio del chat.
        System.out.println("Chat iniciado...");
        // Creamos el socket del servidor.
        ServerSocket listener = new ServerSocket(PORT);
        try {
            // Esperamos por nuevas conexiones entrantes.
            while (true) {
                Socket socket = listener.accept();
                // Mensaje de control que indica que un cliente se ha conectado.
                System.out.println("Cliente conectado: " + socket.getInetAddress());
                // Creamos un nuevo manejador de conexión para el cliente.
                Handler handler = new Handler(socket);
                handlers.add(handler);
                handler.start();
            }
        } finally {
            listener.close();
        }
    }

    // Clase interna que maneja la conexión de cada cliente.
    private static class Handler extends Thread {
        private String username;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        // Constructor que recibe el socket de conexión del cliente.
        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                // Creamos el flujo de entrada de datos del cliente.
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                // Creamos el flujo de salida de datos del cliente.
                out = new PrintWriter(socket.getOutputStream(), true);

                // Solicitamos al cliente que ingrese su nombre de usuario.
                out.println("SUBMITNAME");
                // Esperamos a que el cliente ingrese su nombre.
                username = in.readLine();
                // Verificamos si el cliente ingresó un nombre válido.
                if (username == null) {
                    return;
                }
                synchronized (listaUsuariosConectados) {
                    // Si el nombre no está en uso, lo agregamos a la lista de usuarios conectados.
                    if (!listaUsuariosConectados.contains(username)) {
                        listaUsuariosConectados.add(username);
                        // Enviamos un mensaje a todos los clientes informando que se ha conectado un
                        // nuevo usuario.
                        enviarMensajeTodos(username + " se ha conectado al chat.");
                    } else {
                        // Si el nombre está en uso, informamos al cliente.
                        out.println("NICKNAMEEXISTS");
                        return;
                    }
                }

                // Agregamos el flujo de salida de datos del cliente a la lista de writers.
                writers.add(out);
                // Enviamos un mensaje de aceptación al cliente.
                out.println("NAMEACCEPTED " + username);

                // Ciclo que recibe y envía mensajes.
                while (true) {
                    // Esperamos por un mensaje del cliente.
                    String input = in.readLine();
                    // Verificamos si el cliente cerró la conexión.
                    if (input == null) {
                        return;
                    }
                    // Mensaje de control que indica que un cliente ha enviado un mensaje.
                    System.out.println("Mensaje recibido de " + username + ": " + input);
                    // Verificamos si se recibió una petición de desconexión.
                    if (input.startsWith("/dc")) {
                        // Separamos el nombre de usuario especificado.
                        String[] parts = input.split(" -->");
                        // Verificamos si el usuario que solicitó la desconexión es un administrador.
                        if (parts.length >= 2 && username.startsWith(identificadorAdmin)) {
                            // Desconectamos al usuario especificado.
                            String targetUsername = parts[1];
                            for (Handler handler : handlers) {
                                if (handler.username.equals(targetUsername)) {
                                    handler.socket.close();
                                }
                            }
                        }
                    }
                    // Verificamos si se recibió una petición de lista de usuarios.
                    else if (input.startsWith("/getUsers")) {
                        // Verificamos si el usuario que solicitó la lista es un administrador.
                        if (username.startsWith(identificadorAdmin)) {
                            // Enviamos la lista de usuarios al administrador.
                            String listaDeUsuarios = String.join(",", listaUsuariosConectados);
                            out.println(listaDeUsuarios);
                        }
                    } else {
                        // Si no es una petición especial, enviamos el mensaje a todos los clientes
                        // conectados.
                        enviarMensajeTodos(username + ": " + input);
                    }
                }
            } catch (IOException e) {
                // Mensaje de control que indica que un cliente se ha desconectado.
                System.out.println("Cliente desconectado: " + username);
            } finally {
                if (username != null) {
                    // Si el cliente se desconectó, lo removemos de la lista de usuarios conectados.
                    listaUsuariosConectados.remove(username);
                    // Enviamos un mensaje a todos los clientes informando que se ha desconectado un
                    // usuario.
                    enviarMensajeTodos(username + " se ha desconectado del chat.");
                    // Mensaje de control que muestra la lista de usuarios conectados.
                    System.out.println("Usuarios conectados: " + listaUsuariosConectados);
                }
                if (out != null) {
                    // Removemos el flujo de salida de datos del cliente de la lista de writers.
                    writers.remove(out);
                }
                try {
                    // Cerramos el socket de conexión del cliente.
                    socket.close();
                } catch (IOException e) {
                }
            }
        }

        // Método que envía un mensaje a todos los clientes conectados.
        private void enviarMensajeTodos(String message) {
            for (PrintWriter writer : writers) {
                writer.println(message);
            }
        }
    }

    // Clase interna que ordena la lista de usuarios conectados.
    private static class ordenarLista implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            if (o1.startsWith(identificadorAdmin) && !o2.startsWith(identificadorAdmin)) {
                return -1;
            }
            if (!o1.startsWith(identificadorAdmin) && o2.startsWith(identificadorAdmin)) {
                return 1;
            }
            if (o1.startsWith(identificadorAdmin) && o2.startsWith(identificadorAdmin)) {
                return o1.compareTo(o2);
            }
            return o1.compareTo(o2);
        }
    }
}
