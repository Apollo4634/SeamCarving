import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;

public class EnergyTask implements Callable<EnergyInfo> {

    private final Picture img;
    private final int imgW;
    private final int imgH;
    private final int from;
    private final int to;
    private final Semaphore semaphore;
    private EnergyInfo info;
    private double[][] energy;

    public EnergyTask(Semaphore semaphore, Picture img, int from, int to) {
        this.semaphore = semaphore;
        this.img = img;
        this.imgW = img.width();
        this.imgH = img.height();
        this.from = from;
        this.to = to;

        //info = new EnergyInfo(null, from);
    }

    @Override
    public EnergyInfo call() throws Exception {
        try {
            energy = new double[to-from][imgH];
            for (int i = from; i < to; i++) {
                for (int j = 0; j < imgH; j++) {
                    energy[i-from][j] = energyAt(i, j);
                }
            }
            info = new EnergyInfo(energy, from);
        } catch (RejectedExecutionException e) {
            info = null;
        } finally {
            semaphore.release();
        }
        return info;
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
