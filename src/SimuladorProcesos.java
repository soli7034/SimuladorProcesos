import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.data.time.DynamicTimeSeriesCollection;
import org.jfree.data.time.Second;

public class SimuladorProcesos extends JFrame {
    private static final int RAM_TOTAL = 1024; // 1 GB en MB
    private int ramDisponible;
    private final List<Proceso> procesosEnEjecucion = new ArrayList<>();
    private final List<Proceso> colaEspera = new ArrayList<>();
    private final List<Proceso> historialProcesos = new ArrayList<>();
    private final ExecutorService ejecutor = Executors.newSingleThreadExecutor();
    private JTextArea areaEstado, areaHistorial;
    private JTextField campoNombre, campoRAM, campoDuracion;
    private JPanel graficoPanel;
    private JComboBox<Proceso> comboEjecucion, comboEspera;
    private static int siguientePID = 1; // Contador para generar PIDs automáticamente

    // Variables para el gráfico de JFreeChart
    private DynamicTimeSeriesCollection dataset;
    private JFreeChart chart;

    public SimuladorProcesos() {
        ramDisponible = RAM_TOTAL;
        inicializarInterfaz();
        actualizarEstado();
        iniciarGraficoMemoria(); // Inicializa el gráfico al arrancar
    }

    private void inicializarInterfaz() {
        setTitle("Simulador de Procesos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setSize(1000, 700); // Tamaño para mejor visualización

        // Cambiar fondo general
        getContentPane().setBackground(new Color(230, 240, 255));

        // Panel de entrada
        JPanel panelEntrada = new JPanel(new GridLayout(4, 2, 5, 5));
        panelEntrada.setBorder(BorderFactory.createTitledBorder("Agregar Proceso"));
        panelEntrada.setBackground(new Color(200, 220, 255));

        panelEntrada.add(new JLabel("Nombre:"));
        campoNombre = new JTextField(10);
        panelEntrada.add(campoNombre);

        panelEntrada.add(new JLabel("RAM (MB):"));
        campoRAM = new JTextField(5);
        panelEntrada.add(campoRAM);

        panelEntrada.add(new JLabel("Duración (s):"));
        campoDuracion = new JTextField(5);
        panelEntrada.add(campoDuracion);

        JButton botonAgregar = new JButton("Agregar Proceso");
        botonAgregar.setBackground(new Color(100, 150, 255));
        botonAgregar.setForeground(Color.WHITE);
        botonAgregar.setFocusPainted(false);
        botonAgregar.setFont(new Font("Arial", Font.BOLD, 14));
        botonAgregar.addActionListener(e -> agregarProceso());
        panelEntrada.add(new JLabel());
        panelEntrada.add(botonAgregar);

        // Panel de controles
        JPanel panelControles = new JPanel(new FlowLayout());
        panelControles.setBackground(new Color(210, 230, 255));
        JButton botonDetener = new JButton("Detener Seleccionado");
        botonDetener.setBackground(new Color(255, 100, 100));
        botonDetener.setForeground(Color.WHITE);
        botonDetener.setFocusPainted(false);
        botonDetener.setFont(new Font("Arial", Font.BOLD, 14));
        panelControles.add(botonDetener);

        // Panel de selección de procesos
        JPanel panelSeleccion = new JPanel(new GridLayout(2, 2, 10, 10));
        panelSeleccion.setBorder(BorderFactory.createTitledBorder("Selección de Procesos"));
        panelSeleccion.setBackground(new Color(200, 220, 255));
        panelSeleccion.add(new JLabel("Procesos en Ejecución:"));
        comboEjecucion = new JComboBox<>();
        comboEjecucion.setPreferredSize(new Dimension(200, 25));
        panelSeleccion.add(comboEjecucion);
        panelSeleccion.add(new JLabel("Cola de Espera:"));
        comboEspera = new JComboBox<>();
        comboEspera.setPreferredSize(new Dimension(200, 25));
        panelSeleccion.add(comboEspera);

        // Panel de estado y historial
        JPanel panelInfo = new JPanel(new GridLayout(1, 2, 10, 10));
        panelInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelInfo.setBackground(new Color(230, 240, 255));

        // Estado
        JPanel panelEstado = new JPanel(new BorderLayout());
        panelEstado.setBorder(BorderFactory.createTitledBorder("Estado Actual"));
        panelEstado.setBackground(new Color(220, 235, 255));
        areaEstado = new JTextArea(10, 30);
        areaEstado.setEditable(false);
        areaEstado.setBackground(new Color(245, 250, 255));
        areaEstado.setFont(new Font("Consolas", Font.PLAIN, 13));
        areaEstado.setForeground(new Color(0, 60, 120));
        panelEstado.add(new JScrollPane(areaEstado), BorderLayout.CENTER);

        // Historial
        JPanel panelHistorial = new JPanel(new BorderLayout());
        panelHistorial.setBorder(BorderFactory.createTitledBorder("Historial de Procesos"));
        panelHistorial.setBackground(new Color(220, 235, 255));
        areaHistorial = new JTextArea(10, 30);
        areaHistorial.setEditable(false);
        areaHistorial.setBackground(new Color(245, 250, 255));
        areaHistorial.setFont(new Font("Consolas", Font.PLAIN, 13));
        areaHistorial.setForeground(new Color(0, 60, 120));
        panelHistorial.add(new JScrollPane(areaHistorial), BorderLayout.CENTER);

        panelInfo.add(panelEstado);
        panelInfo.add(panelHistorial);

        // Panel gráfico
        graficoPanel = new JPanel(new BorderLayout());
        graficoPanel.setPreferredSize(new Dimension(400, 300));
        graficoPanel.setBackground(new Color(230, 240, 255));

        // Organizar layout
        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.add(panelControles, BorderLayout.NORTH);
        panelCentro.add(panelSeleccion, BorderLayout.CENTER);
        panelCentro.add(panelInfo, BorderLayout.SOUTH);
        panelCentro.setBackground(new Color(230, 240, 255));

        add(panelEntrada, BorderLayout.NORTH);
        add(panelCentro, BorderLayout.CENTER);
        add(graficoPanel, BorderLayout.EAST);

        // Lógica de botones
        botonDetener.addActionListener(e -> detenerProcesoSeleccionado());
    }

    private void agregarProceso() {
        try {
            String nombre = campoNombre.getText();
            int ram = Integer.parseInt(campoRAM.getText());
            int duracion = Integer.parseInt(campoDuracion.getText());

            int pid = siguientePID++;
            Proceso proceso = new Proceso(pid, nombre, ram, duracion);
            historialProcesos.add(proceso);
            if (ram > ramDisponible) {
                colaEspera.add(proceso);
                JOptionPane.showMessageDialog(this, "Proceso (No: " + pid + ") agregado a la cola de espera por falta de RAM.");
            } else {
                procesosEnEjecucion.add(proceso);
                ramDisponible -= ram;
                proceso.setEstado(Proceso.Estado.EJECUTANDO);
                ejecutor.submit(() -> this.ejecutarProceso(proceso));
            }

            limpiarCampos();
            actualizarEstado();
            verificarColaEspera();
            actualizarGraficoMemoria();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese valores numéricos válidos para RAM y Duración.");
        }
    }

    private void ejecutarProceso(Proceso proceso) {
        while (proceso.getEstado() == Proceso.Estado.EJECUTANDO) {
            try {
                Thread.sleep(1000);
                if (proceso.getDuracionRestante() <= 0) {
                    synchronized (this) {
                        procesosEnEjecucion.remove(proceso);
                        ramDisponible += proceso.ram;
                        proceso.setEstado(Proceso.Estado.TERMINADO);
                        actualizarEstado();
                        verificarColaEspera();
                    }
                    break;
                }
                proceso.decrementarDuracion();
                actualizarGraficoMemoria();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void verificarColaEspera() {
        
        
        for (int i = 0; i < colaEspera.size(); i++) {
            Proceso proceso = colaEspera.get(i);
            if (proceso.ram <= ramDisponible) {
                colaEspera.remove(proceso);
                procesosEnEjecucion.add(proceso);
                ramDisponible -= proceso.ram;
                proceso.setEstado(Proceso.Estado.EJECUTANDO);
                ejecutor.submit(() -> this.ejecutarProceso(proceso));
                i--;
            }
        }
        actualizarEstado();
    }

    private void actualizarEstado() {
        areaEstado.setText("RAM Disponible: " + ramDisponible + " MB\n\nProcesos en Ejecución:\n");
        comboEjecucion.removeAllItems();
        for (Proceso p : procesosEnEjecucion) {
            areaEstado.append(p.toString() + "\n");
            comboEjecucion.addItem(p);
        }
        areaEstado.append("\nCola de Espera:\n");
        comboEspera.removeAllItems();
        for (Proceso p : colaEspera) {
            areaEstado.append(p.toString() + "\n");
            comboEspera.addItem(p);
        }

        areaHistorial.setText("Historial de Procesos:\n");
        for (Proceso p : historialProcesos) {
            areaHistorial.append(p.toString() + "\n");
        }
    }

    private void limpiarCampos() {
        campoNombre.setText("");
        campoRAM.setText("");
        campoDuracion.setText("");
    }

    private void detenerProcesoSeleccionado() {
        Proceso proceso = (Proceso) comboEjecucion.getSelectedItem();
        if (proceso != null) {
            synchronized (this) {
                procesosEnEjecucion.remove(proceso);
                ramDisponible += proceso.ram;
                proceso.setEstado(Proceso.Estado.TERMINADO);
                actualizarEstado();
                verificarColaEspera();
                actualizarGraficoMemoria();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un proceso para detener.");
        }
    }

    private void iniciarGraficoMemoria() {
        dataset = new DynamicTimeSeriesCollection(1, 60, new Second());
        dataset.setTimeBase(new Second());
        dataset.addSeries(new float[]{RAM_TOTAL - ramDisponible}, 0, "Memoria Usada");

        chart = ChartFactory.createTimeSeriesChart(
            "Uso de Memoria", "Tiempo", "Memoria (MB)", dataset, true, true, false);

        ValueAxis yAxis = chart.getXYPlot().getRangeAxis();
        yAxis.setRange(0, RAM_TOTAL);

        ChartPanel chartPanel = new ChartPanel(chart);
        graficoPanel.add(chartPanel, BorderLayout.CENTER);
        graficoPanel.revalidate();
        graficoPanel.repaint();
    }

    private void actualizarGraficoMemoria() {
        SwingUtilities.invokeLater(() -> {
            dataset.advanceTime();
            dataset.appendData(new float[]{RAM_TOTAL - ramDisponible});
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SimuladorProcesos simulador = new SimuladorProcesos();
            simulador.setVisible(true);
        });
    }
}
