package tunnel;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Programmer
 * Date: 04.05.2007
 * Time: 3:25:41
 * To change this template use File | Settings | File Templates.
 */
class SmallBeePair {
    public SmallBee beeIn;
    public SmallBee beeOut;

    public SmallBeePair(SmallBee beeIn, SmallBee beeOut) {
        this.beeIn = beeIn;
        this.beeOut = beeOut;
    }


    public SmallBee getBeeIn() {
        return beeIn;
    }

    public SmallBee getBeeOut() {
        return beeOut;
    }

    ImageIcon red = new ImageIcon(PaneOfLogUI.class.getClassLoader().getResource("red.jpg"));
    ImageIcon green = new ImageIcon(PaneOfLogUI.class.getClassLoader().getResource("green.jpg"));
    ImageIcon yellow = new ImageIcon(PaneOfLogUI.class.getClassLoader().getResource("yellow.jpg"));

    public ImageIcon getLifePresentattion() {
        if (beeIn.isTerminated() && beeOut.isTerminated())
            return red;
        if ((!beeIn.isTerminated()) && (!beeOut.isTerminated()))
            return green;
        return yellow;
    }
}
