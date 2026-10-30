package logica;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import datos.dao.DAOFactory;
import datos.dao.DAOLoader;
import datos.dto.ServicioDTO;
import datos.dto.UsuarioDTO;
import datos.dto.VehiculoDTO;
import enums.TipoServicio;
import modelo.Servicio;
import modelo.Usuario;
import modelo.Vehiculo;

public class SistemaGuardado {

	private final List<Servicio> servicios;
	private final Map<String, Vehiculo> vehiculos;
	private final List<Usuario> usuarios;
	private final DAOFactory fabrica;   // junto a los otros atributos

	public SistemaGuardado() {
		this(DAOLoader.getFactory());
	}

	public SistemaGuardado(DAOFactory fabrica) {
		this.fabrica = fabrica;
		
		// Obtiene los dto mediante los DAO
		List<ServicioDTO> serviciosDTO = fabrica.getServicioDAO().obtenerTodos();
		Map<String, VehiculoDTO> vehiculosDTO = fabrica.getVehiculoDAO().obtenerTodos();
		List<UsuarioDTO> usuariosDTO = fabrica.getUsuarioDAO().obtenerTodos();

		// Convierte los servicios dto al modelo
		servicios = new ArrayList<>();

		for (ServicioDTO dto : serviciosDTO) {

			Servicio servicio = new Servicio(dto.getNombre(), dto.getTarifaBase(), dto.getPrecioKm(),
					dto.getPrecioMinuto(), dto.getTipoVehiculo(), dto.getCategoriaVehiculo(), dto.getTipoServicio());
			servicios.add(servicio);
		}
		// Convierte los vehiculos dto al modelo
		vehiculos = new LinkedHashMap<>();

		for (VehiculoDTO dto : vehiculosDTO.values()) {
			List<TipoServicio> tipos = dto.getTiposServicio();

			if (tipos == null || tipos.isEmpty()) {
				throw new IllegalArgumentException("El vehiculo no tiene servicios");
			}
			// Crea el vehiculo con su primer servicio
			Vehiculo vehiculo = new Vehiculo(dto.getPatente(), dto.getModelo(), dto.getCapacidadPasajeros(),
					dto.getTipoVehiculo(), dto.getCategoriaVehiculo(), tipos.get(0));
			// Agrega los otros servicios
			for (int i = 1; i < tipos.size(); i++) {
				vehiculo.agregarTipoServicio(tipos.get(i));
			}
			vehiculos.put(vehiculo.getPatente(), vehiculo);
		}
		// Convierte los usuarios dto al modelo
		usuarios = new ArrayList<>();

		for (UsuarioDTO dto : usuariosDTO) {
			Usuario usuario = new Usuario(dto.getNombre(), dto.getTelefono(), dto.getEmail());

			// Si tiene lisencia, se registra como conductor
			if (dto.getLicencia() != null) {

				List<String> patentes = dto.getPatentes();

				if (patentes == null || patentes.isEmpty()) {
					throw new IllegalArgumentException("El conductor no tiene vehiculos");
				}
				// Busca el primer vehiculo en el mapa
				Vehiculo primero = vehiculos.get(patentes.get(0));

				if (primero == null) {
					throw new IllegalArgumentException("Patente inexistente: " + patentes.get(0));
				}

				usuario.altaConductor(dto.getLicencia(), primero);

				// Agrega los demas vehiculos
				for (int i = 1; i < patentes.size(); i++) {
					Vehiculo extra = vehiculos.get(patentes.get(i));

					if (extra == null) {
						throw new IllegalArgumentException("Patente inexistente: " + patentes.get(i));
					}
					usuario.getConductor().agregarVehiculo(extra);
				}
			}
			usuarios.add(usuario);
		}
	}

	public List<Servicio> getServicios() {
		return servicios;
	}

	public Map<String, Vehiculo> getVehiculos() {
		return vehiculos;
	}

	public List<Usuario> getUsuarios() {
		return usuarios;
	}
	
	// Pasa los usuarios del modelo a DTO y le pide al DAO que los escriba
	public void guardarUsuarios() {
		List<UsuarioDTO> dtos = new ArrayList<>();

		for (Usuario u : usuarios) {
			String licencia = null;
			List<String> patentes = new ArrayList<>();

			if (u.getConductor() != null) {
				licencia = u.getConductor().getLicenciaConducir();
				for (Vehiculo v : u.getConductor().getVehiculos()) {
					patentes.add(v.getPatente());
				}
			}
			dtos.add(new UsuarioDTO(u.getNombre(), u.getTelefono(), u.getEmail(), licencia, patentes));
		}
		fabrica.getUsuarioDAO().guardarTodos(dtos);
	}

	// Valida el email, agrega el usuario a la lista y lo guarda en el txt
	public void registrarUsuario(Usuario nuevo) {
		for (Usuario u : usuarios) {
			if (u.getEmail().equalsIgnoreCase(nuevo.getEmail())) {
				throw new IllegalArgumentException("Ya existe un usuario con ese email");
			}
		}
		usuarios.add(nuevo);
		guardarUsuarios();
	}	
}