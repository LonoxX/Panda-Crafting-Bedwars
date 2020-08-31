package de.pandacrafting.mc.countdowns;

import com.google.inject.Inject;
import de.pandacrafting.mc.main.Main;
import org.bukkit.Bukkit;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class EndingCountdown extends Countdown {

    @Inject
    private final Main instance;
    private final int endingSeconds;
    private volatile ScheduledExecutorService scheduler;
    private int seconds;
    private boolean running;

    {
        scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    @Inject
    public EndingCountdown(Main instance) {
        this.instance = instance;
        endingSeconds = instance.getCacheContainer().getEndingCountdownLength();
        seconds = endingSeconds;
    }

    @Override
    public void run() {
        if(!running)  {
            running = true;
            Runnable runnable = () -> {
                switch(seconds) {
                    case 10: case 5: case 4: case 3: case 2:
                        Bukkit.broadcastMessage(Main.PREFIX + "Der Server stoppt in §e" + seconds + " §rSekunden!");
                        break;
                    case 1:
                        Bukkit.broadcastMessage(Main.PREFIX + "Der Server stoppt in §eeiner Sekunde!");
                        break;
                    case 0:
                        Bukkit.broadcastMessage(Main.PREFIX + "Der Server stoppt §ejetzt!");
                        instance.getGameStateManager().stopGameStates();
                    default:
                        break;
                }
                seconds--;
            };
            ScheduledFuture<?> task = scheduler.scheduleWithFixedDelay(runnable, 0, 1, TimeUnit.SECONDS);
            Runnable canceler = () -> task.cancel(false);
            scheduler.schedule(canceler, endingSeconds+1, TimeUnit.SECONDS);
        }
    }

    @Override
    public void cancel() {
        if(running = true) {
            if(!scheduler.isShutdown()) {
                scheduler.shutdownNow();
                running = false;
                seconds = endingSeconds;
            }
        }
    }

}
