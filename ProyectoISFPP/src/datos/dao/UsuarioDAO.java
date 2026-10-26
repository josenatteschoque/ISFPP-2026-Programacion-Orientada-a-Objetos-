package datos.dao;

import java.util.List;

import datos.dto.UsuarioDTO;

public interface UsuarioDAO {

	List<UsuarioDTO> obtenerTodos();
}