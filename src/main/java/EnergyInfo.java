

public final class EnergyInfo {
    private final int from;
    private final double[][] energy;


    private EnergyInfo() {
        this.energy = null;
        this.from = 0;
    }

    public EnergyInfo(double[][] energy, int from) {
        this.energy = energy;
        this.from = from;
    }

    public synchronized int from() { return from; }

    public synchronized double[][] energy() { return energy; }
}
