package dao;

import hbt.HibernateUtil;

public class HibernateDAOProveedor extends HibernateDAO {
	
	private static HibernateDAOProveedor instancia;

	public static HibernateDAOProveedor getInstancia() {
		if (instancia == null) {
			sessionFactory = HibernateUtil.getSessionfactory();
			instancia = new HibernateDAOProveedor();
		}
		return instancia;
	}
	

}
