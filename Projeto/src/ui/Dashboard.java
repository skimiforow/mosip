package ui;

import com.intellij.openapi.ui.InputException;
import com.intellij.ui.components.JBScrollPane;
import mosip.company.Main;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class Dashboard {
    float[] values;
    private JPanel MainPanel;
    private JTextField txtNumIteracao;
    private JTextField txtTempoSim;
    private JTextField txtMinTransporte;
    private JTextField txtMaxTransporte;
    private JTextField txtChegada;
    private JTextField txtNumFunArmazem;
    private JTextField txtNumFuncCompras;
    private JTextField txtMinEntregaCliente;
    private JTextField txtMaxEntregaCliente;
    private JTextField txtRand;
    private JCheckBox chkRand;
    private JButton btnExecutar;
    private JButton btnLimpar;
    private JButton btnFechar;
    private JPanel PanelResultados;
    private JLabel completion;
    private JTextArea txtResultados;
    private JScrollPane scrool;
    private Main main;
    private boolean validated;


    public Dashboard() {
        this.txtNumIteracao.setText("100");
        this.txtTempoSim.setText("9600");
        this.txtChegada.setText("10");
        this.txtMinTransporte.setText("720");
        this.txtMaxTransporte.setText("1240");
        this.txtNumFunArmazem.setText("4");
        this.txtNumFuncCompras.setText("3");
        this.txtMinEntregaCliente.setText("720");
        this.txtMaxEntregaCliente.setText("1240");
        this.txtRand.setEnabled(false);
        txtResultados = new JTextArea();
        this.txtResultados.setText("");
        JBScrollPane scrollPane = new JBScrollPane(txtResultados);
        getPanelResultados().setPreferredSize(new Dimension(450, 50));
        getPanelResultados().add(scrollPane);
        main = new Main();
        validated = true;
        values = new float[10];

        btnLimpar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpar();
            }
        });
        chkRand.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (getChkRand().isSelected()) {
                    getTxtRand().setBackground(Color.white);
                    getTxtRand().setEditable(true);
                    getTxtRand().setEnabled(true);
                } else {
                    getBtnExecutar().setEnabled(true);
                    getTxtRand().setBackground(Color.gray);
                    getTxtRand().setEditable(false);
                    getTxtRand().setEnabled(false);
                }
            }
        });
        btnExecutar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exec();
            }
        });

        txtMaxTransporte.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                try {
                    if (Float.parseFloat(getTxtMinTransporte().getText()) > Float.parseFloat(getTxtMaxTransporte().getText())) {
                        getTxtResultados().append(" \n O valor mínimo de transporte tem de ser menor ou igual ao máximo!");
                        getTxtMaxTransporte().requestFocus();
                        getTxtMaxTransporte().setBackground(Color.RED);
                        getBtnExecutar().setEnabled(false);
                    } else {
                        getTxtMaxTransporte().setBackground(Color.white);
                        getBtnExecutar().setEnabled(true);
                        validated = true;
                    }
                } catch (Exception ie) {
                    validated = false;
                    getTxtMaxTransporte().requestFocus();
                    getTxtMaxTransporte().setBackground(Color.RED);
                    getBtnExecutar().setEnabled(false);
                    getTxtResultados().append(" \n " + ie.getMessage());
                }

            }
        });


        getTxtMaxEntregaCliente().addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                try {
                    if (Float.parseFloat(getTxtMinEntregaCliente().getText()) > Float.parseFloat(getTxtMaxEntregaCliente().getText())) {
                        getTxtResultados().append(" \n O valor mínimo de entrega a cliente tem de ser menor ou igual ao máximo!");
                        getTxtMaxEntregaCliente().requestFocus();
                        getTxtMaxEntregaCliente().setBackground(Color.RED);
                        getBtnExecutar().setEnabled(false);
                    } else {
                        getTxtMaxEntregaCliente().setBackground(Color.white);
                        getBtnExecutar().setEnabled(true);
                        validated = true;
                    }
                } catch (Exception ie) {
                    validated = false;
                    getTxtMaxEntregaCliente().requestFocus();
                    getTxtMaxEntregaCliente().setBackground(Color.RED);
                    getBtnExecutar().setEnabled(false);
                    getTxtResultados().append(" \n " + ie.getMessage());
                }

            }
        });
        txtTempoSim.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                try {
                    if (Float.parseFloat(getTxtTempoSim().getText()) <= 0) {
                        getTxtResultados().append(" \n O tempo da simulação tem de ser superior a 0!");
                        getTxtTempoSim().requestFocus();
                        getTxtTempoSim().setBackground(Color.RED);
                        getBtnExecutar().setEnabled(false);
                    } else {
                        getTxtTempoSim().setBackground(Color.white);
                        getBtnExecutar().setEnabled(true);
                        validated = true;
                    }
                } catch (Exception ie) {
                    validated = false;
                    getTxtTempoSim().requestFocus();
                    getTxtTempoSim().setBackground(Color.RED);
                    getBtnExecutar().setEnabled(false);
                    getTxtResultados().append(" \n " + ie.getMessage());
                }
            }
        });
        txtNumIteracao.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                try {
                    if (Float.parseFloat(getTxtNumIteracao().getText()) <= 0) {
                        getTxtResultados().append(" \n O número de iterações tem de ser superior a 0!");
                        getTxtNumIteracao().requestFocus();
                        getTxtNumIteracao().setBackground(Color.RED);
                        getBtnExecutar().setEnabled(false);
                    } else {
                        getTxtNumIteracao().setBackground(Color.white);
                        getBtnExecutar().setEnabled(true);
                        validated = true;
                    }
                } catch (Exception ie) {
                    validated = false;
                    getTxtNumIteracao().setBackground(Color.RED);
                    getTxtNumIteracao().requestFocus();
                    getBtnExecutar().setEnabled(false);
                    getTxtResultados().append(" \n " + ie.getMessage());
                }
            }
        });
        txtChegada.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                try {
                    if (Float.parseFloat(getTxtChegada().getText()) <= 0) {
                        getTxtResultados().append(" \n A taxa de chegada tem de ser superior a 0!");
                        getTxtChegada().requestFocus();
                        getTxtChegada().setBackground(Color.RED);
                        getBtnExecutar().setEnabled(false);
                    } else {
                        getTxtChegada().setBackground(Color.white);
                        getBtnExecutar().setEnabled(true);
                        validated = true;
                    }
                } catch (Exception ie) {
                    validated = false;
                    getTxtChegada().requestFocus();
                    getTxtChegada().setBackground(Color.RED);
                    getBtnExecutar().setEnabled(false);
                    getTxtResultados().append(" \n " + ie.getMessage());
                }
            }
        });

        txtNumFunArmazem.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                try {
                    if (Float.parseFloat(getTxtNumFunArmazem().getText()) <= 0) {
                        getTxtResultados().append(" \n O número de funcionários do armazém tem de ser superior a 0!");
                        getTxtNumFunArmazem().requestFocus();
                        getTxtNumFunArmazem().setBackground(Color.RED);
                        getBtnExecutar().setEnabled(false);
                    } else {
                        getTxtNumFunArmazem().setBackground(Color.white);
                        getBtnExecutar().setEnabled(true);
                        validated = true;
                    }
                } catch (Exception ie) {
                    validated = false;
                    getTxtNumFunArmazem().requestFocus();
                    getTxtNumFunArmazem().setBackground(Color.RED);
                    getBtnExecutar().setEnabled(false);
                    getTxtResultados().append(" \n " + ie.getMessage());
                }
            }
        });
        txtNumFuncCompras.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                try {
                    if (Float.parseFloat(getTxtNumFuncCompras().getText()) <= 0) {
                        getTxtResultados().append(" \n O número de funcionários do compras tem de ser superior a 0!");
                        getTxtNumFuncCompras().requestFocus();
                        getTxtNumFuncCompras().setBackground(Color.RED);
                        getBtnExecutar().setEnabled(false);
                    } else {
                        getTxtNumFuncCompras().setBackground(Color.white);
                        getBtnExecutar().setEnabled(true);
                        validated = true;
                    }
                } catch (Exception ie) {
                    validated = false;
                    getTxtNumFuncCompras().requestFocus();
                    getTxtNumFuncCompras().setBackground(Color.RED);
                    getBtnExecutar().setEnabled(false);
                    getTxtResultados().append(" \n " + ie.getMessage());
                }
            }
        });
        txtRand.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                try {
                    if (Float.parseFloat(getTxtRand().getText()) <= 0) {
                        getTxtResultados().append(" \n A seed tem de ser superior a 0!");
                        getTxtRand().requestFocus();
                        getTxtRand().setBackground(Color.RED);
                        getBtnExecutar().setEnabled(false);
                    } else {
                        getTxtRand().setBackground(Color.white);
                        getBtnExecutar().setEnabled(true);
                        validated = true;
                    }
                } catch (Exception ie) {
                    validated = false;
                    getTxtRand().requestFocus();
                    getTxtRand().setBackground(Color.RED);
                    getBtnExecutar().setEnabled(false);
                    getTxtResultados().append(" \n " + ie.getMessage());
                }
            }
        });
        btnFechar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(1);
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Simulador MOSIP");
        frame.setContentPane(new Dashboard().MainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public JPanel getPanelResultados() {
        return PanelResultados;
    }

    public void setPanelResultados(JPanel panelResultados) {
        PanelResultados = panelResultados;
    }

    private void exec() {
        boolean ok = true;
        float timeOfSimulation = 0;
        float iterations = 0;
        float arrivalRate = 0;
        float numFuncArmazem = 0;
        float numFuncCompras = 0;
        float minTimeTransport = 0;
        float maxTimeTransport = 0;
        float minDeliveryTime = 0;
        float maxDeliveryTime = 0;
        float randTime = 0;


        try {
            timeOfSimulation = Float.parseFloat(getTxtTempoSim().getText());
            iterations = Float.parseFloat(getTxtNumIteracao().getText());
            arrivalRate = Float.parseFloat(getTxtChegada().getText());
            numFuncArmazem = Float.parseFloat(getTxtNumFunArmazem().getText());
            numFuncCompras = Float.parseFloat(getTxtNumFuncCompras().getText());
            minTimeTransport = Float.parseFloat(getTxtMinTransporte().getText());
            maxTimeTransport = Float.parseFloat(getTxtMaxTransporte().getText());
            minDeliveryTime = Float.parseFloat(getTxtMinEntregaCliente().getText());
            maxDeliveryTime = Float.parseFloat(getTxtMaxEntregaCliente().getText());
            if (getChkRand().isSelected())
                randTime = Float.parseFloat(getTxtRand().getText());
        } catch (InputException ie) {
            ok = false;
            getTxtResultados().append(" \n " + ie.getMessage());
        }
        if (timeOfSimulation <= 0)
            ok = false;
        if (iterations <= 0)
            ok = false;
        if (arrivalRate <= 0)
            ok = false;
        if (numFuncArmazem <= 0)
            ok = false;
        if (numFuncCompras <= 0)
            ok = false;
        if (maxTimeTransport <= 0)
            ok = false;
        if (maxDeliveryTime <= 0)
            ok = false;
        if (getChkRand().isSelected() && randTime <= 0)
            ok = false;
        if (ok && validated) {
            for (int i = 0; i < values.length; i++) {
                switch (i) {
                    case 0:
                        values[0] = timeOfSimulation;
                        break;
                    case 1:
                        values[1] = iterations;
                        break;
                    case 2:
                        values[2] = arrivalRate;
                        break;
                    case 3:
                        values[3] = numFuncArmazem;
                        break;
                    case 4:
                        values[4] = numFuncCompras;
                        break;
                    case 5:
                        values[5] = minTimeTransport;
                        break;
                    case 6:
                        values[6] = maxTimeTransport;
                        break;
                    case 7:
                        values[7] = minDeliveryTime;
                        break;
                    case 8:
                        values[8] = maxDeliveryTime;
                        break;
                    case 9:
                        values[9] = randTime;
                        if (!getChkRand().isSelected())
                            randTime = 0;
                        break;
                }
            }

            String fileName = "Execution-" + values[0] + "-" + values[1] + "-" + values[2] + "-" + values[3] + "-" + values[4] + "-" + values[5] + "-" + values[6] + "-" + values[7] + "-" + values[8] + "-" + values[9] + "-" + ".txt";
            PrintWriter writer = null;
            try {
                writer = new PrintWriter(new FileOutputStream(fileName, false));
                writer.println("Enc. na fila de espera por processar na fila  do Armazém" + "\t"
                        + "Enc. na fila de espera por processar na fila  das Compras" + "\t"
                        + "Eventos por processar" + "\t"
                        + "Total Encomendas" + "\t"
                        + "Encomendas entregues" + "\t"
                        + "Tempo médio de espera do armazém" + "\t"
                        + "Tempo médio de espera das compras" + "\t"
                        + "Tempo médio de entrega a cliente" + "\t"
                        + "Média de uso da fila de espera do armazém" + "\t"
                        + "Média de uso da fila de espera das compras" + "\t"
                        + "Média de utilização do servidor Armazém" + "\t"
                        + "Média de utilização do servidor Compras");
                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            main.run(fileName, values, Dashboard.this);
        } else {
            getTxtResultados().append("\n Erro nos dados!");
        }
    }

    private void limpar() {
        this.txtRand.setText("");
        this.txtRand.setEnabled(false);
        this.txtResultados.setText("");
    }

    public JPanel getMainPanel() {
        return MainPanel;
    }

    public void setMainPanel(JPanel mainPanel) {
        this.MainPanel = mainPanel;
    }

    public JTextField getTxtNumIteracao() {
        return txtNumIteracao;
    }

    public void setTxtNumIteracao(JTextField txtNumIteracao) {
        this.txtNumIteracao = txtNumIteracao;
    }

    public JTextField getTxtTempoSim() {
        return txtTempoSim;
    }

    public void setTxtTempoSim(JTextField txtTempoSim) {
        this.txtTempoSim = txtTempoSim;
    }

    public JTextField getTxtMinTransporte() {
        return txtMinTransporte;
    }

    public void setTxtMinTransporte(JTextField txtMinTransporte) {
        this.txtMinTransporte = txtMinTransporte;
    }

    public JTextField getTxtMaxTransporte() {
        return txtMaxTransporte;
    }

    public void setTxtMaxTransporte(JTextField txtMaxTransporte) {
        this.txtMaxTransporte = txtMaxTransporte;
    }

    public JTextField getTxtChegada() {
        return txtChegada;
    }

    public void setTxtChegada(JTextField txtChegada) {
        this.txtChegada = txtChegada;
    }

    public JTextField getTxtNumFunArmazem() {
        return txtNumFunArmazem;
    }

    public void setTxtNumFunArmazem(JTextField txtNumFunArmazem) {
        this.txtNumFunArmazem = txtNumFunArmazem;
    }

    public JTextField getTxtNumFuncCompras() {
        return txtNumFuncCompras;
    }

    public void setTxtNumFuncCompras(JTextField txtNumFuncCompras) {
        this.txtNumFuncCompras = txtNumFuncCompras;
    }

    public JTextField getTxtMinEntregaCliente() {
        return txtMinEntregaCliente;
    }

    public void setTxtMinEntregaCliente(JTextField txtMinEntregaCliente) {
        this.txtMinEntregaCliente = txtMinEntregaCliente;
    }

    public JTextField getTxtMaxEntregaCliente() {
        return txtMaxEntregaCliente;
    }

    public void setTxtMaxEntregaCliente(JTextField txtMaxEntregaCliente) {
        this.txtMaxEntregaCliente = txtMaxEntregaCliente;
    }

    public JTextField getTxtRand() {
        return txtRand;
    }

    public void setTxtRand(JTextField txtRand) {
        this.txtRand = txtRand;
    }

    public JCheckBox getChkRand() {
        return chkRand;
    }

    public void setChkRand(JCheckBox chkRand) {
        this.chkRand = chkRand;
    }

    public JButton getBtnExecutar() {
        return btnExecutar;
    }

    public void setBtnExecutar(JButton btnExecutar) {
        this.btnExecutar = btnExecutar;
    }

    public JButton getBtnLimpar() {
        return btnLimpar;
    }

    public void setBtnLimpar(JButton btnLimpar) {
        this.btnLimpar = btnLimpar;
    }

    public JButton getBtnFechar() {
        return btnFechar;
    }

    public void setBtnFechar(JButton btnFechar) {
        this.btnFechar = btnFechar;
    }

    public JTextArea getTxtResultados() {
        return txtResultados;
    }

    public void setTxtResultados(JTextArea txtResultados) {
        this.txtResultados = txtResultados;
    }

    public JLabel getCompletion() {
        return completion;
    }

    public void setCompletion(JLabel completion) {
        this.completion = completion;
    }
}
