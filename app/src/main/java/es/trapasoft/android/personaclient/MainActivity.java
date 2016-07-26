package es.trapasoft.android.personaclient;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import es.trapasoft.android.personaclient.adapters.ListaPersonasAdapter;
import es.trapasoft.android.personaclient.modelo.Persona;

public class MainActivity extends AppCompatActivity {

    // lo que viene en: <definitions targetNamespace="http://servicio.jaxws.trapasoft.es/" name="PersonServiceService"> en el WSDL
    final String NAMESPACE = "http://servicio.jaxws.trapasoft.es/";
    // lo que viene en: <soap:address location="http://ws-trapasoft.rhcloud.com:80/JAXWSServer/persons"/> en el WSDL
    final String URL="http://ws-trapasoft.rhcloud.com:80/JAXWSServer/persons?wsdl";
    // el nombre de una de las operaciones del WSDL
    final String METHOD_NAME = "getPersona";
    // NAMESPACE+METHOD_NAME como un literal de cadena
    final String SOAP_ACTION =  "http://servicio.jaxws.trapasoft.es/getPersona";

    static TextView tvRespuesta;
    static EditText etID;
    static ImageButton btnGo;
    static ImageButton btnGoList;
    static ListView lvPersonas;

    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvRespuesta = (TextView) findViewById(R.id.resultado);
        etID = (EditText)findViewById(R.id.etID);
        btnGo = (ImageButton) findViewById(R.id.btnGo);
        btnGoList = (ImageButton) findViewById(R.id.btnGoList);
        lvPersonas = (ListView)findViewById(R.id.lvPersonas);

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etID.getText() == null || etID.getText().toString() == null || etID.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Hay que introducir algún valor numérico", Toast.LENGTH_LONG).show();
                } else {
                    getUnaPersona task = new getUnaPersona();
                    task.execute(new String[]{etID.getText().toString()});
                    //accessWebService(tvRespuesta, new String[]{etID.getText().toString()});
                }
            }
        });

        btnGoList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // simplemente sacar una lista de todas las personas
                // ya veremos despues que hacer con ella
                    getListaPersonas task = new getListaPersonas(v.getContext());
                    //task.execute(new String[]{"es"});
                // y sin filtro?
                // task.execute(new String[]{}); // da un error
                // y con una cadena vacia?
                task.execute(new String[]{""}); // así, sí funciona
            }
        });






        Log.i("Respuesta", "He vuelto a Main");




    } // fin onCreate



    /**
     * Clase que hace una llamada asíncrona al método getPersona del WS
     * De momento voy a hacer una de estas para cada método
     */
    private class getUnaPersona extends AsyncTask<String, Void, String> {


        String METHOD_NAME = "getPersona";


        private Exception exception;


        @Override
        protected void onPreExecute() {
            // Por si queremos hacer algo antes de ejecutar la llamada
            Log.i("ASYNC", "Antes de ejecutar");
        }

        @Override
        protected String doInBackground(String... params) {

            String webResponse = "";



            // crear un objeto org.ksoap2.serialization.SoapObject para construir una request SOAP.
            // Especificar el namespace del objeto SOAP y el nombre del método al que vamos a llamar
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            // Crear un objeto org.ksoap2.serialization.PropertyInfo para contener la informacion
            // sobre parametros que se le vayan a pasar al metodo en la llamada SOAP
            // Cada propiedad necesita un objeto PropertyInfo.
            // En este caso el metodo getPersona admite un parametro de tipo int (el id de la persona)
            PropertyInfo propInfo=new PropertyInfo();
            propInfo.name="arg0";
            propInfo.type=PropertyInfo.INTEGER_CLASS;
            propInfo.setValue(params[0]);
            request.addProperty(propInfo);

            // Ahora creamos un sobre SOAP. La clase SoapSerializationEnvelope extiende SoapEnvelop y soporta
            // serializacion. Mejor para portabilidad.
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            // metemos la request en el sobre
            envelope.setOutputSoapObject(request);

            // Creamos un objeto org.ksoap2.transport.HttpTransportSE que representa la capa de transporte http basada en J2SE.
            // HttpTransportSE extiende org.ksoap2.transport.Transport que encapsuls serializacion y deserializacion de los mensajes SOAP
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            try {
                // hacemos la llamada mediante SOAP_ACTION
                androidHttpTransport.call(SOAP_ACTION, envelope);
                // obtenemos la respuesta del webservice mediante el metodo getResponse del sobre y lo encapsulamos
                //SoapObject response = (SoapObject) envelope.getResponse();

                // comprobar si ha habido SoapFault
                if (envelope.bodyIn instanceof SoapObject) { // no hay error
                    SoapObject response = (SoapObject) envelope.getResponse();
                    Log.i("RESPUESTA UNA PERSONA", response.toString());
                    /*
                    Persona p = new Persona();
                    p.setCity(response.getPropertyAsString("city"));
                    p.setFirstName(response.getPropertyAsString("firstName"));
                    p.setLastName(response.getPropertyAsString("lastName"));
                    p.setPhoneNumber(response.getPropertyAsString("phoneNumber"));
                    p.setDeptID(Integer.parseInt(response.getPropertyAsString("deptID")));
                    p.setID(Integer.parseInt(response.getPropertyAsString("ID")));
                    */

                    Persona p = soap2persona(response);
                    webResponse = p.toStringShort();

                } else if (envelope.bodyIn instanceof SoapFault) { // ha devuelto un SOAPFAULT
                    SoapFault e = (SoapFault) envelope.bodyIn;
                    Log.e("SOAPFAULT", e.toString());
                    webResponse="Error SOAPFAULT: " + e.faultstring;
                }


            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }

            return webResponse;
        } // fin doInBackground

        @Override
        protected void onPostExecute(String result) {
            tvRespuesta.setText(result.toString());
            Log.i("Respuesta", "Me ha llegado: " + result.toString());
        }


    }


    /**
     * Clase que hace una llamada asíncrona al método getPersonas del WS
     * Le pasa una cadena filtro y devuelve un array de Personas
     * Aqui voy a devolver un
     * De momento voy a hacer una de estas para cada método
     *
     * Si lo que quiero devolver es un array de objetos modelo, como en este caso,
     * lo que hago es poner el tercer parametro como ArrayList<Persona>, y luego en el metodo
     * onPostExecute tambien tengo que recibir un ArrayList igual.
     * En este caso simplemente se lo he pasado a un ArrayAdapter para un ListView.
     * Ya veremos cómo irlo complicando.
     */
    private class getListaPersonas extends AsyncTask<String, Void, ArrayList<Persona>> {


        String METHOD_NAME = "getPersonas";

        Context mContext;

        private Exception exception;


        public getListaPersonas(final Context context) {
            this.mContext = context;
        }
        @Override
        protected void onPreExecute() {
            // Por si queremos hacer algo antes de ejecutar la llamada
            Log.i("ASYNC LISTA", "Antes de ejecutar");
        }

        @Override
        protected ArrayList<Persona> doInBackground(String... params) {

            ArrayList<Persona> webResponse = null;

            // crear un objeto org.ksoap2.serialization.SoapObject para construir una request SOAP.
            // Especificar el namespace del objeto SOAP y el nombre del método al que vamos a llamar
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            // Crear un objeto org.ksoap2.serialization.PropertyInfo para contener la informacion
            // sobre parametros que se le vayan a pasar al metodo en la llamada SOAP
            // Cada propiedad necesita un objeto PropertyInfo.
            // En este caso el metodo getPersona admite un parametro de tipo String (para filtrar por el apellido)
            PropertyInfo propInfo=new PropertyInfo();
            propInfo.name="arg0";
            propInfo.type=PropertyInfo.STRING_CLASS;
            propInfo.setValue(params[0]);
            request.addProperty(propInfo);

            // Ahora creamos un sobre SOAP. La clase SoapSerializationEnvelope extiende SoapEnvelop y soporta
            // serializacion. Mejor para portabilidad.
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            // metemos la request en el sobre
            envelope.setOutputSoapObject(request);

            // Creamos un objeto org.ksoap2.transport.HttpTransportSE que representa la capa de transporte http basada en J2SE.
            // HttpTransportSE extiende org.ksoap2.transport.Transport que encapsuls serializacion y deserializacion de los mensajes SOAP
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            try {
                // hacemos la llamada mediante SOAP_ACTION
                androidHttpTransport.call(SOAP_ACTION, envelope);
                // obtenemos la respuesta del webservice mediante el metodo getResponse del sobre y lo encapsulamos
                //SoapObject response = (SoapObject) envelope.getResponse();

                // comprobar si ha habido SoapFault
                if (envelope.bodyIn instanceof SoapObject) { // no hay error

                    ArrayList<Persona> arrayPer = new ArrayList<>();
                    Vector<SoapObject> response = (Vector<SoapObject>) envelope.getResponse();
                    StringBuilder sb = new StringBuilder();
                    Log.i("RESPUESTA lista ", response.toString());


                    int largo = response.size();
                    for (int i=0; i < largo; i++) {
                        SoapObject so = response.get(i);
                        Persona p = soap2persona(so);
                        arrayPer.add(p);
                        sb.append("\n");
                        sb.append(p.toString());
                    }


                    Log.i("LISTA_CADENA", sb.toString());

                    // y si cargo aqui la vista?
                    //tvLista.setText(webResponse);
                    // tampoco: no me deja ni compilar
                    // las vistas se cargan en el onPostExecute, que se ejecuta ya en el UI, y tiene todos los objetos de la vista a su disposicion

                    return arrayPer;


                } else if (envelope.bodyIn instanceof SoapFault) { // ha devuelto un SOAPFAULT
                    SoapFault e = (SoapFault) envelope.bodyIn;
                    Log.e("SOAPFAULT", e.toString());
                    return null;
                }


            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }

            return webResponse;
        } // fin doInBackground



        /**
         * El parametro ha de coincidir en tipo con el tercer parametro de AsyncTask.
         * Este metodo se ejecuta en el UI, luego podemos cargar aqui objetos del UI, que dentro de la
         * clase no nos deja.
         * Aqui recibo un array de personas, que de momento se lo he pasado simplemente como ArrayAdapter
         * al ListView. El ArrayAdapter por defecto utiliza el método toString de cada uno de los objetos de la colección
         * Pero se podría usar para un gridview, para una tabla de algún tipo etc.
         * He creado un Adapter personalizado, en la clase ListaPersonasAdapter, para que pinte la cosa
         * como a mi me interesa
         * @param arr
         */
        @Override
        protected void onPostExecute(ArrayList<Persona> arr) {
            Log.i("Respuesta Lista", "onPostExecute: " + arr.toString());

            // final ArrayAdapter adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1 ,arr );
            ListAdapter adapter = new ListaPersonasAdapter(mContext, R.layout.personarow, arr);
            lvPersonas.setAdapter(adapter);


            lvPersonas.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Persona p = (Persona)parent.getItemAtPosition(position);
                    //Toast.makeText(mContext, "Iniciar sesion detalle para \n" + p.getFirstName() + " " + p.getLastName(), Toast.LENGTH_LONG).show();

                    // ya tengo el objeto a pasar: creo un Intent y se lo paso:
                    Intent intent = new Intent(mContext, DetalleActivity.class);
                    intent.putExtra("parametro", p);
                    startActivity(intent);


                    /*
                    aqui voy a llamar a otro activity (deberia ser un fragment, pero esto mas adelante)
                    La otra activity sera el detalle de cada persona
                    Llevara una imagen que voy a tener en local (ÑAPA ÑAPA ÑAPA para no tener que reprogramar el WS)
                    tengo una coleccion de imagenes subidas como png a la carpeta drawable.

                    Hay que declarar un array de enteros con las referencias de R.id

                    ASI LO EXPLICAN EN STACKOVERFLOW:

                            Yes you could create an array of drawables.

                            As an alternate you could also create an array of ints which map to your resource ids. So drop your images in the drawable folder which gives them resource IDs.

                            R.drawable.yourimage1
                            R.drawable.yourimage2
                            ...
                            R.drawable.yourimagen
                            Then when you want to load an image and draw to your imageview you do something like this from your Activity. Assuming "yourarray" is the array:

                            Drawable d = getResources().getDrawable(yourarray[n]);
                            Then I believe the call is setImageDrawable on ImageView. So:

                            yourImageView.setImageDrawable(d);

                            Go with the alternate solution rreeverb describes. You don't have to 'get' the drawable, you can simply set the image resource ID on the view! developer.android.com/reference/android/widget/… – Johan Pelgrim Nov 17 '10 at 6:21


                            You can use the resources from drawable-mdpi and store them in an array of integers like

                            private static final Integer[] Icons = {
                                R.drawable.bg_android_icon,
                                R.drawable.bg_sunrise_icon,
                                R.drawable.bg_sunset_icon,
                                ......
                            };
                            And then you can use it in a loop with Postion ranging from 0 to Icons.length and set it to ImageView

                            ImageView.setBackground=getResources().getDrawable(Icons[Position]);
                     */

                }
            });

            // ahora se le podria poner un listener a la lista para que cuando pinchemos un item aparezca por
            // ejemplo la pantalla de detalle, o lo que sea:
            /******************************
             listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
            final String item = (String) parent.getItemAtPosition(position);
            view.animate().setDuration(2000).alpha(0)
            .withEndAction(new Runnable() {
            @Override
            public void run() {
            list.remove(item);
            adapter.notifyDataSetChanged();
            view.setAlpha(1);
            }
            });
            }
             ********************************************/
        }


    }


    /**
     * Metodo de utilidad
     * Convierte un objeto SoapObject a un objeto persona
     * @param so
     * @return Persona
     */
    public Persona soap2persona(SoapObject so) {
        Persona p = new Persona();

        p.setCity(so.getPropertyAsString("city"));
        p.setFirstName(so.getPropertyAsString("firstName"));
        p.setLastName(so.getPropertyAsString("lastName"));
        p.setPhoneNumber(so.getPropertyAsString("phoneNumber"));
        p.setDeptID(Integer.parseInt(so.getPropertyAsString("deptID")));
        p.setID(Integer.parseInt(so.getPropertyAsString("ID")));
        p.setStreet(so.getPropertyAsString("street"));
        p.setZipCode(so.getPropertyAsString("zipCode"));
        return p;
    }

}
