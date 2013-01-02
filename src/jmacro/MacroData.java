package jmacro;

import java.awt.AWTException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

public class MacroData {
    
    private File data;
    private File macroInstructions;
    private int currentEntry;
    private HashMap<String, ArrayList<String>> commandMap;
    private ArrayList<MacroDataItem> items;
    private SmartRobot bot;
    private JFrame window;
    
    public MacroData() {
        items = new ArrayList<>();
        currentEntry = 0;
        
        commandMap = new HashMap<>();
        commandMap.put("start", new ArrayList<String>());
        commandMap.put("item", new ArrayList<String>());
        commandMap.put("reset", new ArrayList<String>());
        commandMap.put("end", new ArrayList<String>());
        
        try {
            bot = new SmartRobot();
        } catch (AWTException ex) {
            Logger.getLogger(MacroData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void advanceEntry() {
        currentEntry++;
    }
    public void resetEntryPosition() {
        currentEntry = 0;
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
    
    public void parseData() {
        if (data == null) { return; }
        if (macroInstructions == null) { return; }
        try {
            if (data.getName().toLowerCase().endsWith("prn")) {
                parsePRN(data);
            }
            if (macroInstructions.getName().toLowerCase().endsWith("mfl")) {
                parseMFL(macroInstructions);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MacroData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void parsePRN(File prn) throws FileNotFoundException {
        items = new ArrayList<>();
        Scanner scan;
        scan = new Scanner(prn);
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
        commandMap.get("start").addAll(Arrays.asList(commandData.substring(
            commandData.indexOf("[start]") + 8, commandData.indexOf("[/start]")).split("\n")));
        commandMap.get("item").addAll(Arrays.asList(commandData.substring(
            commandData.indexOf("[item]") + 7, commandData.indexOf("[/item]")).split("\n")));
        commandMap.get("reset").addAll(Arrays.asList(commandData.substring(
            commandData.indexOf("[reset]") + 8, commandData.indexOf("[/reset]")).split("\n")));
        commandMap.get("end").addAll(Arrays.asList(commandData.substring(
            commandData.indexOf("[end]") + 6, commandData.indexOf("[/end]")).split("\n")));
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
    public ArrayList<String> getCommandList(String name) {
        return commandMap.get(name);
    }
    public String getField(int n) {
        if (n <= getEntrySize()) {
            return items.get(currentEntry).fields[n];
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
        private final int fieldWidth = 32;
        
        public MacroDataItem(String data) {
            fields = new String[(int)Math.ceil(data.length() * 1.0 / 32.0)];
            for (int i = 0; i < fields.length; i++) {
                fields[i] = data.substring(i * fieldWidth, Math.min((i + 1) * fieldWidth, data.length())).trim();
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
