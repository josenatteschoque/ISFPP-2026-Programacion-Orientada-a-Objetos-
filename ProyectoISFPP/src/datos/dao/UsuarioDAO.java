package datos.dao;

import java.util.List;

import datos.dto.UsuarioDTO;

public interface UsuarioDAO {

	List<UsuarioDTO> obtenerTodos();
	
	//Este metodo agregara los nuevos usurios a los archivos txt
	void guardarTodos(List<UsuarioDTO> usuarios);
}