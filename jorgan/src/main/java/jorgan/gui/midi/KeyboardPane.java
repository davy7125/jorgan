/*
 * jOrgan - Java Virtual Organ
 * Copyright (C) 2003 Sven Meier
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package jorgan.gui.midi;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;

import jorgan.midi.KeyboardProvider;

/**
 * A keyboard. 
 */
public class KeyboardPane extends JComponent {

    protected static final ResourceBundle resources = ResourceBundle.getBundle("jorgan.gui.resources");

    private static final int C   = 0; 
    private static final int CIS = 1; 
    private static final int D   = 2; 
    private static final int DIS = 3; 
    private static final int E   = 4; 
    private static final int F   = 5; 
    private static final int FIS = 6; 
    private static final int G   = 7; 
    private static final int GIS = 8; 
    private static final int A   = 9; 
    private static final int AIS = 10; 
    private static final int B   = 11; 

    private static final int whiteWidth  = 12;
    private static final int whiteHeight = 54;   
    
    private static final int blackWidth  = 8;
    private static final int blackHeight = 31;

    private JPopupMenu  popupMenu;
    private JMenuItem[] channelMenuItems = new JCheckBoxMenuItem[16];
    private JMenuItem   velocityMenuItem;
    private JMenuItem   polyPressureMenuItem;
    
    private int     channel          = 0;
    private boolean sendVelocity     = true;
    private boolean sendPolyPressure = true;
    private boolean sendAftertouch   = true;
    
    private List keys = new ArrayList();
    
    private Receiver receiver;
    
    public KeyboardPane() {
        MouseHandler handler = new MouseHandler(); 
        addMouseListener      (handler);
        addMouseMotionListener(handler);
        
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        int x = 0;
        for (int pitch = 0; pitch < 128; pitch++) {
            switch (mod(pitch, 12)) {
                case C:
                    keys.add(0, new WhiteKey(pitch, x));
                    x += whiteWidth;
                    break;
                case CIS:
                    keys.add(new BlackKey(pitch, x - blackWidth*5/8));
                    break;
                case D:
                    keys.add(0, new WhiteKey(pitch, x));
                    x += whiteWidth;
                    break;
                case DIS:
                    keys.add(new BlackKey(pitch, x - blackWidth*3/8));
                    break;
                case E:
                    keys.add(0, new WhiteKey(pitch, x));
                    x += whiteWidth;
                    break;
                case F:
                    keys.add(0, new WhiteKey(pitch, x));
                    x += whiteWidth;
                    break;
                case FIS:
                    keys.add(new BlackKey(pitch, x - blackWidth*6/8));
                    break;
                case G:
                    keys.add(0, new WhiteKey(pitch, x));
                    x += whiteWidth;
                    break;
                case GIS:
                    keys.add(new BlackKey(pitch, x - blackWidth*4/8));
                    break;
                case A:
                    keys.add(0, new WhiteKey(pitch, x));
                    x += whiteWidth;
                    break;
                case AIS:
                    keys.add(new BlackKey(pitch, x - blackWidth*2/8));
                    break;
                case B:
                    keys.add(0, new WhiteKey(pitch, x));
                    x += whiteWidth;
                    break;
            }
        }

        setMinimumSize(new Dimension(0, whiteHeight));
        setPreferredSize(new Dimension(x, whiteHeight));
        
        setReceiver(KeyboardProvider.getLoopback().loopbackReceiver);              
    }
    
    public void setReceiver(Receiver receiver) {
        clearKeys();       

        this.receiver = receiver;
    }

    public void clearKeys() {
        for (int k = 0; k < 128; k++) {
            Key key = (Key)keys.get(k);
            key.release();
        }

        repaint();
    }

    public void setChannel(int channel) {
        clearKeys();       

        this.channel = channel;
    }
    
    public int getChannel() {
        return channel;
    }
    
    public void setSendVelocity(boolean sendVelocity) {
        this.sendVelocity = sendVelocity;
    }
    
    public boolean getSendVelocity() {
        return sendVelocity;
    }
    
    public void setSendPolyPressure(boolean sendPolyPressure) {
        this.sendPolyPressure = sendPolyPressure;
    }
    
    public boolean getSendPolyPressure() {
        return sendPolyPressure;
    }
    
    public void setSendAftertouch(boolean sendAftertouch) {
        this.sendAftertouch = sendAftertouch;
    }
    
    public boolean getSendAftertouch() {
        return sendAftertouch;
    }
    
    /**
     * Paint the keys.
     */
    protected void paintComponent(Graphics g) {

        Dimension preferredSize = getPreferredSize();
        
        g.translate((getWidth () - preferredSize.width ) / 2,
                    (getHeight() - preferredSize.height) / 2);
        for (int k = 0; k < 128; k++) {
            Key key = (Key)keys.get(k);
            key.paint(g);
        }
    }
    
    private class MouseHandler extends MouseInputAdapter {
        
        private Key     key;
        private boolean wasPressed;
        
        public void mousePressed(MouseEvent e) {
          
            Dimension preferredSize = getPreferredSize();
            
            int x = e.getX() - (getWidth () - preferredSize.width ) / 2;
            int y = e.getY() - (getHeight() - preferredSize.height) / 2;

            setKey(getKey(x, y), y);

            showPopup(e);
        }

        public void mouseDragged(MouseEvent e) {

            Dimension preferredSize = getPreferredSize();

            int x = e.getX() - (getWidth () - preferredSize.width ) / 2;
            int y = e.getY() - (getHeight() - preferredSize.height) / 2;

            setKey(getKey(x, y), y);
        }

        public void mouseReleased(MouseEvent e) {
            if (e.isControlDown() && !wasPressed) {
                key = null;
            } else {
                setKey(null, 0); 
            }
            
            showPopup(e);        
        }

        private void showPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                KeyboardPane.this.showPopup(e.getX(), e.getY());
            }
        }

        private void setKey(Key key, int y) {
            if (this.key != key) {
                if (this.key != null) {
                    this.key.release();
                }
            
                this.key = key;
           
                if (this.key != null) {
                    wasPressed = this.key.pressed;
                    this.key.press(y);
                }                
            } else {
                if (this.key != null) {
                    this.key.drag(y);
                }
            }
        }        
    }

    private Key getKey(int x, int y) {

        for (int k = 128 - 1; k >= 0; k--) {
            Key key = (Key)keys.get(k);
            if (key.hits(x, y)) {
                return key;
            }
        }
        return null;
    }
    
    private void send(int command, int data1, int data2) {
        try {
            ShortMessage message = new ShortMessage();
            message.setMessage(command, channel, data1, data2);
    
            if (receiver != null) {
                receiver.send(message, -1);
            }
        } catch (InvalidMidiDataException ex) {
            throw new Error(ex);
        }
    }

    protected void showPopup(int x, int y) {
        if (popupMenu == null) {
            popupMenu = createPopup();
        }

        channelMenuItems[channel].setSelected(true);        
        velocityMenuItem    .setSelected(sendVelocity);
        polyPressureMenuItem.setSelected(sendPolyPressure);
        
        popupMenu.show(this, x, y);    
    }

    protected JPopupMenu createPopup() {
        JPopupMenu popupMenu = new JPopupMenu();

        ButtonGroup channelGroup = new ButtonGroup(); 
        ChannelHandler channelHandler = new ChannelHandler();
        for (int c = 0; c < 16; c++) {
            channelMenuItems[c] = new JCheckBoxMenuItem(resources.getString("keyboard.channel") + " " + (c + 1));
            channelMenuItems[c].getModel().setGroup(channelGroup);
            channelMenuItems[c].putClientProperty(channelHandler, new Integer(c));
            channelMenuItems[c].addItemListener(channelHandler);
            popupMenu.add(channelMenuItems[c]);
        }

        popupMenu.addSeparator();

        velocityMenuItem = new JCheckBoxMenuItem(resources.getString("keyboard.velocity"));
        velocityMenuItem.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                sendVelocity = velocityMenuItem.isSelected();
            }
        });
        popupMenu.add(velocityMenuItem);

        polyPressureMenuItem = new JCheckBoxMenuItem(resources.getString("keyboard.polyPressure"));
        polyPressureMenuItem.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                sendPolyPressure = polyPressureMenuItem.isSelected();
            }
        });
        popupMenu.add(polyPressureMenuItem);
       
        return popupMenu;
    }
    
    private abstract class Key {
        protected int pitch;
        protected int x;
        protected int width;
        protected int height;
        protected boolean pressed;

        public Key(int pitch, int x, int width, int height) {
            this.pitch  = pitch;
            this.x      = x;
            this.width  = width;
            this.height = height;
        }
        
        public void press(int y) {
            if (!pressed) {
                int velocity = 100;
                if (sendVelocity) {
                    velocity = 127 * y / height;
                }
                send(ShortMessage.NOTE_ON, pitch, velocity);
                pressed = true;
                repaint();
            }
        }

        public void drag(int y) {
            if (pressed) {
                int pressure = 127 * y / height;
                if (sendPolyPressure) {
                    send(ShortMessage.POLY_PRESSURE   , pitch   , pressure);
                }
                if (sendAftertouch) {
                    send(ShortMessage.CHANNEL_PRESSURE, pressure, 0);
                }
            }
        }

        public void release() {
            if (pressed) {
                send(ShortMessage.NOTE_OFF, pitch, 0);
                //send(ShortMessage.NOTE_ON, pitch, 0);
                pressed = false;
                repaint();
            }
        }

        public boolean hits(int x, int y) {
            return x >= this.x && x < this.x + this.width &&
                   y >= 0      && y < this.height;         
        }
        
        public abstract void paint(Graphics g);
    }
    
    private class BlackKey extends Key {
        
        public BlackKey(int pitch, int x) {
            super(pitch, x, blackWidth, blackHeight);
        }

        public void paint(Graphics g) {
            g.setColor(Color.BLACK);
            g.fillRect(x, 0, blackWidth, blackHeight);

            if (pressed) {
                g.setColor(Color.GRAY);
                g.fillRect(x + 1, 1, blackWidth - 2, blackHeight - 2);
            } else {
                g.setColor(Color.DARK_GRAY);
                g.drawLine(x + 1, 3, x + 1, blackHeight - 3);

                g.setColor(Color.GRAY);
                g.fillRect(x + 1, blackHeight - 3, blackWidth - 2, 2);
            }
        }
    }

    private class WhiteKey extends Key {
        public WhiteKey(int pitch, int x) {
            super(pitch, x, whiteWidth, whiteHeight);
        }
 
        public void paint(Graphics g) {
            if (pressed) {
                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(x, 0, whiteWidth, whiteHeight);
            } else {
                g.setColor(Color.WHITE);
                g.fillRect(x, 0, whiteWidth, whiteHeight);

                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(x, whiteHeight - 3, whiteWidth, 3);
            }
            g.setColor(Color.BLACK);
            g.drawRect(x, 0, whiteWidth - 1, whiteHeight - 1);

            g.setColor(Color.GRAY);
            g.drawLine(x, 1, x, whiteHeight - 2);
            
            if (pitch == 60) {
                g.setColor(Color.BLACK);
                g.fillOval(x + whiteWidth/2 - 2, whiteHeight - 8, 4, 4);
            }
        }
    }
    
    private class ChannelHandler implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem)e.getSource();
            
            Integer channel = (Integer)menuItem.getClientProperty(this); 
            setChannel(channel.intValue());
        }    
    }
    
    private static int mod(int x, int y) {
        return x - ((x / y) * y);
    }    
}