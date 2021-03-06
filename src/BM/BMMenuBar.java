/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BM;

import EXPORT.AbstractExport;
import EXPORT.ExportCSV;
import EXPORT.ExportTSV;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTable.PrintMode;
import javax.swing.KeyStroke;
import org.jopendocument.dom.OOUtils;
import org.jopendocument.dom.spreadsheet.SpreadSheet;

/**
 *Classe che serve a gestire tutti gli eventi collegati alla barra dei menu in alto.
 * 
 * <br> Estende una <a href="https://docs.oracle.com/javase/7/docs/api/javax/swing/JMenuBar.html">JMenuBar</a>.
 * <br>Sono implementati tutti i metodi di gestione di salvataggio apertura e stampa del bilancio.<br>
 * E' inoltre possibile esportare il bilancio in formato csv,tsv e ods.
 * @author giacomo
 */
public class BMMenuBar  extends JMenuBar implements ActionListener{
    private static final long serialVersionUID = 1L;
    private JMenu fileMenu = new JMenu("File");
    private JMenuItem newArchive = new JMenuItem("Nuovo archivio");
    private JMenuItem openArchive = new JMenuItem("Apri archivio");
    private JMenuItem saveArchive = new JMenuItem("Salva archivio");
    private JMenuItem print = new JMenuItem("Stampa");
    private JMenuItem exitBM = new JMenuItem("Esci");
    
    private JMenu exportMenu = new JMenu("Esporta");
    private JMenuItem exportCSV = new JMenuItem("Esporta in formato CommaSV");
    private JMenuItem exportTSV = new JMenuItem("Esporta in formato TabSV");
    private JMenuItem exportOpenDocument = new JMenuItem("Esporta in formato OpenDocument");
    
    private BMTablePanel table;    
    
    /**
     * Costruttore della classe  <a href="../BM/BMMenuBar">BMMenuBar</a>.
     * 
     * <br>Implementa tutta l'interfaccia grafica della barra dei menu.
     * @param table riferimento al <a href="../BM/BMTablePanel">BMTablePanel</a> 
     */
    public BMMenuBar(BMTablePanel table) {
        this.table = table;
        
        fileMenu.add(newArchive);
        fileMenu.add(openArchive);
        fileMenu.add(saveArchive);
        fileMenu.addSeparator();
        fileMenu.add(print);
        fileMenu.addSeparator();
        fileMenu.add(exitBM);
        newArchive.addActionListener(this);
        newArchive.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        openArchive.addActionListener(this);
        openArchive.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        saveArchive.addActionListener(this);
        saveArchive.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        print.addActionListener(this);
        print.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
        exitBM.addActionListener(this);        
        exitBM.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
        
        exportMenu.add(exportCSV);
        exportMenu.add(exportTSV);
        exportMenu.add(exportOpenDocument);
        exportCSV.addActionListener(this);
        exportTSV.addActionListener(this);
        exportOpenDocument.addActionListener(this);
        
        this.add(fileMenu);
        this.add(exportMenu);
    }

    /**
     * Metodo che cattura la voce di menu che è stata cliccata.
     * 
     * <br>Questo metodo fara partire una sequenza di operazione in base alla voce del menu che è stata 
     * selezionata.
     * @param e serve a visualizzare chi è che ha invocato l'evento.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        //System.out.println(e.getActionCommand());
        switch(e.getActionCommand()) {
            case "Nuovo archivio":
                int confirmVal = JOptionPane.showConfirmDialog(null, "Vuoi salvare l'archivio aperto prima di aprire un archivio vuoto?", "Conferma apertura nuovo archivio.", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if(confirmVal == JOptionPane.YES_OPTION)    
                    saveArchive();               
                                
                ((BMTableModel)table.getTable().getModel()).resetTableModel();
                table.refreshTotal();                
                break;
            case "Apri archivio":
                try {
                    openArchive();
                }
                catch (FileNotFoundException fnf) {
                    System.out.println("Impossibile trovare il file.\n" + fnf);
                }
                catch (IOException ioe) {
                    System.out.println("Errore di I/O.\n" + ioe);
                } 
                catch (ClassNotFoundException cnf) { 
                    System.out.println("Classe non trovata.\n" + cnf);
                }    
                catch (ArrayIndexOutOfBoundsException aio) {
                    System.out.println("Errore apertura\n" + aio);
                }
                break;
            case "Salva archivio":
                    saveArchive();
                break;
            case "Stampa":
                MessageFormat header = new MessageFormat("Gestione Bilancio");
                MessageFormat footer = new MessageFormat(BMItem.completeDate.format(Calendar.getInstance().getTime()));
                boolean printVal = false;
                try {
                    printVal = table.getTable().print(PrintMode.FIT_WIDTH, header, footer);
                }
                catch (PrinterException pe) {
                    System.out.println("Impossibile stampare.\n" + pe);
                }
                if (printVal) {
                    Image image = new ImageIcon("./icon/successful.png","").getImage().getScaledInstance(32, 32, Image.SCALE_DEFAULT);
                    ImageIcon icon = new ImageIcon(image);
                    JOptionPane.showMessageDialog(null, "La stampa e' avvenuta con successo", "Esito Stampa", JOptionPane.INFORMATION_MESSAGE, icon);
                }
                else
                    JOptionPane.showMessageDialog(null, "La stampa non e' avvenuta con successo", "Esito Stampa", JOptionPane.WARNING_MESSAGE);
                break;
            case "Esci":
                System.exit(0);
                break;
            case "Esporta in formato CommaSV":
                AbstractExport exportCsv = new ExportCSV(((BMTableModel)table.getTable().getModel()).getTransactionsList());
                exportCsv.exportData();
                break;                
            case "Esporta in formato TabSV":
                AbstractExport exportTsv = new ExportTSV(((BMTableModel)table.getTable().getModel()).getTransactionsList());
                exportTsv.exportData();
                break;
            case "Esporta in formato OpenDocument":
                if(((BMTableModel)table.getTable().getModel()).getTransactionsList().size() > 0) {
                    String date = BMItem.completeDate.format(Calendar.getInstance().getTime());
                    final File file = new File(((System.getProperty("user.dir").endsWith("class")) ? "../archive/ods/Archivio" : "./archive/ods/Archivio")+date.substring(17, 19));
                    try {
                        SpreadSheet.createEmpty((BMTableModel)table.getTable().getModel()).saveAs(file);
                        OOUtils.open(new File(file.getAbsolutePath() +".ods"));               
                    }
                    catch(FileNotFoundException fnf) {
                        System.out.println("File non trovato.\n" + fnf);
                    } 
                    catch (IOException ex) {
                        System.out.println("Errore di Input/Output.\n" + ex);   
                    } 
                }
                else {
                    JOptionPane.showMessageDialog(null, "Esportazione annullata perche non e' presente alcuna transazione.", "Esportazione annullata", JOptionPane.WARNING_MESSAGE);
                }
                break;
            default:
        }
    }
    
    /**
     * Metodo che gestisce tutto  il caricamento di un bilancio da un archivio salvato in precedenza.
     * 
     * <br> La gestione di tutte le eccezioni che possono essere sollevato utilizzando questo metodo devono essere gestite dal
     * chiamante4 di questo metodo.
     * @throws ClassNotFoundException Classe non trovata.
     * @throws FileNotFoundException File non trovato.
     * @throws IOException Errore di Input/Output.
     */
    private void openArchive() throws ClassNotFoundException, FileNotFoundException, IOException {   
        JFileChooser open = new JFileChooser((System.getProperty("user.dir").endsWith("class")) ? "../archive/bin" : "./archive/bin");             
        open.setMultiSelectionEnabled(false);
        open.setFileSelectionMode(JFileChooser.FILES_ONLY);
        open.setApproveButtonMnemonic(KeyEvent.VK_ENTER);
        int returnVal = open.showOpenDialog(this);
        
        ArrayList<BMItem> transactions = new ArrayList<BMItem>();
        if(returnVal == JFileChooser.APPROVE_OPTION) {            
            ((BMTableModel)table.getTable().getModel()).resetTableModel();
            
            String filePath = open.getSelectedFile().getAbsolutePath();
            try (FileInputStream fin = new FileInputStream(filePath); ObjectInputStream ois = new ObjectInputStream(fin)) {
                Object obj = ois.readObject();
                if(obj instanceof ArrayList<?> ) {
                    for(Object e : (ArrayList<?>) obj){
                        if(e != null && e instanceof BMItem) {
                            transactions.add((BMItem)e);
                        }
                    }
                }
            }            
            ((BMTableModel) table.getTable().getModel()).setTransactionList(transactions);
            table.refreshTotal();
        }
    }
    
    /**
     * Metodo per effettuare un corretto salvataggio del bilancio su un file binario.
     * 
     * <br>In particolare viene salvato l'intero ArrayList di transazioni che sono presenti all'interno
     * del <a href="../BM/BMTableModel">BMTableModel</a>.
     */
    private void saveArchive(){
        String date="";
        String filePath; 

        JFileChooser save = new JFileChooser((System.getProperty("user.dir").endsWith("class")) ? "../archive/bin" : "./archive/bin");
        save.setMultiSelectionEnabled(false);
        save.setFileSelectionMode(JFileChooser.FILES_ONLY);
        save.setApproveButtonMnemonic(KeyEvent.VK_ENTER);
        save.setSelectedFile(new File("MioArchivio"));
        int returnVal = save.showSaveDialog(save);
        
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            if(save.getSelectedFile().exists()) {
                int optVal =JOptionPane.showConfirmDialog(  
                        null,
                        "Vuoi davvero sovrascrivere il file: "+ save.getSelectedFile().getName() +" ?",
                        "Sovrascrivere", 
                        JOptionPane.YES_NO_OPTION, 
                        JOptionPane.QUESTION_MESSAGE);
                if(optVal == JOptionPane.YES_OPTION)
                    save.getSelectedFile().delete();
                else {
                    date = "(";
                    date = date.concat(BMItem.completeDate.format(Calendar.getInstance().getTime()) + ")");
                    //System.out.println(date);
                }
            }
            filePath = save.getSelectedFile().getAbsolutePath() + date;
            //System.out.println(filePath);
            try {
                FileOutputStream fout = new FileOutputStream(filePath);
                ObjectOutputStream oos = new ObjectOutputStream(fout);
                oos.writeObject(((BMTableModel)table.getTable().getModel()).getTransactionsList());
            } 
            catch (FileNotFoundException fnf) {
                    System.out.println("Impossibile trovare il file.\n" + fnf);
            } 
            catch (IOException ioe) {
                    System.out.println("Errore di I/O.\n" + ioe);
            }
        }
    }    
}