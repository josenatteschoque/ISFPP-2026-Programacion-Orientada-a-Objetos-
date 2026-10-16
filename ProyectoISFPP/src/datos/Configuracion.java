package datos;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuracion {

	public static final String ARCHIVO_POR_DEFECTO = "config.properties";//asigno al atributo el nombre del archivo

	private final Properties diccionario = new Properties();// creo un diccionario que almacena clave valor que se usara para que lea el archivo

	public Configuracion() {//el contructor resive el parametro del archivo por defecto y se lo delega al segundo contructor para que cree el objeto
		this(ARCHIVO_POR_DEFECTO);
	}

	public Configuracion(String archivo) {//
		try (FileInputStream entrada = new FileInputStream(archivo)) {//abre el archivo que se le pase o desde el anterior constructor o otro desde un test
			diccionario.load(entrada);//lee el archivo abierto y carga en el diccionario por clave valor ignorando comentarios o linea vacias
		} catch (IOException e) {
			throw new IllegalStateException("No se pudo leer la configuracion " + archivo);
		}
	}

	public String getArchivoUsuarios() {
		return texto("usuario");//devuelve el nombre del archivo txt
	}

	public String getArchivoServicios() {
		return texto("servicio");//retorna el nombre del archivo txt
	}

	public String getArchivoVehiculos() {
		return texto("vehiculo");//devuelve el nombre del archivo txt
	}
//retorna el valor de las latitudes y longitudes que son el parametro de madryn
	public double getLatitud1() {
		return numero("latitud1");
	}

	public double getLongitud1() {
		return numero("longitud1");
	}

	public double getLatitud2() {
		return numero("latitud2");
	}

	public double getLongitud2() {
		return numero("longitud2");
	}

	// devuelve el valor de una propiedad o avisa cual falta
	private String texto(String clave) {
		String valor = diccionario.getProperty(clave);//se le asigna el valor que esta guardado en el diccionario apartir de la clave
		if (valor == null || valor.trim().isEmpty()) {//valida que el valor no sea null y que ell valor no este vacio
			throw new IllegalStateException("Falta el valor de la clave '" + clave + "' en la configuracion");
		}
		return valor.trim();//retorna el valor quitandole los espacios
	}

	private double numero(String clave) {
		try {
			return Double.parseDouble(texto(clave));//se le delega al metodo texto la validacion y busqueda del valor con dicha clave y se hace un parce para que el valor sea un numero double
		} catch (NumberFormatException e) {
			throw new IllegalStateException("el valor de la clave '" + clave + "' no es un numero");
		}//si este no pudo ser asigna por que no era un numero se lanza una excepcion  
	}
}