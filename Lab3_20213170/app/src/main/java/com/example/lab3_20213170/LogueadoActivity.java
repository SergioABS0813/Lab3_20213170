package com.example.lab3_20213170;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LogueadoActivity extends AppCompatActivity {

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






    }
}