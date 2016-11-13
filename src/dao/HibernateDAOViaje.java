package dao;

import hbt.HibernateUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import dto.PedidoDTO;
import dto.VehiculoDTO;
import dto.ViajeDTO;
import entities.Viaje;

public class HibernateDAOViaje extends HibernateDAO {
	
	private static HibernateDAOViaje instancia;

	public static HibernateDAOViaje getInstancia() {
		if (instancia == null) {
			sessionFactory = HibernateUtil.getSessionfactory();
			instancia = new HibernateDAOViaje();
		}
		return instancia;
	}

	@SuppressWarnings("unchecked")
	public List<ViajeDTO> obtenerViajesDeCliente(int idCliente) {
		List<ViajeDTO> viajesDTO = new ArrayList<ViajeDTO>();
		Session s = this.getSession();
		try {
			List<Viaje> viajes = s
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
	
	public int seleccionarViaje(int idViaje) {
		int dias = 0;
		Session s = this.getSession();
		try {
			Date fechaLlegada = (Date) s
					.createQuery(
							"Select v.fechaLlegada from Viaje v where v.=:id ")
					.setParameter("id", idViaje).uniqueResult();
			Calendar cal = Calendar.getInstance();
			dias = (int) (fechaLlegada.getTime() - cal.getTime().getTime()) / 86400000;
		} catch (Exception e) {
			System.out.println(e);
		}
		this.closeSession();
		return dias;
	}
	
	@SuppressWarnings("unchecked")
	public List<ViajeDTO> obtenerViajes() {
		List<ViajeDTO> viajesDTO = new ArrayList<ViajeDTO>();
		Session s = this.getSession();
		try {
			List<Viaje> viajes = s.createQuery("FROM Viaje").list();
			for (Viaje viaje : viajes) {
				viajesDTO.add(viaje.toDTO());
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		this.closeSession();
		return viajesDTO;
	}
	
	public ViajeDTO obtenerViajePorVehiculo(VehiculoDTO vehiculo) {
		ViajeDTO viaje = new ViajeDTO();
		int id = vehiculo.getIdVehiculo();
		Session s = this.getSession();
		try {
			Viaje v = (Viaje) s
					.createQuery(
							" FROM Viaje v where v.vehiculo.id =:idVehiculo")
					.setParameter("idVehiculo", id).uniqueResult();

			viaje = v.toDTO();
			this.closeSession();
			return viaje;
		} catch (Exception e) {
			System.out.println(e);
		}
		this.closeSession();
		return null;
	}
	
	public void updateViaje(Viaje viaje) {

		Transaction t = null;
		Session s = sessionFactory.getCurrentSession();
		try {

			t = s.beginTransaction();

			s.update(viaje);
			t.commit();

		} catch (Exception e) {
			t.rollback();
			System.out.println(e);
			System.out.println("ErrorDAO: " + viaje.getClass().getName()
					+ ".modificar");
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<ViajeDTO> obtenerViajesDePedidos(List<PedidoDTO> pedidosDTO) {
		List<ViajeDTO> viajesDTO = new ArrayList<ViajeDTO>();
		int x = 0;

		List<Viaje> aux = new ArrayList<Viaje>();
		Session s = this.getSession();
		for (int i = 0; i < pedidosDTO.size(); i++) {
			x = pedidosDTO.get(i).getIdPedido();
			try {
				aux = s.createQuery(
						"Select e.viajes from Envio e where e.pedido.idPedido IN (:id) ")
						.setParameter("id", x).list();

				for (Viaje viaje : aux) {
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
		Session s = this.getSession();
		ViajeDTO viajeDTO = new ViajeDTO();
		try {
			Viaje v = (Viaje) s
					.createQuery("FROM Viaje v JOIN v.envios.idEnvio=:idEnvio ")
					.setParameter("idEnvio", id).uniqueResult();

			viajeDTO = v.toDTO();
			System.out.println(viajeDTO.getIdViaje());
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
		Session s = this.getSession();
		try {
			Viaje v = (Viaje) s.createQuery("FROM Viaje v where v.id = :viaje")
					.setParameter("viaje", id).uniqueResult();

			viajeDTO = v.toDTO();
			this.closeSession();
			return viajeDTO;
		} catch (Exception e) {
			System.out.println(e);
		}
		this.closeSession();
		return null;
	}
}
