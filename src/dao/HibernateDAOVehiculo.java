package dao;

import java.util.List;

import dto.ClienteDTO;
import dto.VehiculoDTO;
import hbt.HibernateUtil;

public class HibernateDAOVehiculo extends HibernateDAO {

	private static HibernateDAOVehiculo instancia;

	public static HibernateDAOVehiculo getInstancia() {
		if (instancia == null) {
			sessionFactory = HibernateUtil.getSessionfactory();
			instancia = new HibernateDAOVehiculo();
		}
		return instancia;
	}

	public List<VehiculoDTO> obtenerVehiculos() {
		// TODO Auto-generated method stub
		return null;
	}

	public ClienteDTO obtenerVehiculoePorID(int id) {
		// TODO Auto-generated method stub
		return null;
	}
}
