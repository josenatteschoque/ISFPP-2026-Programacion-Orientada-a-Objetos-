package datos.dao.txt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import datos.dao.UsuarioDAO;
import modelo.Usuario;
import modelo.Vehiculo;

public class UsuarioDaoTxt implements UsuarioDAO {

	private static final int CAMPOS_USUARIO = 3; // nombre, telefono y email
	private static final int POSICION_LICENCIA = 3; // indice de la posicion de la licencia de conducir
	private static final int POSICION_PRIMERA_PATENTE = 4; // indice de la posicion de la primer patente del vehiculo que le pertenece

	private final String archivo;

	public UsuarioDaoTxt(String archivo) {
		this.archivo = archivo;
	}

	// recibe el mapa de vehiculos para poder buscar cada patente
	@Override
	public List<Usuario> obtenerTodos(Map<String, Vehiculo> vehiculos) {

		List<Usuario> usuarios = new ArrayList<>(); // crea una list para guardar los usuarios del archivo.txt
		Set<String> emails = new HashSet<>(); // para detectar emails repetidos ya que set solo es una coleccion de elementos unicos
		Set<String> patentesAsignadas = new HashSet<>(); // para detectar vehiculos con dos dueños

		for (Linea linea : LectorArchivo.leer(archivo)) { // con un for each recorremos la list con todas las lineas del archivo
			Usuario usuario = crear(linea, vehiculos, patentesAsignadas); // crea un objeto de tipo usuario pidiendole que lo haga el metodo crear que hace unas verificaciones antes de crearlo

			if (!emails.add(usuario.getEmail())) { // add devuelve false si ya estaba
				throw new ArchivoInvalidoException(archivo, linea.getNumero(),
						"email repetido " + usuario.getEmail());
			}
			usuarios.add(usuario); // como es un usuario valido lo agrego a la list
		}
		return usuarios; // retorno la list de usuarios con conductores en alta y usuarios normales
	}

	private Usuario crear(Linea linea, Map<String, Vehiculo> vehiculos, Set<String> patentesAsignadas) {

		List<String> c = linea.getCampos();//asigno la linea de datos del archivo

		int numero = linea.getNumero(); // asigno de que numero de linea del archivo son dichos datos

		if (c.size() < CAMPOS_USUARIO) { // me fijo si dicha linea cuenta con la cantidad minima de datos para ser un usuario, si no es asi lanzo una excepcion
			throw new ArchivoInvalidoException(archivo, numero, "faltan datos (nombre, telefono y email)");
		}

		Usuario usuario = new Usuario(c.get(0), c.get(1), c.get(2)); // si cumplio con los minimos datos creo un objeto de tipo usuario basico

		if (c.size() == CAMPOS_USUARIO) {
			return usuario; // no tiene licencia, es solo cliente
		}
		if (c.size() == POSICION_PRIMERA_PATENTE) {
			throw new ArchivoInvalidoException(archivo, numero, "tiene licencia pero ninguna patente");
		}

		// busca cada patente en el mapa de vehiculos
		List<Vehiculo> propios = new ArrayList<>();
		for (String patente : c.subList(POSICION_PRIMERA_PATENTE, c.size())) { // creo una sublist que contiene todas las patentes de los vehiculos que le pertenecen
			Vehiculo vehiculo = vehiculos.get(patente);
			if (vehiculo == null) {
				throw new ArchivoInvalidoException(archivo, numero, "la patente " + patente + " no existe");
			}
			if (!patentesAsignadas.add(patente)) {//ya que el set solo devuelve true si este objeto unico no esta en la lista y lo pudo agragar
				throw new ArchivoInvalidoException(archivo, numero,
						"la patente " + patente + " ya pertenece a otro conductor");
			}
			propios.add(vehiculo);
		}

		// el alta de conductor pide un primer vehiculo los demas se agregan despues de dar dicha alta
		usuario.altaConductor(c.get(POSICION_LICENCIA), propios.get(0));
		for (Vehiculo extra : propios.subList(1, propios.size())) {
			usuario.getConductor().agregarVehiculo(extra);
		}
		return usuario; // retorno un usuario dado de alta como conductor
	}
}