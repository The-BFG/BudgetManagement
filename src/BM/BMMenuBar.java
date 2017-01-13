/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BM;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 *
 * @author giacomo
 */
public class BMMenuBar  extends JMenuBar implements ActionListener{
    private JMenu fileMenu = new JMenu("File");
    private JMenuItem openArchive = new JMenuItem("Apri archivio");
    private JMenuItem saveArchive = new JMenuItem("Salva archivio");
    private JMenuItem exitBM = new JMenuItem("Esci");
    
    private JMenu exportMenu = new JMenu("Esporta");
    private JMenuItem exportCSV = new JMenuItem("Esporta in formato CommaSV");
    private JMenuItem exportTSV = new JMenuItem("Esporta in formato TabSV");
    private JMenuItem exportOpenDocument = new JMenuItem("Esporta in formato OpenDoument"); 
    
    private BMTablePanel table;    
    
    public BMMenuBar(BMTablePanel table) {
        this.table = table;
        
        fileMenu.add(openArchive);
        fileMenu.add(saveArchive);
        fileMenu.addSeparator();
        fileMenu.add(exitBM);
        openArchive.addActionListener(this);
        openArchive.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        saveArchive.addActionListener(this);
        saveArchive.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
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

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println(e.getActionCommand());
        switch(e.getActionCommand()) {
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
                break;
            case "Salva archivio":
                try {
                    saveArchive();
                }
                catch (FileNotFoundException fnf) {
                    System.out.println("Impossibile trovare il file.\n" + fnf);
                }
                catch (IOException ioe) {
                    System.out.println("Errore di I/O.\n" + ioe);
                }
                break;
            case "Esci":
                System.exit(0);
                break;
            case "Esporta in formato CSV":
                
                break;
                
            case "Esporta in formato TSV":
                
                break;
            case "Esporta in formato ODT":
                
                break;
            default:
        }
    }
    private void openArchive() throws ClassNotFoundException, FileNotFoundException, IOException {
        JFileChooser open = new JFileChooser("./archive");
        open.setMultiSelectionEnabled(false);
        open.setFileSelectionMode(JFileChooser.FILES_ONLY);
        open.setApproveButtonMnemonic(KeyEvent.VK_ENTER);
        int returnVal = open.showOpenDialog(this);
        ArrayList<BMItem> transactions;
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            String filePath = open.getSelectedFile().getAbsolutePath();
            System.out.println(filePath);
            FileInputStream fin = new FileInputStream(filePath);
            ObjectInputStream ois = new ObjectInputStream(fin);
            transactions = (ArrayList<BMItem>) ois.readObject();
            ois.close();
            fin.close();
            
            ((BMTableModel) table.getTable().getModel()).setTransactionList(transactions);
            table.refreshTotal();
        }
    }
    private void saveArchive() throws FileNotFoundException, IOException {
        JFileChooser save = new JFileChooser("./archive");
        save.setMultiSelectionEnabled(false);
        save.setFileSelectionMode(JFileChooser.FILES_ONLY);
        save.setApproveButtonMnemonic(KeyEvent.VK_ENTER);
        save.setSelectedFile(new File("MioArchivio"));
        int returnVal = save.showSaveDialog(save);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            String filePath = save.getSelectedFile().getAbsolutePath()+".bin";
            System.out.println(filePath);
            FileOutputStream fout = new FileOutputStream(filePath);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(((BMTableModel)table.getTable().getModel()).getTransactionsList());
            oos.close();
            fout.close();
        }
    }    
}