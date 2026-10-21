package logica;

import java.util.List;
import java.util.Map;

import datos.dao.DAOFactory;
import datos.dao.DAOLoader;
import modelo.Servicio;
import modelo.Usuario;
import modelo.Vehiculo;

public class SistemaGuardado {
	private final List<Servicio> servicios;
	private final Map<String, Vehiculo> vehiculos; 
	private final List<Usuario> usuarios;
	
	public SistemaGuardado() {
		this(DAOLoader.getFactory());
	}
	
	public SistemaGuardado(DAOFactory fabrica) {
		servicios = fabrica.getServicioDAO().obtenerTodos();
		vehiculos = fabrica.getVehiculoDAO().obtenerTodos();
		usuarios = fabrica.getUsuarioDAO().obtenerTodos(vehiculos);
	}
	
	public List<Servicio> getServicios() {
		return servicios;
	}

	public Map<String, Vehiculo> getVehiculos() {
		return vehiculos;
	}

	public List<Usuario> getUsuarios() {
		return usuarios;
	}

}
