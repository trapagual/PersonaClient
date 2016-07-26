package es.trapasoft.android.personaclient.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import es.trapasoft.android.personaclient.R;
import es.trapasoft.android.personaclient.modelo.Persona;

/**
 * Created by alejandro on 25/07/2016.
 */
public class ListaPersonasAdapter extends ArrayAdapter<Persona>{
    public ListaPersonasAdapter(Context context, int resource) {
        super(context, resource);
    }

    public ListaPersonasAdapter(Context context, int resource, List<Persona> objects) {
        super(context, resource, objects);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.personarow, null);
        }
        Persona p = getItem(position);
        if (p != null) {
            TextView tvNombre = (TextView) v.findViewById(R.id.tvNombre);
            TextView tvCiudad = (TextView) v.findViewById(R.id.tvCiudad);
            TextView tvDept = (TextView) v.findViewById(R.id.tvDept);

            tvNombre.setText(p.getFirstName() + " " + p.getLastName());
            tvCiudad.setText(p.getCity());
            tvDept.setText(String.valueOf(p.getDeptID()));

        }

        return v;
    }
}
