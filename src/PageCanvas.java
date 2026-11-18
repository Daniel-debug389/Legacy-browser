import javax.microedition.lcdui.*;
import java.util.*;

public class PageCanvas extends Canvas {
    Browser midlet;
    Font font = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL);
    Vector items = new Vector();
    Vector rows = new Vector();      // String
    Vector rowLink = new Vector();   // Integer link index or -1
    Vector linkUrls = new Vector();  // String
    Vector linkRow = new Vector();   // Integer first row of link
    int scroll = 0, sel = -1;
    String status = "";

    public PageCanvas(Browser m) {
        midlet = m;
        setFullScreenMode(false);
    }

    void setMessage(String s) {
        items = new Vector();
        items.addElement(new String[] { "T", s, null });
        layout();
        repaint();
    }

    void setStatus(String s) {
        status = s;
        repaint();
    }

    void setItems(Vector it, String st) {
        items = it;
        status = st;
        scroll = 0;
        sel = -1;
        layout();
        repaint();
    }

    protected void sizeChanged(int w, int h) {
        layout();
    }

    void layout() {
        rows = new Vector();
        rowLink = new Vector();
        linkUrls = new Vector();
        linkRow = new Vector();
        int w = getWidth() - 4;
        for (int i = 0; i < items.size(); i++) {
            String[] it = (String[]) items.elementAt(i);
            int li = -1;
            if (it[0].equals("L")) {
                li = linkUrls.size();
                linkUrls.addElement(it[2]);
                linkRow.addElement(new Integer(rows.size()));
            }
            wrap(it[1], w, li);
        }
    }

    void wrap(String text, int w, int li) {
        StringBuffer line = new StringBuffer();
        int pos = 0;
        int len = text.length();
        while (pos < len) {
            int sp = text.indexOf(' ', pos);
            if (sp < 0) sp = len;
            String word = text.substring(pos, sp);
            pos = sp + 1;
            while (font.stringWidth(word) > w) {
                int k = word.length();
                while (k > 1 && font.stringWidth(word.substring(0, k)) > w) k--;
                if (line.length() > 0) addRow(line.toString(), li);
                line.setLength(0);
                addRow(word.substring(0, k), li);
                word = word.substring(k);
            }
            String cand = line.length() == 0 ? word : line.toString() + " " + word;
            if (font.stringWidth(cand) > w && line.length() > 0) {
                addRow(line.toString(), li);
                line.setLength(0);
                line.append(word);
            } else {
                line.setLength(0);
                line.append(cand);
            }
        }
        if (line.length() > 0) addRow(line.toString(), li);
    }

    void addRow(String s, int li) {
        rows.addElement(s);
        rowLink.addElement(new Integer(li));
    }

    int visibleRows() {
        int v = (getHeight() - font.getHeight() - 2) / font.getHeight();
        return v < 1 ? 1 : v;
    }

    protected void paint(Graphics g) {
        int w = getWidth(), h = getHeight(), fh = font.getHeight();
        g.setColor(0xFFFFFF);
        g.fillRect(0, 0, w, h);
        g.setFont(font);
        int vis = visibleRows();
        for (int i = 0; i < vis; i++) {
            int r = scroll + i;
            if (r >= rows.size()) break;
            int li = ((Integer) rowLink.elementAt(r)).intValue();
            int y = i * fh;
            if (li >= 0 && li == sel) {
                g.setColor(0x000080);
                g.fillRect(0, y, w, fh);
                g.setColor(0xFFFFFF);
            } else if (li >= 0) {
                g.setColor(0x0000CC);
            } else {
                g.setColor(0x000000);
            }
            g.drawString((String) rows.elementAt(r), 2, y, Graphics.TOP | Graphics.LEFT);
        }
        g.setColor(0xDDDDDD);
        g.fillRect(0, h - fh - 1, w, fh + 1);
        g.setColor(0x000000);
        g.drawString(status, 2, h - fh, Graphics.TOP | Graphics.LEFT);
    }

    int rowOfLink(int l) {
        return ((Integer) linkRow.elementAt(l)).intValue();
    }

    protected void keyPressed(int key) {
        int ga = getGameAction(key);
        int vis = visibleRows();
        if (key == KEY_NUM1) { midlet.prevPage(); return; }
        if (key == KEY_NUM3) { midlet.nextPage(); return; }
        if (key == KEY_NUM2) ga = UP;
        if (key == KEY_NUM8) ga = DOWN;
        if (key == KEY_NUM5) ga = FIRE;
        if (key == KEY_NUM4) ga = LEFT;
        if (key == KEY_NUM6) ga = RIGHT;
        if (ga == DOWN) {
            if (sel + 1 < linkUrls.size() && rowOfLink(sel + 1) < scroll + vis) {
                sel++;
            } else if (scroll + vis < rows.size()) {
                scroll += 2;
                if (scroll > rows.size() - 1) scroll = rows.size() - 1;
            }
        } else if (ga == UP) {
            if (sel - 1 >= 0 && rowOfLink(sel - 1) >= scroll) {
                sel--;
            } else if (scroll > 0) {
                scroll -= 2;
                if (scroll < 0) scroll = 0;
            }
        } else if (ga == FIRE) {
            if (sel >= 0) {
                int r = rowOfLink(sel);
                if (r >= scroll && r < scroll + vis)
                    midlet.openLink((String) linkUrls.elementAt(sel));
            }
        } else if (ga == LEFT) {
            midlet.prevPage();
        } else if (ga == RIGHT) {
            midlet.nextPage();
        }
        repaint();
    }

    protected void keyRepeated(int key) {
        keyPressed(key);
    }
}
