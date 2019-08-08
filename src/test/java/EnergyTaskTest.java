import org.junit.Test;

import static org.junit.Assert.assertNull;

public class EnergyTaskTest {
    @Test
    public void testCall() throws Exception {
        EnergyTask task = new EnergyTask(null, null, 0, 0);
        assertNull(task.call());
    }
}