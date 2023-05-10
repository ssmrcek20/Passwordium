import javax.swing.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.Date;
import java.util.EventListener;
import java.util.Timer;
import java.util.TimerTask;

public class LoginNadzornik {
    private int brojPokusaja;

    private final Timer timer;

    LoginNadzornik() {
        brojPokusaja=0;
        timer = new Timer();
    }
    public void neuspjeliPokusaj(){
        brojPokusaja++;
    }

    public boolean viseOdTriPokusaja(){
        return brojPokusaja>=3;
    }

    public int brojNeuspjelihPokusaja(){
        return brojPokusaja;
    }

    public void zakljucajLogin(JButton gumb){
        gumb.setEnabled(false);
        postaviBrojac(gumb);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                otkljucajLogin(gumb);
            }
        }, 60000);
    }

    private void otkljucajLogin(JButton gumb){
        gumb.setEnabled(true);
        brojPokusaja=0;
    }

    public void postaviBrojac(JButton gumb){
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            private int sekunde = 60;
            @Override
            public void run() {
                mijenjajTekst(sekunde,timer,gumb);
                sekunde--;
            }
        },0,1000);
    }

    private void mijenjajTekst(int sekunde, Timer timer, JButton gumb){
        gumb.setText(String.valueOf(sekunde));
        if(sekunde<1){
            timer.cancel();
            gumb.setText("Prijavi se");
        }
    }
}
