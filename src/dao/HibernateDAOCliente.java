package dao;

import hbt.HibernateUtil;

import java.util.List;

import dto.ClienteDTO;

public class HibernateDAOCliente extends HibernateDAO {

	private static HibernateDAOCliente instancia;

	public static HibernateDAOCliente getInstancia() {
		if (instancia == null) {
			sessionFactory = HibernateUtil.getSessionfactory();
			instancia = new HibernateDAOCliente();
		}
		return instancia;
	}

	public Object obtenerClientePorDNI(int dNI) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<ClienteDTO> obtenerClientes() {
		// TODO Auto-generated method stub
		return null;
	}

	public ClienteDTO obtenerClientePorID(int id) {
		// TODO Auto-generated method stub
		return null;
	}
}
