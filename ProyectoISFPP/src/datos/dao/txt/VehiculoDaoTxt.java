package datos.dao.txt;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import datos.dao.VehiculoDAO;
import enums.CategoriaVehiculo;
import enums.TipoServicio;
import enums.TipoVehiculo;
//import logica.GeneradorUbicacion;
import modelo.Vehiculo;

public class VehiculoDaoTxt implements VehiculoDAO {

	private static final int CAMPOS_MINIMOS = 6;//son los datos minimos ya que dicho vehiculo debe tener almenos un servicio

	private final String archivo;//se usa para almacenar el nombre del archivo
	//private final GeneradorUbicacion generador;//se usara para la cracion de la ubcacion de dicho vehiculo ya que este no cuenta con una propia desde el txt

	public VehiculoDaoTxt(String archivo /*, GeneradorUbicacion generador*/) {
		this.archivo = archivo;
		//this.generador = generador;
	}

	@Override
	public Map<String, Vehiculo> obtenerTodos() {
		Map<String, Vehiculo> vehiculos = new LinkedHashMap<>();//se crea un map para almacenar todos los vehiculos de manera que se pueda buscar usando como llave su matricula
		for (Linea linea : LectorArchivo.leer(archivo)) {//lee cada linea de la list que contiene todas las lineas del txt
			Vehiculo vehiculo = crear(linea);//se crea un nuevo vehiculo dada dicha linea y el metodo crear valida si se puede crear
			if (vehiculos.containsKey(vehiculo.getPatente())) {//verifica que dicha patente del vehiculo no haya sido registrada antes
				throw new ArchivoInvalidoException(archivo, linea.getNumero(),//si es asi lanza una excepcion que la patente esta repetida
						"patente repetida " + vehiculo.getPatente());
			}
			vehiculos.put(vehiculo.getPatente(), vehiculo);//carga el vehiculo creado al mapa usando como llave su patente
		}
		return vehiculos;//retorna el map de vehiculos cargados del txt o almenos los que son validos
	}

	private Vehiculo crear(Linea linea) {
		List<String> c = linea.getCampos();//se le asigna dicha linea del txt a una nueva list
		if (c.size() < CAMPOS_MINIMOS) {//velida que tenga los campos minimos requeridos
			throw new ArchivoInvalidoException(archivo, linea.getNumero(),
					//si no es asi lanza una excepcion diiendo que dicha linea no cumple con los capos minimos
					"se esperaban al menos " + CAMPOS_MINIMOS + " campos y hay " + c.size());
		}
		try {
			Vehiculo vehiculo = new Vehiculo(c.get(0), c.get(1), Integer.parseInt(c.get(2)),
					TipoVehiculo.valueOf(c.get(3)), CategoriaVehiculo.valueOf(c.get(4)),
					TipoServicio.valueOf(c.get(5)));
			// si trae un segundo tipo de servicio se le agraga al vehiculo
			for (int i = CAMPOS_MINIMOS; i < c.size(); i++) {
				vehiculo.agregarTipoServicio(TipoServicio.valueOf(c.get(i)));
			}
			return vehiculo;
		} catch (IllegalArgumentException e) {
			throw new ArchivoInvalidoException(archivo, linea.getNumero(), "valor invalido (" + e.getMessage() + ")");
		}
	}
}