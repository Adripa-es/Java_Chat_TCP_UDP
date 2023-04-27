package Chat_Final.Chat_UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ServidorUDP {

    private static ArrayList<Cliente> clientes = new ArrayList<>();
    private static final int MAX_USUARIOS = 10;

    public static void main(String[] args) {
        DatagramSocket socketServidor = null;
        try {
            socketServidor = new DatagramSocket(12345);
        } catch (SocketException e1) {
            e1.printStackTrace();
        }

        byte[] receiveBuffer = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

        // mensaje de control
        System.out.println("\nServidor iniciado...");

        try {
            while (true) {
                try {
                    // recibiendo paquete
                    socketServidor.receive(receivePacket);
                } catch (IOException e) {
                    // mensaje de control en caso de error
                    System.out.println("\nError al recibir paquete: " + e);
                    break;
                }

                // Transformamos el paquete en un string para procesarlo
                String mensaje = new String(receivePacket.getData(), 0, receivePacket.getLength());
                InetAddress direccion = receivePacket.getAddress();
                int puerto = receivePacket.getPort();

                // mensaje de control con el mensaje recibido
                System.out.println("\n\n\t" + mensaje);

                if (mensaje.startsWith("newUser:")) {
                    if (clientes.size() >= MAX_USUARIOS) {
                        // mensaje de control en caso de que el chat esté lleno
                        System.out.println("\nEl chat está lleno, no se pueden añadir más usuarios.");
                        String mensajeChatLleno = "CHATFULL";
                        enviarACliente(mensajeChatLleno, direccion, puerto, socketServidor);
                    } else {
                        manejarNuevoUsuario(mensaje, direccion, puerto, socketServidor);
                    }
                } else if (mensaje.contains("/dc ") || mensaje.contains("/getUsers")) {

                    if (mensaje.contains("/dc")) {
                        String[] partes = mensaje.split(": /dc ");
                        String nombreEscritor = partes[0];
                        String nombreUsuarioObjetivo = partes[1];
    
                        // si el escritor es un admin, se desconecta al usuario marcado
                        if (nombreEscritor.startsWith("(ADMIN)")) {
                            manejarDesconexion(nombreUsuarioObjetivo, socketServidor);
                        }
                        // en este caso, es el propio usuario quien se quiere desconectar
                        else if(nombreEscritor.equals(nombreUsuarioObjetivo)){
                            manejarDesconexion(nombreEscritor, socketServidor);
                        }else{
                            // mensaje de control si no se tienen los permisos necesarios para desconectar al usuario
                            System.out.println("\nPermiso denegado para desconectar al usuario: " + nombreUsuarioObjetivo);
                            enviarACliente("PERMISO DENEGADO", direccion, puerto, socketServidor);
                        }
                    } else if(mensaje.contains("/getUsers") && mensaje.startsWith("(ADMIN)")){
                        // se envía la lista de usuarios al admin que lo solicitó
                        enviarACliente(crearListadoUsuarios(), direccion, puerto, socketServidor);
                    }

                } else {
                    // Es un mensaje normal, reenviar a todos los clientes conectados
                    for (Cliente c : clientes) {
                        enviarACliente(mensaje, c.obtenerDireccion(), c.obtenerPuerto(), socketServidor);
                    }
                }
    
            }
        } catch (Exception e) {
            System.out.println("\nError: " + e);
        } finally {
            if (socketServidor != null) {
                socketServidor.close();
            }
        }
    
    }
    
    // Para enviar mensajes a un cliente
    private static void enviarACliente(String mensaje, InetAddress direccion, int puerto, DatagramSocket socket)
            throws IOException {
        byte[] datos = mensaje.getBytes();
        DatagramPacket paquete = new DatagramPacket(datos, datos.length, direccion, puerto);
        socket.send(paquete);
    }
    
    // Cuando haya que registrar un nuevo usuario
    private static void manejarNuevoUsuario(String mensaje, InetAddress direccion, int puerto, DatagramSocket socket)
            throws IOException {
        String[] partes = mensaje.split(":");
        String nombreUsuario = partes[1];
    
        if (usuarioExistente(nombreUsuario)) {
            Cliente cliente = new Cliente(nombreUsuario, direccion, puerto);
            clientes.add(cliente);
    
            // enviar mensaje de bienvenida a todos los clientes conectados
            String mensajeBienvenida = nombreUsuario + " se ha conectado al chat.";
            // mensaje de control
            System.out.println(mensajeBienvenida);
            for (Cliente c : clientes) {
                enviarACliente(mensajeBienvenida, c.obtenerDireccion(), c.obtenerPuerto(), socket);
            }
            // mensaje de control con la lista de usuarios
            System.out.print(crearListadoUsuarios());
        } else {
            // enviar mensaje al cliente de que el nombre de usuario ya existe
            String mensajeError = "NICKNAMEEXISTS";
            enviarACliente(mensajeError, direccion, puerto, socket);
        }
    
    }
    
    // Para comprobar si un usuario ya existe
    private static boolean usuarioExistente(String nombreUsuario) {
        for (Cliente c : clientes) {
            if (c.obtenerNombreUsuario().equals(nombreUsuario)) {
                return false;
            }
        }
        return true;
    }
    
    // Cuando haya que desconectar a un usuario
    private static void manejarDesconexion(String nombreUsuarioObjetivo, DatagramSocket socket) throws IOException {
    
        // buscamos al usuario que se quiere desconectar
        for (Cliente c : clientes) {
            if (c.obtenerNombreUsuario().equals(nombreUsuarioObjetivo)) {
    
                // Enviar comando de desconexion al usuario que se desconecta
                String mensajeDesconexion = "/dc";
                enviarACliente(mensajeDesconexion, c.obtenerDireccion(), c.obtenerPuerto(), socket);
    
                // Enviar mensaje de despedida a todos los clientes conectados
                String mensajeDespedida = nombreUsuarioObjetivo + " se ha desconectado del chat.";
                // mensaje de control
                System.out.println(mensajeDespedida);
                for (Cliente usuario : clientes) {
                    enviarACliente(mensajeDespedida, usuario.obtenerDireccion(), usuario.obtenerPuerto(), socket);
                }
    
                // Eliminar el cliente de la lista
                clientes.remove(c);
    
                break;
            }
        }
        // mensaje de control con la lista de usuarios
        System.out.print(crearListadoUsuarios());
    }
    
    public static String crearListadoUsuarios() {
        List<String> nombresUsuarios = new ArrayList<>();
        for (Cliente c : clientes) {
            nombresUsuarios.add(c.obtenerNombreUsuario());
        }
        nombresUsuarios.sort(new ordenarLista()); // se ordena la lista
        return "/getUsers " + String.join(",", nombresUsuarios);
    }

    private static class ordenarLista implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
    
            if (o1.startsWith("(ADMIN)") && !o2.startsWith("(ADMIN)")) {
                return -1;
            }
            if (!o1.startsWith("(ADMIN)") && o2.startsWith("(ADMIN)")) {
                return 1;
            }
            if (o1.startsWith("(ADMIN)") && o2.startsWith("(ADMIN)")) {
                return o1.compareTo(o2);
            }
            return o1.compareTo(o2);
        }
    }

    static class Cliente {
        private String nombreUsuario;
        private InetAddress direccion;
        private int puerto;

        public Cliente(String nombreUsuario, InetAddress direccion, int puerto) {
            this.nombreUsuario = nombreUsuario;
            this.direccion = direccion;
            this.puerto = puerto;
        }

        public String obtenerNombreUsuario() {
            return nombreUsuario;
        }

        public InetAddress obtenerDireccion() {
            return direccion;
        }

        public int obtenerPuerto() {
            return puerto;
        }

    }

}
