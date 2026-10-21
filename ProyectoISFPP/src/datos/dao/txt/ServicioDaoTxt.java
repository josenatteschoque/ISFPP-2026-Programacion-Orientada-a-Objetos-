package datos.dao.txt;

import java.util.ArrayList;
import java.util.List;

import datos.dao.ServicioDAO;
import enums.CategoriaVehiculo;
import enums.TipoServicio;
import enums.TipoVehiculo;
import modelo.Servicio;

public class ServicioDaoTxt implements ServicioDAO {

	private static final int CAMPOS = 7;//se usa como parametro de los campos resibidos de parte del txt

	private final String archivo;//donde se almacena el nombre del archivo

	public ServicioDaoTxt(String archivo) {
		this.archivo = archivo;//se le asigna el nombre del archivo
	}

	@Override
	public List<Servicio> obtenerTodos() {
		List<Servicio> servicios = new ArrayList<>();//se crea una list para almacenar todos los servicios permitidos del archivo
		for (Linea linea : LectorArchivo.leer(archivo)) {//se lee una linea dela list de lineas que almacena cada linea del archivo txt
			servicios.add(crear(linea));//se fija si se pudo crear dicho servicio apartir de la linea si es asi se le agrega ala list
		}
		return servicios;//retorna la list de servicios validos del txt
	}

	private Servicio crear(Linea linea) {
		List<String> c = linea.getCampos(); //se le asigna ala list la list que contiene los campos de una linea del txt
		if (c.size() != CAMPOS) {//verifica que contenga dicha linea el tamaño o campos de informacion correcta
			throw new ArchivoInvalidoException(archivo, linea.getNumero(),//si no es asi suelta una excepcion que faltan campos
					"se esperaban " + CAMPOS + " campos y hay " + c.size());
		}
		try { //se usa un try catch para validar que no haya errores ala hora de crear el objeto servicio y si es asi que no rompa el programa y solo tire una excepcion
			 Servicio servi = new Servicio(c.get(0), Double.parseDouble(c.get(1)), Double.parseDouble(c.get(2)),
					Double.parseDouble(c.get(3)), TipoVehiculo.valueOf(c.get(4)),
					CategoriaVehiculo.valueOf(c.get(5)), TipoServicio.valueOf(c.get(6)));
			 return servi;
		} catch (IllegalArgumentException e) {
			//se convierte en una excepcion que indica archivo y linea del error
			throw new ArchivoInvalidoException(archivo, linea.getNumero(), "valor invalido" + e.getMessage());
		}
	}
}