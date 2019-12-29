import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ThreadMonitor {

    public static void createBusyThread() {
        Thread thread = new Thread(() -> {
            while (true) ;
        },"BusyThread");
        thread.start();
    }

    public static void createLockThread(final Byte lock) {
        Thread thread = new Thread(() -> {
            synchronized (lock) {
                try {
                    lock.wait();
                    System.out.println("lock get, I am running");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"TestLockThread");
        thread.start();
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
        br.readLine();
        createBusyThread();
        br.readLine();
        Byte lock=1;
        createLockThread(lock);

        br.readLine();
        lock.notifyAll();
    }


}
