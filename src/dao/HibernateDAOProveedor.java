package dao;

import hbt.HibernateUtil;

import java.util.ArrayList;
import java.util.List;

import dto.ProveedorDTO;
import entities.Proveedor;

public class HibernateDAOProveedor extends HibernateDAO {
	
	private static HibernateDAOProveedor instancia;

	public static HibernateDAOProveedor getInstancia() {
		if (instancia == null) {
			sessionFactory = HibernateUtil.getSessionfactory();
			instancia = new HibernateDAOProveedor();
		}
		return instancia;
	}
	
	@SuppressWarnings("unchecked")
	public List<ProveedorDTO> obtenerProveedores() {
		List<ProveedorDTO> proveedoresDTO = new ArrayList<ProveedorDTO>();
		try {
			List<Proveedor> proveedores = sessionFactory.openSession().createQuery("FROM Sucursal").list();
			for (Proveedor proveedor : proveedores) {
				proveedoresDTO.add(proveedor.toDTO());
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		this.closeSession();
		return proveedoresDTO;
	}
	
	public ProveedorDTO obtenerProveedorPorId(int id) {
		ProveedorDTO dto = new ProveedorDTO();
		try {
			Proveedor proveedor = obtenerProveedorEntityPorId(id);
			dto = proveedor.toDTO();
		} catch (Exception e) {
			System.out.println(e);
		}
		this.closeSession();
		return dto;
	}
	
	private Proveedor obtenerProveedorEntityPorId(int id) {
		Proveedor proveedor = (Proveedor) sessionFactory.openSession().createQuery("FROM Cliente c where c.id=:id")
				.setParameter("id", id).uniqueResult(); 
		return proveedor;
	}
	
	public void eliminarProveedor(int id) {
		Proveedor proveedor = obtenerProveedorEntityPorId(id);
		borrar(proveedor);
	}
	
	public void modificarProveedor() {
		
	}
}
