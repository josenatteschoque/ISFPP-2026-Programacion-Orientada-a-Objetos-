package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import datos.dao.DAOFactory;
import datos.dao.DAOLoader;
import datos.dao.txt.ArchivoInvalidoException;
import datos.dao.txt.ServicioDaoTxt;
import datos.dao.txt.UsuarioDaoTxt;
import datos.dao.txt.VehiculoDaoTxt;
import enums.TipoServicio;
//import logica.GeneradorUbicacion;
import modelo.Servicio;
import modelo.Usuario;
import modelo.Vehiculo;

class TestCargaDatos {

//	private static GeneradorUbicacion generador;
	private static List<Servicio> servicios;
	private static Map<String, Vehiculo> vehiculos;
	private static List<Usuario> usuarios;

	@BeforeAll
	static void cargarDatosReales() {
		//generador = new GeneradorUbicacion(-42.87689, -65.20872, -42.57303, -64.78616);

		DAOFactory fabrica = DAOLoader.getFactory();
		servicios = fabrica.getServicioDAO().obtenerTodos();
		vehiculos = fabrica.getVehiculoDAO().obtenerTodos();
		usuarios = fabrica.getUsuarioDAO().obtenerTodos(vehiculos);
	}

	// crea un archivo temporal con el contenido indicado
	private Path archivo(Path carpeta, String contenido) throws IOException {
		return Files.writeString(carpeta.resolve("datos.txt"), contenido);
	}

	// ---------- carga de los archivos reales ----------

	@Test
	void cargaServicios() {
		assertEquals(6, servicios.size());
		assertEquals(2000, servicios.get(0).getTarifaBase(), 0.001);
	}

	@Test
	void cargaVehiculosConUnoYDosTiposDeServicio() {
		assertEquals(15, vehiculos.size());
		assertEquals(2, vehiculos.get("AB456DE").getTipoServicios().size());
		assertTrue(vehiculos.get("AB456DE").getTipoServicios().contains(TipoServicio.ENVIOS));
		assertEquals(1, vehiculos.get("AD123BC").getTipoServicios().size());
		assertNull(vehiculos.get("AD123BC").getUbicacion());
	}

	@Test
	void cargaUsuariosYConductores() {
		assertEquals(50, usuarios.size());
		assertEquals(10, usuarios.stream().filter(u -> u.getConductor() != null).count());

		Usuario juan = usuarios.get(0);
		assertEquals("A13-B1-D1-E1", juan.getConductor().getLicenciaConducir());
		assertEquals(3, juan.getConductor().getVehiculos().size());

		Usuario maria = usuarios.get(1); // solo cliente
		assertNull(maria.getConductor());
	}

	// ---------- errores en los archivos ----------

	@Test
	void servicioConValorInvalidoIndicaLaLinea(@TempDir Path carpeta) throws IOException {
		Path f = archivo(carpeta, "# comentario\nEstandar;2000;200;100;AUTO;LUJO;PASAJEROS;\n");

		ArchivoInvalidoException e = assertThrows(ArchivoInvalidoException.class,
				() -> new ServicioDaoTxt(f.toString()).obtenerTodos());
		assertEquals(2, e.getNumeroLinea());
	}

	@Test
	void vehiculoConCamposDeMenos(@TempDir Path carpeta) throws IOException {
		Path f = archivo(carpeta, "AB123CD;Ford Ka;4;AUTO;\n");

		assertThrows(ArchivoInvalidoException.class, () -> new VehiculoDaoTxt(f.toString()/*, generador*/).obtenerTodos());
	}

	@Test
	void vehiculoConPatenteRepetida(@TempDir Path carpeta) throws IOException {
		Path f = archivo(carpeta,
				"AB123CD;Ford Ka;4;AUTO;ESTANDAR;PASAJEROS;\n" + "AB123CD;Fiat Uno;4;AUTO;ESTANDAR;PASAJEROS;\n");

		assertThrows(ArchivoInvalidoException.class, () -> new VehiculoDaoTxt(f.toString()/*, generador*/).obtenerTodos());
	}

	@Test
	void usuarioConLicenciaSinPatente(@TempDir Path carpeta) throws IOException {
		Path f = archivo(carpeta, "Ana;+54911;ana@email.com;B1;\n");

		assertThrows(ArchivoInvalidoException.class, () -> new UsuarioDaoTxt(f.toString()).obtenerTodos(vehiculos));
	}

	@Test
	void usuarioConPatenteInexistente(@TempDir Path carpeta) throws IOException {
		Path f = archivo(carpeta, "Ana;+54911;ana@email.com;B1;ZZZ999;\n");

		assertThrows(ArchivoInvalidoException.class, () -> new UsuarioDaoTxt(f.toString()).obtenerTodos(vehiculos));
	}

	@Test
	void vehiculoConDosConductores(@TempDir Path carpeta) throws IOException {
		Path f = archivo(carpeta, "Ana;+54911;ana@email.com;B1;AB456DE;\nLuis;+54912;luis@email.com;B1;AB456DE;\n");

		assertThrows(ArchivoInvalidoException.class, () -> new UsuarioDaoTxt(f.toString()).obtenerTodos(vehiculos));
	}

	@Test
	void emailRepetido(@TempDir Path carpeta) throws IOException {
		Path f = archivo(carpeta, "Ana;+54911;mismo@email.com;\nLuis;+54912;mismo@email.com;\n");

		assertThrows(ArchivoInvalidoException.class, () -> new UsuarioDaoTxt(f.toString()).obtenerTodos(vehiculos));
	}

	// ---------- configuracion ----------

	@Test
	void configuracionInexistente() {
		assertThrows(IllegalStateException.class, () -> DAOLoader.crearFactory("noexiste.properties"));	}

	@Test
	void configuracionSinUnaPropiedad(@TempDir Path carpeta) throws IOException {
		Path f = archivo(carpeta, "usuario=usuarios.txt\n");
		assertThrows(IllegalStateException.class, () -> DAOLoader.crearFactory(f.toString()));
	}
}