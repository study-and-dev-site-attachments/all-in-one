package tunnel;


public interface UISummator {
    public abstract void receiveByte(int b);

    public abstract void fireThreadStatusChanged(SmallBee smallb);
}
