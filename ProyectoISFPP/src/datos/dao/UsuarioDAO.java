package datos.dao;

import java.util.List;
import java.util.Map;

import datos.dto.UsuarioDTO;
import datos.dto.VehiculoDTO;
import modelo.Usuario;
import modelo.Vehiculo;

public interface UsuarioDAO {

	List<UsuarioDTO> obtenerTodos(Map<String, VehiculoDTO> vehiculos);
}