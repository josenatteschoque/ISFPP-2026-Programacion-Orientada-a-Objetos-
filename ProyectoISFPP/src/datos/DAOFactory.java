package datos;

public interface DAOFactory {

	ServicioDAO getServicioDAO();

	VehiculoDAO getVehiculoDAO();

	UsuarioDAO getUsuarioDAO();
}