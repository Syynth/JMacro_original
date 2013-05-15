/**
 * @date May 10, 2013
 * @author Ben Cochrane
 */
package jmacro;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

public class Command {
    
    public Command(String command) {
        Matcher m = Pattern.compile("^\\d+").matcher(command);
        if (!m.find()) {
            this.reps = 1;
        } else {
            try {
                this.reps = Integer.parseInt(m.group(0));
            } catch (Exception e) {
                javax.swing.JOptionPane.showMessageDialog(null,
                        "There was an error trying to parse the follwing command: " + command,
                        "Error parsing Command",
                        javax.swing.JOptionPane.ERROR_MESSAGE,
                        null);
                reps = 0;
            }
        }
        command = command.replaceFirst("^\\d*", "");
        
        
        if (command.startsWith("<")) {
            commandType = PRESS;
            commandBody = command.substring(1);
            System.out.println("Press command found> " + command);
        } else if (command.startsWith(">")) {
            commandType = RELEASE;
            commandBody = command.substring(1);
            System.out.println("Release command found> " + command);
        } else if (command.startsWith("#")) {
            commandType = WAIT;
            commandBody = command.substring(1);
            if (commandBody.isEmpty()) {
                commandBody = defaultWaitBody;
            }
            System.out.println("Wait command found> " + command);
        } else if (command.startsWith("!")) {
            commandType = COMMENT;
            commandBody = command.substring(1);
            System.out.println("Comment found> " + command);
} else if (command.startsWith("*")) {
            commandType = CLICK;
            commandBody = command.substring(1);
            System.out.println("Click command found> " + command);
        } else if (command.startsWith("@")) {
            m = Pattern.compile("#\\d*$").matcher(command);
            if (m.find()) {
                commandType = TYPE_AND_WAIT;
                String waittext = m.group();
                System.out.println(waittext);
                commandBody = command.substring(1);
                System.out.println("Type/Wait command found> " + command);
            } else {
                System.out.println("Type command found> " + command);
                commandType = TYPE;
                commandBody = command.substring(1);
                if (commandBody.isEmpty()) {
                    commandBody = Command.defaultTypeBody;
                }
            }
        }
    }
    
    public void execute(Macro m, SmartRobot r) throws InterruptedException {
        if (reps < 1 || commandType == COMMENT) {
            return;
        }
        for (int i = 0; i < reps; ++i) {
            switch (commandType) {
                case PRESS: r.keyPress(SmartRobot.getKeyCode(commandBody)); break;
                case RELEASE: r.keyRelease(SmartRobot.getKeyCode(commandBody)); break;
                case CLICK: r.click(); break;
                case WAIT: System.out.println("Waiting: " + commandBody + "ms"); m.sleep(Integer.parseInt(commandBody)); break;
                case TYPE:
                    System.out.println("Typing: " + commandBody);
                    if (!commandBody.startsWith("\"")) {
                        if (Character.isDigit(commandBody.charAt(0))) {
                            try {
                                r.type(m.macroData.getField(Integer.parseInt(commandBody)));
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                JOptionPane.showMessageDialog(null, "Error handling message: " + commandBody, "Parsing Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            r.keyPress(SmartRobot.getKeyCode(commandBody));
                            r.keyRelease(SmartRobot.getKeyCode(commandBody));
                        }
                    } else {
                        r.type(commandBody.substring(1, commandBody.length() - 1));
                    }
                    break;
                case TYPE_AND_WAIT:
                    int hash = commandBody.lastIndexOf("#");
                    int wait = Integer.parseInt(!commandBody.endsWith("#") ? commandBody.substring(hash+1) : defaultWaitBody);
                    if (!commandBody.startsWith("\"")) {
                        if (Character.isDigit(commandBody.charAt(0))) {
                            try {
                                r.type(m.macroData.getField(Integer.parseInt(commandBody.substring(0, hash))));
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                JOptionPane.showMessageDialog(null, "2Error handling message: " + commandBody, "Parsing Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            r.keyPress(SmartRobot.getKeyCode(commandBody.substring(0, hash)));
                            r.keyRelease(SmartRobot.getKeyCode(commandBody.substring(0, hash)));
                        }
                    } else {
                        r.type(commandBody.substring(2, commandBody.length() - 2));
                    }
                    m.sleep(wait);
                    break;
            }
        }
    }
    
    public static void define(String[] definitions) {
        for (String d : definitions) {
            System.out.println(d);
            if (d.startsWith("#")) {
                Command.defaultWaitBody = d.substring(1);
            } else if (d.startsWith("@")) {
                Command.defaultTypeBody = d.substring(1);
            }
        }
    }

    protected int reps = 0;
    protected short commandType = COMMENT;
    protected String commandBody = "";
    
    protected static String defaultWaitBody = "100";
    protected static String defaultTypeBody = "\" \"";
    
    public static final short TYPE = 0;
    public static final short PRESS = 1;
    public static final short RELEASE = 2;
    public static final short WAIT = 3;
    public static final short TYPE_AND_WAIT = 4;
    public static final short CLICK = 5;
    public static final short COMMENT = 6;
    
}
