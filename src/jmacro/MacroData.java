package jmacro;

import java.awt.AWTException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class MacroData {
    
    private File data;
    private File macroInstructions;
    private int currentEntry;
    private HashMap<String, ArrayList<Command>> commandMap;
    private ArrayList<MacroDataItem> items;
    private SmartRobot bot;
    private JFrame window;
    private JMacroWindow macroWindow;
    
    public MacroData(JMacroWindow window) {
        macroWindow = window;
        items = new ArrayList<>();
        currentEntry = 0;
        
        commandMap = new HashMap<>();
        commandMap.put("start", new ArrayList<Command>());
        commandMap.put("item", new ArrayList<Command>());
        commandMap.put("reset", new ArrayList<Command>());
        commandMap.put("end", new ArrayList<Command>());
        
        try {
            bot = new SmartRobot();
        } catch (AWTException ex) {
            Logger.getLogger(MacroData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void advanceEntry() {
        currentEntry++;
        enforceEntryPosition();
    }
    public void revertEntry() {
        currentEntry--;
        enforceEntryPosition();
    }
    public void resetEntryPosition() {
        currentEntry = 0;
        enforceEntryPosition();
    }
    private void enforceEntryPosition() {
        if (currentEntry < 0) {
            currentEntry = 0;
        }
        if (currentEntry > getNumberOfEntries() - 1) {
            currentEntry = getNumberOfEntries() - 1;
        }
        if (getNumberOfEntries() > 0) {
            macroWindow.setPreviewWindow(currentEntry, getCurrentEntry());
        }
    }
    
    public void setData(File data) {
        this.data = data;
    }
    public void setMacroInstructions(File macroInstructions) {
        this.macroInstructions = macroInstructions;
    }
    public void setWindow(JFrame window) {
        this.window = window;
    }
    
    public boolean parseData() {
        if (data == null) { return false; }
        if (macroInstructions == null) { return false; }
        try {
            if (data.getName().toLowerCase().endsWith("csv")) {
                parseCSV(data);
            }
            if (macroInstructions.getName().toLowerCase().endsWith("mfl")) {
                parseMFL(macroInstructions);
            }
            resetEntryPosition();
            return true;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MacroData.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    private void parseCSV(File csv) throws FileNotFoundException {
        items = new ArrayList<>();
        Scanner scan;
        scan = new Scanner(csv);
        while (scan.hasNextLine()) {
            items.add(new MacroDataItem(scan.nextLine()));
        }
    }
    private void parseMFL(File mfl) throws FileNotFoundException {
        commandMap.get("start").clear();
        commandMap.get("item").clear();
        commandMap.get("reset").clear();
        commandMap.get("end").clear();
        Scanner scan;
        scan = new Scanner(mfl);
        String commandData = "";
        while (scan.hasNextLine()) {
            commandData += scan.nextLine() + "\n";
        }
        
        Command.define(commandData.substring(commandData.indexOf("[define]") + 9, commandData.indexOf("[/define]")).split("\n"));
        for (String s : commandData.substring(commandData.indexOf("[start]") + 8, commandData.indexOf("[/start]")).split("\n")) {
            commandMap.get("start").add(new Command(s));
        }
        for (String s : commandData.substring(commandData.indexOf("[item]") + 7, commandData.indexOf("[/item]")).split("\n")) {
            commandMap.get("item").add(new Command(s));
        }
        for (String s : commandData.substring(commandData.indexOf("[reset]") + 8, commandData.indexOf("[/reset]")).split("\n")) {
            commandMap.get("reset").add(new Command(s));
        }
        for (String s : commandData.substring(commandData.indexOf("[end]") + 6, commandData.indexOf("[/end]")).split("\n")) {
            commandMap.get("end").add(new Command(s));
        }
    }
    
    public int getNumberOfEntries() {
        return items.size();
    }
    public int getEntrySize() {
        if (items.size() > 0) {
            return items.get(0).fields.length;
        }
        else {
            return 0;
        }
    }
    public SmartRobot getRobot() { return bot; }
    public ArrayList<Command> getCommandList(String name) {
        return commandMap.get(name);
    }
    public String getField(int n) {
        if (n <= getEntrySize()) {
            try {
                return items.get(currentEntry).fields[n];
            } catch (ArrayIndexOutOfBoundsException e) {
                JOptionPane.showMessageDialog(null, "You have tried to access a field(" + n + ") which does not exist!", "Error", JOptionPane.ERROR_MESSAGE);
                return "Error; Field does not exist!";
            }
        } else {
            return getEntrySize() + "<" + n;
        }
    }
    public String getCurrentEntry() {
        return items.get(currentEntry).toString();
    }
    public JFrame getWindow() { return window; }
    
    @Override
    public String toString() {
        String s;
        s = "[";
        for (Iterator<MacroDataItem> it = items.iterator(); it.hasNext();) {
            MacroDataItem d = it.next();
            s += d.toString() + ",";
        }
        s += "]";
        return s;
    }
    
    private class MacroDataItem {
        
        String[] fields;
        
        public MacroDataItem(String data) {
            fields = data.split(",");
            for (String s : fields) {
                s = s.trim();
            }
        }
        
        @Override
        public String toString() {
            String s;
            s = "[ ";
            if (fields != null) {
                for (int i = 0; i < fields.length; i++) {
                    s += fields[i];
                    if (i < fields.length - 1) {
                        s += ", ";
                    }
                }
            }
            s += " ]";
            return s;
        }
    }
}
