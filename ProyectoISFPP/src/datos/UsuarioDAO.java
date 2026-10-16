package datos;

import java.util.List;
import java.util.Map;

import modelo.Usuario;
import modelo.Vehiculo;

public interface UsuarioDAO {

	List<Usuario> obtenerTodos(Map<String, Vehiculo> vehiculos);
}