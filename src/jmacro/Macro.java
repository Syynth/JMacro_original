package jmacro;

import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class Macro extends Thread {
    
    MacroData macroData;
    
    boolean abort = false;
    boolean running = false;
    boolean paused = false;
    
    public Macro(MacroData macroData) {
        this.macroData = macroData;
    }
    
    public void abort() {
        abort = true;
    }
    
    public void pause() {
        paused = true;
    }
    
    public boolean isPaused() {
        return paused;
    }
    
    public void proceed() {
        paused = false;
    }
    
    @Override
    public void run() {
        executeMacro();
    }
    
    private void executeMacro() {
        if (macroData == null) {
            return;
        }
        abort = false;
        running = true;
        for (Command cmd : macroData.getCommandList("start")) {
            if (abort) { break; }
            try {
                cmd.execute(this, macroData.getRobot());
            } catch (InterruptedException e) {}
        }
        for (int i = 0; i < macroData.getNumberOfEntries(); i++) {
            if (abort) { break; }
            if (paused) {
                i--;
                continue;
            }
            for (Command cmd : macroData.getCommandList("item")) {
                if (abort) { break; }
                try {
                    cmd.execute(this, macroData.getRobot());
                } catch (InterruptedException e) {}
            }
            if (i > 0) {
                if (abort) { break; }
                for (Command cmd : macroData.getCommandList("reset")) {
                    if (abort) { break; }
                    try {
                        cmd.execute(this, macroData.getRobot());
                    } catch (InterruptedException e) {}
                }
            }
            macroData.advanceEntry();
            if (abort) { break; }
        }
        for (Command cmd : macroData.getCommandList("end")) {
            if (abort) { break; }
            try {
                cmd.execute(this, macroData.getRobot());
            } catch (InterruptedException e) {}
        }
        if (abort) {
            JOptionPane.showMessageDialog(null, "Last active record was " +
                    macroData.getCurrentEntry());
        }
        abort = false;
        running = false;
        JMacroWindow.getInstance().resetWindowColor();
    }
    
    private void executeCommand(String cmd) {
        if (cmd.isEmpty()) {
            return;
        }
        char c = cmd.charAt(0);
        String param = cmd.substring(1);
        if (c == '<') {
            macroData.getRobot().keyPress(SmartRobot.getKeyCode(param));
        } else if (c == '>') {
            macroData.getRobot().keyRelease(SmartRobot.getKeyCode(param));
        } else if (c == '@') {
            if (param.startsWith("\"")) {
                macroData.getRobot().type(param.substring(1, param.length() - 1));
            } else if (Character.isDigit(param.charAt(0))) {
                macroData.getRobot().type(macroData.getField(Integer.parseInt(param)));
            } else {
                macroData.getRobot().keyPress(SmartRobot.getKeyCode(param));
                macroData.getRobot().keyRelease(SmartRobot.getKeyCode(param));
            }
        } else if (c == '*') {
            macroData.getRobot().click();
        } else if (c == '#') {
            sleep(Integer.parseInt(param));
        }
    }
    
    private void sleep(int n) {
        try {
            Thread.sleep(n);
        } catch (InterruptedException ex) {
            Logger.getLogger(Macro.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
