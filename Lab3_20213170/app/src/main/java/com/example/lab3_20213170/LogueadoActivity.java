package com.example.lab3_20213170;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Locale;

public class LogueadoActivity extends AppCompatActivity {
    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;
    private long tiempoRestanteMS = 10000;
    ImageButton playButton;
    TextView timerTextView, textDescanso;
    private CountDownTimer descansoTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_logueado);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Opciones
        ImageView logout = findViewById(R.id.logout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Redirigir al MainActivity cuando se hace clic en logout
                Intent intent = new Intent(LogueadoActivity.this, MainActivity.class);
                // Opción adicional para cerrar la actividad actual
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Opcional, limpia la pila de actividades
                startActivity(intent);
                finish(); // Finaliza la actividad actual para que no se pueda regresar a ella
            }
        });

        // Recibir los datos enviados desde MainActivity
        Intent intent = getIntent();
        String token = intent.getStringExtra("token");
        String username = intent.getStringExtra("username");
        String email = intent.getStringExtra("email");
        String firstName = intent.getStringExtra("firstName");
        String lastName = intent.getStringExtra("lastName");
        String gender = intent.getStringExtra("gender");
        String refreshToken = intent.getStringExtra("refreshToken");

        TextView userNameApellidotxt = findViewById(R.id.userNameApellido);
        TextView userEmailtxt = findViewById(R.id.userEmail);
        ImageView userImage = findViewById(R.id.userImage);
        userNameApellidotxt.setText(firstName + " " + lastName);
        userEmailtxt.setText(email);

        //Validamos si es hombre o mujer:
        if(gender.equals("female")){
            userImage.setImageResource(R.drawable.woman);
        }else{
            userImage.setImageResource(R.drawable.man);
        }

        //Logica de temporizador:

        playButton = findViewById(R.id.playButton);
        timerTextView = findViewById(R.id.timerTextView);
        textDescanso = findViewById(R.id.textDescanso);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (countDownTimer != null){
                    countDownTimer.cancel(); //Si esta ejecutando, se pausa
                }
                resetTimer();
                startTimer();
            }
        });
        updateCountDownText();
    }

    // Método para iniciar el temporizador
    private void startTimer() {
        countDownTimer = new CountDownTimer(tiempoRestanteMS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tiempoRestanteMS = millisUntilFinished;
                updateCountDownText(); // Actualizacion del texto
            }

            @Override
            public void onFinish() {

                //Se define si se muestra el dialog o no si y solo si el usuario no tiene pendientes extras

                

                new MaterialAlertDialogBuilder(LogueadoActivity.this)
                        .setTitle("¡Felicidades!")
                        .setMessage("Empezó el tiempo de descanso")
                        .setPositiveButton("Entendido", (dialog, which) -> {
                            // Acción al hacer clic en el botón, solo cerrar el diálogo
                            dialog.dismiss();
                        })
                        .show();

                //Luego del Alert Dialog realizamos el flujo de temporizador de 5 minutos
                textDescanso.setText("En descanso");
                iniciarDescanso();



            }
        }.start();

        playButton.setImageResource(R.drawable.baseline_restart_alt_24); // Cambia a icono de reiniciar durante la ejecución
    }

    //Método de iniciar Descanso

    private void iniciarDescanso(){

        descansoTimer = new CountDownTimer(10*1000,1000) {
            @Override
            public void onTick(long l) {
                // Actualiza el texto
                long minutosDescanso = l / 60000;
                long segundosDescanso = (l % 60000) / 1000;
                timerTextView.setText(String.format(Locale.getDefault(), "%02d:%02d", minutosDescanso, segundosDescanso)); //Formato para la vista lab
            }

            @Override
            public void onFinish() {
                // Aquí puedes realizar alguna acción cuando termine el temporizador de descanso
                new MaterialAlertDialogBuilder(LogueadoActivity.this)
                        .setTitle("¡Descanso terminado!")
                        .setMessage("Es hora de volver al trabajo.")
                        .setPositiveButton("Entendido", (dialog, which) -> dialog.dismiss())
                        .show();

            }
        }.start();

    }

    // Método para reiniciar el temporizador a 25 minutos
    private void resetTimer() {
        tiempoRestanteMS = 10000; // Reiniciar a 25 minutos
        updateCountDownText();
    }

    // Método para actualizar el texto del temporizador
    private void updateCountDownText() {
        int minutos = (int) (tiempoRestanteMS / 1000) / 60;
        int segundos = (int) (tiempoRestanteMS / 1000) % 60;

        String timeFormatted = String.format("%02d:%02d", minutos, segundos); //Le damos el formato del lab
        timerTextView.setText(timeFormatted);
    }



}