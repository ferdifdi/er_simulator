package com.ersim.gui;

import com.ersim.service.TriageService;

import javax.swing.*;
import java.awt.*;

/**
 * Live dashboard window for monitoring the ER simulator. Polls TriageService
 * every 500 ms via javax.swing.Timer and refreshes the queue table and
 * room panel.
 *
 * Optional monitoring layer — does not appear in the backend class diagram.
 *
 * TODO #Sruthi: full Swing implementation: JTable for queue, JPanel of
 *               JLabels for rooms, manual entry form for new patients,
 *               color-coded rows by ESI level, live wait-time counter.
 */
public class DashboardFrame extends JFrame {

    private final TriageService service;
    private JTable queueTable;
    private JPanel roomPanel;
    private Timer refreshTimer;

    public DashboardFrame(TriageService service) {
        super("ER Triage Simulator – Live Dashboard");
        this.service = service;
        // TODO #Sruthi: setSize, setDefaultCloseOperation, build layout
    }

    /** Build queue JTable with columns: ID, Name, ESI, Wait. */
    private void buildQueueTable() {
        // TODO #Sruthi: create JTable with custom TableModel that pulls
        //               from service.getQueueStatus()
    }

    /** Build the room status panel with one JLabel per room. */
    private void buildRoomPanel() {
        // TODO #Sruthi: create JPanel(new GridLayout(...)), one label per room
    }

    /** Build the form (name + ESI level + Add button). */
    private void buildAdmitForm() {
        // TODO #Sruthi: text field + JComboBox<TriageLevel> + JButton wired to
        //               service.admitPatient(...)
    }

    /** Color rows by triage level. */
    private Color colorForEsi(int esiOrdinal) {
        // TODO #Sruthi: ESI 1 = red, 2 = orange, 3 = yellow, 4/5 = green
        return Color.WHITE;
    }

    /** Start the periodic refresh timer (500 ms). */
    public void startRefresh() {
        // TODO #Sruthi: refreshTimer = new Timer(500, e -> refresh()); refreshTimer.start();
    }

    /** Pull latest snapshots and update components. */
    private void refresh() {
        // TODO #Sruthi: re-render queue table and room panel from service snapshots
    }
}
