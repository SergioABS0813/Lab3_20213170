package com.example.lab3_20213170;

import retrofit2.Callback;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;
import android.util.Log;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class TareasActivity extends AppCompatActivity {

    private Spinner tareasSpinner;
    private Button cambiarEstadoButton;
    private List<String> tareasList;
    private TextView tareasUsuarioName;
    private ImageView back;
    private int positionId;
    private int idTarea;
    ImageView logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tareas);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tareasSpinner = findViewById(R.id.tareasSpinner);
        cambiarEstadoButton = findViewById(R.id.cambiarEstadoButton);
        tareasUsuarioName = findViewById(R.id.tareasUsuarioName);
        back = findViewById(R.id.back);
        logout = findViewById(R.id.logout);

        Intent intent = getIntent();
        ArrayList<String> tareas = intent.getStringArrayListExtra("tareas");
        String firstName = intent.getStringExtra("firstName");
        ArrayList<Integer> tareasIdList = intent.getIntegerArrayListExtra("tareasIdList");
        // Verifica que las listas no sean nulas
        if (tareas == null || tareasIdList == null) {
            Log.e("TareasActivity", "Tareas o IDs no recibidos correctamente.");
            return;
        }
        tareasUsuarioName.setText("Ver tareas de " + firstName);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tareas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tareasSpinner.setAdapter(adapter);

        cambiarEstadoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Logica tarea seleccionada
                String tareaSeleccionada = tareasSpinner.getSelectedItem().toString();
                int selectedPosition = tareasSpinner.getSelectedItemPosition();
                idTarea = tareasIdList.get(selectedPosition); // Obtener el ID de la tarea seleccionada
                Log.d("TareasActivity", "Tarea seleccionada: " + tareaSeleccionada + ", ID de tarea: " + idTarea);

                //Verificamos si es que está o no completada la tarea
                boolean isCompleted = tareaSeleccionada.contains("Completado");
                boolean nuevoEstado = !isCompleted;

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://dummyjson.com/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                ApiUser api = retrofit.create(ApiUser.class);

                TodoUpdateRequest updateRequest = new TodoUpdateRequest(nuevoEstado);

                Call<TodoResponse> call = api.updateTodo(idTarea, updateRequest);
                call.enqueue(new Callback<TodoResponse>() {
                    @Override
                    public void onResponse(Call<TodoResponse> call, Response<TodoResponse> response) {
                        if (response.isSuccessful()) {
                            // Mostrar mensaje de éxito
                            if(isCompleted){
                                Toast.makeText(TareasActivity.this, "Cambió realizado a la tarea: No completada", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(TareasActivity.this, "Cambió realizado a la tarea: Completada", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Mostrar mensaje de error
                            Toast.makeText(TareasActivity.this, "Error al actualizar el estado", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<TodoResponse> call, Throwable t) {
                        // Mostrar mensaje de error en caso de fallo en la conexión
                        Toast.makeText(TareasActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                    }
                });




            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                        .edit()
                        .putBoolean("isLoggedOut", false)
                        .apply();
                finish();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                        .edit()
                        .putBoolean("isLoggedOut", true)
                        .apply();
                // Crear un intent para ir al MainActivity
                Intent intent = new Intent(TareasActivity.this, MainActivity.class);
                // Limpiar la pila de actividades para que el usuario no pueda regresar presionando "back"
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                // Iniciar MainActivity
                startActivity(intent);
                // Finalizar la actividad actual para que no se quede en segundo plano
                finish();

            }
        });



    }
}