package ru.yuriyyurinskiy.perceptron.Component;

import ru.yuriyyurinskiy.perceptron.Entity.Line;
import ru.yuriyyurinskiy.perceptron.Entity.Point;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public abstract class DrawingJFrame extends JFrame {
    private final int width = 800;
    private final int height = 700;

    protected List<Point> points = new ArrayList<>();

    private JPanel mainPanel = new JPanel();
    private JPanel southPanel = new JPanel();
    protected JPanel westPanel = new JPanel();
    protected DrawPanel drawPanel = new DrawPanel();

    private static JScrollPane pane;
    private static JTextArea logging = new JTextArea();
    protected JTextArea inputData = new JTextArea();

    protected JTextField inputC = new JTextField("1");

    protected JButton btnStart = new JButton("Начать построение"),
            btnStartToEnd = new JButton("Построить"),
            btnDrawPoint = new JButton("Построить точки"),
            btnClear = new JButton("Очистить всё"),
            btnClearDraw = new JButton("Очистить построения"),
            btnClearLog = new JButton("Отчистить лог"),
            btnSaveData = new JButton("Сохранить данные"),
            btnLoadData = new JButton("Загрузить данные");

    protected int step = 1;

    protected abstract Thread threadCalculation();

    protected abstract ActionListener btnStartListener();

    protected abstract ActionListener btnStartToEndListener();

    protected ActionListener btnDrawPointListener() {
        return e -> {
            if (inputData.getText().isEmpty()) {
                logLn("Ничего не введено");
                return;
            }

            // TO DO
            int number_line = 1;
            try {
                for (String line : inputData.getText().split("\\n")) {
                    String[] array = line.trim().split("\\s+");
                    double x = Double.valueOf(array[0]);
                    double y = Double.valueOf(array[1]);
                    int type = Integer.valueOf(array[2]) % 3;

                    points.add(new Point(x, y, type));

                    number_line++;
                }

                drawPanel.addAllPoints(points);
                logLn("Точки построены");

                Thread thread = threadCalculation();
                if (thread != null)
                    thread.start();

                btnDrawPoint.setEnabled(false);

                btnStart.setEnabled(true);
                btnClearDraw.setEnabled(true);
                btnStartToEnd.setEnabled(true);
            } catch (Exception ex) {
                logLn("Ошибка чтения данных в строке " + number_line);
            }
        };
    }

    public DrawingJFrame(String string) {
        super(string);

        initVerticalSplit();
        initLogging();
        initMainLayout();
        initButton();

        inputData.setText("0 0 0\n1 1 1\n-1 1 2");

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(width, height);
        setVisible(true);
    }

    private void initButton() {
        btnStart.setEnabled(false);
        btnClear.setEnabled(false);
        btnClearDraw.setEnabled(false);
        btnStartToEnd.setEnabled(false);

        btnDrawPoint.addActionListener(btnDrawPointListener());

        btnStart.addActionListener(btnStartListener());

        btnStartToEnd.addActionListener(btnStartToEndListener());

        btnClearDraw.addActionListener(e -> {
            btnClearDraw.setEnabled(false);
            btnStart.setText("Начать построение");
            btnStart.setEnabled(false);
            btnStartToEnd.setEnabled(false);

            // TO DO
            points.clear();
            drawPanel.clearPoints();
            drawPanel.clearLines();
            step = 1;

            logging.setText("Область построений очищена");
            btnDrawPoint.setEnabled(true);
        });

        btnClear.addActionListener(e -> {
            btnClear.setEnabled(false);
            btnClearDraw.setEnabled(false);
            btnStart.setEnabled(false);
            btnStartToEnd.setEnabled(false);

            inputData.setText("");
            logging.setText("");
            drawPanel.clearPoints();
            drawPanel.clearLines();
            step = 1;

            btnStart.setText("Начать построение");
            btnDrawPoint.setEnabled(true);
        });

        btnClearLog.addActionListener(e -> logging.setText(""));

        btnSaveData.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            chooser.setFileFilter(new FileNameExtensionFilter("txt files (*.txt)", "txt"));
            chooser.setAcceptAllFileFilterUsed(false);

            int result = chooser.showDialog(null, "Сохранить файл");
            if (result == JFileChooser.APPROVE_OPTION) {
                String file = chooser.getSelectedFile().toString();
                try (FileWriter writer = new FileWriter(
                        file.lastIndexOf(".txt") == file.length() - 4 ? file : file + ".txt",
                        false)
                ) {
                    writer.write(inputData.getText());
                    writer.flush();
                } catch (IOException ex) {
                    logLn(ex.getMessage());
                    System.out.println(ex.getMessage());
                }
            }
        });

        btnLoadData.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            chooser.setFileFilter(new FileNameExtensionFilter("txt files (*.txt)", "txt"));
            chooser.setAcceptAllFileFilterUsed(false);

            int result = chooser.showDialog(null, "Открыть файл");
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();

                try (FileReader reader = new FileReader(file)) {
                    BufferedReader buffer = new BufferedReader(reader);
                    inputData.read(buffer, null);
                    buffer.close();
                    inputData.requestFocus();
                } catch (IOException ex) {
                    logLn(ex.getMessage());
                    System.out.println(ex.getMessage());
                }
            }
        });
    }

    private void initMainLayout() {
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(drawPanel, BorderLayout.CENTER);
        mainPanel.add(westPanel, BorderLayout.WEST);
        mainPanel.add(southPanel, BorderLayout.SOUTH);

        westPanel.setLayout(new BoxLayout(westPanel, BoxLayout.PAGE_AXIS));

        westPanel.add(new JLabel("Введите паттерны"));
        westPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        westPanel.add(new JScrollPane(inputData));
        westPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        westPanel.add(btnSaveData);
        westPanel.add(btnLoadData);
        westPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        westPanel.add(new JLabel("Введите C"));
        westPanel.add(inputC);
        inputC.setMaximumSize(new Dimension(400, 24));
        westPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        inputData.setColumns(10);

        southPanel.setLayout(new FlowLayout());
        southPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        southPanel.add(btnDrawPoint);
        southPanel.add(btnClearDraw);
        southPanel.add(btnStart);
        southPanel.add(btnStartToEnd);
        southPanel.add(btnClear);
        southPanel.add(btnClearLog);
    }

    private void initVerticalSplit() {
        // Прокручен всегда вниз logging
        pane = new JScrollPane(logging);
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(height - 250);
        splitPane.setTopComponent(mainPanel);
        splitPane.setBottomComponent(pane);

        getContentPane().add(splitPane);
    }

    private void initLogging() {
        logging.setEditable(false);
    }

    protected Line makeLine(double[] weight, int type) {
        double x_1, y_1, x_2, y_2;
        if (weight[0] == 0 && weight[1] == 0) {
            logLn("Не могу построить прямую типа " + type);
            return null;
        } else if (weight[0] == 0) {
            x_1 = -drawPanel.getBorderX();
            x_2 = drawPanel.getBorderX();
            y_1 = -weight[2] / weight[1];
            y_2 = -weight[2] / weight[1];
        } else if (weight[1] == 0) {
            y_1 = -drawPanel.getBorderY();
            y_2 = drawPanel.getBorderY();
            x_1 = -weight[2] / weight[0];
            x_2 = -weight[2] / weight[0];
        } else {
            x_1 = -drawPanel.getBorderX();
            x_2 = drawPanel.getBorderX();
            y_1 = (-weight[2] - weight[0] * x_1) / weight[1];
            y_2 = (-weight[2] - weight[0] * x_2) / weight[1];
            if (y_1 > drawPanel.getBorderY() || y_1 < -drawPanel.getBorderY()) {
                y_1 = drawPanel.getBorderY();
                x_1 = (-weight[2] - weight[1] * y_1) / weight[0];
            }
            if (y_2 > drawPanel.getBorderY() || y_2 < -drawPanel.getBorderY()) {
                y_2 = -drawPanel.getBorderY();
                x_2 = (-weight[2] - weight[1] * y_2) / weight[0];
            }
        }

        return new Line(x_1, y_1, x_2, y_2, type);
    }

    public static void logLn(String message) {
        if (logging != null && pane != null) {
            String sb = getTime() + " ---- " +
                    message +
                    "\n" +
                    logging.getText();
            logging.setText(sb);
            pane.getVerticalScrollBar().getModel().setValue(
                    pane.getVerticalScrollBar().getMinimum()
            );
        }
    }

    public static void log(String message) {
        if (logging != null && pane != null) {
            String sb = getTime() + " ---- " +
                    message +
                    logging.getText();
            logging.setText(sb);
            pane.getVerticalScrollBar().getModel().setValue(
                    pane.getVerticalScrollBar().getMinimum()
            );
        }
    }



    private static String getTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(cal.getTime());
    }
}
