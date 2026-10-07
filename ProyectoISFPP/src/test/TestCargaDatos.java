package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import datos.ArchivoInvalidoException;
import datos.CargaDatos;
import datos.CargaServicios;
import datos.CargaUsuarios;
import datos.CargaVehiculos;
import datos.Configuracion;
import datos.Datos;
import enums.TipoServicio;
import logica.GeneradorUbicacion;
import modelo.Usuario;
import modelo.Vehiculo;

class TestCargaDatos {

	private static Configuracion config;
	private static GeneradorUbicacion generador;
	private static Datos datos;

	@BeforeAll
	static void cargarDatosReales() {
		config = new Configuracion();
		generador = new GeneradorUbicacion(config.getLatitud1(), config.getLongitud1(), config.getLatitud2(),
				config.getLongitud2());
		datos = CargaDatos.cargar(config);
	}

	// crea un archivo temporal con el contenido indicado
	private Path archivo(Path carpeta, String contenido) throws IOException {
		return Files.writeString(carpeta.resolve("datos.txt"), contenido);
	}

	// ---------- carga de los archivos reales ----------

	@Test
	void cargaServicios() {
		assertEquals(6, datos.getServicios().size());
		assertEquals(2000, datos.getServicios().get(0).getTarifaBase(), 0.001);
	}

	@Test
	void cargaVehiculosConUnoYDosTiposDeServicio() {
		Map<String, Vehiculo> vehiculos = datos.getVehiculos();

		assertEquals(15, vehiculos.size());
		assertEquals(2, vehiculos.get("AB456DE").getTipoServicios().size());
		assertTrue(vehiculos.get("AB456DE").getTipoServicios().contains(TipoServicio.ENVIOS));
		assertEquals(1, vehiculos.get("AD123BC").getTipoServicios().size());
		assertNotNull(vehiculos.get("AD123BC").getUbicacion());
	}

	@Test
	void cargaUsuariosYConductores() {
		assertEquals(50, datos.getUsuarios().size());
		assertEquals(10, datos.getUsuarios().stream().filter(u -> u.getConductor() != null).count());

		Usuario juan = datos.getUsuarios().get(0);
		assertEquals("A13-B1-D1-E1", juan.getConductor().getLicenciaConducir());
		assertEquals(3, juan.getConductor().getVehiculos().size());

		Usuario maria = datos.getUsuarios().get(1); // solo cliente
		assertNull(maria.getConductor());
	}

	// ---------- errores en los archivos ----------

	@Test
	void servicioConValorInvalidoIndicaLaLinea(@TempDir Path carpeta) throws IOException {
		Path f = archivo(carpeta, "# comentario\nEstandar;2000;200;100;AUTO;LUJO;PASAJEROS;\n");

		ArchivoInvalidoException e = assertThrows(ArchivoInvalidoException.class,
				() -> CargaServicios.cargar(f.toString()));
		assertEquals(2, e.getNumeroLinea());
	}

	@Test
	void vehiculoConCamposDeMenos(@TempDir Path carpeta) throws IOException {
		Path f = archivo(carpeta, "AB123CD;Ford Ka;4;AUTO;\n");

		assertThrows(ArchivoInvalidoException.class, () -> CargaVehiculos.cargar(f.toString(), generador));
	}

	@Test
	void vehiculoConPatenteRepetida(@TempDir Path carpeta) throws IOException {
		Path f = archivo(carpeta, "AB123CD;Ford Ka;4;AUTO;ESTANDAR;PASAJEROS;\n"
				+ "AB123CD;Fiat Uno;4;AUTO;ESTANDAR;PASAJEROS;\n");

		assertThrows(ArchivoInvalidoException.class, () -> CargaVehiculos.cargar(f.toString(), generador));
	}

	@Test
	void usuarioConLicenciaSinPatente(@TempDir Path carpeta) throws IOException {
		Path f = archivo(carpeta, "Ana;+54911;ana@email.com;B1;\n");

		assertThrows(ArchivoInvalidoException.class, () -> CargaUsuarios.cargar(f.toString(), datos.getVehiculos()));
	}

	@Test
	void usuarioConPatenteInexistente(@TempDir Path carpeta) throws IOException {
		Path f = archivo(carpeta, "Ana;+54911;ana@email.com;B1;ZZZ999;\n");

		assertThrows(ArchivoInvalidoException.class, () -> CargaUsuarios.cargar(f.toString(), datos.getVehiculos()));
	}

	@Test
	void vehiculoConDosConductores(@TempDir Path carpeta) throws IOException {
		Path f = archivo(carpeta,
				"Ana;+54911;ana@email.com;B1;AB456DE;\nLuis;+54912;luis@email.com;B1;AB456DE;\n");

		assertThrows(ArchivoInvalidoException.class, () -> CargaUsuarios.cargar(f.toString(), datos.getVehiculos()));
	}

	@Test
	void emailRepetido(@TempDir Path carpeta) throws IOException {
		Path f = archivo(carpeta, "Ana;+54911;mismo@email.com;\nLuis;+54912;mismo@email.com;\n");

		assertThrows(ArchivoInvalidoException.class, () -> CargaUsuarios.cargar(f.toString(), datos.getVehiculos()));
	}

	// ---------- configuracion ----------

	@Test
	void configuracionInexistente() {
		assertThrows(IllegalStateException.class, () -> new Configuracion("noexiste.properties"));
	}

	@Test
	void configuracionSinUnaPropiedad(@TempDir Path carpeta) throws IOException {
		Path f = archivo(carpeta, "usuario=usuarios.txt\n");
		Configuracion incompleta = new Configuracion(f.toString());

		assertThrows(IllegalStateException.class, () -> incompleta.getArchivoServicios());
	}
}