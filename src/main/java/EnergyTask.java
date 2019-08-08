import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;

public class EnergyTask<V> implements Callable<V> {
    private volatile Picture img;
    private int imgW;
    private int imgH;
    private int from;
    private int to;
    private CyclicBarrier barrier;
    private volatile double[][] energy;

    public EnergyTask(CyclicBarrier barrier, Picture img, int from, int to) {
        //super(runnable, result);
        this.barrier = barrier;
        this.img = img;
        this.imgW = img.width();
        this.imgH = img.height();
        this.from = from;
        this.to = to;
    }

    @Override
    public V call() throws Exception {
        energy = new double[to-from][imgH];
        for (int i = from; i < to; i++) {
            for (int j = 0; j < imgH; i++) {
                energy[i][j] = energyAt(i, j);
            }
        }

        try {
            barrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
            System.out.println("BrokenBarrierException");
        }
        return (V) energy;
    }


    private double energyAt(int x, int y) {
        // bound check
        if (x < 0 || x > imgW - 1) {
            throw new IllegalArgumentException("idx x out of range");
        }
        if (y < 0 || y > imgH - 1) {
            throw new IllegalArgumentException("idx y out of range");
        }

        // border
        if (x == 0 || x == imgW - 1 || y == 0 || y == imgH - 1) {
            return 1000.0;
        }

        // interior
        int colorUpper = img.getRGB(x, y - 1);
        int colorBelow = img.getRGB(x, y + 1);
        int colorLeft = img.getRGB(x - 1, y);
        int colorRight = img.getRGB(x + 1, y);
        double delta = 0.0;

        // red
        delta += Math.pow(((colorLeft >> 16) & 0xFF) - ((colorRight >> 16) & 0xFF), 2);
        delta += Math.pow(((colorUpper >> 16) & 0xFF) - ((colorBelow >> 16) & 0xFF), 2);

        // green
        delta += Math.pow(((colorLeft >> 8) & 0xFF) - ((colorRight >> 8) & 0xFF), 2);
        delta += Math.pow(((colorUpper >> 8) & 0xFF) - ((colorBelow >> 8) & 0xFF), 2);

        // blue
        delta += Math.pow((colorLeft & 0xFF) - (colorRight & 0xFF), 2);
        delta += Math.pow((colorUpper & 0xFF) - (colorBelow & 0xFF), 2);

        // return
        delta = Math.sqrt(delta);
        return delta;
    }
}
