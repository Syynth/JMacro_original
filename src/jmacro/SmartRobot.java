package jmacro;

import java.awt.AWTException;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SmartRobot extends java.awt.Robot {
    
    public SmartRobot() throws AWTException {
        
    }
    
    public void click() {
        this.mousePress(InputEvent.BUTTON1_MASK);
        this.mouseRelease(InputEvent.BUTTON1_MASK);
    }

    public void type(String text) {
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            int code;
            boolean shift = false;
            if (Character.isLetterOrDigit(c)) {
                code = getKeyCode(c);
                shift = !Character.isLowerCase(c) && Character.isLetter(c);
            } else {
                switch (c) {
                    case '_': shift = true;
                    case '-': code = KeyEvent.VK_MINUS; break;
                    case '+': shift = true;
                    case '=': code = KeyEvent.VK_EQUALS; break;
                    case '{': shift = true;
                    case '[': code = KeyEvent.VK_OPEN_BRACKET; break;
                    case '}': shift = true;
                    case ']': code = KeyEvent.VK_CLOSE_BRACKET; break;
                    case ':': shift = true;
                    case ';': code = KeyEvent.VK_SEMICOLON; break;
                    case '"': shift = true;
                    case '\'': code = KeyEvent.VK_QUOTE; break;
                    case '<': shift = true;
                    case ',': code = KeyEvent.VK_COMMA; break;
                    case '>': shift = true;
                    case '.': code = KeyEvent.VK_PERIOD; break;
                    case '?': shift = true;
                    case '/': code = KeyEvent.VK_SLASH; break;
                    case '|': shift = true;
                    case '\\': code = KeyEvent.VK_BACK_SLASH; break;
                    case '!': shift = true; code = KeyEvent.VK_1; break;
                    case '@': shift = true; code = KeyEvent.VK_2; break;
                    case '#': shift = true; code = KeyEvent.VK_3; break;
                    case '$': shift = true; code = KeyEvent.VK_4; break;
                    case '%': shift = true; code = KeyEvent.VK_5; break;
                    case '^': shift = true; code = KeyEvent.VK_6; break;
                    case '&': shift = true; code = KeyEvent.VK_7; break;
                    case '*': shift = true; code = KeyEvent.VK_8; break;
                    case '(': shift = true; code = KeyEvent.VK_9; break;
                    case ')': shift = true; code = KeyEvent.VK_0; break;                        
                    default: code = KeyEvent.VK_SPACE; break;
                }
            }
            if (shift) {
                super.keyPress(KeyEvent.VK_SHIFT);
            }
            super.keyPress(code);
            super.keyRelease(code);
            if (shift) {
                super.keyRelease(KeyEvent.VK_SHIFT);
            }
        }
    }

    public static int getKeyCode(Character c) {
        try {
            Field f = KeyEvent.class.getField("VK_" + Character.toUpperCase(c));
            f.setAccessible(true);
            try {
                return (Integer) f.get(null);
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                Logger.getLogger(SmartRobot.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (NoSuchFieldException | SecurityException ex) {
            Logger.getLogger(SmartRobot.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    
    public static int getKeyCode(String s) {
        int code = 0;
        if (s.equals("tab")) { code = KeyEvent.VK_TAB; }
        if (s.equals("enter")) { code = KeyEvent.VK_ENTER; }
        if (s.equals("forward")) { code = KeyEvent.VK_F3; }
        if (s.equals("back")) { code = KeyEvent.VK_F9; }
        if (s.equals("backspace")) { code = KeyEvent.VK_BACK_SPACE; }
        if (s.equals("home")) { code = KeyEvent.VK_HOME; }
        if (s.equals("end")) { code = KeyEvent.VK_END; }
        if (s.equals("up")) { code = KeyEvent.VK_UP; }
        if (s.equals("down")) { code = KeyEvent.VK_DOWN; }
        if (s.equals("left")) { code = KeyEvent.VK_LEFT; }
        if (s.equals("right")) { code = KeyEvent.VK_RIGHT; }
        if (s.equals("capslock")) { code = KeyEvent.VK_CAPS_LOCK; }
        if (s.equals("ctrl")) { code = KeyEvent.VK_CONTROL; }
        if (s.equals("shift")) { code = KeyEvent.VK_SHIFT; }
        if (s.equals("alt")) { code = KeyEvent.VK_ALT; }
        if (s.equals("escape")) { code = KeyEvent.VK_ESCAPE; }
        if (s.equals("f1")) { code = KeyEvent.VK_F1; }
        if (s.equals("f2")) { code = KeyEvent.VK_F2; }
        if (s.equals("f3")) { code = KeyEvent.VK_F3; }
        if (s.equals("f4")) { code = KeyEvent.VK_F4; }
        if (s.equals("f5")) { code = KeyEvent.VK_F5; }
        if (s.equals("f6")) { code = KeyEvent.VK_F6; }
        if (s.equals("f7")) { code = KeyEvent.VK_F7; }
        if (s.equals("f8")) { code = KeyEvent.VK_F8; }
        if (s.equals("f9")) { code = KeyEvent.VK_F9; }
        if (s.equals("f10")) { code = KeyEvent.VK_F10; }
        if (s.equals("f11")) { code = KeyEvent.VK_F11; }
        if (s.equals("f12")) { code = KeyEvent.VK_F12; }
        if (s.equals("insert")) { code = KeyEvent.VK_INSERT; }
        if (s.equals("delete")) { code = KeyEvent.VK_DELETE; }
        if (s.equals("pageup")) { code = KeyEvent.VK_PAGE_UP; }
        if (s.equals("pagedown")) { code = KeyEvent.VK_PAGE_DOWN; }
        if (s.equals("super")) { code = KeyEvent.VK_WINDOWS; }
        return code;
    }
}