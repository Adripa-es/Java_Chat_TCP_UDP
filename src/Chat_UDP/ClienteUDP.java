package Chat_Final.Chat_UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import Chat_Final.InterfacesGraficas.InterfazAdmin;
import Chat_Final.InterfacesGraficas.InterfazClientes;


public class ClienteUDP {
    private static final int PUERTO_SERVIDOR = 12345;
    private static boolean conectado = true;
    private static InetAddress direccionServidor;
    private static String nombreUsuario;
    private static DatagramSocket socket;

    private static InterfazAdmin ChatAdmin = null;
    private static InterfazClientes ChatCliente = null;

    // Variable que determinará si es administrador o no, de cara a funcionalidades acordes a su interfaz gráfica
    private static boolean esAdmin = false;

    public static void main(String[] args) {
        // Comprobar si se ejecuta desde el Cliente o el Admin
        try {
            esAdmin = Boolean.parseBoolean(args[0]);
            System.out.println("Administrador ejecutandose...");
        } catch (Exception e) {
            System.out.println("Cliente ejecutandose...");
        }

        new ClienteUDP();
    }



    public ClienteUDP() {
        Scanner scanner = new Scanner(System.in);

        try {
            // Crear el socket y obtener la dirección del servidor
            socket = new DatagramSocket();
            direccionServidor = InetAddress.getLocalHost();

            // Obtener el nombre de usuario
            nombreUsuario = obtenerNombreUsuario(scanner);

            // Crear la interfaz de usuario
            if (esAdmin) {
                ChatAdmin = new InterfazAdmin(this);
                ChatAdmin.setNickName(nombreUsuario);
            } else {
                ChatCliente = new InterfazClientes(this);
                ChatCliente.setNickName(nombreUsuario);
            }

            // Iniciar el hilo que recibe mensajes
            Thread hiloReceptor = new Thread(() -> {
                try {
                    if (esAdmin) {
                        enviarMensaje("/getUsers");
                    }
                    recibirMensajes();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            });
            hiloReceptor.start();

        } catch (IOException | InterruptedException e) {
            System.out.println("Error: " + e);
        }
    }

    // Recibe mensajes del servidor y los procesa.
    private static void recibirMensajes() throws IOException, InterruptedException {
        while (conectado) {
            byte[] buffer = new byte[1024];
            DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);

            try {
                socket.receive(paquete);
                String mensaje = new String(paquete.getData(), 0, paquete.getLength());

                if (mensaje.equals("/dc")) {
                    // Se ha cerrado la conexión
                    conectado = false;
                    socket.close();
                    esperar(3000, "DESCONECTADO DEL CHAT");
                    mostrarMensajeControl("Conexión cerrada. Cerrando el programa..."); // Aviso al usuario
                    System.exit(0); // Cierra el programa
                } else if (mensaje.startsWith("/getUsers") && esAdmin) {
                    // Se ha actualizado la lista de usuarios
                    if (!mensaje.equals("/getUsers")) { // si es solo "/getUsers", no se muestra
                        actualizarListaUsuarios(mensaje);
                    }
                }else {
                    // Se ha recibido un mensaje de chat
                    if ((mensaje.endsWith(" se ha desconectado del chat.") || (mensaje.endsWith(" se ha conectado al chat."))) && esAdmin) {
                        // Si un usuario se ha conectado o desconectado, actualiza la lista de usuarios
                        enviarMensaje("/getUsers");
                    }
                
                    if (esAdmin) {
                        ChatAdmin.actualizarCuadroMensajes(mensaje);
                    } else {
                        ChatCliente.actualizarCuadroMensajes(mensaje);
                    }
                }
                
            } catch (IOException e) {
                // Se ha perdido la conexión
                e.printStackTrace();
                conectado = false;
                socket.close();
                mostrarMensajeControl("Conexión perdida. Cerrando el programa..."); // Aviso al usuario
                System.exit(0); // Cierra el programa
            }
        }
    }

    // Envía un mensaje al servidor.
    public static void enviarMensaje(String mensaje) throws IOException, InterruptedException {
        try {
            byte[] datos = (nombreUsuario + ": " + mensaje).getBytes();
            DatagramPacket paquete = new DatagramPacket(datos, datos.length, direccionServidor, PUERTO_SERVIDOR);
            socket.send(paquete);
        } catch (IOException e) {
            // Se ha perdido la conexión
            conectado = false;
            socket.close();
            mostrarMensajeControl("Conexión perdida. Cerrando el programa..."); // Aviso al usuario
            System.exit(0); // Cierra el programa
        }
    }

    // Obtiene el nombre de usuario del cliente/admin y lo envía al servidor para
    // comprobar si está disponible.
    private static String obtenerNombreUsuario(Scanner scanner) throws IOException, InterruptedException {
        String nombreUsuario = null;
        do {
            nombreUsuario = JOptionPane.showInputDialog("Introduce tu nickname: ");
    
            if (esAdmin) {
                nombreUsuario = "(ADMIN) " + nombreUsuario;
            }
    
            // Validar que el nombre de usuario no esté en blanco o tenga solo espacios en blanco
            if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "El nombre de usuario no puede estar en blanco.", "Error", JOptionPane.ERROR_MESSAGE);
                nombreUsuario = null;
                continue;
            }
    
            // Enviar nombre de usuario al servidor
            String mensajeNuevoUsuario = "newUser:" + nombreUsuario;
            byte[] datos = mensajeNuevoUsuario.getBytes();
            DatagramPacket paquete = new DatagramPacket(datos, datos.length, direccionServidor, PUERTO_SERVIDOR);
            socket.send(paquete);
    
            // Esperar respuesta del servidor
            byte[] buffer = new byte[1024];
            paquete = new DatagramPacket(buffer, buffer.length);
            socket.receive(paquete);
    
            String mensaje = new String(paquete.getData(), 0, paquete.getLength());
            mostrarMensajeControl(mensaje);
            if (mensaje.equals("NICKNAMEEXISTS")) {
                // El nombre de usuario ya está en uso, pedir otro nombre de usuario
                nombreUsuario = null;
            } else if (mensaje.equals("CHATFULL")) {
                // El chat está lleno, esperar 10 segundos
                esperar(10000, "El chat está lleno. Esperando 10 segundos...");
                nombreUsuario = null;
            }
    
        } while (nombreUsuario == null);
        return nombreUsuario;
    }
    
    // Actualiza la lista de usuarios en la interfaz de admin.
    public static void actualizarListaUsuarios(String lista) throws IOException {
        String[] partes = lista.split("/getUsers ");
        String[] listadoUsuarios = partes[1].split(",");

        ArrayList<String> listaUsuariosConectados = new ArrayList<>(Arrays.asList(listadoUsuarios));

        // Actualizar la lista en la interfaz
        DefaultListModel<String> modelo = new DefaultListModel<>();
        for (String usuario : listaUsuariosConectados) {
            modelo.addElement(usuario);
        }
        InterfazAdmin.listaUsuarios.setModel(modelo);
    }

    // Muestra un mensaje de control en la consola.
    private static void mostrarMensajeControl(String mensaje) {
        System.out.println("[CONTROL] " + mensaje);
    }

    // Espera un número de milisegundos y muestra un mensaje de espera en la
    // interfaz.
    private static void esperar(int milisegundos, String mensaje) throws InterruptedException {
        JOptionPane optionPane = new JOptionPane(mensaje, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION,
                null, new Object[] {}, null);
        JDialog dialog = optionPane.createDialog("Esperando...");
        dialog.setModal(false);
        dialog.setVisible(true);
        Thread.sleep(milisegundos);
        dialog.setVisible(false);
    }
}


