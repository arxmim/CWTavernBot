package org.nia.bots;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

/**
 * @author Иван, 09.03.2017.
 */
public class OficiantThread implements Runnable {
    private volatile boolean barmenCommand;

    @Override
    public void run() {
        while (true) {
            try {
                GregorianCalendar gcWas = new GregorianCalendar();
                gcWas.setTime(new Date());
                TimeUnit.SECONDS.sleep(3);
                if (timedStart(gcWas) || barmenCommand) {
                    barmenCommand = false;
                    doWork();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private boolean timedStart(GregorianCalendar gcWas) {
        GregorianCalendar gcNow = new GregorianCalendar();
        gcNow.setTime(new Date());
        int INIT_MINUTE = 5;
        int INTERVAL = 10;
        return gcWas.get(GregorianCalendar.MINUTE) % INTERVAL == INIT_MINUTE - 1 && gcNow.get(GregorianCalendar.MINUTE) % INTERVAL == INIT_MINUTE;
    }

    private void doWork() {

    }

    public void setBarmenCommand(boolean barmenCommand) {
        this.barmenCommand = barmenCommand;
    }
}
