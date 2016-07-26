package es.trapasoft.android.personaclient;

import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import es.trapasoft.android.personaclient.modelo.Persona;




public class LanzaRequest extends AsyncTask<String, Void, String> {

    // lo que viene en: <definitions targetNamespace="http://servicio.jaxws.trapasoft.es/" name="PersonServiceService"> en el WSDL
    private static final String NAMESPACE = "http://servicio.jaxws.trapasoft.es/";
    // lo que viene en: <soap:address location="http://ws-trapasoft.rhcloud.com:80/JAXWSServer/persons"/> en el WSDL
    private static String URL="http://ws-trapasoft.rhcloud.com:80/JAXWSServer/persons?wsdl";
    // el nombre de una de las operaciones del WSDL
    private static final String METHOD_NAME = "getPersona";
    // NAMESPACE+METHOD_NAME como un literal de cadena
    private static final String SOAP_ACTION =  "http://servicio.jaxws.trapasoft.es/getPersona";

    private Exception exception;

    protected String doInBackground(String... params) {
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
        propInfo.setValue(12);
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
            SoapObject resultsRequestSOAP = (SoapObject) envelope.getResponse();

            // PRIMERA VUELTA
            // como tenemos un bonito toString en la clase Persona, simplemente lo devolvemos
            String laRespuesta = resultsRequestSOAP.toString();

            return laRespuesta;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        return null;
    } // fin doInBackground

    protected void onPostExecute(SoapObject result) {
        MainActivity.tvRespuesta.setText(result.toString());
        Log.i("Respuesta", "Me ha llegado: " + result.toString());
    }
}
