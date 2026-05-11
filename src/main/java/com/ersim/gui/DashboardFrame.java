package com.ersim.gui;

import com.ersim.model.Patient;
import com.ersim.model.enums.TriageLevel;
import com.ersim.service.TriageService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Live dashboard window for monitoring the ER simulator. Polls TriageService
 * every 500 ms via javax.swing.Timer and refreshes the queue table and
 * room panel.
 *
 * Optional monitoring layer — does not appear in the backend class diagram.
 */
public class DashboardFrame extends JFrame {

    private final TriageService service;
    private JTable queueTable;
    private JPanel roomPanel;
    private Timer refreshTimer;
    private DefaultTableModel tableModel;
    private JLabel[] roomLabels;

    public DashboardFrame(TriageService service) {
        super("ER Triage Simulator – Live Dashboard");
        this.service = service;
        
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        buildQueueTable();
        buildRoomPanel();
        buildAdmitForm();
        
        add(new JScrollPane(queueTable), BorderLayout.CENTER);
        add(new JScrollPane(roomPanel), BorderLayout.EAST);
        
        setVisible(true);
    }

    /** Build queue JTable with columns: ID, Name, ESI, Wait. */
    private void buildQueueTable() {
        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "ESI Level", "Wait (s)"}, 0);
        queueTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                Object esiObj = tableModel.getValueAt(row, 2);
                if (esiObj != null) {
                    String esiStr = esiObj.toString();
                    int esiOrdinal = Integer.parseInt(esiStr.split("_")[1]) - 1;
                    c.setBackground(colorForEsi(esiOrdinal));
                }
                return c;
            }
        };
    }

    /** Build the room status panel with one JLabel per room. */
    private void buildRoomPanel() {
        int roomCount = service.getRoomStatus().size();
        roomPanel = new JPanel(new GridLayout(roomCount, 1));
        roomLabels = new JLabel[roomCount];
        for (int i = 0; i < roomCount; i++) {
            roomLabels[i] = new JLabel();
            roomPanel.add(roomLabels[i]);
        }
    }

    /** Build the form (name + ESI level + Add button). */
    private void buildAdmitForm() {
        JPanel formPanel = new JPanel(new FlowLayout());
        
        JTextField nameField = new JTextField(15);
        JComboBox<TriageLevel> levelCombo = new JComboBox<>(TriageLevel.values());
        JButton addButton = new JButton("Add Patient");
        
        addButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (!name.isEmpty()) {
                Patient p = new Patient(
                    java.util.UUID.randomUUID().toString(),
                    name,
                    30,
                    (TriageLevel) levelCombo.getSelectedItem()
                );
                service.admitPatient(p);
                nameField.setText("");
                refresh();
            }
        });
        
        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("ESI Level:"));
        formPanel.add(levelCombo);
        formPanel.add(addButton);
        
        add(formPanel, BorderLayout.SOUTH);
    }

    /** Color rows by triage level. */
    private Color colorForEsi(int esiOrdinal) {
        return switch (esiOrdinal) {
            case 0 -> Color.RED;
            case 1 -> new Color(255, 165, 0); // ORANGE
            case 2 -> Color.YELLOW;
            default -> Color.GREEN;
        };
    }

    /** Start the periodic refresh timer (500 ms). */
    public void startRefresh() {
        refreshTimer = new Timer(500, e -> refresh());
        refreshTimer.start();
    }

    /** Pull latest snapshots and update components. */
    private void refresh() {
        // Update queue table
        tableModel.setRowCount(0);
        List<Patient> queue = service.getQueueStatus();
        for (Patient p : queue) {
            long waitSeconds = java.time.Duration.between(p.getArrivalTime(), LocalDateTime.now()).getSeconds();
            tableModel.addRow(new Object[]{
                p.getPatientId().substring(0, 8),
                p.getName(),
                p.getTriageLevel(),
                waitSeconds
            });
        }
        
        // Update room panel
        List<com.ersim.concurrent.TreatmentRoom.RoomStatusSnapshot> rooms = service.getRoomStatus();
        for (int i = 0; i < rooms.size() && i < roomLabels.length; i++) {
            var snap = rooms.get(i);
            String text = String.format("<html><b>%s</b><br>%s<br>%s</html>",
                snap.roomId,
                snap.status,
                snap.currentPatientName != null ? snap.currentPatientName : "—");
            roomLabels[i].setText(text);
            roomLabels[i].setBorder(BorderFactory.createLineBorder(Color.BLACK));
        }
    }
}
