package com.ersim.gui;

import com.ersim.concurrent.TreatmentRoom.RoomStatusSnapshot;
import com.ersim.model.Patient;
import com.ersim.model.enums.TriageLevel;
import com.ersim.service.TriageService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Live dashboard window for monitoring the ER simulator.
 *
 * Tab 1 — Live Dashboard: queue table + room status panel + admit form.
 * Tab 2 — Report: real-time statistics pulled from TriageService.getReport().
 *
 * Both tabs refresh every 500 ms via javax.swing.Timer.
 */
public class DashboardFrame extends JFrame {

    private final TriageService service;

    // Live Dashboard tab
    private DefaultTableModel queueTableModel;
    private JLabel[] roomLabels;

    // Report tab
    private JLabel lblQueueSize, lblTotalRooms, lblOccupied,
                   lblAdmitted,  lblDischarged,  lblUpgrades, lblTotalEvents;

    private Timer refreshTimer;

    public DashboardFrame(TriageService service) {
        super("ER Triage Simulator – Live Dashboard");
        this.service = service;

        setSize(1000, 640);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Live Dashboard", buildDashboardTab());
        tabs.addTab("Report",         buildReportTab());

        add(tabs, BorderLayout.CENTER);
        add(buildAdmitForm(), BorderLayout.SOUTH);

        setVisible(true);
    }

    // ── Tab 1: Live Dashboard ──────────────────────────────────────────

    private JPanel buildDashboardTab() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Queue table
        queueTableModel = new DefaultTableModel(new String[]{"ID", "Name", "ESI Level", "Wait (s)"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable queueTable = new JTable(queueTableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) {
                    Object esi = queueTableModel.getValueAt(row, 2);
                    c.setBackground(esi != null ? colorForEsi(esi.toString()) : Color.WHITE);
                }
                return c;
            }
        };
        panel.add(new JScrollPane(queueTable));

        // Room status panel
        int roomCount = Math.max(service.getRoomStatus().size(), 4);
        JPanel roomPanel = new JPanel(new GridLayout(roomCount, 1, 0, 4));
        roomPanel.setBorder(BorderFactory.createTitledBorder("Treatment Rooms"));
        roomLabels = new JLabel[roomCount];
        for (int i = 0; i < roomCount; i++) {
            roomLabels[i] = new JLabel(" ");
            roomLabels[i].setOpaque(true);
            roomLabels[i].setBackground(new Color(240, 240, 240));
            roomLabels[i].setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
            roomPanel.add(roomLabels[i]);
        }
        panel.add(new JScrollPane(roomPanel));
        return panel;
    }

    // ── Tab 2: Report ─────────────────────────────────────────────────

    private JPanel buildReportTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(24, 40, 24, 40));
        GridBagConstraints gc = new GridBagConstraints();
        gc.anchor = GridBagConstraints.WEST;
        gc.insets = new Insets(6, 8, 6, 8);

        String[] labels = {
            "Patients in queue:", "Total rooms:", "Occupied rooms:",
            "Total admitted:", "Total discharged:", "Triage upgrades:", "Total events logged:"
        };
        lblQueueSize   = new JLabel("—");
        lblTotalRooms  = new JLabel("—");
        lblOccupied    = new JLabel("—");
        lblAdmitted    = new JLabel("—");
        lblDischarged  = new JLabel("—");
        lblUpgrades    = new JLabel("—");
        lblTotalEvents = new JLabel("—");
        JLabel[] values = {lblQueueSize, lblTotalRooms, lblOccupied,
                           lblAdmitted,  lblDischarged, lblUpgrades, lblTotalEvents};

        Font valFont = new Font(Font.MONOSPACED, Font.BOLD, 16);
        for (int i = 0; i < labels.length; i++) {
            gc.gridx = 0; gc.gridy = i;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
            lbl.setForeground(new Color(80, 80, 80));
            panel.add(lbl, gc);

            gc.gridx = 1;
            values[i].setFont(valFont);
            panel.add(values[i], gc);
        }

        JLabel hint = new JLabel("Auto-refreshes every 500 ms");
        hint.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 11));
        hint.setForeground(Color.GRAY);
        gc.gridx = 0; gc.gridy = labels.length; gc.gridwidth = 2;
        gc.insets = new Insets(20, 8, 0, 8);
        panel.add(hint, gc);

        return panel;
    }

    // ── Admit form ─────────────────────────────────────────────────────

    private JPanel buildAdmitForm() {
        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        formPanel.setBorder(BorderFactory.createTitledBorder("Admit Patient"));

        JTextField nameField = new JTextField(15);
        JSpinner   ageSpinner = new JSpinner(new SpinnerNumberModel(30, 0, 130, 1));
        JComboBox<TriageLevel> levelCombo = new JComboBox<>(TriageLevel.values());
        JButton addButton = new JButton("Admit");

        addButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (!name.isEmpty()) {
                int age = (Integer) ageSpinner.getValue();
                Patient p = new Patient(
                    java.util.UUID.randomUUID().toString(),
                    name, age,
                    (TriageLevel) levelCombo.getSelectedItem()
                );
                service.admitPatient(p);
                nameField.setText("");
                ageSpinner.setValue(30);
                refresh();
            }
        });

        formPanel.add(new JLabel("Name:"));   formPanel.add(nameField);
        formPanel.add(new JLabel("Age:"));    formPanel.add(ageSpinner);
        formPanel.add(new JLabel("ESI:"));    formPanel.add(levelCombo);
        formPanel.add(addButton);
        return formPanel;
    }

    // ── Refresh ────────────────────────────────────────────────────────

    /** Start the 500 ms auto-refresh timer. Called by ErSimulatorApplication. */
    public void startRefresh() {
        refreshTimer = new Timer(500, e -> refresh());
        refreshTimer.start();
    }

    private void refresh() {
        refreshQueue();
        refreshRooms();
        refreshReport();
    }

    private void refreshQueue() {
        queueTableModel.setRowCount(0);
        for (Patient p : service.getQueueStatus()) {
            long wait = java.time.Duration.between(p.getArrivalTime(), LocalDateTime.now()).getSeconds();
            queueTableModel.addRow(new Object[]{
                p.getPatientId().substring(0, 8),
                p.getName(),
                p.getTriageLevel(),
                wait
            });
        }
    }

    private void refreshRooms() {
        List<RoomStatusSnapshot> rooms = service.getRoomStatus();
        for (int i = 0; i < rooms.size() && i < roomLabels.length; i++) {
            RoomStatusSnapshot snap = rooms.get(i);
            String patient = snap.currentPatientName != null ? snap.currentPatientName : "Available";
            roomLabels[i].setText(String.format(
                "<html><b>%s</b> &nbsp; %s &nbsp; <i>%s</i></html>",
                snap.roomId, snap.status, patient));
            roomLabels[i].setBackground(colorForRoomStatus(snap.status.name()));
        }
    }

    @SuppressWarnings("unchecked")
    private void refreshReport() {
        try {
            Map<String, Object> report = (Map<String, Object>) service.getReport();
            lblQueueSize  .setText(String.valueOf(report.getOrDefault("queueSize",   "—")));
            lblTotalRooms .setText(String.valueOf(report.getOrDefault("totalRooms",  "—")));
            lblOccupied   .setText(String.valueOf(report.getOrDefault("occupiedRooms","—")));
            lblAdmitted   .setText(String.valueOf(report.getOrDefault("admitted",    "—")));
            lblDischarged .setText(String.valueOf(report.getOrDefault("discharged",  "—")));
            lblUpgrades   .setText(String.valueOf(report.getOrDefault("upgrades",    "—")));
            lblTotalEvents.setText(String.valueOf(report.getOrDefault("totalEvents", "—")));
        } catch (Exception ignored) {}
    }

    // ── Helpers ────────────────────────────────────────────────────────

    private Color colorForEsi(String esiName) {
        if (esiName.contains("1")) return new Color(255, 180, 180);
        if (esiName.contains("2")) return new Color(255, 218, 160);
        if (esiName.contains("3")) return new Color(255, 255, 180);
        return new Color(200, 240, 200);
    }

    private Color colorForRoomStatus(String status) {
        return switch (status) {
            case "OCCUPIED"  -> new Color(210, 230, 255);
            case "CLEANING"  -> new Color(230, 230, 230);
            default          -> new Color(210, 245, 215);
        };
    }
}
