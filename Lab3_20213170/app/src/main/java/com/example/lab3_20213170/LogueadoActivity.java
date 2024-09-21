package com.example.lab3_20213170;
import okhttp3.OkHttpClient;
import retrofit2.Callback;


import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LogueadoActivity extends AppCompatActivity {
    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;
    private long tiempoRestanteMS = 25*60*1000;
    ImageButton playButton;
    TextView timerTextView, textDescanso;
    private CountDownTimer descansoTimer;
    String firstName;
    boolean isLoggedOut;

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
                // Cancelar el CountDownTimer si está en ejecución
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
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
        firstName = intent.getStringExtra("firstName");
        String lastName = intent.getStringExtra("lastName");
        String gender = intent.getStringExtra("gender");
        String refreshToken = intent.getStringExtra("refreshToken");
        int idUser = intent.getIntExtra("idUser", 1);

        isLoggedOut = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                .getBoolean("isLoggedOut", false);

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
                Intent intent = getIntent();
                int userId = intent.getIntExtra("idUser", -1);
                obtenerTareas(userId);

            }
        }.start();

        playButton.setImageResource(R.drawable.baseline_restart_alt_24); // Cambia a icono de reiniciar durante la ejecución
    }

    private void obtenerTareas(int userId) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://dummyjson.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        ApiUser api = retrofit.create(ApiUser.class);
        Call<TodoResponse> call = api.getTodos(userId);

        call.enqueue(new Callback<TodoResponse>() {
            @Override
            public void onResponse(Call<TodoResponse> call, Response<TodoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Todo> todos = response.body().getTodos();
                    if (todos.isEmpty()) {
                        textDescanso.setText("En descanso");
                        mostrarDialogoSinTareas();
                    } else {
                        //Mostrar Activity con las tareas pendientes
                        textDescanso.setText("En descanso");
                        iniciarDescanso(); // Iniciar el temporizador de descanso sí o sí
                        Intent intent = new Intent(LogueadoActivity.this, TareasActivity.class);
                        ArrayList<String> tareasList = new ArrayList<>();
                        ArrayList<Integer> tareasIdList = new ArrayList<>();
                        for (Todo tarea : todos) {
                            String estado = tarea.isCompleted() ? "Completado" : "No completado";
                            tareasList.add(tarea.getTodo() + " - " + estado);
                            tareasIdList.add(tarea.getId()); //guardamos las IDs (estarán en la misma posición que los noombres respecto a la otra lista)
                        }

                        intent.putStringArrayListExtra("tareas", tareasList);
                        intent.putExtra("firstName", firstName);
                        intent.putIntegerArrayListExtra("tareasIdList", tareasIdList);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(LogueadoActivity.this, "Error al obtener las tareas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TodoResponse> call, Throwable t) {
                Toast.makeText(LogueadoActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });

    }



    private void mostrarDialogoSinTareas() {
        new MaterialAlertDialogBuilder(LogueadoActivity.this)
                .setTitle("¡Felicidades!")
                .setMessage("Empezó el tiempo de descanso!")
                .setPositiveButton("Entendido", (dialog, which) -> {
                    dialog.dismiss();
                    iniciarDescanso(); // Iniciar el temporizador de descanso
                })
                .show();
    }

    //Método de iniciar Descanso

    private void iniciarDescanso(){

        descansoTimer = new CountDownTimer(5*60*1000,1000) {
            @Override
            public void onTick(long l) {
                // Actualiza el texto
                long minutosDescanso = l / 60000;
                long segundosDescanso = (l % 60000) / 1000;
                timerTextView.setText(String.format(Locale.getDefault(), "%02d:%02d", minutosDescanso, segundosDescanso)); //Formato para la vista lab
            }

            @Override
            public void onFinish() {


                //Solo cuando no se desloguee sí muestra el Dialog para que no ocurra un bug

                if(!isLoggedOut){
                    // Aquí puedes realizar alguna acción cuando termine el temporizador de descanso
                    new MaterialAlertDialogBuilder(LogueadoActivity.this)
                            .setTitle("Atención")
                            .setMessage("Terminó el tiempo de descanso. Dale al botón de reinicio para empezar otro ciclo")
                            .setPositiveButton("Entendido", (dialog, which) -> dialog.dismiss())
                            .show();

                }

            }
        }.start();

    }

    // Método para reiniciar el temporizador a 25 minutos
    private void resetTimer() {
        tiempoRestanteMS = 25*60*1000; // Reiniciar a 25 minutos
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