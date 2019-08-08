import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * EnergyCalculator
 * Calculate energy of pixels for input image
 */

public class EnergyCalculator {
    private Picture img;
    private int imgW;
    private int imgH;
    private volatile double[][] energy;

    private static final int CPU_NUMS = Runtime.getRuntime().availableProcessors();
    private final ExecutorService exec = Executors.newFixedThreadPool(CPU_NUMS+1);

    public EnergyCalculator(Picture img) {
        this.img = img;
        this.imgH = img.height();
        this.imgW = img.width();
    }


    public void start() throws ExecutionException, InterruptedException {
        int nBlock = (int) Math.round(imgW/400.0);
        CyclicBarrier barrier = new CyclicBarrier(nBlock);
        Future[] futures = new Future[nBlock];

        int blockSize = imgW/nBlock + 1;
        for (int i = 0; i < nBlock; i++) {
            int from = i * blockSize;
            int to = from + blockSize;
            if (to > imgH - 1) to = imgH - 1;
            EnergyTask task = new EnergyTask(barrier, img, from, to);
            futures[i] = exec.submit(task);
        }

        for (Future future: futures) {
            double[][] ret = (double[][]) future.get();
        }
        exec.shutdown();
    }
}
