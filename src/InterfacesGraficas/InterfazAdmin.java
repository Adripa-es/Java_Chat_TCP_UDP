package Chat_Final.InterfacesGraficas;

import java.io.IOException;

import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import Chat_Final.Chat_TCP.ClienteTCP;
import Chat_Final.Chat_UDP.ClienteUDP;

public class InterfazAdmin extends javax.swing.JFrame {

        private javax.swing.JTable areaMensajesChat;
        private javax.swing.JButton botonDesconectar;
        private javax.swing.JButton botonEnviar;
        private javax.swing.JButton botonExpulsar;
        private javax.swing.JTextArea entradaTexto;
        private javax.swing.JPanel jPanel1;
        private javax.swing.JPanel jPanel2;
        private javax.swing.JPanel jPanel3;
        private javax.swing.JScrollPane jScrollPane_entradaTexto;
        private javax.swing.JScrollPane jScrollPane_areaTexto;
        private javax.swing.JScrollPane jScrollPane_listaUsuarios;
        public static javax.swing.JList<String> listaUsuarios;

        private String nickName = "";

        private int columna = 0, fila = 0;

        private boolean esTCP = false;
        private boolean esUDP = false;

        public InterfazAdmin(Object cualquierObjeto) {
                if (cualquierObjeto instanceof ClienteTCP) {
                        esTCP = true;
                } else if (cualquierObjeto instanceof ClienteUDP) {
                        esUDP = true;
                }

                initComponents();

                DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
                leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
                areaMensajesChat.getColumnModel().getColumn(0).setCellRenderer(leftRenderer);

                DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
                centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
                areaMensajesChat.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

                DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
                rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
                areaMensajesChat.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);

                setVisible(true);
        }

        public static void main(String args[]) {
                new InterfazAdmin(null);
        }

        private void initComponents() {

                jPanel1 = new javax.swing.JPanel();
                jPanel3 = new javax.swing.JPanel();
                botonEnviar = new javax.swing.JButton();
                botonDesconectar = new javax.swing.JButton();
                jScrollPane_areaTexto = new javax.swing.JScrollPane();
                areaMensajesChat = new javax.swing.JTable();
                jScrollPane_entradaTexto = new javax.swing.JScrollPane();
                entradaTexto = new javax.swing.JTextArea();
                jPanel2 = new javax.swing.JPanel();
                jScrollPane_listaUsuarios = new javax.swing.JScrollPane();
                listaUsuarios = new javax.swing.JList<>();
                botonExpulsar = new javax.swing.JButton();

                setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
                setTitle("Chat Administrador");
                setMaximumSize(new java.awt.Dimension(720, 360));
                setMinimumSize(new java.awt.Dimension(720, 360));
                setResizable(false);

                botonEnviar.setText("Enviar");
                botonEnviar.setEnabled(false);
                botonEnviar.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                botonEnviarActionPerformed(evt);
                        }
                });

                botonDesconectar.setText("Desconectar");
                botonDesconectar.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                botonDesconectarActionPerformed(evt);
                        }
                });

                jScrollPane_areaTexto.setAutoscrolls(true);

                areaMensajesChat.setModel(new javax.swing.table.DefaultTableModel(
                                new Object[][] {},
                                new String[] { "", "", "" }));
                areaMensajesChat.setFocusable(false);
                areaMensajesChat.setShowHorizontalLines(false);
                areaMensajesChat.setShowVerticalLines(false);
                areaMensajesChat.setTableHeader(null);
                areaMensajesChat.setDefaultEditor(Object.class, null);
                areaMensajesChat.setCellSelectionEnabled(false);
                jScrollPane_areaTexto.setViewportView(areaMensajesChat);

                entradaTexto.setColumns(20);
                entradaTexto.setLineWrap(true);
                entradaTexto.setRows(5);
                entradaTexto.setWrapStyleWord(true);
                jScrollPane_entradaTexto.setViewportView(entradaTexto);

                javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
                jPanel3.setLayout(jPanel3Layout);
                jPanel3Layout.setHorizontalGroup(
                                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(jScrollPane_areaTexto,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE, 709,
                                                                Short.MAX_VALUE)
                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout
                                                                .createSequentialGroup()
                                                                .addComponent(jScrollPane_entradaTexto)
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(botonEnviar,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                73,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addComponent(botonDesconectar, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
                jPanel3Layout.setVerticalGroup(
                                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout
                                                                .createSequentialGroup()
                                                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE)
                                                                .addComponent(jScrollPane_areaTexto,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                290,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(18, 18, 18)
                                                                .addGroup(jPanel3Layout
                                                                                .createParallelGroup(
                                                                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                                                                false)
                                                                                .addComponent(botonEnviar,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                38,
                                                                                                Short.MAX_VALUE)
                                                                                .addComponent(jScrollPane_entradaTexto,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                0, Short.MAX_VALUE))
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(botonDesconectar,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                23,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)));

                javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
                jPanel1.setLayout(jPanel1Layout);
                jPanel1Layout.setHorizontalGroup(
                                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout
                                                                .createSequentialGroup()
                                                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE)
                                                                .addComponent(jPanel3,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addContainerGap()));
                jPanel1Layout.setVerticalGroup(
                                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout
                                                                .createSequentialGroup()
                                                                .addGap(0, 0, Short.MAX_VALUE)
                                                                .addComponent(jPanel3,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)));

                jScrollPane_listaUsuarios.setViewportView(listaUsuarios);

                botonExpulsar.setText("Expulsar");
                botonExpulsar.setAlignmentX(0.5F);
                botonExpulsar.setEnabled(false);
                botonExpulsar.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                botonExpulsarActionPerformed(evt);
                        }
                });

                javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
                jPanel2.setLayout(jPanel2Layout);
                jPanel2Layout.setHorizontalGroup(
                                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(jPanel2Layout.createSequentialGroup()
                                                                .addContainerGap()
                                                                .addGroup(jPanel2Layout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                .addComponent(jScrollPane_listaUsuarios,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                0, Short.MAX_VALUE)
                                                                                .addComponent(botonExpulsar,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                117,
                                                                                                Short.MAX_VALUE))
                                                                .addContainerGap()));
                jPanel2Layout.setVerticalGroup(
                                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(jPanel2Layout.createSequentialGroup()
                                                                .addComponent(jScrollPane_listaUsuarios)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(botonExpulsar,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                23,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addContainerGap()));

                javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
                getContentPane().setLayout(layout);
                layout.setHorizontalGroup(
                                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(layout.createSequentialGroup()
                                                                .addContainerGap()
                                                                .addComponent(jPanel1,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(jPanel2,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE)));
                layout.setVerticalGroup(
                                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(layout.createSequentialGroup()
                                                                .addComponent(jPanel1,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE)
                                                                .addContainerGap())
                                                .addGroup(layout.createSequentialGroup()
                                                                .addContainerGap()
                                                                .addComponent(jPanel2,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE)));

                entradaTexto.getDocument().addDocumentListener(new DocumentListener() {
                        @Override
                        public void insertUpdate(DocumentEvent e) {
                                updateButtonState();
                        }

                        @Override
                        public void removeUpdate(DocumentEvent e) {
                                updateButtonState();
                        }

                        @Override
                        public void changedUpdate(DocumentEvent e) {
                                updateButtonState();
                        }

                        // el boton enviar no se activará hasta que no haya texto escrito, ignorando
                        // saltos y espacios al inicio y final
                        private void updateButtonState() {
                                botonEnviar.setEnabled(!entradaTexto.getText().replaceAll("^\\s*\n|\n\\s*$", "").trim()
                                                .isEmpty());
                        }
                });

                listaUsuarios.addListSelectionListener(new ListSelectionListener() {
                        @Override
                        public void valueChanged(ListSelectionEvent e) {
                                botonExpulsar.setEnabled(listaUsuarios.getSelectedIndex() != -1);
                        }
                });

                pack();
        }// </editor-fold>

        // por si acaso, se borrarán los espacios en blanco que tenga el nombre de
        // usuario
        public void setNickName(String nickName) {
                this.nickName = nickName;
        }

        public void actualizarCuadroMensajes(String mensaje) {
                // imprimimos el mensaje recibido en el chat
                String[] lines = mensaje.split("\r?\n");
                DefaultTableModel model = (DefaultTableModel) areaMensajesChat.getModel();

                // cuando el mensaje sea el propio, se escribe en la columna de la derecha
                if (mensaje.startsWith(nickName)) {
                        columna = 2;
                }
                // si es un mensaje del sistema, se escribirá en la columna central
                else if (mensaje.endsWith(" se ha desconectado del chat.")
                                || mensaje.endsWith(" se ha conectado al chat.")) {
                        columna = 1;
                }
                // si es un mensaje de cualquier otro usuario, se escribe a la izquierda
                else {
                        columna = 0;
                }

                // imprimir texto ajustandolo para que no sobresalga de la celda
                for (int i = 0; i < lines.length; i++) {
                        String line = lines[i];
                        while (line.length() > 30) {
                                int index = line.substring(0, 30).lastIndexOf(" ");
                                model.addRow(new Object[] { null, null, null });
                                areaMensajesChat.setValueAt(line.substring(0, index), fila, columna);
                                fila++;
                                line = line.substring(index + 1);
                        }
                        model.addRow(new Object[] { null, null, null });
                        areaMensajesChat.setValueAt(line, fila, columna);
                        fila++;
                }

        }

        private void botonDesconectarActionPerformed(java.awt.event.ActionEvent evt) {
                // aquí se maneja la desconexión del cliente
                if (esTCP && !esUDP) {
                        System.exit(0);
                } else if (!esTCP && esUDP) {
                        desconectarCerrar();
                }

        }

        private void botonEnviarActionPerformed(java.awt.event.ActionEvent evt) {
                // obtenemos el mensaje escrito, 
                //limpiandolo de huecos al inicio y final del texto
                String mensaje = entradaTexto.getText().replaceAll("^\\s*\n|\n\\s*$", "").trim();

                // recogemos el mensaje escrito, y lo mandamos al motor del cliente
                if (esTCP && !esUDP) {
                        ClienteTCP.getOut().println(mensaje);
                } else if (!esTCP && esUDP) {
                        try {
                                ClienteUDP.enviarMensaje(mensaje);
                        } catch (IOException | InterruptedException e) {
                                e.printStackTrace();
                        }
                }

                entradaTexto.setText("");
        }

        private void botonExpulsarActionPerformed(java.awt.event.ActionEvent evt) {
                int index = listaUsuarios.getSelectedIndex();
                if (index != -1) {
                        // se enviará el comando de desconectar al usuario elegido al servidor
                        if (esTCP && !esUDP) {
                                ClienteTCP.getOut().println("/dc -->" + listaUsuarios.getSelectedValue());
                        } else if (!esTCP && esUDP) {
                                try {
                                        ClienteUDP.enviarMensaje("/dc " + listaUsuarios.getSelectedValue());
                                } catch (IOException | InterruptedException e) {
                                        e.printStackTrace();
                                }
                        }
                }
        }

        private void desconectarCerrar() {
                try {
                        ClienteUDP.enviarMensaje("/dc " + nickName);
                } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                }
                // aquí se maneja la desconexión del cliente
                System.exit(0);
        }

}
