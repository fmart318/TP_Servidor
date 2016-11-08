package dao;

import hbt.HibernateUtil;

import java.util.ArrayList;
import java.util.List;

import dto.VehiculoDTO;
import entities.Vehiculo;

public class HibernateDAOVehiculo extends HibernateDAO {

	private static HibernateDAOVehiculo instancia;

	public static HibernateDAOVehiculo getInstancia() {
		if (instancia == null) {
			sessionFactory = HibernateUtil.getSessionfactory();
			instancia = new HibernateDAOVehiculo();
		}
		return instancia;
	}

	@SuppressWarnings("unchecked")
	public List<VehiculoDTO> obtenerVehiculos() {
		List<VehiculoDTO> vehiculosDTO = new ArrayList<VehiculoDTO>();
		try {
			List<Vehiculo> vehiculos = sessionFactory.openSession().createQuery("FROM Vehiculo").list();
			for (Vehiculo vehiculo : vehiculos) {
				vehiculosDTO.add(vehiculo.toDTO());
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		this.closeSession();
		return vehiculosDTO;
	}

	public VehiculoDTO obtenerVehiculoPorId(int id) {
		VehiculoDTO vehiculoDTO = new VehiculoDTO();
		try {
			Vehiculo vehiculo = (Vehiculo) sessionFactory.openSession()
					.createQuery("FROM Vehiculo v where v.id = :vehiculo")
					.setParameter("vehiculo", id).uniqueResult();

			vehiculoDTO = vehiculo.toDTO();
			this.closeSession();
		} catch (Exception e) {
			System.out.println(e);
		}
		this.closeSession();
		return vehiculoDTO;
	}
}
