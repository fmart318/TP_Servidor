package rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import Strategy.PoliticaEspecificidad;
import Strategy.PoliticaGarantia;
import Strategy.PoliticaGeneral;
import Strategy.PoliticaMantenimiento;
import dao.HibernateDAO;
import dto.CargaDTO;
import dto.ClienteDTO;
import dto.EnvioDTO;
import dto.FacturaDTO;
import dto.HabilitadoDTO;
import dto.MapaDeRutaDTO;
import dto.PedidoDTO;
import dto.PlanDeMantenimientoDTO;
import dto.ProveedorDTO;
import dto.RemitoDTO;
import dto.RutaDTO;
import dto.SeguroDTO;
import dto.SucursalDTO;
import dto.TransporteDTO;
import dto.TrayectoDTO;
import dto.VehiculoDTO;
import dto.ViajeDTO;
import entities.Cliente;
import entities.Pedido;

public class RemoteObject extends UnicastRemoteObject implements RemoteInterface {

	private static final long serialVersionUID = 1L;
	private static HibernateDAO hibernateDAO;
	private EntityManager entityManager;
	public MapaDeRutaDTO mapaDeRuta;
	private PoliticaMantenimiento politicaMantenimiento;

	public void InicializarMapaDeRuta() {
		cargarMapaDeRuta();
	}

	public RemoteObject() throws RemoteException {
		super();
		hibernateDAO = HibernateDAO.getInstancia();
	}

	public void altaPedido(PedidoDTO pedidoDTO) {

		Pedido pedido = new Pedido();
		SucursalDTO sucursalOrigen = obtenerSucursal(pedidoDTO.getSucursalOrigen());
		SucursalDTO sucursalDestino = obtenerSucursal(pedidoDTO.getSucursalDestino());
		pedidoDTO.setPrecio(calcularPrecioRuta(sucursalOrigen, sucursalDestino));
		pedido = entityManager.pedidoToEntity(pedidoDTO);

		hibernateDAO.guardar(pedido);
	}

	public void altaCliente(ClienteDTO clienteDto) throws RemoteException {
		Cliente cliente = new Cliente();
		cliente = entityManager.clienteToEntity(clienteDto);
		hibernateDAO.guardar(cliente);
	}

	public float calcularPrecioRuta(SucursalDTO sucursalOrigen, SucursalDTO sucursalDestino) {
		cargarMapaDeRuta();
		RutaDTO ruta = obtenerMejorRuta(sucursalOrigen, sucursalDestino);
		float precio = 0;
		for (TrayectoDTO trayecto : ruta.getTrayectos()) {
			precio = precio + trayecto.getPrecio();
		}
		return precio;
	}

	public void altaEnvio(EnvioDTO envioDTO) {
		hibernateDAO.guardar(entityManager.envioToEntity(envioDTO));
	}

	public void altaVehiculo(VehiculoDTO vehiculoDTO) {
		hibernateDAO.guardar(entityManager.vehiculoToEntity(vehiculoDTO));
	}

	public void altaPlanMantenimiento(PlanDeMantenimientoDTO planDeMantenimientoDTO) {
		hibernateDAO.guardar(entityManager.planDeMantenimientoToEntity(planDeMantenimientoDTO));
	}

	public void altaViaje(ViajeDTO viajeDTO) {
		hibernateDAO.guardar(entityManager.viajeToEntity(viajeDTO));
	}

	public void altaCarga(CargaDTO cargaDTO) {
		hibernateDAO.guardar(entityManager.cargaToEntity(cargaDTO));
	}

	public void altaRemito(RemitoDTO remitoDTO) {
		hibernateDAO.guardar(entityManager.remitoToEntity(remitoDTO));
	}

	public void altaFactura(FacturaDTO facturaDTO) {
		hibernateDAO.guardar(entityManager.facturaToEntity(facturaDTO));
	}

	public void altaTransporte(TransporteDTO transporteDTO) {
		hibernateDAO.guardar(entityManager.transporteToEntity(transporteDTO));
	}

	public void altaProveedor(ProveedorDTO proveedorDTO) {
		hibernateDAO.guardar(entityManager.proveedorToEntity(proveedorDTO));
	}

	public List<ViajeDTO> obtenerViajesDeCliente(int idCliente) {
		return hibernateDAO.obtenerViajesDeCliente(idCliente);
	}

	public float obtenerDiasHastaFechaDeLlegadaParaViaje(int idViaje) {
		return hibernateDAO.obtenerDiasHastaFechaDeLlegadaParaViaje(idViaje);
	}

	public List<SucursalDTO> obtenerSucursales() throws RemoteException {
		return hibernateDAO.obtenerSucursales();
	}

	public List<ViajeDTO> obtenerViajes() throws RemoteException {
		return hibernateDAO.obtenerViajes();
	}

	private boolean existeClienteParticular(int dni) {
		return hibernateDAO.obtenerClienteParticular(dni) != null;
	}

	public List<SeguroDTO> obtenerSegurosParaCarga(String tipoMercaderia) {
		return hibernateDAO.obtenerSegurosParaCarga(tipoMercaderia);
	}

	public List<VehiculoDTO> obtenerVehiculos() {
		return hibernateDAO.obtenerVehiculos();
	}

	public boolean controlarMantenimientoVehiculo(VehiculoDTO vehiculoDTO) {
		/*
		 * Obtengo la fecha en la que deberian hacerle el mantenimiento al
		 * vehiculo
		 */
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(vehiculoDTO.getFechaUltimoControl());
		calendar.add(Calendar.DATE, vehiculoDTO.getPlanDeMantenimiento().getDiasProxControl());
		Date fechaActual = calendar.getTime();

		boolean vehiculoSeMandoAMantenimiento = false;
		if (vehiculoDTO.getEstado().equals("En Deposito")) {
			System.out.println(vehiculoDTO.getKilometraje() + vehiculoDTO.getPlanDeMantenimiento().getKmProxControl());
			if (vehiculoDTO.getKilometraje() >= vehiculoDTO.getPlanDeMantenimiento().getKmProxControl()) {
				vehiculoSeMandoAMantenimiento = true;
				if (vehiculoDTO.isEnGarantia()) {
					politicaMantenimiento = new PoliticaGarantia();
					politicaMantenimiento.mandarAMantenimiento(vehiculoDTO);
				} else if (vehiculoDTO.isTrabajoEspecifico()) {
					politicaMantenimiento = new PoliticaEspecificidad();
					politicaMantenimiento.mandarAMantenimiento(vehiculoDTO);
				} else {
					politicaMantenimiento = new PoliticaGeneral();
					politicaMantenimiento.mandarAMantenimiento(vehiculoDTO);
				}

				// TODO: Fijarse si estos son los dias necesarios para el
				// control
				vehiculoDTO.getPlanDeMantenimiento().setKmProxControl(vehiculoDTO.getKilometraje() + 200);
				vehiculoDTO.getPlanDeMantenimiento().setDiasProxControl(60);
				hibernateDAO.modificar(entityManager.vehiculoToEntity(vehiculoDTO));
			}
			/*
			 * Me fijo si la fecha calculada al principio esta antes que la de
			 * hoy, si lo esta, mando mantenimiento
			 */
			else if (fechaActual.before(new Date())) {
				vehiculoSeMandoAMantenimiento = true;
				if (vehiculoDTO.isEnGarantia()) {
					politicaMantenimiento = new PoliticaGarantia();
					politicaMantenimiento.mandarAMantenimiento(vehiculoDTO);
				} else if (vehiculoDTO.isTrabajoEspecifico()) {
					politicaMantenimiento = new PoliticaEspecificidad();
					politicaMantenimiento.mandarAMantenimiento(vehiculoDTO);
				} else {
					politicaMantenimiento = new PoliticaGeneral();
					politicaMantenimiento.mandarAMantenimiento(vehiculoDTO);
				}
				// TODO: Fijarse si estos son los dias necesarios para el
				// control
				vehiculoDTO.getPlanDeMantenimiento().setKmProxControl(vehiculoDTO.getKilometraje() + 200);
				vehiculoDTO.getPlanDeMantenimiento().setDiasProxControl(60);
				hibernateDAO.modificar(entityManager.vehiculoToEntity(vehiculoDTO));
			}
		}
		return vehiculoSeMandoAMantenimiento;
	}

	public VehiculoDTO obtenerVehiculo(int vehiculoId) {
		return hibernateDAO.obtenerVehiculo(vehiculoId);

	}

	public ViajeDTO obtenerViajaDelVehiculo(VehiculoDTO vehiculoDTO) {
		return hibernateDAO.obtenerViajeDelVehiculo(vehiculoDTO);
	}

	public void actualizarViaje(ViajeDTO viajeDTO) {
		hibernateDAO.actualizarViaje(entityManager.viajeToEntity(viajeDTO));

	}

	private List<PedidoDTO> obtenerPedidosDelCliente(ClienteDTO clienteDTO) {
		return hibernateDAO.obtenerPedidosDeCliente(clienteDTO.getIdCliente());
	}

	private List<ViajeDTO> obtenerViajesDePedidos(List<PedidoDTO> pedidosDTO) {
		return hibernateDAO.obtenerViajesDePedidos(pedidosDTO);

	}

	// TODO: Para que sirve??
	public List<ViajeDTO> controlarPedidosDeCliente(ClienteDTO clienteDTO) {
		List<ViajeDTO> viajesDTO = new ArrayList<ViajeDTO>();
		List<PedidoDTO> pedidosDTO = obtenerPedidosDelCliente(clienteDTO);
		List<SucursalDTO> sucursales = hibernateDAO.obtenerSucursales();
		/*
		 * for(SucursalDTO s:sucursales) {
		 * viajesDTo.add(obtenerViajesPorPedidos()); }
		 */
		viajesDTO = obtenerViajesDePedidos(pedidosDTO);
		return viajesDTO;
	}


	public void AgregarDemoraParaViaje(ViajeDTO viajeDTO, int tiempoDemoraEnMinutos) {
		long milisegundos = 60000;
		long minutosDemora = tiempoDemoraEnMinutos * milisegundos;
		Date fechaLlegadaOriginal = viajeDTO.getFechaLlegada();
		Date fechaLlegadaActualizada = new Date(fechaLlegadaOriginal.getTime() + minutosDemora);
		viajeDTO.setFechaLlegada(fechaLlegadaActualizada);
		hibernateDAO.actualizarViaje(entityManager.viajeToEntity(viajeDTO));
	}

	public ViajeDTO obtenerViaje(int viajeId) {
		return hibernateDAO.obtenerViaje(viajeId);
	}

	private List<TrayectoDTO> obtenerTrayectosParaRuta(SucursalDTO sucursalOrigen, SucursalDTO sucursalDestino) {
		List<TrayectoDTO> trayectos = new ArrayList<TrayectoDTO>();
		for (RutaDTO ruta : mapaDeRuta.getRutas()) {
			for (TrayectoDTO trayecto : ruta.getTrayectos()) {
				if (trayecto.getSucursalOrigen() == sucursalOrigen && trayecto.getSucursalDestino() == sucursalDestino) {
					trayectos.add(trayecto);
				}
			}
		}
		return trayectos;
	}

	public void cargarMapaDeRuta() {
		List<RutaDTO> rutas = hibernateDAO.obtenerRutas();
		mapaDeRuta = new MapaDeRutaDTO();
		mapaDeRuta.setRutas(rutas);
	}
	
	// TODO: PARA QUE SIRVE??
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

		agregarTrayectoAMapaDeRutas(trayDTO);
		List<RutaDTO> rutas = new ArrayList<RutaDTO>();
		rutas = obtenerTodasLasRutasDirectasASucursal(trayDTO.getSucursalOrigen());

		aux = new TrayectoDTO();
		for (RutaDTO r : rutas) {
			// Se que tiene un solo trayecto porque eso es lo que devuelve
			// obtenerTodasLasRutasDirectas
			TrayectoDTO t = r.getTrayectos().get(0);
			float tiempo = 0;

			// Veo cual es la sucursal mas cercana en TIEMPO o si me conviene
			// volver a la sucursal de origen

			tiempo = t.getTiempo();
			if (tiempo < btiempo) {
				aux = t;
				btiempo = tiempo;
			}

		}

		rutas = obtenerTodasLasRutasDirectasASucursal(trayDTO.getSucursalDestino());
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
		List<ViajeDTO> viajesDTO = hibernateDAO.obtenerViajes();
		for (ViajeDTO v : viajesDTO) {
			if (v.getSucursalOrigen().getIdSucursal() == trayDTO.getSucursalOrigen().getIdSucursal()
					&& v.getSucursalDestino().getIdSucursal() == trayDTO.getSucursalDestino().getIdSucursal()) {
				v.setSucursalDestino(aux.getSucursalDestino());
				long m = (long) aux.getTiempo();
				long milisegundos = 60000;
				Date auxiliar = Calendar.getInstance().getTime();
				long minutosAux = m * milisegundos;
				Date auxiliar2 = new Date(auxiliar.getTime() + minutosAux);
				v.setFechaLlegada(auxiliar2);
				for (EnvioDTO e : v.getEnvios()) {
					e.setFechaLlegada(auxiliar2);
					hibernateDAO.modificar(entityManager.envioToEntity(e));
				}
				hibernateDAO.modificar(entityManager.viajeToEntity(v));
			}

		}

	}

	public MapaDeRutaDTO getMapaDeRuteo() {
		return mapaDeRuta;
	}

	private List<RutaDTO> obtenerTodasLasRutasDirectasASucursal(SucursalDTO sucursal) {
		List<RutaDTO> rutas = new ArrayList<RutaDTO>();
		for (RutaDTO ruta : mapaDeRuta.getRutas()) {
			if (ruta.getOrigen().getIdSucursal() == sucursal.getIdSucursal() && ruta.getTrayectos().size() == 1) {
				rutas.add(ruta);
			}
		}
		return rutas;
	}

	// Una ruta se considera mejor si es mas corta. Si son la misma distancia, depende del precio 
	public RutaDTO obtenerMejorRuta(SucursalDTO sucursalOrigen, SucursalDTO sucursalDestino) {
		cargarMapaDeRuta();
		RutaDTO mejorRuta = null;
		for (RutaDTO ruta : mapaDeRuta.getRutas()){
			if (ruta.getOrigen().getIdSucursal() == sucursalOrigen.getIdSucursal()
					&& ruta.getDestino().getIdSucursal() == sucursalDestino.getIdSucursal()) {
				if (mejorRuta == null) {
					mejorRuta = ruta;
				} else if (ruta.calcularKm() < mejorRuta.calcularKm()) {
					mejorRuta = ruta;
				} else if (ruta.calcularKm() == mejorRuta.calcularKm()) {
					if (ruta.getPrecio() < mejorRuta.getPrecio()) {
						mejorRuta = ruta;
					}
				}
			}
		}
		return mejorRuta;
	}

	// TODO: PARA QUE SIRVE???
	public List<TransporteDTO> obtenerTransportesDeTerceros(CargaDTO cargaDTO, TransporteDTO transporteDTO) {
		return hibernateDAO.obtenerTransportesDeTerceros(cargaDTO, transporteDTO);
	}

	public void agregarTrayectoAMapaDeRutas(TrayectoDTO trayectoDTO) {
		trayectoDTO.setSucursalOrigen(hibernateDAO.obtenerSucursal(trayectoDTO.getSucursalOrigen()));
		trayectoDTO.setSucursalDestino(hibernateDAO.obtenerSucursal(trayectoDTO.getSucursalDestino()));
		hibernateDAO.modificar(entityManager.trayectoToEntity(trayectoDTO));
		cargarMapaDeRuta();
	}

	public void actualizarPedido(PedidoDTO pedidoDTO) {
		hibernateDAO.modificar(entityManager.pedidoToEntity(pedidoDTO));
	}

	public List<PedidoDTO> obtenerPedidos() {
		return hibernateDAO.obtenerPedidos();
	}

	public List<ClienteDTO> obtenerClientes() {
		return hibernateDAO.obtenerClientes();
	}

	public List<CargaDTO> obtenerCargasDeUnPedido(PedidoDTO pedidoDTO) {
		return hibernateDAO.obtenerCargasDeUnPedido(pedidoDTO);
	}

	public String validarCredenciales(String username, String password) {
		return hibernateDAO.validarCredenciales(username, password);
	}

	// TODO: NO SE ENTIENDE
	public void enviar() {
		
		List<PedidoDTO> pedidos = hibernateDAO.obtenerPedidos();
		List<EnvioDTO> envios = new ArrayList<EnvioDTO>();
		List<EnvioDTO> enviosAux = new ArrayList<EnvioDTO>();
		
		pedidos = ordenarPedidosPorPrioridad(pedidos);
		
		Date fechaLlegada = Calendar.getInstance().getTime();
		List<VehiculoDTO> vehiculos = new ArrayList<VehiculoDTO>();
		SucursalDTO sucursalOrigen = new SucursalDTO();
		SucursalDTO sucursal = new SucursalDTO();
		RutaDTO ruta = new RutaDTO();
		ViajeDTO viaje = new ViajeDTO();
		cargarMapaDeRuta();
		boolean existiaEnvioParaElPedido;
		float tiempo = 0;
		float carga = 0;
		
		for (PedidoDTO pedido : pedidos) {
			
			existiaEnvioParaElPedido = true;
			EnvioDTO envio = hibernateDAO.obtenerEnvioDePedido(pedido.getIdPedido());
			
			if (envio == null) {
				envio = new EnvioDTO();
				sucursalOrigen = obtenerSucursal(pedido.getSucursalOrigen());
				existiaEnvioParaElPedido = false;
				envio.setEstado("listo");
			}
			
			if (!envio.getEstado().equals("despachado")) {
				
				if (envio.getEstado().equals("listo")) {
					
					EnvioDTO envioAuxiliar = envio;
					
					if (existiaEnvioParaElPedido) {
						ViajeDTO viajeAux = hibernateDAO.obtenerViajeDeEnvio(envio.getIdEnvio());
						hibernateDAO.borrar(entityManager.viajeToEntity(viajeAux));
					}
					
					envioAuxiliar.setCumpleCondicionesCarga(true);
					envioAuxiliar.setPedido(pedido);
					envioAuxiliar.setFechaSalida(Calendar.getInstance().getTime());
					envioAuxiliar.setPrioridad(1);
					envioAuxiliar.setFechaLlegada(fechaLlegada);
					
					if (existiaEnvioParaElPedido) {
						sucursalOrigen = obtenerSucursal(envioAuxiliar.getSucursalOrigen());
					}

					envioAuxiliar.setSucursalOrigen(String.valueOf(sucursalOrigen.getIdSucursal()));
					envio = envioAuxiliar;
				}

				carga = 0;
				for (CargaDTO c : pedido.getCargas()) {
					carga = c.getVolumen() + carga;
				}
				
				vehiculos = obtenerVehiculosDisponibles();
				
				if (vehiculos != null) {
					
					VehiculoDTO vehiculo = vehiculos.get(0);
					float volu = (vehiculo.getVolumen() * 70) / 100;
					
					if (carga > volu && carga < vehiculo.getVolumen()) {
						
						vehiculo.setEstado("En uso");
						List<EnvioDTO> envioAuxiliar = new ArrayList<EnvioDTO>();
						envio.setEstado("despachado");
						envio.setPedido(pedido);
						envios.add(envio);
						envioAuxiliar.add(envio);
						viaje.setEnvios(envioAuxiliar);
						viaje.setSucursalOrigen(sucursalOrigen);

						sucursal = obtenerSucursal(pedido.getSucursalDestino());
						ruta = obtenerMejorRuta(sucursalOrigen, sucursal);
						viaje.setSucursalDestino(ruta.getTrayectos().get(0).getSucursalDestino());
						viaje.setVehiculo(vehiculo);
						Date llegada = Calendar.getInstance().getTime();

						for (TrayectoDTO t : ruta.getTrayectos()) {
							tiempo = t.getTiempo() + tiempo;
						}
						long m = (long) tiempo;
						long milisegundos = 60000;
						Date auxiliar = Calendar.getInstance().getTime();
						long minutosAux = m * milisegundos;
						Date auxiliar2 = new Date(auxiliar.getTime() + minutosAux);
						viaje.setFechaLlegada(auxiliar2);
						m = (long) ruta.getTrayectos().get(0).getTiempo();
						milisegundos = 60000;
						auxiliar = Calendar.getInstance().getTime();
						minutosAux = m * milisegundos;
						auxiliar2 = new Date(auxiliar.getTime() + minutosAux);
						viaje.setFinalizado(false);
						envio.setFechaLlegada(auxiliar2);
						vehiculo.setEstado("En uso");

						hibernateDAO.modificar(entityManager.vehiculoToEntity(vehiculo));
						/*
						 * 
						 * hbtDAO.modificar(EnvioToEntity(envio)); else
						 */
						// hbtDAO.guardar(EnvioToEntity(envio));
						hibernateDAO.guardar(entityManager.viajeToEntity(viaje));

					}

					else {

						envio.setPedido(pedido);
						enviosAux.add(envio);

					}

				}
			}
		}
		
		if (enviosAux.size() != 0) {
			List<EnvioDTO> en = new ArrayList<EnvioDTO>();
			float volu, cargaAux;
			volu = cargaAux = 0;
			vehiculos = obtenerVehiculos();
			List<SucursalDTO> sucursales = hibernateDAO.obtenerSucursales();
			for (SucursalDTO sOrigen : sucursales) {
				for (SucursalDTO s : sucursales) {
					for (VehiculoDTO v : vehiculos) {
						if (v.getEstado().equals("En Deposito")) {
							carga = 0;
							volu = (v.getVolumen() * 70) / 100;
							en = new ArrayList<EnvioDTO>();
							for (EnvioDTO e : enviosAux) {

								cargaAux = 0;

								if (obtenerSucursal(e.getSucursalOrigen()) == sOrigen
										&& getProximoDestinoParaElPedido(e.getPedido()) == s) {
									for (CargaDTO c : e.getPedido().getCargas()) {
										cargaAux = carga + c.getVolumen();
										if (cargaAux > volu && cargaAux < v.getVolumen()) {
											carga = c.getVolumen() + carga;
											en.add(e);
										}
									}
								}

							}

							if (carga > volu && carga < v.getVolumen()) {

								sucursalOrigen = sOrigen;
								viaje.setEnvios(en);
								viaje.setSucursalOrigen(sucursalOrigen);
								viaje.setSucursalDestino(s);
								viaje.setVehiculo(v);
								Date llegada = Calendar.getInstance().getTime();

								for (TrayectoDTO t : ruta.getTrayectos()) {
									tiempo = t.getTiempo() + tiempo;
								}
								long m = (long) tiempo;
								long milisegundos = 60000;
								Date auxiliar = Calendar.getInstance().getTime();
								long minutosAux = m * milisegundos;
								Date auxiliar2 = new Date(auxiliar.getTime() + minutosAux);
								viaje.setFechaLlegada(auxiliar2);
								viaje.setFinalizado(false);

								for (EnvioDTO e : en) {
									m = (long) ruta.getTrayectos().get(0).getTiempo();
									milisegundos = 60000;
									auxiliar = Calendar.getInstance().getTime();
									minutosAux = m * milisegundos;
									auxiliar2 = new Date(auxiliar.getTime() + minutosAux);

									e.setFechaLlegada(auxiliar2);
									e.setEstado("despachado");

									// hbtDAO.guardar(EnvioToEntity(e));
								}
								v.setEstado("En uso");
								viaje.setEnvios(en);
								hibernateDAO.modificar(entityManager.vehiculoToEntity(v));
								hibernateDAO.guardar(entityManager.viajeToEntity(viaje));
							}
						}
					}

				}
				System.out.println("s" + sOrigen.getIdSucursal());
			}

		}
		System.out.println("SALI");
	}

	// TODO: HACE FALTA HACER
	private List<PedidoDTO> ordenarPedidosPorPrioridad(List<PedidoDTO> pedidos) {

		List<PedidoDTO> aux = new ArrayList<PedidoDTO>();
		for (PedidoDTO p : pedidos) {

			if (!p.getCargas().get(0).isDespachado()) {
				aux.add(p);
			}

		}
		pedidos = new ArrayList<PedidoDTO>();
		pedidos = aux;
		PedidoDTO pedAux = new PedidoDTO();
		for (int i = 0; i < pedidos.size(); i++) {
			/*
			 * SucursalDTO o
			 * =obtenerSucursal(pedidos.get(i).getSucursalOrigen()); SucursalDTO
			 * d =obtenerSucursal(pedidos.get(i).getSucursalDestino()); ViajeDTO
			 * viajeDTO=calcularMejorFechaLlegada(o,d); respuesta=false;
			 * if(!pedidos.get(j).getFechaMaxima().before(viajeDTO.
			 * getFechaLlegada())) { respuesta=enviarUrgente(pedidos.get(j));
			 * if(respuesta) { pedidos.remove(i); } } else if(!respuesta) {
			 */
			for (int j = 0; j < pedidos.size(); j++) {
				if (pedidos.get(j).getFechaMaxima().before(pedidos.get(i).getFechaMaxima())) {
					/*
					 * pedAux=pedidos.get(i); pedAux2=pedidos.get(j);
					 * pedidos.remove(j); pedidos.remove(i);
					 * pedidos.add(i,pedAux2); pedidos.add(j,pedAux);
					 */

				}

			}
			// ]
		}
		return pedidos;

	}

	public PedidoDTO obtenerPedido(int idPedido) throws RemoteException {
		return hibernateDAO.obtenerPedido(idPedido);
	}

	public List<HabilitadoDTO> obtenerHabilitados() throws RemoteException {
		return hibernateDAO.obtenerHabilitados();
	}

	public List<EnvioDTO> obtenerEnviosDelCliente(int idCliente) throws RemoteException {
		return hibernateDAO.obtenerEnviosDelCliente(idCliente);
	}

	public void viajeFinalizado(ViajeDTO viaje) {
		
		viaje = hibernateDAO.obtenerViaje(viaje.getIdViaje());
		
		for (EnvioDTO envio : viaje.getEnvios()) {
			if (Integer.parseInt(envio.getPedido().getSucursalDestino()) == viaje.getSucursalDestino().getIdSucursal()) {
				for (CargaDTO c : envio.getPedido().getCargas()) {
					c.setDespachado(true);
					hibernateDAO.guardar(entityManager.cargaToEntity(c));
				}

			} else {
				envio.setEstado("listo");
			}
			viaje.setFinalizado(true);
		}
		
		VehiculoDTO vehiculo = hibernateDAO.obtenerVehiculo(viaje.getVehiculo().getIdVehiculo());
		vehiculo.setEstado("En uso");

		hibernateDAO.modificar(entityManager.viajeToEntity(viaje));
		hibernateDAO.modificar(entityManager.vehiculoToEntity(vehiculo));
	}

	public List<TransporteDTO> obtenerTransportes() throws RemoteException {
		return hibernateDAO.obtenerTransportes();
	}

	private SucursalDTO getProximoDestinoParaElPedido(PedidoDTO pedido) {
		cargarMapaDeRuta();
		SucursalDTO sucursalOrigen = obtenerSucursal(pedido.getSucursalOrigen());
		SucursalDTO sucursalDestino = obtenerSucursal(pedido.getSucursalDestino());
		RutaDTO ruta = obtenerMejorRuta(sucursalOrigen, sucursalDestino);
		return ruta.getTrayectos().get(0).getSucursalDestino();
	}

	public Date calcularMejorFechaLlegada(SucursalDTO sucursalOrigen, SucursalDTO sucursalDestino) {
		cargarMapaDeRuta();
		RutaDTO ruta = obtenerMejorRuta(sucursalOrigen, sucursalDestino);
		float tiempo = 0;
		for (TrayectoDTO trayecto : ruta.getTrayectos()) {
			tiempo = trayecto.getTiempo() + tiempo;
		}
		long minutosFechaLlegada = (long) tiempo * 60000;
		Date fechaActual = Calendar.getInstance().getTime();
		Date mejorFechaLlegada = new Date(fechaActual.getTime() + minutosFechaLlegada);
		return mejorFechaLlegada;
	}

	private SucursalDTO obtenerSucursal(String nombreSucursal) {
		List<SucursalDTO> sucursales = hibernateDAO.obtenerSucursales();
		for (SucursalDTO sucursal : sucursales) {
			if (sucursal.getNombre().equals(nombreSucursal)) {
				return sucursal;
			}
		}
		return null;
	}

	private List<VehiculoDTO> obtenerVehiculosDisponibles() {
		List<VehiculoDTO> vehiculosDisponibles = new ArrayList<VehiculoDTO>();
		List<VehiculoDTO> vehiculos = hibernateDAO.obtenerVehiculos();
		for (VehiculoDTO v : vehiculos) {
			if (v.getEstado().equals("En Deposito")) {
				vehiculosDisponibles.add(v);
			}
		}
		return vehiculosDisponibles;
	}

	public ClienteDTO obtenerClientePorID(int clienteId) {
		return hibernateDAO.obtenerClientePorID(clienteId);
	}
}
/*
 * //NUEVO FALTA PROBARLO private boolean enviarUrgente(PedidoDTO p) { EnvioDTO
 * envio =hbtDAO.obtenerEnvioDePedido(p.getIdPedido()); if(envio==null) {
 * envio=new EnvioDTO();
 * 
 * sucursalOrigen=obtenerSucursal(p.getSucursalOrigen()); nul=true;
 * envio.setEstado("listo");
 * 
 * } if(!envio.getEstado().equals("despachado")) {
 * if(envio.getEstado().equals("listo")) { EnvioDTO envio2=envio;
 * if(envio!=null) { ViajeDTO
 * viajeAux=hbtDAO.obtenerViajeDeEnvio(envio.getIdEnvio());
 * hbtDAO.borrar(ViajeToEntity(viajeAux)); }
 * envio2.setCumpleCondicionesCarga(true); envio2.setPedido(p);
 * envio2.setFechaSalida(Calendar.getInstance().getTime());
 * envio2.setPrioridad(1); envio2.setFechaLlegada(fechaLlegada);
 * if(envio2!=null) sucursalOrigen=obtenerSucursal(envio2.getSucursalOrigen());
 * 
 * envio2.setSucursalOrigen(sucursalOrigen.getNombre()); envio=envio2; }
 * carga=0; for(CargaDTO c:p.getCargas()) { carga=c.getVolumen()+carga; }
 * vehiculos=obtenerVehiculosListos(); if(vehiculos!=null) { VehiculoDTO
 * v=vehiculos.get(0);
 * 
 * v.setEstado("En uso"); List<EnvioDTO>e=new ArrayList<EnvioDTO>();
 * envio.setEstado("despachado"); envio.setPedido(p); envios.add(envio);
 * e.add(envio); viaje.setEnvios(e); viaje.setSucursalOrigen(sucursalOrigen);
 * 
 * sucursal=obtenerSucursal(p.getSucursalDestino());
 * ruta=obtenerMejorRuta(sucursalOrigen,sucursal);
 * viaje.setSucursalDestino(ruta.getTrayectos).get0).getSucursalDestino());
 * viaje.setVehiculo(v); Date llegada=Calendar.getInstance().getTime();
 * 
 * for(TrayectoDTO t:ruta.getTrayectos()) { tiempo=t.getTiempo()+tiempo; } long
 * m=(long) tiempo; long milisegundos=60000; Date auxiliar=
 * Calendar.getInstance().getTime(); long minutosAux=m*milisegundos; Date
 * auxiliar2=new Date(auxiliar.getTime() + minutosAux);
 * viaje.setFechaLlegada(auxiliar2); m=(long)
 * ruta.getTrayectos().get(0).getTiempo(); milisegundos=60000; auxiliar=
 * Calendar.getInstance().getTime(); minutosAux=m*milisegundos; auxiliar2=new
 * Date(auxiliar.getTime() + minutosAux); viaje.setFinalizado(false);
 * envio.setFechaLlegada(auxiliar2); v.setEstado("En uso");
 * 
 * 
 * hbtDAO.modificar(VehiculoToEntity(v));
 * 
 * 
 * hbtDAO.guardar(ViajeToEntity(viaje)); return true;
 * 
 * } else{
 * 
 * return false;
 * 
 * }
 * 
 * } } return false; } }
 */