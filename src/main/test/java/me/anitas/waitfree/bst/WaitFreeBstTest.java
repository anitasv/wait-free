package me.anitas.waitfree.bst;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertEquals;

public class WaitFreeBstTest {

    private WaitFreeBst<Integer, Integer> bstUnderTest;

    @BeforeMethod
    public void setUp() throws Exception {
        bstUnderTest = new WaitFreeBst<>(10);
    }

    @Test
    public void testPutAndGet() {
        bstUnderTest.put(3, 4);

        assertEquals(bstUnderTest.get(3), (Integer)4);
    }

    @Test
    public void testNineThreads() throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(9);
        for (int i = 0; i < 9; i++) {
            int j = i;
            service.submit(() -> bstUnderTest.put(j, j * 2));
        }
        service.shutdown();
        service.awaitTermination(100, TimeUnit.MILLISECONDS);

        for (int i = 0; i < 9; i++) {
            assertEquals(bstUnderTest.get(i), (Integer) (i * 2));
        }
    }
}
