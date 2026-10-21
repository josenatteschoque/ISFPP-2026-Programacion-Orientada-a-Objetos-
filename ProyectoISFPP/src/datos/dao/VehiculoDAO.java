package datos.dao;

import java.util.Map;

import modelo.Vehiculo;

public interface VehiculoDAO {

	Map<String, Vehiculo> obtenerTodos();
}