package datos.dao.txt;

import datos.dao.DAOFactory;
import datos.dao.ServicioDAO;
import datos.dao.UsuarioDAO;
import datos.dao.VehiculoDAO;

//import logica.GeneradorUbicacion;

// fabrica de daos para txt: crea los tres DAO que leen archivos de texto
public class TxtDAOFactory implements DAOFactory {

	private final String archivoServicios; // nombre del archivo txt de servicios
	private final String archivoVehiculos; // nombre del archivo txt de vehiculos
	private final String archivoUsuarios; // nombre del archivo txt de usuarios
	//private final GeneradorUbicacion generador; // genera las ubicaciones al azar de los vehiculos

	// los nombres de archivo entran desde afuera la fabrica
	public TxtDAOFactory(String archivoServicios, String archivoVehiculos, String archivoUsuarios /*,
			GeneradorUbicacion generador*/) {
		this.archivoServicios = archivoServicios;
		this.archivoVehiculos = archivoVehiculos;
		this.archivoUsuarios = archivoUsuarios;
		//this.generador = generador;
	}

	// crea el dao de servicios indicandole que archivo leer
	@Override
	public ServicioDAO getServicioDAO() {
		return new ServicioDaoTxt(archivoServicios);
	}

	// crea el dao de vehiculos con su archivo y el generador de ubicaciones
	@Override
	public VehiculoDAO getVehiculoDAO() {
		return new VehiculoDaoTxt(archivoVehiculos /*, generador*/);
	}

	// crea el dao de usuarios indicandole que archivo leer
	@Override
	public UsuarioDAO getUsuarioDAO() {
		return new UsuarioDaoTxt(archivoUsuarios);
	}
}