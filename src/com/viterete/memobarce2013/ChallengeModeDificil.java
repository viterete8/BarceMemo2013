package com.viterete.memobarce2013;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class ChallengeModeDificil extends Activity {
    private static boolean isswitch;
    private boolean pisdestroyed=false;
    private BackgroundSound mBackgroundSound=new BackgroundSound();
    private long segundos =-1, minutos =0;
    private View v1;
    private Card firstCard,secondCard;
    private static Timer myTimer,myTimer2;
    private int turnos,cont=0,contwinner=0,v1pos,puntaje=0,puntajef=0,cuentaregresiva=0,tiempoplayer=0,cancion=0;
    private Handler mHandler = new Handler();
    private TextView TVturnos,TVpuntaje,TVtiempo;
    private List<Integer> imagenes=new ArrayList<Integer>();
    private GridView gv;
    private GridAdapter adapter=new GridAdapter(this,20);
    private Jugador jugador;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_mode_dificil);
        jugador=new Jugador(ChallengeModeDificil.this);
        TVtiempo =(TextView)findViewById(R.id.TVCMtimer);
        TVpuntaje=(TextView)findViewById(R.id.TVCMpoints);
        TVturnos =(TextView)findViewById(R.id.TVCMturns);
        gv=(GridView) findViewById(R.id.GV1);
        gv.setNumColumns(4);
        if (savedInstanceState != null) {
            minutos = savedInstanceState.getLong("minutos");
            segundos = savedInstanceState.getLong("segundos");
            turnos =savedInstanceState.getInt("turnos");
            puntaje=savedInstanceState.getInt("puntaje");
            imagenes.add(savedInstanceState.getInt("imagen0"));
            imagenes.add(savedInstanceState.getInt("imagen1"));
            imagenes.add(savedInstanceState.getInt("imagen2"));
            imagenes.add(savedInstanceState.getInt("imagen3"));
            imagenes.add(savedInstanceState.getInt("imagen4"));
            imagenes.add(savedInstanceState.getInt("imagen5"));
            imagenes.add(savedInstanceState.getInt("imagen6"));
            imagenes.add(savedInstanceState.getInt("imagen7"));
            imagenes.add(savedInstanceState.getInt("imagen8"));
            imagenes.add(savedInstanceState.getInt("imagen9"));
            imagenes.add(savedInstanceState.getInt("imagen10"));
            imagenes.add(savedInstanceState.getInt("imagen11"));
            imagenes.add(savedInstanceState.getInt("imagen12"));
            imagenes.add(savedInstanceState.getInt("imagen13"));
            imagenes.add(savedInstanceState.getInt("imagen14"));
            imagenes.add(savedInstanceState.getInt("imagen15"));
            imagenes.add(savedInstanceState.getInt("imagen16"));
            imagenes.add(savedInstanceState.getInt("imagen17"));
            imagenes.add(savedInstanceState.getInt("imagen18"));
            imagenes.add(savedInstanceState.getInt("imagen19"));
        } else {
            SharedPreferences preferences =getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
            isswitch=preferences.getBoolean("sCancion", false);
            cancion=preferences.getInt("nCancion",0);
            loadImages();
            GridAdapterTemp adaptert=new GridAdapterTemp(this,20, imagenes.get(0), imagenes.get(1), imagenes.get(2), imagenes.get(3), imagenes.get(4), imagenes.get(5), imagenes.get(6), imagenes.get(7), imagenes.get(8), imagenes.get(9), imagenes.get(10), imagenes.get(11), imagenes.get(12), imagenes.get(13), imagenes.get(14), imagenes.get(15), imagenes.get(16), imagenes.get(17), imagenes.get(18), imagenes.get(19));
            gv.setAdapter(adaptert);
            gv.setEnabled(false);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    gv.setAdapter(adapter);
                    gv.setEnabled(true);
                }
            }, 750);
        }
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(final AdapterView<?> parent, final View v, int position,
                                    long id) {
                final ImageView ImageV=(ImageView) v;
                if(cont==0){
                    v1=v;
                    v1pos=position;

                    firstCard=new Card(imagenes.get(position));
                    ImageV.setImageResource(imagenes.get(position));
                    cont++;
                }
                else{
                    if(v1pos==position){
                        Toast.makeText(getApplicationContext(), "La misma carta fué seleccionada", Toast.LENGTH_SHORT).show();
                        turnos++;
                        TVturnos.setText(String.valueOf(turnos));
                    }
                    else
                    {
                        ImageV.setImageResource(imagenes.get(position));
                        secondCard=new Card(imagenes.get(position));
                        if(compare(firstCard.getId(),secondCard.getId())){
                            turnos++;
                            TVturnos.setText(String.valueOf(turnos));
                            parent.setEnabled(false);
                            mHandler.postDelayed(new Runnable(){
                                public void run() {
                                    parent.setEnabled(true);
                                    v.setVisibility(View.GONE);
                                    v1.setVisibility(View.GONE);
                                }}, 300);
                            contwinner++;
                            TVpuntaje.setText(String.valueOf(puntaje=puntaje+500));
                            CheckWin(contwinner);
                        }
                        else{
                            turnos++;
                            TVturnos.setText(String.valueOf(turnos));
                            final ImageView ImageV1=(ImageView) v1;
                            parent.setEnabled(false);
                            mHandler.postDelayed(new Runnable(){
                                public void run() {
                                    parent.setEnabled(true);
                                    ImageV.setImageResource(R.drawable.bsc14);
                                    ImageV1.setImageResource(R.drawable.bsc14);
                                }}, 300);
                        }
                        cont=0;
                    }

                }
            }

            private void CheckWin(int contwinner) {
                if(contwinner==10){
                    CalcularPuntaje();
                }
            }
        }
        );
        ResetTiempo();
    }

    private void CalcularPuntaje() {
        LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View vi = inflater.inflate(R.layout.custom_alert_dialog, null);
        TextView TVTturnos=(TextView)vi.findViewById(R.id.TVFturnos);
        TextView TVTtiempo=(TextView)vi.findViewById(R.id.TVFtiempo);
        TextView TVTpuntaje = (TextView)vi.findViewById(R.id.TVFpuntaje);
        TextView TVTbonoturnos=(TextView)vi.findViewById(R.id.TVFbonoturnos);
        TextView TVTbonotiempo=(TextView)vi.findViewById(R.id.TVFbonotiempo);
        TextView TVTpuntajetotal=(TextView)vi.findViewById(R.id.TVFpuntajetotal);
        TVTturnos.setText(String.valueOf(turnos));
        TVTpuntaje.setText(String.valueOf(puntaje));
        int bonotiempo = 0,bonoturnos=0;

        switch(turnos){
            case 10:bonoturnos= turnos *3000;
                TVTbonoturnos.setText(String.valueOf(turnos)+" x 3000");
                break;
            case 11:bonoturnos= turnos *2900;
                TVTbonoturnos.setText(String.valueOf(turnos)+" x 2900");
                break;
            case 12:bonoturnos= turnos *2800;
                TVTbonoturnos.setText(String.valueOf(turnos)+" x 2800");
                break;
            case 13:bonoturnos= turnos *2700;
                TVTbonoturnos.setText(String.valueOf(turnos)+" x 2700");
                break;
            case 14:bonoturnos= turnos *2600;
                TVTbonoturnos.setText(String.valueOf(turnos)+" x 2600");
                break;
            case 15:bonoturnos= turnos *2500;
                TVTbonoturnos.setText(String.valueOf(turnos)+" x 2500");
                break;
            case 16:bonoturnos= turnos *2400;
                TVTbonoturnos.setText(String.valueOf(turnos)+" x 2400");
                break;
            case 17:bonoturnos= turnos *2300;
                TVTbonoturnos.setText(String.valueOf(turnos)+" x 2300");
                break;
            case 18:bonoturnos= turnos *2200;
                TVTbonoturnos.setText(String.valueOf(turnos)+" x 2200");
                break;
            case 19:bonoturnos= turnos *2100;
                TVTbonoturnos.setText(String.valueOf(turnos)+" x 2100");
                break;
            case 20:bonoturnos= turnos *2000;
                TVTbonoturnos.setText(String.valueOf(turnos)+" x 2000");
                break;
            case 21:bonoturnos= turnos *1900;
                TVTbonoturnos.setText(String.valueOf(turnos)+" x 1900");
                break;
            case 22:bonoturnos= turnos *1800;
                TVTbonoturnos.setText(String.valueOf(turnos)+" x 1800");
                break;
            case 23:bonoturnos= turnos *1700;
                TVTbonoturnos.setText(String.valueOf(turnos)+" x 1700");
                break;
            case 24:bonoturnos= turnos *1600;
                TVTbonoturnos.setText(String.valueOf(turnos)+" x 1600");
                break;
            case 25:bonoturnos= turnos *1500;
                TVTbonoturnos.setText(String.valueOf(turnos)+" x 1500");
                break;
            default:bonoturnos=0;
                TVTbonoturnos.setText(String.valueOf(turnos)+" + 0");
                break;
        }
        if(minutos ==0){
            if(segundos <14){
                TVTtiempo.setText(String.valueOf(segundos)+" s");
                bonotiempo=8000;
                TVTbonotiempo.setText(String.valueOf(bonotiempo));
            }
            else if(segundos <16){
                TVTtiempo.setText(String.valueOf(segundos)+" s");
                bonotiempo=7000;
                TVTbonotiempo.setText(String.valueOf(bonotiempo));
            }
            else if(segundos <18){
                TVTtiempo.setText(String.valueOf(segundos)+" s");
                bonotiempo=6000;
                TVTbonotiempo.setText(String.valueOf(bonotiempo));
            }
            else if(segundos <20){
                TVTtiempo.setText(String.valueOf(segundos)+" s");
                bonotiempo=5000;
                TVTbonotiempo.setText(String.valueOf(bonotiempo));
            }
            else if(segundos <22){
                TVTtiempo.setText(String.valueOf(segundos)+" s");
                bonotiempo=4000;
                TVTbonotiempo.setText(String.valueOf(bonotiempo));
            }
            else if(segundos <24){
                TVTtiempo.setText(String.valueOf(segundos)+" s");
                bonotiempo=3000;
                TVTbonotiempo.setText(String.valueOf(bonotiempo));
            }
            else if(segundos <26){
                TVTtiempo.setText(String.valueOf(segundos)+" s");
                bonotiempo=2000;
                TVTbonotiempo.setText(String.valueOf(bonotiempo));
            }
            else if(segundos <28){
                TVTtiempo.setText(String.valueOf(segundos)+" s");
                bonotiempo=1000;
                TVTbonotiempo.setText(String.valueOf(bonotiempo));
            }
            else if(segundos <30){
                TVTtiempo.setText(String.valueOf(segundos)+" s");
                bonotiempo=900;
                TVTbonotiempo.setText(String.valueOf(bonotiempo));
            }
            else if(segundos <32){
                TVTtiempo.setText(String.valueOf(segundos)+" s");
                bonotiempo=800;
                TVTbonotiempo.setText(String.valueOf(bonotiempo));
            }
            else if(segundos <34){
                TVTtiempo.setText(String.valueOf(segundos)+" s");
                bonotiempo=700;
                TVTbonotiempo.setText(String.valueOf(bonotiempo));
            }
            else if(segundos <38){
                TVTtiempo.setText(String.valueOf(segundos)+" s");
                bonotiempo=600;
                TVTbonotiempo.setText(String.valueOf(bonotiempo));
            }
            else if(segundos <40){
                TVTtiempo.setText(String.valueOf(segundos)+" s");
                bonotiempo=550;
                TVTbonotiempo.setText(String.valueOf(bonotiempo));
            }
            else if(segundos <50){
                TVTtiempo.setText(String.valueOf(segundos)+" s");
                bonotiempo=500;
                TVTbonotiempo.setText(String.valueOf(bonotiempo));
            }
            else if(segundos <55){
                TVTtiempo.setText(String.valueOf(segundos)+" s");
                bonotiempo=450;
                TVTbonotiempo.setText(String.valueOf(bonotiempo));
            }
            else{
                TVTtiempo.setText(String.valueOf(segundos)+" s");
                bonotiempo=300;
                TVTbonotiempo.setText(String.valueOf(bonotiempo));
            }
        }
        else{
            if(segundos <10 && minutos >0){
                TVTtiempo.setText(String.valueOf(minutos)+":0"+String.valueOf(segundos)+" s");
                bonotiempo=500;
                TVTbonotiempo.setText(String.valueOf(bonotiempo));
            }
            else{
                TVTtiempo.setText(String.valueOf(minutos)+":"+String.valueOf(segundos)+" s");
                bonotiempo=100;
                TVTbonotiempo.setText(String.valueOf(bonotiempo));
            }
        }

        puntajef=bonotiempo+bonoturnos+puntaje;
        TVTpuntajetotal.setText(String.valueOf(puntajef));
        insertarpuntaje(puntajef);
        AlertDialog.Builder dialog = new AlertDialog.Builder(ChallengeModeDificil.this);
        dialog.setView(vi);
        dialog.setNegativeButton("Comparte", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "Mi puntuación en Barce Memo 2013 fue: "+String.valueOf(puntajef)+" , Quieres superarme? Bajate la aplicación aquí https://play.google.com/store/apps/details?id=com.viterete.memobarce2013 ");
                startActivity(Intent.createChooser(intent, "Comparte"));

            }

        });

        dialog.setNeutralButton("Replay", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ResetGame(vi);
            }

        });

        dialog.setPositiveButton("Salir", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                Intent i=new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
            }
        });
        dialog.show();
        myTimer.cancel();
    }

    protected void TimerMethod() {
        this.runOnUiThread(Timer_Tick);

    }

    private Runnable Timer_Tick = new Runnable() {
        public void run() {
            if(segundos <10){
                TVtiempo.setText(String.valueOf(minutos) + ":0" + String.valueOf(segundos));
            }
            else{
                TVtiempo.setText(String.valueOf(minutos) + ":" + String.valueOf(segundos));
            }
        }
    };

    public void ResetGame(View v){
        myTimer.cancel();
        segundos =-1;
        minutos =0;
        turnos =0;
        TVturnos.setText("0");
        cont=0;
        contwinner=0;
        puntaje=0;
        TVpuntaje.setText("0");
        loadImages();
        ResetTiempo();
        GridAdapterTemp adaptert=new GridAdapterTemp(this,20, imagenes.get(0), imagenes.get(1), imagenes.get(2), imagenes.get(3), imagenes.get(4), imagenes.get(5), imagenes.get(6), imagenes.get(7), imagenes.get(8), imagenes.get(9), imagenes.get(10), imagenes.get(11), imagenes.get(12), imagenes.get(13), imagenes.get(14), imagenes.get(15), imagenes.get(16), imagenes.get(17), imagenes.get(18), imagenes.get(19));
        gv.setAdapter(adaptert);
        gv.setEnabled(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                gv.setAdapter(adapter);
                gv.setEnabled(true);
            }
        }, 750);
    }

    private boolean compare(int a,int b) {
        boolean check = false;
        if(a==b){
            check=true;
        }
        else{
            check=false;
        }
        return check;
    }

    private void loadImages() {
        imagenes.clear();
        imagenes.add(R.drawable.card1);
        imagenes.add(R.drawable.card2);
        imagenes.add(R.drawable.card3);
        imagenes.add(R.drawable.card4);
        imagenes.add(R.drawable.card5);
        imagenes.add(R.drawable.card6);
        imagenes.add(R.drawable.card7);
        imagenes.add(R.drawable.card8);
        imagenes.add(R.drawable.card9);
        imagenes.add(R.drawable.card10);
        imagenes.add(R.drawable.card1);
        imagenes.add(R.drawable.card2);
        imagenes.add(R.drawable.card3);
        imagenes.add(R.drawable.card4);
        imagenes.add(R.drawable.card5);
        imagenes.add(R.drawable.card6);
        imagenes.add(R.drawable.card7);
        imagenes.add(R.drawable.card8);
        imagenes.add(R.drawable.card9);
        imagenes.add(R.drawable.card10);
        shuffle(imagenes);
    }

    private static void swap(List<Integer> l, int i, int change) {
        int helper = l.get(i);
        l.set(i, l.get(change));
        l.set(change, helper);
    }

    public void shuffle(List<Integer> l){
        int n = l.size();
        Random random = new Random();
        random.nextInt();
        for (int i = 0; i < n; i++) {
            int change = i + random.nextInt(n - i);
            swap(l, i, change);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBackgroundSound.cancel(true);
    }

    protected void onStop() {
        super.onStop();
        mBackgroundSound.cancel(true);
        myTimer.cancel();
    }

    protected void onResume() {
        super.onResume();

        if(isswitch){
            mBackgroundSound=new BackgroundSound();
            mBackgroundSound.execute(pisdestroyed,null,null);
        }
        TVpuntaje.setText(String.valueOf(puntaje));
        TVturnos.setText(String.valueOf(turnos));
    }
    protected void onRestart(){
        super.onRestart();
        gv.setEnabled(false);
        CuentaRegresivaToast();
    }

    protected void onStart(){
        super.onStart();
    }

    protected void onDestroy(){
        super.onDestroy();
        mBackgroundSound.cancel(true);
        imagenes.clear();
        pisdestroyed=true;
    }

    public void ResetTiempo(){
        myTimer=new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                TimerMethod();
                segundos++;
                if(segundos >=60){
                    segundos =0;
                    minutos++;
                }
            }
        }, 0, 1000);
    }

    public void CuentaRegresivaToast(){
        myTimer2=new Timer();
        myTimer2.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                CuentaRegresiva();
                cuentaregresiva++;
            }
        },0,900);
    }
    private void CuentaRegresiva() {
        this.runOnUiThread(Timer_Regresiva);
    }

    private Runnable Timer_Regresiva = new Runnable() {
        public void run() {
            if(cuentaregresiva==1){
                final Toast toast= Toast.makeText(getApplicationContext(), "3", Toast.LENGTH_SHORT);
                toast.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toast.cancel();
                    }
                }, 450);
            }
            else if(cuentaregresiva==2){
                final Toast toast= Toast.makeText(getApplicationContext(), "2", Toast.LENGTH_SHORT);
                toast.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toast.cancel();
                    }
                }, 450);
            }
            else if(cuentaregresiva==3){
                final Toast toast= Toast.makeText(getApplicationContext(), "1", Toast.LENGTH_SHORT);
                toast.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toast.cancel();
                    }
                }, 450);
                cuentaregresiva=0;
                myTimer2.cancel();
                ResetTiempo();
                gv.setEnabled(true);
            }

        }
    };

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("tiempoplayer", tiempoplayer);
        savedInstanceState.putLong("minutos", minutos);
        savedInstanceState.putLong("segundos", segundos);
        savedInstanceState.putInt("turnos", turnos);
        savedInstanceState.putInt("puntaje", puntaje);
        savedInstanceState.putInt("imagen0", imagenes.get(0));
        savedInstanceState.putInt("imagen1", imagenes.get(1));
        savedInstanceState.putInt("imagen2", imagenes.get(2));
        savedInstanceState.putInt("imagen3", imagenes.get(3));
        savedInstanceState.putInt("imagen4", imagenes.get(4));
        savedInstanceState.putInt("imagen5", imagenes.get(5));
        savedInstanceState.putInt("imagen6", imagenes.get(6));
        savedInstanceState.putInt("imagen7", imagenes.get(7));
        savedInstanceState.putInt("imagen8", imagenes.get(8));
        savedInstanceState.putInt("imagen9", imagenes.get(9));
        savedInstanceState.putInt("imagen10", imagenes.get(10));
        savedInstanceState.putInt("imagen11", imagenes.get(11));
        savedInstanceState.putInt("imagen12", imagenes.get(12));
        savedInstanceState.putInt("imagen13", imagenes.get(13));
        savedInstanceState.putInt("imagen14", imagenes.get(14));
        savedInstanceState.putInt("imagen15", imagenes.get(15));
        savedInstanceState.putInt("imagen16", imagenes.get(16));
        savedInstanceState.putInt("imagen17", imagenes.get(17));
        savedInstanceState.putInt("imagen18", imagenes.get(18));
        savedInstanceState.putInt("imagen19", imagenes.get(19));
        super.onSaveInstanceState(savedInstanceState);
    }

    public void insertarpuntaje(int puntajef1){
        try {
            jugador.abrir();
            jugador.crearRegistro(puntajef1,getUsername());
            jugador.cerrar();
        } catch (Exception e) {

        }
    }

    public String getUsername(){
        AccountManager manager = AccountManager.get(this);
        Account[] accounts = manager.getAccountsByType("com.google");
        List<String> possibleEmails = new LinkedList<String>();

        for (Account account : accounts) {
            possibleEmails.add(account.name);
        }

        if(!possibleEmails.isEmpty() && possibleEmails.get(0) != null){
            String email = possibleEmails.get(0);
            String[] parts = email.split("@");
            if(parts.length > 0 && parts[0] != null)
                return parts[0];
            else
                return "user";
        }else
            return "user";
    }

    public class BackgroundSound extends AsyncTask<Boolean, Void, Void> {
        MediaPlayer player;
        @Override
        protected Void doInBackground(Boolean... booleans) {
            if(cancion==0){
                player= MediaPlayer.create(ChallengeModeDificil.this, R.raw.unsoloidolo);
            }
            else if(cancion==1){
                player= MediaPlayer.create(ChallengeModeDificil.this, R.raw.sisi);
            }
            else if(cancion==2){
                player= MediaPlayer.create(ChallengeModeDificil.this, R.raw.amarilloesmicolor);
            }
            player.setLooping(true); // Set looping
            player.setVolume(100, 100);
            if(Boolean.TRUE){
                player.seekTo(tiempoplayer);
                player.start();
            }
            else{
                player.start();
            }

            while (!isCancelled()) {

            }
            tiempoplayer=player.getCurrentPosition();
            player.stop();
            player.release();
            return null;
        }
        protected void onPostExecute(Void... params) {

        }

        protected void seek(int i){
            player.seekTo(i);
        }
    }
}
