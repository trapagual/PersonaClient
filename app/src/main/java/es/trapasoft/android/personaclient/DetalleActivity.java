package es.trapasoft.android.personaclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

import es.trapasoft.android.personaclient.modelo.Persona;

public class DetalleActivity extends AppCompatActivity {

    TextView tvResumen;
    TextView tvNombre;
    TextView tvCalle;
    TextView tvZip;
    TextView tvPhone;
    TextView tvCity;
    TextView tvDeptId;
    ImageView ivFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.persona_detail);

        tvResumen = (TextView) findViewById(R.id.tvResumen);


        // extraemos el parametro que nos llega con el intent
        Persona p = (Persona)getIntent().getExtras().getSerializable("parametro");

        // rellenamos el textview
        tvResumen.setText(p.toString()); // funciona la prueba

        // vamos a complicarlo un poco: layout mas complejo
        tvNombre = (TextView) findViewById(R.id.tvNombre);
        tvCalle = (TextView) findViewById(R.id.tvCalle);
        tvCity = (TextView) findViewById(R.id.tvCity);
        tvPhone = (TextView) findViewById(R.id.tvPhone);
        tvZip = (TextView) findViewById(R.id.tvZip);
        tvDeptId = (TextView) findViewById(R.id.tvDeptId);

        ivFoto = (ImageView) findViewById(R.id.imgFoto);

        // rellenar los campos de texto
        tvNombre.setText(p.getFirstName() + " " + p.getLastName());
        tvCalle.setText(p.getStreet());
        tvCity.setText(p.getCity());
        tvPhone.setText(p.getPhoneNumber());
        tvZip.setText(p.getZipCode());
        // ÑAPA ÑAPA ÑAPA
        // la operacion del Jax-ws que devuelve la Persona
        // debería devolver también el nombre del departamento (hacer la JOIN en servidor)
        // como no lo hace, pongo aquí el id del departamento
        tvDeptId.setText(String.valueOf(p.getDeptID()));

        // ahora la foto
        ivFoto.setImageResource(lotoFoto());






    }

    /**
     * Esto es una ÑAPA ÑAPA ÑAPA
     * Porque en el JAX-WS no tengo una operacion que me devuelva la foto del contacto
     * Lo simulo poniendola desde local
     * En una situación real, vendría con el objeto Persona
     * Devuelvo una imagen aleatoria entre las 16 que tengo
     * @return R.id.lafoto
     */
    private int lotoFoto() {
        final Integer[] fotos = {
                R.drawable.persona1,
                R.drawable.persona2,
                R.drawable.persona3,
                R.drawable.persona4,
                R.drawable.persona5,
                R.drawable.persona6,
                R.drawable.persona7,
                R.drawable.persona8,
                R.drawable.persona9,
                R.drawable.persona10,
                R.drawable.persona11,
                R.drawable.persona12,
                R.drawable.persona13,
                R.drawable.persona14,
                R.drawable.persona15,
                R.drawable.persona16
        };

        Random randomGenerator = new Random();
        int valor = randomGenerator.nextInt(15) +1;

        return fotos[valor];
    }


}
