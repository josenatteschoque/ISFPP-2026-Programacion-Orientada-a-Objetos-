package datos.dao;

import java.util.Map;

import datos.dto.VehiculoDTO;
import modelo.Vehiculo;

public interface VehiculoDAO {

	Map<String, VehiculoDTO> obtenerTodos();
}