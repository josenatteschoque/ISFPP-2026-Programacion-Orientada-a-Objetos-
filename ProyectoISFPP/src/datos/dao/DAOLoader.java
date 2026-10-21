package datos.dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import datos.dao.txt.TxtDAOFactory;

//import logica.GeneradorUbicacion;

public class DAOLoader {

	public static final String ARCHIVO_POR_DEFECTO = "config.properties"; // asigno al atributo el nombre del archivo

	private static DAOFactory fabrica; // guarda la fabrica ya creada para no volver a leer el archivo

	// si todavia no hay fabrica le delega a crearFactory el archivo por defecto para que la cree
	public static DAOFactory getFactory() {
		if (fabrica == null) {
			fabrica = crearFactory(ARCHIVO_POR_DEFECTO);
		}
		return fabrica;
	}

	// recibe el nombre del archivo (el por defecto o uno desde un test) y con lo que lee crea la fabrica de txt
	public static DAOFactory crearFactory(String archivo) {
		Properties diccionario = cargar(archivo);
		// retorna el valor de las latitudes y longitudes que son el parametro de madryn
		/*GeneradorUbicacion generador = new GeneradorUbicacion(numero(diccionario, "latitud1"),
				numero(diccionario, "longitud1"), numero(diccionario, "latitud2"), numero(diccionario, "longitud2"));*/

		// le pasa a la fabrica los nombres de los archivos txt
		return new TxtDAOFactory(texto(diccionario, "servicio"), texto(diccionario, "vehiculo"),
				texto(diccionario, "usuario") /*, generador*/);
	}

	private static Properties cargar(String archivo) {
		Properties diccionario = new Properties(); // creo un diccionario que almacena clave valor que se usara para que lea el archivo
		try (FileInputStream entrada = new FileInputStream(archivo)) { // abre el archivo que se le pase o desde getFactory o desde un test
			diccionario.load(entrada); // lee el archivo abierto y carga en el diccionario por clave valor ignorando comentarios o lineas vacias
		} catch (IOException e) {
			throw new IllegalStateException("No se pudo leer la configuracion " + archivo);
		}	
		return diccionario;
	}

	// devuelve el valor de una propiedad o avisa cual falta
	private static String texto(Properties diccionario, String clave) {
		String valor = diccionario.getProperty(clave); // se le asigna el valor que esta guardado en el diccionario a partir de la clave
		if (valor == null || valor.trim().isEmpty()) { // valida que el valor no sea null y que el valor no este vacio
			throw new IllegalStateException("Falta el valor de la clave '" + clave + "' en la configuracion");
		}
		return valor.trim(); // retorna el valor quitandole los espacios
	}

	/*private static double numero(Properties diccionario, String clave) {
		try {
			return Double.parseDouble(texto(diccionario, clave)); // se le delega al metodo texto la validacion y busqueda del valor con dicha clave y se hace un parse para que el valor sea un numero double
		} catch (NumberFormatException e) {
			throw new IllegalStateException("el valor de la clave '" + clave + "' no es un numero");
		} // si este no pudo ser asignado porque no era un numero se lanza una excepcion
	}*/
}