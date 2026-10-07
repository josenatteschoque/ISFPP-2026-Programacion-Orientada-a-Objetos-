package modelo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

import enums.CalificacionViaje;
import enums.RolUsuario;
import enums.EstadoViaje;
import enums.EstadoConductor;

public class Viaje {
	private UUID id;
	private String motivoCancelacion;

	private Ubicacion origen;
	private Ubicacion destino;
	private Servicio servicio;
	private List<RegistroViaje> registroViaje;
	private Vehiculo vehiculo;
	private Usuario cliente;
	private Usuario conductor;

	private RolUsuario rolCancela;
	private CalificacionViaje calificacionConductor;
	private CalificacionViaje calificacionCliente;

	public Viaje(UUID id, Ubicacion origen, Ubicacion destino, Servicio servicio, Usuario cliente) {
		super();
		this.id = id;
		this.origen = origen;
		this.destino = destino;
		this.servicio = servicio;
		this.cliente = cliente;
		this.registroViaje = new ArrayList<>();
	}

	public Viaje(Usuario cliente, Ubicacion origen, Ubicacion destino, Servicio servicio) {
		this(UUID.randomUUID(), origen, destino, servicio, cliente);
	}

	public void solicitar(LocalDateTime fechaHora) {
		if (!registroViaje.isEmpty()) {
			throw new IllegalStateException("El viaje ya fue solicitado");
		}
		if (cliente.getCliente().enViaje()) {
			throw new IllegalStateException("El cliente ya tiene un viaje activo");
		}
		this.registroViaje.add(new RegistroViaje(fechaHora, EstadoViaje.SOLICITADO));
		this.cliente.getCliente().agregarViaje(this);
	}

	public void aceptar(LocalDateTime fechaHora, Usuario conductor) {
		if (estadoActual() != EstadoViaje.SOLICITADO) {
			throw new IllegalStateException("El viaje no esta solicitado");
		}
		Vehiculo vehiculoConductor = conductor.getConductor().getVehiculoActivo();
		if (vehiculoConductor.getCategoriaVehiculo().getValor() < servicio.getCategoriaVehiculo().getValor()) {
			throw new IllegalStateException("El vehiculo no cumple con la categoria solicitada");
		}
		this.conductor = conductor;
		this.vehiculo = vehiculoConductor;
		conductor.getConductor().setEstado(EstadoConductor.VIAJE_A_ORIGEN);
		this.registroViaje.add(new RegistroViaje(fechaHora, EstadoViaje.ACEPTADO));
	}

	public void iniciar(LocalDateTime fechaHora) {

		if (estadoActual() != EstadoViaje.ACEPTADO) {
			throw new IllegalStateException("El viaje no esta aceptado");
		}
		conductor.getConductor().setEstado(EstadoConductor.VIAJE_A_DESTINO);
		this.registroViaje.add(new RegistroViaje(fechaHora, EstadoViaje.INICIADO));
	}

	public void finalizar(LocalDateTime fechaHora, CalificacionViaje calificacionCliente,
			CalificacionViaje calificacionConductor) {
		if (estadoActual() != EstadoViaje.INICIADO) {
			throw new IllegalStateException("El viaje no esta iniciado");
		}
		this.calificacionCliente = calificacionCliente;
		this.calificacionConductor = calificacionConductor;
		conductor.getConductor().setEstado(EstadoConductor.DISPONIBLE);
		this.registroViaje.add(new RegistroViaje(fechaHora, EstadoViaje.FINALIZADO));
	}

	public void cancelar(LocalDateTime fechaHora, Usuario usuario, String motivo) {

		EstadoViaje estado = estadoActual();
		if (estado == EstadoViaje.FINALIZADO || estado == EstadoViaje.CANCELADO || estado == EstadoViaje.RECHAZADO) {
			throw new IllegalStateException("El viaje no se puede cancelar");
		}
		if (usuario.equals(cliente)) {
			this.rolCancela = RolUsuario.CLIENTE;
		} else if (usuario.equals(conductor)) {
			this.rolCancela = RolUsuario.CONDUCTOR;
		} else {

			throw new IllegalArgumentException("El usuario no pertenece al viaje");
		}
		this.motivoCancelacion = motivo;
		if (conductor != null) {
			conductor.getConductor().setEstado(EstadoConductor.DISPONIBLE);
		}
		this.registroViaje.add(new RegistroViaje(fechaHora, EstadoViaje.CANCELADO));
	}

	public void rechazar(LocalDateTime fechaHora) {
		if (estadoActual() != EstadoViaje.SOLICITADO) {
			throw new IllegalStateException("El viaje no esta solicitado");
		}
		this.registroViaje.add(new RegistroViaje(fechaHora, EstadoViaje.RECHAZADO));
	}

	public EstadoViaje estadoActual() {
		if (registroViaje.isEmpty()) {
			return null;
		}
		return this.registroViaje.get(this.registroViaje.size() - 1).getEstadoViaje();
	}

	// Getters y Setters
	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getMotivoCancelacion() {
		return motivoCancelacion;
	}

	public void setMotivoCancelacion(String motivoCancelacion) {
		this.motivoCancelacion = motivoCancelacion;
	}

	public Ubicacion getOrigen() {
		return origen;
	}

	public void setOrigen(Ubicacion origen) {
		this.origen = origen;
	}

	public Ubicacion getDestino() {
		return destino;
	}

	public void setDestino(Ubicacion destino) {
		this.destino = destino;
	}

	public Servicio getServicio() {
		return servicio;
	}

	public void setServicio(Servicio servicio) {
		this.servicio = servicio;
	}

	public List<RegistroViaje> getRegistroViaje() {
		return registroViaje;
	}

	public Vehiculo getVehiculo() {
		return vehiculo;
	}

	public void setVehiculo(Vehiculo vehiculo) {
		this.vehiculo = vehiculo;
	}

	public Usuario getCliente() {
		return cliente;
	}

	public void setCliente(Usuario cliente) {
		this.cliente = cliente;
	}

	public Usuario getConductor() {
		return conductor;
	}

	public void setConductor(Usuario conductor) {
		this.conductor = conductor;
	}

	public RolUsuario getRolCancela() {
		return rolCancela;
	}

	public void setRolCancela(RolUsuario rolCancela) {
		this.rolCancela = rolCancela;
	}

	public CalificacionViaje getCalificacionConductor() {
		return calificacionConductor;
	}

	public void setCalificacionConductor(CalificacionViaje calificacionConductor) {
		this.calificacionConductor = calificacionConductor;
	}

	public CalificacionViaje getCalificacionCliente() {
		return calificacionCliente;
	}

	public void setCalificacionCliente(CalificacionViaje calificacionCliente) {
		this.calificacionCliente = calificacionCliente;
	}

}
