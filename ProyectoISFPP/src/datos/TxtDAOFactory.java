package datos;

import logica.GeneradorUbicacion;

public class TxtDAOFactory implements DAOFactory {

	private final Configuracion config;//crea un nuevo objeto configuracion 

	public TxtDAOFactory(Configuracion config) {
		this.config = config;
	}

	@Override
	public ServicioDAO getServicioDAO() {
		return new ServicioDaoTxt(config.getArchivoServicios());//se crea la carga de datos de el archivo servicios sacando el nobre del archivo del config
	}

	@Override
	public VehiculoDAO getVehiculoDAO() {
		GeneradorUbicacion generador = new GeneradorUbicacion(config.getLatitud1(), config.getLongitud1(),
				config.getLatitud2(), config.getLongitud2());//crea un generador pasandole los parametros de config osea ls limites de madryn
		return new VehiculoDaoTxt(config.getArchivoVehiculos(), generador);//se crea la carga de datos de el archivo vehiculo pasandole el nombre de dicho archivo y el generador para la cracion de cada vehiculo
	}

	@Override
	public UsuarioDAO getUsuarioDAO() {
		return new UsuarioDaoTxt(config.getArchivoUsuarios());//se crea la carga de datos del archivo usuario apsandole el nombre del archivo sacado de config
	}
}