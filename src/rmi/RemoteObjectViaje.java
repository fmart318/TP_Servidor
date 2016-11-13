package rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import dao.HibernateDAOCliente;
import dao.HibernateDAOVehiculo;
import dao.HibernateDAOViaje;
import dto.ClienteDTO;
import dto.EnvioDTO;
import dto.PedidoDTO;
import dto.RutaDTO;
import dto.SucursalDTO;
import dto.TrayectoDTO;
import dto.VehiculoDTO;
import dto.ViajeDTO;
import entities.Envio;
import entities.Viaje;

public class RemoteObjectViaje extends UnicastRemoteObject implements RemoteInterfaceViaje {

	private static final long serialVersionUID = -6219019782337720132L;
	private HibernateDAOViaje hbtDAOViaje;
	private HibernateDAOCliente hbtDAOCliente;
	
	protected RemoteObjectViaje() throws RemoteException {
		super();
		hbtDAOViaje = HibernateDAOViaje.getInstancia();
	}

	private static Viaje ViajeToEntity(ViajeDTO viajeDTO) {
		List<Envio> envios = new ArrayList<Envio>();
		for (EnvioDTO envioDTO : viajeDTO.getEnvios())
			envios.add(EnvioToEntity(envioDTO));
		return new Viaje(viajeDTO.getIdViaje(), envios,
				viajeDTO.getFechaLlegada(),
				SucursalToEntity(viajeDTO.getSucursalOrigen()),
				SucursalToEntity(viajeDTO.getSucursalDestino()),
				viajeDTO.isFinalizado(),
				VehiculoToEntity(viajeDTO.getVehiculo()));
	}

	public void altaViaje(ViajeDTO viajeDTO) {
		hbtDAOViaje.guardar(ViajeToEntity(viajeDTO));
	}
	
	public List<ViajeDTO> ObtenerViajesDeCliente(int idCliente) {
		return hbtDAOViaje.obtenerViajesDeCliente(idCliente);
	}

	public float SeleccionarViaje(int idViaje) {
		return hbtDAOViaje.seleccionarViaje(idViaje);
	}
	
	public List<ViajeDTO> obtenerViajes() throws RemoteException {
		return hbtDAOViaje.obtenerViajes();
	}
	
	public ViajeDTO obtenerViajePorVehiculo(VehiculoDTO vehiculo) {
		return hbtDAOViaje.obtenerViajePorVehiculo(vehiculo);
	}

	public void actualiarViaje(ViajeDTO viajeDTO) {
		hbtDAOViaje.updateViaje(ViajeToEntity(viajeDTO));

	}

	private List<ViajeDTO> obtenerViajesPorPedidos(List<PedidoDTO> pedidosDTO) {
		return hbtDAOViaje.obtenerViajesDePedidos(pedidosDTO);
	}

	// QUE RECIBA UN int como los minutos a demorar sino creo que es mas
	// complicado
	public void demorarViaje(ViajeDTO viajeDTO, int m) {

		long milisegundos = 60000;
		Date auxiliar = viajeDTO.getFechaLlegada();
		long minutosAux = m * milisegundos;
		Date auxiliar2 = new Date(auxiliar.getTime() + minutosAux);
		viajeDTO.setFechaLlegada(auxiliar2);
		hbtDAOViaje.updateViaje(ViajeToEntity(viajeDTO));
	}

	// En realidad lo unico que tiene es el id que le paso el empleado
	public ViajeDTO obtenerViaje(ViajeDTO viajeDTO) {
		return hbtDAOViaje.obtenerViaje(viajeDTO.getIdViaje());
	}

	// el Trayecto pasado como parametro tiene los nuevos valores de km ,
	// tiempo, y precio
	// Lo habia pensado como que solo se llama para decir que un trayecto no se
	// puede hacer pero el CU dice que se modifica
	// el precio, km ,etc entonces lo cambie. Y agregue que si el trayecto ahora
	// es mejor que antes entonces no cambio nada solamente
	// actualizo los tiempos

	public void actualizarViajes(TrayectoDTO trayDTO, SucursalDTO sucursalDTO) {
		cargarMapaDeRuta();
		TrayectoDTO aux = new TrayectoDTO();
		float btiempo = 99999999;
		// aux=hbtDAO.obtenerTrayecto(trayDTO);

		actualizarMapaDeRutas(trayDTO);
		List<RutaDTO> rutas = new ArrayList<RutaDTO>();
		rutas = obtenerTodasLasRutasDirectas(trayDTO.getSucursalOrigen());

		aux = new TrayectoDTO();
		for (RutaDTO r : rutas) {
			// Se que tiene un solo trayecto porque eso es lo que devuelve
			// obtenerTodasLasRutasDirectas
			TrayectoDTO t = r.getTrayectos().get(0);
			;
			float tiempo = 0;

			// Veo cual es la sucursal mas cercana en TIEMPO o si me conviene
			// volver a la sucursal de origen

			tiempo = t.getTiempo();
			if (tiempo < btiempo) {
				aux = t;
				btiempo = tiempo;
			}

		}

		rutas = obtenerTodasLasRutasDirectas(trayDTO.getSucursalDestino());
		for (RutaDTO r : rutas) {
			float tiempo = 0;
			TrayectoDTO t = r.getTrayectos().get(0);
			if (t.getSucursalDestino() == trayDTO.getSucursalOrigen()) {
				if (tiempo < btiempo) {
					aux = t;
					btiempo = tiempo;
				}
			}
		}

		// DEBERIA OBTENER LOS VIAJES SOLO DELA SUCURSAL DE ORIGEN PERO DEVUELVE
		// TODOS
		// ASIQ VERIFICO QUE LA SUCURSAL ORIGEN SEA LA INDICADA
		List<ViajeDTO> viajesDTO = hbtDAOViaje.obtenerViajes();
		for (ViajeDTO v : viajesDTO) {
			if (v.getSucursalOrigen().getIdSucursal() == trayDTO
					.getSucursalOrigen().getIdSucursal()
					&& v.getSucursalDestino().getIdSucursal() == trayDTO
							.getSucursalDestino().getIdSucursal()) {
				v.setSucursalDestino(aux.getSucursalDestino());
				long m = (long) aux.getTiempo();
				long milisegundos = 60000;
				Date auxiliar = Calendar.getInstance().getTime();
				long minutosAux = m * milisegundos;
				Date auxiliar2 = new Date(auxiliar.getTime() + minutosAux);
				v.setFechaLlegada(auxiliar2);
				for (EnvioDTO e : v.getEnvios()) {
					e.setFechaLlegada(auxiliar2);
					hbtDAOViaje.modificar(EnvioToEntity(e));
				}
				hbtDAOViaje.modificar(ViajeToEntity(v));
			}

		}

	}
}
