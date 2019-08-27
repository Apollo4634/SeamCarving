import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

/**
 * EnergyCalculator
 * Calculate energy of pixels for input image
 */

public class EnergyCalculator {

    private final Picture img;
    private final int imgW;
    private final int imgH;
    private volatile double[][] energy;

    private static final int BLOCK_SIZE = 450000;
    private static final int CPU_NUMS = Runtime.getRuntime().availableProcessors();
    private static final ExecutorService executor = Executors.newFixedThreadPool(CPU_NUMS+1);
    private static final Semaphore semaphore = new Semaphore(CPU_NUMS+1);


    EnergyCalculator(Picture img) {
        this.img = img;
        this.imgH = img.height();
        this.imgW = img.width();
        this.energy = new double[imgW][imgH];
    }

    synchronized double[][] energy() {
        return energy;
    }

    void start() throws InterruptedException {
        energy = new double[imgW][imgH];

        int nBlock = (int) Math.round(1.0*imgW*imgH/BLOCK_SIZE);
        System.out.println("nBlock "+nBlock);
        CyclicBarrier barrier = new CyclicBarrier(nBlock);

        ExecutorCompletionService<EnergyInfo> completionService =
                new ExecutorCompletionService<>(executor);

        int blockWidth = imgW/nBlock + 1;
        for (int i = 0; i < nBlock; i++) {
            int from = i * blockWidth;
            int to = from + blockWidth;
            if (to > imgW) to = imgW;
            EnergyTask task = new EnergyTask(semaphore, img, from, to);
            semaphore.acquire();
            completionService.submit(task);
        }

        for (int i = 0; i < nBlock; i++) {
            Future<EnergyInfo> ret = completionService.take();
            try {
                EnergyInfo info = ret.get();
                System.arraycopy(info.energy(), 0, energy, info.from(), info.energy().length);
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();
    }
}
