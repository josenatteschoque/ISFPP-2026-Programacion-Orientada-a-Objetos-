package datos.dao;

import java.util.List;

import datos.dto.ServicioDTO;
import modelo.Servicio;

public interface ServicioDAO {

	List<ServicioDTO> obtenerTodos();
}