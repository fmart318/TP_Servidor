package dao;

import hbt.HibernateUtil;
import hbt.PersistentObject;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import dto.CargaDTO;
import dto.EnvioDTO;
import dto.HabilitadoDTO;
import dto.PedidoDTO;
import dto.RutaDTO;
import dto.SeguroDTO;
import dto.SucursalDTO;
import dto.TransporteDTO;
import dto.VehiculoDTO;

public class HibernateDAO {

	private static HibernateDAO instancia;
	protected static SessionFactory sessionFactory = null;
	protected static Session session = null;

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
		Transaction t = null;
		Session s = sessionFactory.getCurrentSession();
		try {
			t = (Transaction) s.beginTransaction();

			s.save(entidad);
			System.out.println("Object Saved");
			t.commit();

		} catch (Exception e) {
			t.rollback();
			System.out.println(e);
			System.out.println("ErrorDAO: " + entidad.getClass().getName()
					+ ".guardar");
		}
	}

	public void borrar(PersistentObject entidad) {
		Transaction t = null;
		Session s = sessionFactory.getCurrentSession();
		try {

			t = s.beginTransaction();

			s.delete(entidad);
			t.commit();

		} catch (Exception e) {
			t.rollback();
			System.out.println(e);
			System.out.println("ErrorDAO: " + entidad.getClass().getName()
					+ ".borrar");
		}
	}

	public void modificar(PersistentObject entidad) {
		Transaction t = null;
		Session s = sessionFactory.getCurrentSession();
		try {

			t = s.beginTransaction();

			s.update(entidad);
			t.commit();

		} catch (Exception e) {
			t.rollback();
			System.out.println(e);
			System.out.println("ErrorDAO: " + entidad.getClass().getName()
					+ ".modificar");
		}
	}

	public List<SucursalDTO> obtenerSucursales() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<VehiculoDTO> obtenerVehiculos() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<SeguroDTO> obtenerSegurosParaCarga(String tipoMercaderia) {
		// TODO Auto-generated method stub
		return null;
	}

	public VehiculoDTO obtenerVehiculo(int idVehiculo) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<RutaDTO> obtenerRutas() {
		// TODO Auto-generated method stub
		return null;
	}

	public SucursalDTO obtenerSucursal(SucursalDTO sucursalOrigen) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<TransporteDTO> obtenerTransportesDeTerceros(CargaDTO c,
			TransporteDTO tr) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<PedidoDTO> obtenerPedidos() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<CargaDTO> obtenerCargasDeUnPedido(PedidoDTO pedido) {
		// TODO Auto-generated method stub
		return null;
	}

	public String validarCredenciales(String username, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	public EnvioDTO obtenerEnvioDePedido(int idPedido) {
		// TODO Auto-generated method stub
		return null;
	}

	public PedidoDTO obtenerPedido(int idPedido) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<HabilitadoDTO> obtenerHabilitados() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<EnvioDTO> obtenerEnvios(String nombre) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<TransporteDTO> obtenerTransportes() {
		// TODO Auto-generated method stub
		return null;
	}
}
