package dao;

import hbt.HibernateUtil;
import hbt.PersistentObject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import dto.CargaDTO;
import dto.ClienteDTO;
import dto.EnvioDTO;
import dto.HabilitadoDTO;
import dto.ParticularDTO;
import dto.PedidoDTO;
import dto.PlanDeMantenimientoDTO;
import dto.RutaDTO;
import dto.SeguroDTO;
import dto.SucursalDTO;
import dto.TransporteDTO;
import dto.TrayectoDTO;
import dto.VehiculoDTO;
import dto.ViajeDTO;
import entities.Carga;
import entities.Cliente;
import entities.Envio;
import entities.Habilitado;
import entities.Particular;
import entities.Pedido;
import entities.PlanDeMantenimiento;
import entities.Ruta;
import entities.Seguro;
import entities.Sucursal;
import entities.Transporte;
import entities.Trayecto;
import entities.Vehiculo;
import entities.Viaje;

public class HibernateDAO {

	private static HibernateDAO instancia;
	private static SessionFactory sessionFactory = null;
	private static Session session = null;

	public static HibernateDAO getInstancia() {
		if (instancia == null) {
			sessionFactory = HibernateUtil.getSessionfactory();
			instancia = new HibernateDAO();
		}
		return instancia;
	}

	public Session getSession() {
		if (session == null || !session.isOpen()) {
			session = sessionFactory.openSession();
		}
		return session;
	}

	public void closeSession() {
		if (session.isOpen()) {
			session.close();
		}
	}

	public void guardar(PersistentObject entidad) {
		Transaction transaction = null;
		Session session = sessionFactory.getCurrentSession();
		try {
			transaction = (Transaction) session.beginTransaction();
			session.save(entidad);
			System.out.println("Object Saved");
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			System.out.println("ErrorDAO: " + entidad.getClass().getName() + ".guardar");
		}
	}

	public void borrar(PersistentObject entidad) {
		Transaction transaction = null;
		Session session = sessionFactory.getCurrentSession();
		try {
			transaction = session.beginTransaction();
			session.delete(entidad);
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			System.out.println("ErrorDAO: " + entidad.getClass().getName()
					+ ".borrar");
		}
	}

	public void modificar(PersistentObject entidad) {
		Transaction transaction = null;
		Session session = sessionFactory.getCurrentSession();
		try {
			transaction = session.beginTransaction();
			session.update(entidad);
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			System.out.println("ErrorDAO: " + entidad.getClass().getName()
					+ ".modificar");
		}
	}

	public void mergear(PersistentObject entidad) {
		Transaction transaction = null;
		Session session = sessionFactory.getCurrentSession();
		try {
			transaction = session.beginTransaction();
			session.merge(entidad);
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			System.out.println("ErrorDAO: " + entidad.getClass().getName()
					+ ".mergear");
		}
	}

	@SuppressWarnings("unchecked")
	public List<ViajeDTO> obtenerViajesDeCliente(int idCliente) {
		List<ViajeDTO> viajesDTO = new ArrayList<ViajeDTO>();
		Session session = this.getSession();
		try {
			List<Viaje> viajes = session
					.createQuery(
							"From Viaje v Join Envio e where e.idViaje=v.idViaje And e.pedido.cliente.idCliente=:id ")
					.setParameter("id", idCliente).list();
			for (Viaje viaje : viajes) {
				viajesDTO.add(viaje.toDTO());
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		this.closeSession();
		return viajesDTO;
	}

	public int obtenerDiasHastaFechaDeLlegadaParaViaje(int idViaje) {
		int dias = 0;
		Session session = this.getSession();
		try {
			Date fechaLlegada = (Date) session
					.createQuery(
							"Select v.fechaLlegada from Viaje v where v.=:id ")
					.setParameter("id", idViaje).uniqueResult();
			Calendar calendar = Calendar.getInstance();
			dias = (int) (fechaLlegada.getTime() - calendar.getTime().getTime()) / 86400000;
		} catch (Exception e) {
			System.out.println(e);
		}
		this.closeSession();
		return dias;
	}

	@SuppressWarnings("unchecked")
	public List<SucursalDTO> obtenerSucursales() {
		List<SucursalDTO> sucursalesDTO = new ArrayList<SucursalDTO>();
		Session session = this.getSession();
		try {
			List<Sucursal> sucursales = session.createQuery("FROM Sucursal").list();
			for (Sucursal sucursal : sucursales) {
				sucursalesDTO.add(sucursal.toDTO());
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		this.closeSession();
		return sucursalesDTO;
	}

	@SuppressWarnings("unchecked")
	public List<ViajeDTO> obtenerViajes() {
		List<ViajeDTO> viajesDTO = new ArrayList<ViajeDTO>();
		Session session = this.getSession();
		try {
			List<Viaje> viajes = session.createQuery("FROM Viaje").list();
			for (Viaje viaje : viajes) {
				viajesDTO.add(viaje.toDTO());
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		this.closeSession();
		return viajesDTO;
	}

	public ParticularDTO obtenerClienteParticular(int DNI) {
		ParticularDTO particularDTO = new ParticularDTO();
		Session session = this.getSession();
		try {
			Particular particular = (Particular) session
					.createQuery("FROM Particular p where p.DNI=:dni")
					.setParameter("dni", DNI).uniqueResult();
			particularDTO = particular.toDTO();
		} catch (Exception e) {
			System.out.println(e);
		}
		this.closeSession();
		return particularDTO;
	}

	@SuppressWarnings("unchecked")
	public List<SeguroDTO> obtenerSegurosParaCarga(String tipoMercaderia) {
		List<SeguroDTO> segurosDTO = new ArrayList<SeguroDTO>();
		Session session = this.getSession();
		try {
			List<Seguro> seguros = session
					.createQuery(
							"FROM Seguro s where s.tipoMercaderia=:tipoMercaderia")
					.setParameter("tipoMercaderia", tipoMercaderia).list();
			for (Seguro seguro : seguros) {
				segurosDTO.add(seguro.toDTO());
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		this.closeSession();
		return segurosDTO;
	}

	@SuppressWarnings("unchecked")
	public List<VehiculoDTO> obtenerVehiculos() {
		List<VehiculoDTO> vehiculosDTO = new ArrayList<VehiculoDTO>();
		Session session = this.getSession();
		try {
			List<Vehiculo> vehiculos = session.createQuery("FROM Vehiculo").list();
			for (Vehiculo vehiculo : vehiculos) {
				vehiculosDTO.add(vehiculo.toDTO());
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		this.closeSession();
		return vehiculosDTO;
	}

	public VehiculoDTO obtenerVehiculo(int id) {
		VehiculoDTO vehiculoDTO = new VehiculoDTO();
		Session session = this.getSession();
		try {
			Vehiculo vehiculo = (Vehiculo) session
					.createQuery("FROM Vehiculo v where v.id = :vehiculo")
					.setParameter("vehiculo", id).uniqueResult();

			vehiculoDTO = vehiculo.toDTO();
			this.closeSession();
			return vehiculoDTO;
		} catch (Exception e) {
			System.out.println(e);
		}
		this.closeSession();
		return null;
	}

	public PlanDeMantenimientoDTO obtenerPlanDeMantenimiento(int idVehiculo) {
		PlanDeMantenimientoDTO planDeMantenimientoDTO = new PlanDeMantenimientoDTO();
		Session session = this.getSession();
		try {
			PlanDeMantenimiento planDeMantenimiento = (PlanDeMantenimiento) session
					.createQuery(
							"Select v.planDeMantenimiento FROM Vehiculo v where v.idVehiculo=:id")
					.setParameter("id", idVehiculo).list();
			planDeMantenimientoDTO = planDeMantenimiento.toDTO();
		} catch (Exception e) {
			System.out.println(e);
		}
		this.closeSession();
		return planDeMantenimientoDTO;
	}

	public SucursalDTO obtenerSucursal(SucursalDTO sucursalOrigen) {
		SucursalDTO sucursalDTO = new SucursalDTO();
		Session session = this.getSession();
		try {
			Sucursal suc = (Sucursal) session
					.createQuery(" FROM Sucursal s where s.idSucursal=:id")
					.setParameter("id", sucursalOrigen.getIdSucursal())
					.uniqueResult();
			sucursalDTO = suc.toDTO();
		} catch (Exception e) {
			System.out.println(e);
		}
		this.closeSession();
		return sucursalDTO;
	}

	public SucursalDTO obtenerSucursal(String nombre) {
		SucursalDTO sucursalDTO = new SucursalDTO();
		Session session = this.getSession();
		try {
			Sucursal suc = (Sucursal) session
					.createQuery(" FROM Sucursal s where s.nombre=:id")
					.setParameter("id", nombre).uniqueResult();
			sucursalDTO = suc.toDTO();
		} catch (Exception e) {
			System.out.println(e);
		}
		this.closeSession();
		return sucursalDTO;
	}

	public ViajeDTO obtenerViajeDelVehiculo(VehiculoDTO vehiculo) {
		ViajeDTO viajeDTO = new ViajeDTO();
		int vehiculoId = vehiculo.getIdVehiculo();
		Session session = this.getSession();
		try {
			Viaje viaje = (Viaje) session
					.createQuery(
							" FROM Viaje v where v.vehiculo.id =:idVehiculo")
					.setParameter("idVehiculo", vehiculoId).uniqueResult();

			viajeDTO = viaje.toDTO();
			this.closeSession();
			return viajeDTO;
		} catch (Exception e) {
			System.out.println(e);
		}
		this.closeSession();
		return null;
	}

	public void actualizarViaje(Viaje viaje) {
		Transaction transaction = null;
		Session session = sessionFactory.getCurrentSession();
		try {
			transaction = session.beginTransaction();
			session.update(viaje);
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			System.out.println(e);
			System.out.println("ErrorDAO: " + viaje.getClass().getName()
					+ ".modificar");
		}
	}

	@SuppressWarnings("unchecked")
	public List<PedidoDTO> obtenerPedidosDeCliente(int idCliente) {
		List<PedidoDTO> pedidosDTO = new ArrayList<PedidoDTO>();
		Session session = this.getSession();
		try {
			List<Pedido> pedidos = session
					.createQuery(
							"from Pedido p where p.cliente.idCliente =:id ")
					.setParameter("id", idCliente).list();
			for (Pedido pedido : pedidos) {
				pedidosDTO.add(pedido.toDTO());
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		this.closeSession();
		return pedidosDTO;
	}

	@SuppressWarnings("unchecked")
	public List<ViajeDTO> obtenerViajesDePedidos(List<PedidoDTO> pedidosDTO) {
		List<ViajeDTO> viajesDTO = new ArrayList<ViajeDTO>();
		List<Viaje> viajes = new ArrayList<Viaje>();
		Session session = this.getSession();
		int idPedido = 0;
		for (int i = 0; i < pedidosDTO.size(); i++) {
			idPedido = pedidosDTO.get(i).getIdPedido();
			try {
				viajes = session.createQuery(
						"Select e.viajes from Envio e where e.pedido.idPedido IN (:id) ")
						.setParameter("id", idPedido).list();

				for (Viaje viaje : viajes) {
					viajesDTO.add(viaje.toDTO());
				}
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		this.closeSession();
		return viajesDTO;
	}

	public ViajeDTO obtenerViajeDeEnvio(int id) {
		Session session = this.getSession();
		ViajeDTO viajeDTO = new ViajeDTO();
		try {
			Viaje viaje = (Viaje) session
					.createQuery("FROM Viaje v JOIN v.envios.idEnvio=:idEnvio ")
					.setParameter("idEnvio", id).uniqueResult();
			viajeDTO = viaje.toDTO();
			this.closeSession();
			return viajeDTO;
		} catch (Exception e) {
			System.out.println(e);
		}
		this.closeSession();
		return null;
	}

	public ViajeDTO obtenerViaje(int id) {
		ViajeDTO viajeDTO = new ViajeDTO();
		Session session = this.getSession();
		try {
			Viaje viaje = (Viaje) session.createQuery("FROM Viaje v where v.id = :viaje")
					.setParameter("viaje", id).uniqueResult();

			viajeDTO = viaje.toDTO();
			this.closeSession();
			return viajeDTO;
		} catch (Exception e) {
			System.out.println(e);
		}
		this.closeSession();
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<TransporteDTO> obtenerTransportesDeTerceros(CargaDTO c,
			TransporteDTO tr) {
		List<TransporteDTO> transportesDTO = new ArrayList<TransporteDTO>();
		String mercaderia = c.getTipoMercaderia();
		String tipoTransporte = tr.getTipoTransporte();
		// Transporte tiene un tipo MERcaderia, que no esta en el DC asi que no
		// lo tengo en cuenta
		Session session = this.getSession();
		try {
			List<Transporte> transportes = session
					.createQuery(
							"Select p.Transporte from Proveedor p  where p.tipoMercaderia=(:mer) and p.Transporte.tipoTransporte=(:trans) ")
					.setParameter("mer", mercaderia)
					.setParameter("trans", tipoTransporte).list();
			for (Transporte transporte : transportes) {
				transportesDTO.add(transporte.toDTO());
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		this.closeSession();
		return transportesDTO;
	}

	@SuppressWarnings("unchecked")
	public List<RutaDTO> obtenerRutas() {
		List<RutaDTO> rutasDTO = new ArrayList<RutaDTO>();
		Session session = this.getSession();
		try {
			List<Ruta> rutas = session.createQuery("FROM Ruta").list();
			for (Ruta ruta : rutas) {
				rutasDTO.add(ruta.toDTO());
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		this.closeSession();
		return rutasDTO;
	}

	public TrayectoDTO obtenerTrayecto(TrayectoDTO trayDTO) {
		TrayectoDTO trayectoDTO = new TrayectoDTO();
		Session session = this.getSession();
		int idOrigen = trayDTO.getSucursalOrigen().getIdSucursal();
		int idDestino = trayDTO.getSucursalDestino().getIdSucursal();
		try {
			Trayecto tr = (Trayecto) session
					.createQuery(
							"FROM Trayecto t where t.idSucursalOrigen.idSucursal =:idOrigen and t.idSucursalDestino.idSucursal=:idDestino")
					.setParameter("idOrigen", idOrigen)
					.setParameter("idDestino", idDestino).uniqueResult();
			trayectoDTO = tr.toDTO();
			this.closeSession();
			return trayectoDTO;
		} catch (Exception e) {
			System.out.println(e);
		}
		this.closeSession();
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<PedidoDTO> obtenerPedidos() {
		List<PedidoDTO> pedidosDTO = new ArrayList<PedidoDTO>();
		Session session = this.getSession();
		try {
			List<Pedido> pedidos = session.createQuery("FROM Pedido").list();
			for (Pedido p : pedidos) {
				pedidosDTO.add(p.toDTO());
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		this.closeSession();
		return pedidosDTO;
	}

	@SuppressWarnings("unchecked")
	public List<ClienteDTO> obtenerClientes() {
		List<ClienteDTO> clientesDTO = new ArrayList<ClienteDTO>();
		Session session = this.getSession();
		try {
			List<Cliente> clientes = session.createQuery("FROM Cliente").list();
			for (Cliente c : clientes) {
				clientesDTO.add(c.toDTO());
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		this.closeSession();
		return clientesDTO;
	}

	public ClienteDTO obtenerClientePorID(int id) {
		ClienteDTO clienteDTO = new ClienteDTO();
		Session session = this.getSession();
		try {
			Cliente c = (Cliente) session
					.createQuery("FROM Cliente c where c.id=:id")
					.setParameter("id", id).uniqueResult();
			clienteDTO = c.toDTO();
		} catch (Exception e) {
			System.out.println(e);
		}
		this.closeSession();
		return clienteDTO;
	}

	public EnvioDTO obtenerEnvioDePedido(int idPedido) {
		EnvioDTO envioDTO = new EnvioDTO();
		Envio envio = new Envio();
		Session session = this.getSession();
		try {
			envio = (Envio) session
					.createQuery(" from Envio e where e.pedido.idPedido=:id  ")
					.setParameter("id", idPedido).uniqueResult();
			if (envio.equals(null)) {
				envioDTO = null;
			}
			envioDTO = envio.toDTO();
		} catch (Exception ex) {
			System.out.println(ex);
			this.closeSession();
			return null;
		}
		this.closeSession();
		return envioDTO;
	}

	@SuppressWarnings("unchecked")
	public List<CargaDTO> obtenerCargasDeUnPedido(PedidoDTO pedido) {
		List<CargaDTO> cargasDTO = new ArrayList<CargaDTO>();
		Session session = this.getSession();
		try {
			List<Carga> cargas = session
					.createQuery(
							"SELECT p.cargas FROM Pedido p JOIN p.cargas WHERE p.idPedido=:id")
					.setParameter("id", pedido.getIdPedido()).list();
			for (Carga c : cargas) {
				cargasDTO.add(c.toDTO());
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		this.closeSession();
		return cargasDTO;
	}

	public PedidoDTO obtenerPedido(int idPedido) {
		PedidoDTO pedidoDTO = new PedidoDTO();
		Session session = this.getSession();
		try {
			Pedido pedido = (Pedido) session
					.createQuery("FROM Pedido p WHERE p.idPedido=:id")
					.setParameter("id", idPedido).uniqueResult();
			pedidoDTO = pedido.toDTO();
		} catch (Exception e) {
			System.out.println(e);
		}
		this.closeSession();
		return pedidoDTO;
	}

	@SuppressWarnings("unchecked")
	public List<HabilitadoDTO> obtenerHabilitados() {
		List<HabilitadoDTO> habilitadosDTO = new ArrayList<HabilitadoDTO>();
		Session session = this.getSession();
		try {
			List<Habilitado> habilitados = session.createQuery("FROM Habilitado")
					.list();
			for (Habilitado h : habilitados) {
				habilitadosDTO.add(h.toDTO());
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		this.closeSession();
		return habilitadosDTO;
	}

	public String validarCredenciales(String username, String password) {
		String error = "No Valido";
		Session session = this.getSession();
		try {
			error = (String) session
					.createQuery(
							"Select c.type FROM Credential c WHERE c.username=:username and c.password=:password")
					.setParameter("username", username)
					.setParameter("password", password).uniqueResult();
		} catch (Exception e) {
			System.out.println(e);
		}
		this.closeSession();
		return error;
	}

	@SuppressWarnings("unchecked")
	public List<TransporteDTO> obtenerTransportes() {
		List<TransporteDTO> transportesDTO = new ArrayList<TransporteDTO>();
		Session session = this.getSession();
		try {
			List<Transporte> transportes = session.createQuery("FROM Transporte")
					.list();
			for (Transporte t : transportes) {
				transportesDTO.add(t.toDTO());
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		this.closeSession();
		return transportesDTO;
	}

	@SuppressWarnings("unchecked")
	public List<EnvioDTO> obtenerEnviosDelCliente(int idCliente) {
		List<EnvioDTO> enviosDTO = new ArrayList<EnvioDTO>();
		Session session = this.getSession();
		try {
			List<Envio> envios = session.createQuery("FROM Envio").list();
			List<Pedido> pedidos = session.createQuery("FROM Pedido").list();
			for (Envio envio : envios) {
				for (Pedido pedido : pedidos) {
					if (pedido.getCliente().getIdCliente() == idCliente
							&& envio.getPedido().getIdPedido() == pedido.getIdPedido()) {
						enviosDTO.add(envio.toDTO());
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		this.closeSession();
		return enviosDTO;
	}

}