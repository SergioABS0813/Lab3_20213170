package com.example.lab3_20213170;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        Button btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            EditText emailLogin = findViewById(R.id.emailLogin);
            EditText passwordLogin = findViewById(R.id.passwordLogin);
            HttpLoggingInterceptor loggin = new HttpLoggingInterceptor();
            @Override
            public void onClick(View view) {
                String email = emailLogin.getText().toString().trim(); //.trim() para eliminar espacios en blanco de la cadena de texto al inicio y al final
                String password = passwordLogin.getText().toString().trim();

                loggin.setLevel(HttpLoggingInterceptor.Level.BODY);

                OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
                httpClient.addInterceptor(loggin);

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://dummyjson.com/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(httpClient.build())
                        .build();

                ApiUser login = retrofit.create(ApiUser.class);

                LoginRequest loginRequest = new LoginRequest(email,password);

                Call<User> call = login.LOGIN(loginRequest);

                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.isSuccessful() && response.body() != null){
                            emailLogin.getText().clear(); //username
                            passwordLogin.getText().clear(); //password
                            //Extraer datos del usuario logueado:
                            User user = response.body();
                            String usernameLogueado = user.getUsername();
                            String emailLogueado = user.getEmail();
                            String genderLogueado = user.getGender();
                            String firstNameLogueado = user.getFirstName();
                            String lastNameLogueado = user.getLastName();
                            String token = user.getToken(); //La API nos devuelve el token
                            String refreshToken = user.getRefreshToken();

                            //Al estar logueados abrimos nueva activity
                            Intent intent = new Intent(MainActivity.this, LogueadoActivity.class);
                            intent.putExtra("token", token); // Token del usuario
                            intent.putExtra("username", usernameLogueado);
                            intent.putExtra("email", emailLogueado);
                            intent.putExtra("firstName", firstNameLogueado);
                            intent.putExtra("lastName", lastNameLogueado);
                            intent.putExtra("gender", genderLogueado);
                            intent.putExtra("refreshToken", refreshToken);
                            startActivity(intent);
                            Toast.makeText(MainActivity.this, "Bienvenido", Toast.LENGTH_SHORT).show();
                        } else{
                            Toast.makeText(MainActivity.this, "Error en credenciales", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Lo sentimos, hubo un error de conexión, inténtelo de nuevo", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


    }


}